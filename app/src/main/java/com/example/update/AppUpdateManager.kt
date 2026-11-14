package com.example.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

data class AppUpdateInfo(
    val versionName: String,
    val tagName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val apkFileName: String,
    val apkSizeBytes: Long,
    val sha256Url: String? = null
)

sealed interface AppUpdateUiState {
    data object Hidden : AppUpdateUiState
    data object Checking : AppUpdateUiState
    data object UpToDate : AppUpdateUiState
    data class Available(val info: AppUpdateInfo) : AppUpdateUiState
    data class Downloading(
        val info: AppUpdateInfo,
        val progress: Float,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : AppUpdateUiState
    data class ReadyToInstall(val info: AppUpdateInfo, val apkFile: File) : AppUpdateUiState
    data class NeedsInstallPermission(val info: AppUpdateInfo, val apkFile: File) : AppUpdateUiState
    data class Error(
        val messageAr: String,
        val messageEn: String,
        val info: AppUpdateInfo? = null
    ) : AppUpdateUiState
}

/**
 * Handles in-device automatic update checking, streaming APK download with live progress,
 * optional SHA-256 verification, and native Android PackageInstaller launch directly from
 * https://github.com/charfeddine23/Data releases without opening any external browser or app.
 */
class AppUpdateManager(private val context: Context) {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    suspend fun fetchLatestRelease(): AppUpdateInfo? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(GITHUB_LATEST_RELEASE_API)
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "DataPulse-Android/${BuildConfig.VERSION_NAME}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val rawBody = response.body?.string() ?: return@withContext null
            val json = JSONObject(rawBody)

            val tagName = json.optString("tag_name", "").trim()
            val releaseName = json.optString("name", tagName).trim()
            val bodyMarkdown = json.optString("body", "")
            val assets = json.optJSONArray("assets") ?: return@withContext null

            var apkUrl: String? = null
            var apkFileName = "Data.Pulse.$tagName.apk"
            var apkSize = 0L
            var shaUrl: String? = null

            for (i in 0 until assets.length()) {
                val asset = assets.optJSONObject(i) ?: continue
                val name = asset.optString("name", "")
                val url = asset.optString("browser_download_url", "")
                if (name.endsWith(".apk", ignoreCase = true) && url.isNotBlank()) {
                    apkUrl = url
                    apkFileName = name
                    apkSize = asset.optLong("size", 0L)
                } else if (name.endsWith(".sha256", ignoreCase = true) && url.isNotBlank()) {
                    shaUrl = url
                }
            }

            if (apkUrl.isNullOrBlank()) return@withContext null

            val remoteVersion = extractSemVer(tagName.ifBlank { releaseName })
            val currentVersion = extractSemVer(BuildConfig.VERSION_NAME)

            if (!isNewerVersion(remoteVersion, currentVersion)) {
                return@withContext null
            }

            AppUpdateInfo(
                versionName = "${remoteVersion.first}.${remoteVersion.second}.${remoteVersion.third}",
                tagName = tagName.ifBlank { "v${remoteVersion.first}.${remoteVersion.second}.${remoteVersion.third}" },
                releaseTitle = releaseName.ifBlank { "Data Pulse ${remoteVersion.first}.${remoteVersion.second}.${remoteVersion.third}" },
                releaseNotes = cleanReleaseNotes(bodyMarkdown),
                apkDownloadUrl = apkUrl,
                apkFileName = apkFileName,
                apkSizeBytes = apkSize,
                sha256Url = shaUrl
            )
        }
    }

    suspend fun downloadApk(
        info: AppUpdateInfo,
        onProgress: (progress: Float, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val updatesDir = File(context.cacheDir, "updates").apply {
            if (!exists()) mkdirs()
        }
        // Clean previous cached APKs so cache stays compact
        updatesDir.listFiles()?.forEach { old ->
            if (old.name != info.apkFileName) {
                old.delete()
            }
        }

        val targetFile = File(updatesDir, info.apkFileName)
        val tempFile = File(updatesDir, "${info.apkFileName}.part")

        val request = Request.Builder()
            .url(info.apkDownloadUrl)
            .header("User-Agent", "DataPulse-Android/${BuildConfig.VERSION_NAME}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}")
            }
            val body = response.body ?: throw IllegalStateException("Empty response body")
            val totalBytes = body.contentLength().takeIf { it > 0 } ?: info.apkSizeBytes

            var downloaded = 0L
            var lastEmitTime = 0L
            val buffer = ByteArray(16 * 1024)

            body.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                        downloaded += read

                        val now = System.currentTimeMillis()
                        if (now - lastEmitTime >= 60L) {
                            lastEmitTime = now
                            val fraction = if (totalBytes > 0L) {
                                (downloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                            onProgress(fraction, downloaded, totalBytes)
                        }
                    }
                    output.flush()
                }
            }

            onProgress(1f, downloaded, totalBytes.coerceAtLeast(downloaded))
        }

        // Verify SHA-256 checksum if attached in the GitHub release
        if (!info.sha256Url.isNullOrBlank()) {
            val expectedHash = fetchExpectedSha256(info.sha256Url)
            if (expectedHash != null) {
                val actualHash = computeSha256(tempFile)
                if (!actualHash.equals(expectedHash, ignoreCase = true)) {
                    tempFile.delete()
                    throw SecurityException("APK SHA-256 verification failed")
                }
            }
        }

        if (targetFile.exists()) targetFile.delete()
        if (!tempFile.renameTo(targetFile)) {
            tempFile.copyTo(targetFile, overwrite = true)
            tempFile.delete()
        }

        targetFile
    }

    private fun fetchExpectedSha256(shaUrl: String): String? {
        return try {
            val req = Request.Builder()
                .url(shaUrl)
                .header("User-Agent", "DataPulse-Android/${BuildConfig.VERSION_NAME}")
                .build()
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return null
                val text = resp.body?.string()?.trim() ?: return null
                val match = Regex("([a-fA-F0-9]{64})").find(text)
                match?.groupValues?.get(1)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buf = ByteArray(16 * 1024)
            while (true) {
                val read = input.read(buf)
                if (read == -1) break
                digest.update(buf, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun canInstallPackages(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun launchInstallPermissionSettings(callerContext: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${callerContext.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                callerContext.startActivity(intent)
            } catch (_: Exception) {
                val fallback = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                callerContext.startActivity(fallback)
            }
        }
    }

    fun triggerSystemApkInstaller(callerContext: Context, apkFile: File): Boolean {
        return try {
            val apkUri = FileProvider.getUriForFile(
                callerContext,
                "${callerContext.packageName}.fileprovider",
                apkFile
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            callerContext.startActivity(installIntent)
            true
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        const val GITHUB_REPO_URL = "https://github.com/charfeddine23/Data"
        private const val GITHUB_LATEST_RELEASE_API = "https://api.github.com/repos/charfeddine23/Data/releases/latest"

        internal fun extractSemVer(raw: String): Triple<Int, Int, Int> {
            val match = Regex("(\\d+)\\.(\\d+)(?:\\.(\\d+))?").find(raw)
            if (match != null) {
                val major = match.groupValues[1].toIntOrNull() ?: 1
                val minor = match.groupValues[2].toIntOrNull() ?: 0
                val patch = match.groupValues[3].toIntOrNull() ?: 0
                return Triple(major, minor, patch)
            }
            return Triple(1, 0, 0)
        }

        internal fun isNewerVersion(remote: Triple<Int, Int, Int>, current: Triple<Int, Int, Int>): Boolean {
            if (remote.first != current.first) return remote.first > current.first
            if (remote.second != current.second) return remote.second > current.second
            return remote.third > current.third
        }

        private fun cleanReleaseNotes(raw: String): String {
            if (raw.isBlank()) return ""
            val lines = raw.lineSequence()
                .map { it.trim() }
                .filter { line ->
                    line.isNotEmpty() &&
                        !line.startsWith("### ") &&
                        !line.startsWith("#### ") &&
                        !line.startsWith("**Commit:**") &&
                        !line.startsWith("**Built On:**") &&
                        !line.startsWith("**Version Code:**") &&
                        !line.startsWith("**Developer:**") &&
                        !line.startsWith("**APK:**") &&
                        !line.contains("SHA256 Checksum")
                }
                .map { line ->
                    line.removePrefix("- ")
                        .replace("**", "")
                        .replace("`", "")
                        .trim()
                }
                .take(3)
                .toList()
            return lines.joinToString("\n")
        }
    }
}

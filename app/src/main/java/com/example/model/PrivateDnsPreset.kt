package com.example.model

/**
 * Pre-configured and custom presets for Android Private DNS (DNS-over-TLS).
 */
enum class PrivateDnsPreset(
    val id: String,
    val hostname: String?,
    val isRecommended: Boolean = false
) {
    OFF(
        id = "OFF",
        hostname = null,
        isRecommended = false
    ),
    CLOUDFLARE(
        id = "CLOUDFLARE",
        hostname = "one.one.one.one",
        isRecommended = false
    ),
    ADGUARD(
        id = "ADGUARD",
        hostname = "dns.adguard.com",
        isRecommended = true
    ),
    CUSTOM(
        id = "CUSTOM",
        hostname = null,
        isRecommended = false
    );

    fun getDisplayName(appLanguage: AppLanguage): String {
        return when (this) {
            OFF -> AppStrings.privateDnsOff(appLanguage)
            CLOUDFLARE -> AppStrings.privateDnsCloudflare(appLanguage)
            ADGUARD -> AppStrings.privateDnsAdGuard(appLanguage)
            CUSTOM -> AppStrings.privateDnsCustom(appLanguage)
        }
    }

    fun getDescription(appLanguage: AppLanguage): String {
        return when (this) {
            OFF -> AppStrings.privateDnsOffDesc(appLanguage)
            CLOUDFLARE -> AppStrings.privateDnsCloudflareDesc(appLanguage)
            ADGUARD -> AppStrings.privateDnsAdGuardDesc(appLanguage)
            CUSTOM -> AppStrings.privateDnsCustomDesc(appLanguage)
        }
    }
}

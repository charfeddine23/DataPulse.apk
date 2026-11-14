package com.example.model

/**
 * Pre-configured and custom DNS providers supporting both Local VPN UDP-53 routing
 * and Android System Private DNS (DNS-over-TLS).
 */
enum class PrivateDnsPreset(
    val id: String,
    val hostname: String?,
    val primaryIp: String? = null,
    val secondaryIp: String? = null,
    val isRecommended: Boolean = false
) {
    ADGUARD(
        id = "ADGUARD",
        hostname = "dns.adguard.com",
        primaryIp = "94.140.14.14",
        secondaryIp = "94.140.15.15",
        isRecommended = true
    ),
    CLOUDFLARE(
        id = "CLOUDFLARE",
        hostname = "one.one.one.one",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1",
        isRecommended = false
    ),
    GOOGLE(
        id = "GOOGLE",
        hostname = "dns.google",
        primaryIp = "8.8.8.8",
        secondaryIp = "8.8.4.4",
        isRecommended = false
    ),
    QUAD9(
        id = "QUAD9",
        hostname = "dns.quad9.net",
        primaryIp = "9.9.9.9",
        secondaryIp = "149.112.112.112",
        isRecommended = false
    ),
    OPENDNS(
        id = "OPENDNS",
        hostname = "dns.opendns.com",
        primaryIp = "208.67.222.222",
        secondaryIp = "208.67.220.220",
        isRecommended = false
    ),
    CUSTOM(
        id = "CUSTOM",
        hostname = null,
        primaryIp = null,
        secondaryIp = null,
        isRecommended = false
    ),
    OFF(
        id = "OFF",
        hostname = null,
        primaryIp = null,
        secondaryIp = null,
        isRecommended = false
    );

    fun getDisplayName(appLanguage: AppLanguage): String {
        return when (this) {
            OFF -> AppStrings.privateDnsOff(appLanguage)
            ADGUARD -> AppStrings.privateDnsAdGuard(appLanguage)
            CLOUDFLARE -> AppStrings.privateDnsCloudflare(appLanguage)
            GOOGLE -> AppStrings.privateDnsGoogle(appLanguage)
            QUAD9 -> AppStrings.privateDnsQuad9(appLanguage)
            OPENDNS -> AppStrings.privateDnsOpenDns(appLanguage)
            CUSTOM -> AppStrings.privateDnsCustom(appLanguage)
        }
    }

    fun getDescription(appLanguage: AppLanguage): String {
        return when (this) {
            OFF -> AppStrings.privateDnsOffDesc(appLanguage)
            ADGUARD -> AppStrings.privateDnsAdGuardDesc(appLanguage)
            CLOUDFLARE -> AppStrings.privateDnsCloudflareDesc(appLanguage)
            GOOGLE -> AppStrings.privateDnsGoogleDesc(appLanguage)
            QUAD9 -> AppStrings.privateDnsQuad9Desc(appLanguage)
            OPENDNS -> AppStrings.privateDnsOpenDnsDesc(appLanguage)
            CUSTOM -> AppStrings.privateDnsCustomDesc(appLanguage)
        }
    }

    companion object {
        val integratedServers: List<PrivateDnsPreset> = listOf(
            ADGUARD,
            CLOUDFLARE,
            GOOGLE,
            QUAD9,
            OPENDNS
        )

        fun fromName(name: String?): PrivateDnsPreset {
            return entries.firstOrNull { it.name == name } ?: ADGUARD
        }
    }
}

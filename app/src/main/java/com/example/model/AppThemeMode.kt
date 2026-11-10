package com.example.model

/**
 * Four distinct themes supported by Data Pulse Multi-Theme Engine:
 * - LIGHT_MODE: Luminous daylight background (#F8FAFC), pure crystal white glass (#FFFFFF), vibrant electric blue (#0066FF), and vivid violet accent (#7C3AED).
 * - LIQUID_GLASS_DEFAULT: Deep dark glass background (#0F1423), translucent surface, vibrant blue primary (#0066FF), and violet accent (#7C4DFF).
 * - LIQUID_NEON_CYAN: Dark slate background (#080D1A), bright cyan primary (#00E5FF), and neon green/teal accent (#00FFA3).
 * - AMOLED_PITCH_BLACK: Pure pitch-black background (#000000) for OLED battery saving, dark surface (#111116), and electric blue primary (#3B82F6).
 */
enum class AppThemeMode(
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String,
    val isDark: Boolean = true
) {
    LIGHT_MODE(
        titleEn = "Light Mode",
        titleAr = "الوضع النهاري",
        descEn = "Crisp Daylight & Pure Glass",
        descAr = "إضاءة نهارية وزجاج ناصع",
        isDark = false
    ),
    LIQUID_GLASS_DEFAULT(
        titleEn = "Liquid Glass",
        titleAr = "الزجاج السائل",
        descEn = "Deep Obsidian & Blue",
        descAr = "زجاج أوبسيديان وأزرق داكن",
        isDark = true
    ),
    LIQUID_NEON_CYAN(
        titleEn = "Neon Cyan",
        titleAr = "نيون سيان",
        descEn = "Electric Cyan & Teal",
        descAr = "سيان كهربائي وزمردي مشع",
        isDark = true
    ),
    AMOLED_PITCH_BLACK(
        titleEn = "AMOLED Black",
        titleAr = "سواد نقي AMOLED",
        descEn = "Pure Black #000000",
        descAr = "أسود نقي لتوفير البطارية",
        isDark = true
    );

    fun getDisplayName(lang: AppLanguage): String =
        if (lang == AppLanguage.ARABIC) titleAr else titleEn

    fun getDescription(lang: AppLanguage): String =
        if (lang == AppLanguage.ARABIC) descAr else descEn
}

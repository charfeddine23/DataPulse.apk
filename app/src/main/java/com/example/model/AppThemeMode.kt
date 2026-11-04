package com.example.model

/**
 * Three distinct themes supported by Data Pulse Theme Engine:
 * - LIQUID_GLASS_DEFAULT: Deep dark glass background (#0B0F19), translucent surface, vibrant blue primary (#0066FF), and violet accent (#7C4DFF).
 * - LIQUID_NEON_CYAN: Dark slate background (#020617), bright cyan primary (#00E5FF), and neon green/teal accent (#00FFA3).
 * - AMOLED_PITCH_BLACK: Pure pitch-black background (#000000) for OLED battery saving, dark gray surface (#121212), and electric blue primary (#3B82F6).
 */
enum class AppThemeMode(
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String
) {
    LIQUID_GLASS_DEFAULT(
        titleEn = "Liquid Glass",
        titleAr = "الزجاج السائل",
        descEn = "Deep Obsidian & Blue",
        descAr = "زجاج أوبسيديان وأزرق"
    ),
    LIQUID_NEON_CYAN(
        titleEn = "Neon Cyan",
        titleAr = "نيون سيان",
        descEn = "Electric Cyan & Teal",
        descAr = "سيان كهربائي وزمردي"
    ),
    AMOLED_PITCH_BLACK(
        titleEn = "AMOLED Black",
        titleAr = "سواد نقي AMOLED",
        descEn = "Pure Black #000000",
        descAr = "أسود نقي لتوفير البطارية"
    );

    fun getDisplayName(lang: AppLanguage): String =
        if (lang == AppLanguage.ARABIC) titleAr else titleEn

    fun getDescription(lang: AppLanguage): String =
        if (lang == AppLanguage.ARABIC) descAr else descEn
}

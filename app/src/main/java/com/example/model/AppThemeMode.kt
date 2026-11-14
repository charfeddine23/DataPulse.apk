package com.example.model

/**
 * Multi-Theme Engine modes supported across Data Pulse / DNS Pulse:
 * - SYSTEM: Automatically tracks the device's system dark/light theme.
 * - LIGHT_MODE: Pearl & Platinum crystalline light glass theme.
 * - NEUMORPHISM: Soft ice-gray (#EEF2F6) tactile Neumorphism theme with #FFFFFF/#D1D9E6 dual shadows.
 * - LIQUID_GLASS_DEFAULT: Deep obsidian Dark Liquid Glass theme (#090C10 -> #0D1117).
 * - LIQUID_NEON_CYAN: Dark slate neon cyan & emerald cyber theme.
 * - AMOLED_PITCH_BLACK: Pure pitch-black (#000000) theme for OLED displays.
 */
enum class AppThemeMode(
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String,
    val isDark: Boolean = true
) {
    SYSTEM(
        titleEn = "Follow System",
        titleAr = "اتبع الهاتف",
        descEn = "Dynamic Auto Switch",
        descAr = "تبديل تلقائي نهاري وليلي",
        isDark = false
    ),
    LIGHT_MODE(
        titleEn = "Pearl & Platinum",
        titleAr = "الوضع النهاري الفاخر",
        descEn = "Warm Pearl Glass & Obsidian",
        descAr = "زجاج لؤلؤي دافئ ونصوص فحمية",
        isDark = false
    ),
    NEUMORPHISM(
        titleEn = "Neumorphism",
        titleAr = "نيومورفيزم",
        descEn = "Soft Ice-Gray Tactile UI",
        descAr = "تصميم ثلاثي الأبعاد ناعم",
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

typealias AppTheme = AppThemeMode

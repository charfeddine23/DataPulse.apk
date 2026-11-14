package com.example.model

enum class AppLanguage(val code: String, val displayName: String) {
    ARABIC("AR", "العربية"),
    ENGLISH("EN", "English")
}

object AppStrings {
    // Navigation
    fun navPulse(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "النبض" else "Pulse"
    fun navUsage(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الاستهلاك" else "Usage"
    fun navSpeed(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عداد السرعة" else "Speed Meter"
    fun navForecast(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "التحليل الذكي" else "Smart Forecast"
    fun navDns(lang: AppLanguage) = "DNS"
    fun navRank(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرتبة" else "Rank"
    fun navSettings(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات" else "Settings"

    // Rank / Competition
    fun rankTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرتبة" else "Rank"
    fun rankSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مسابقة أكبر تطبيق استهلاكاً للبيانات على مر التاريخ" else "All-Time Data Consumption Leaderboard"
    fun rankPodiumTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "منصة أبطال الاستهلاك" else "Champions Podium"
    fun rankFirst(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرتبة الأولى" else "1st Place"
    fun rankSecond(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرتبة الثانية" else "2nd Place"
    fun rankThird(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الرتبة الثالثة" else "3rd Place"
    fun rankRemaining(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "باقي التطبيقات" else "Other Applications"
    fun rankTotalCombined(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مجموع استهلاك البيانات والواي فاي" else "Combined Cellular & Wi-Fi Data"
    fun rankAllTimeBadge(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "على مر التاريخ" else "All-Time Record"

    // Connection
    fun wifiActive(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "واي فاي" else "Wi-Fi"
    fun cellular5g(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "شبكة 5G" else "5G Cellular"
    fun cellularGeneric(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "بيانات الجوال" else "Cellular"
    fun ethernet(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إيثرنت" else "Ethernet"
    fun disconnected(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "غير متصل" else "Disconnected"

    // Dashboard
    fun liveSpeedTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مخطط سرعة الشبكة المباشر" else "Live Network Speed Graph"
    fun download(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنزيل" else "Download"
    fun upload(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "رفع" else "Upload"
    fun peak(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الذروة" else "Peak"
    fun ms(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مللي ثانية" else "ms"

    // Monthly Plan Quota
    fun monthlyPlanTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "باقة البيانات الشهرية" else "Monthly Data Plan"
    fun editCap(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تعديل الحد" else "Edit Cap"
    fun used(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مستهلك" else "Used"
    fun remaining(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "متبقي" else "Remaining"
    fun daysRemaining(days: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "$days أيام متبقية في الدورة" else "$days days remaining in cycle"
    fun alert80Warning(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيه: تم استهلاك 80% من باقة البيانات. يرجى مراقبة الاستهلاك." else "Warning: 80% of data plan consumed. Please monitor usage."
    fun alert95Critical(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحذير حرج: تم استهلاك 95% من الباقة! اقتربت من النفاد." else "Critical Alert: 95% of plan consumed! Approaching quota cap."
    fun quotaExceeded(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تم تجاوز سعة باقة البيانات المحددة!" else "Monthly data plan limit has been exceeded!"

    // Data Breakdown
    fun dataBreakdownTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تفاصيل استهلاك البيانات" else "DATA USAGE BREAKDOWN"
    fun refresh(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحديث" else "Refresh"
    fun todayCellular(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "بيانات الجوال اليوم" else "Today Cellular"
    fun todayWifi(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "واي فاي اليوم" else "Today Wi-Fi"
    fun monthlyCellular(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "بيانات الجوال شهرياً" else "Monthly Cellular"
    fun monthlyWifi(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "واي فاي شهرياً" else "Monthly Wi-Fi"
    fun viewAppAnalytics(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استهلاك التطبيقات بالتفصيل" else "App-by-App Analytics"
    fun viewAppAnalyticsSub(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تعرّف على التطبيقات الأكثر استهلاكاً لبياناتك" else "See which apps consume your data"
    fun explore(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عرض التفاصيل ←" else "Explore →"

    // Analytics Screen
    fun analyticsTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استهلاك التطبيقات" else "App-by-App Analytics"
    fun analyticsSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تفاصيل استهلاك بيانات الجوال وشبكات واي فاي لكل تطبيق" else "Breakdown of cellular and Wi-Fi data per application"
    fun searchApps(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "البحث في التطبيقات المثبتة..." else "Search installed applications..."
    fun analyzingNetwork(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "جاري تحليل إحصائيات الشبكة..." else "Analyzing network statistics..."
    fun noMatchingApps(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لم يتم العثور على تطبيقات مطابقة" else "No matching apps found"
    fun noAppTraffic(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لا يوجد استهلاك شبكة مسجل لهذه الفترة" else "No app network traffic recorded for this period"
    fun ofTotal(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "من الإجمالي" else "of total"

    // Time ranges
    fun timeToday(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اليوم" else "Today"
    fun timeWeek(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "هذا الأسبوع" else "This Week"
    fun timeMonth(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "هذا الشهر" else "This Month"

    // Filters
    fun filterAll(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "جميع الشبكات" else "All Traffic"
    fun filterCellular(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "بيانات الجوال" else "Mobile Data"
    fun filterWifi(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "واي فاي" else "Wi-Fi"

    // Speed Meter
    fun speedMeterTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مقياس سرعة الاتصال المباشر" else "Live Speed Telemetry"
    fun speedMeterSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مراقبة فورية لمعدل نقل البيانات وتشخيص الشبكة" else "Real-time bandwidth throughput and network diagnostics"
    fun currentDownload(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "سرعة التنزيل الحالية" else "CURRENT DOWNLOAD"
    fun currentUpload(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "سرعة الرفع الحالية" else "CURRENT UPLOAD"
    fun continuousThroughput(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مخطط تدفق البيانات المستمر" else "CONTINUOUS THROUGHPUT GRAPH"
    fun networkDiagnostics(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تشخيص الشبكة والاتصال" else "NETWORK DIAGNOSTICS"
    fun activeInterface(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الواجهة النشطة" else "Active Interface"
    fun localIp(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عنوان IP المحلي" else "Local IP Address"
    fun estimatedLatency(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "زمن الاستجابة التقديري" else "Estimated Latency"
    fun connectionState(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حالة الاتصال" else "Connection State"
    fun liveBandwidth(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عرض النطاق الترددي" else "Live Bandwidth"

    // Settings
    fun settingsTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات والحصص" else "Quotas & Preferences"
    fun settingsSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تخصيص اللغة والمظهر، وحدود باقة البيانات حتى 1000 غيغابايت، والتنبيهات" else "Customize language, theme, limits up to 1000GB, and smart alerts"
    fun appLanguageSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لغة التطبيق" else "APP LANGUAGE"
    fun themeSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المظهر والسمة" else "THEME & APPEARANCE"
    fun multiThemeEngineSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "محرك المظاهر المتعددة (Multi-Theme Engine)" else "Multi-Theme Engine"
    fun multiThemeEngineSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "٤ أنماط جمالية متكاملة (النهاري، والداكن الزجاجي، ونيون سيان، وAMOLED)" else "4 Distinct Aesthetics (Light, Liquid Dark, Neon Cyan & AMOLED)"
    fun themeSystem(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اتبع الهاتف" else "System"
    fun themeSystemSub(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تلقائي" else "Follow System"
    fun themeLight(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الوضع النهاري" else "Light"
    fun themeLightSub(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مريح للعين" else "Eye Comfort"
    fun themeDark(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الوضع الليلي" else "Dark"
    fun themeDarkSub(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "داكن زجاجي" else "Liquid Dark"

    fun planLimitSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حد باقة البيانات الشهرية" else "MONTHLY DATA PLAN LIMIT"
    fun planCapLabel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "سعة الباقة المحددة (حتى 1000 غيغابايت):" else "Plan Cap (up to 1000 GB):"
    fun customLimitLabel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الحد المخصص (غيغابايت)" else "Custom Limit (GB)"
    fun set(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تعيين" else "Set"

    fun billingCycleSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "يوم تجديد باقة البيانات" else "Billing Cycle Reset Day"
    fun billingCycleDay(day: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اليوم $day من كل شهر" else "Day $day of every month"

    fun smartAlertsSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيهات الباقة الذكية" else "SMART QUOTA ALERTS"
    fun alert80Title(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيه تحذيري عند 80%" else "Warning Alert at 80%"
    fun alert80Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إشعار عند استهلاك 80% من سعة باقة البيانات" else "Notifies when 80% of data plan is consumed"
    fun alert95Title(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيه حرج عند 95%" else "Critical Alert at 95%"
    fun alert95Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحذير عاجل قبل نفاد باقة البيانات تماماً" else "Urgent alert before hitting plan cap"

    fun powerSavingSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "وضع توفير الطاقة والبطارية" else "POWER SAVING & BATTERY"
    fun powerSavingTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "وضع توفير الطاقة" else "Power Saving Mode"
    fun powerSavingActive(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الوضع الاقتصادي نشط: تم تقليل فحص الشبكة إلى 3 ثوانٍ لتوفير شحن البطارية" else "Eco Mode Active: Network polling reduced to 3s to save battery"
    fun powerSavingInactive(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "دقة عالية: فحص استهلاك الشبكة في الوقت الفعلي كل ثانية واحدة" else "High Accuracy: Real-time 1s network polling active"

    fun permissionsSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "أذونات النظام" else "SYSTEM PERMISSIONS"
    fun usageAccess(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إذن الوصول للاستخدام" else "Usage Access"
    fun permGranted(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ممنوح - التحليلات التاريخية الدقيقة مفعلة" else "Granted - Full historical analytics active"
    fun permNotGranted(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "غير ممنوح - يقتصر على جلسة الاستخدام الحالية" else "Not Granted - Real-time session only"
    fun grantPerm(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "منح الإذن" else "Grant"
    fun restrictedGuideQ(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "هل الإعداد مقيّد أو غير متاح في أندرويد 13+؟" else "Setting disabled or restricted on Android 13+?"
    fun viewGuide(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عرض الدليل ←" else "View Guide →"

    // Usage Permission Card & Restricted Settings
    fun permCardTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مطلوب إذن الوصول للاستخدام (محلياً فقط)" else "Usage Access Permission Required (Local Only)"
    fun privacySafe(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "آمن وخصوصية 100% - لا يتم جمع أو مشاركة أي بيانات إطلاقاً." else "100% Secure & Private - No personal data collected or shared."
    fun permCardDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "يحتاج تطبيق Data Pulse إلى إذن الوصول للاستخدام لحساب استهلاك التطبيقات وبيانات الجوال والواي فاي بدقة عبر نظام أندرويد. لا يتم رفع أو حفظ أي بيانات خاصة خارج جهازك." else "Data Pulse requires Usage Access permission to calculate per-app cellular and Wi-Fi data accurately via Android NetworkStatsManager. No private data is ever collected or sent off device."
    fun restrictedSettingsHelp(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إعداد مقيّد؟" else "Restricted Setting?"
    fun restrictedDialogTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حل مشكلة الإعدادات المقيدة" else "Resolve Restricted Settings (Android 13+)"
    fun restrictedDialogDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إذا ظهرت لك رسالة أندرويد 13+: 'إعداد مقيّد - حفاظاً على أمانك، هذا الإعداد غير متاح حالياً'، اتبع الخطوات السهلة التالية:" else "If Android 13+ displays 'Restricted setting - For your security, this setting is currently unavailable', follow these quick steps:"
    fun step1Title(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "فتح معلومات التطبيق" else "Open App Info"
    fun step1Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اضغط على زر 'معلومات التطبيق' بالأسفل للانتقال لصفحة Data Pulse بالنظام." else "Tap 'App Info' below to navigate to the system settings page for Data Pulse."
    fun step2Title(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "السماح بالإعدادات المقيدة" else "Allow Restricted Settings"
    fun step2Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اضغط على القائمة (⋮) أعلى اليمين، واختر 'السماح بالإعدادات المقيدة' ثم قم بتأكيد بصمتك أو رمز القفل." else "Tap the top-right menu (⋮), choose 'Allow restricted settings', and verify your biometric or PIN."
    fun step3Title(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تفعيل إذن الوصول للاستخدام" else "Enable Usage Access"
    fun step3Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عد للتطبيق واضغط على 'إذن الاستخدام' لتفعيل مفتاح Data Pulse بنجاح." else "Return here and tap 'Usage Access' to switch the Data Pulse toggle on."
    fun btnAppInfo(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "1. معلومات التطبيق" else "1. App Info"
    fun btnUsagePerm(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "2. إذن الاستخدام" else "2. Usage Access"

    // Developer & About
    fun developedBy(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تطوير: Charfeddine Ka" else "Developed by Charfeddine Ka"
    fun appVersionInfo(lang: AppLanguage): String {
        val v = com.example.BuildConfig.VERSION_NAME
        return if (lang == AppLanguage.ARABIC) {
            "الإصدار $v (v$v) | آخر تحديث نوفمبر 2026"
        } else {
            "Version $v (v$v) | Last Updated November 2026"
        }
    }
    fun appDescription(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مصمم بنمط الزجاج السائل المتطور (Liquid Glass) للوضعين النهاري والليلي، مع واجهات Jetpack Compose الحديثة، وتتبع NetworkStatsManager، ودعم الشريحة المزدوجة Dual SIM. متوافق كلياً مع التثبيت فوق الإصدارات السابقة." else "Crafted with Liquid Glass aesthetics for Light & Dark mode, modern Jetpack Compose UI, NetworkStatsManager tracking, Dual SIM readiness, and guaranteed seamless update installation."

    // Per-App Customization Dialog
    fun perAppDialogTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تخصيص حد الاستهلاك للتطبيق" else "App Quota & Alert Settings"
    fun perAppDailyLimit(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الحد اليومي المخصص" else "Daily Data Limit"
    fun perAppAlertToggle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تفعيل التنبيه اليومي لهذا التطبيق" else "Enable Daily Alert for this App"
    fun perAppAlertDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إرسال إشعار تحذيري فوري عندما يتجاوز هذا التطبيق حده اليومي" else "Send a warning alert when this specific app exceeds its daily threshold"
    fun perAppCurrentUsage(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الاستهلاك المسجل:" else "Recorded Traffic:"
    fun perAppSaveBtn(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حفظ التخصيص" else "Save Settings"
    fun perAppCancelBtn(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إلغاء" else "Cancel"
    fun perAppLongPressHint(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اضغط مطولاً لتخصيص الحد اليومي" else "Long-press to customize limit"
    fun perAppDefaultIndicator(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الافتراضي: 3 غيغابايت" else "Default: 3.0 GB"

    // Smart Data Forecasting / Analytics
    fun navSmartAnalytics(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "التحليل الذكي" else "Smart Analytics"
    fun smartForecastTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "التنبؤ الذكي باستهلاك البيانات" else "Smart Data Forecasting"
    fun smartForecastSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تحليل تنبؤي لمعدل الحرق وموعد نفاد الباقة الشهرية" else "Predictive burn rate & data depletion analysis"
    fun forecastBurnRate(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "معدل الحرق اليومي" else "Daily Burn Rate"
    fun forecastDaysToDeplete(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "النفاد المتوقع" else "Depletion In"
    fun forecastEndOfCycle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "توقع نهاية الدورة" else "Cycle End Est."
    fun forecastSurplus(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "فائض متوقع" else "Surplus"
    fun forecastDeficit(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "عجز متوقع" else "Deficit"
    fun statusCritical(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حرج: وشيك النفاد" else "Critical Risk"
    fun statusEarlyWarning(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيه: نفاد مبكر" else "Early Depletion"
    fun statusSustainable(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استهلاك متوازن" else "Sustainable Pace"
    fun statusExhausted(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تم استهلاك الباقة" else "Cap Exhausted"
    fun forecastDays(days: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "$days أيام" else "$days Days"
    fun warningCriticalBanner(days: Int, planCapGB: Float, lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "⚠️ تحذير: بناءً على استهلاكك، ستنفد باقتك ($planCapGB غيغابايت) بالكامل خلال $days أيام فقط!"
    else
        "⚠️ Warning: Based on your usage, your ${planCapGB.toInt()} GB data cap will deplete in $days days!"
    fun warningEarlyBanner(days: Int, cycleDays: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "⚠️ تنبيه: بمعدل استهلاكك الحالي، ستنفد باقتك خلال $days يوماً (قبل $cycleDays يوماً من تجديد الدورة)."
    else
        "⚠️ Attention: At your current pace, your data will deplete in $days days ($cycleDays days left in cycle)."
    fun safeBanner(surplusFormatted: String, daysLeft: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "✨ استهلاكك متوازن وممتاز: ستكفيك الباقة طوال الـ $daysLeft يوماً المتبقية مع فائض متوقع قدره $surplusFormatted."
    else
        "✨ Sustainable Pace: At this rate, your data will comfortably last the remaining $daysLeft days with ~$surplusFormatted surplus."
    fun exhaustedBanner(lang: AppLanguage) = if (lang == AppLanguage.ARABIC)
        "🚨 تم استهلاك كامل سعة الباقة المحددة! يرجى شحن الرصيد أو تجديد الباقة."
    else
        "🚨 Data cap has been fully exhausted! Please recharge or extend your monthly plan."

    // Smart Analytics Screen Additional Strings
    fun smartAnalyticsTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "التحليل الذكي" else "Smart Analytics"
    fun smartAnalyticsSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "توقعات استهلاك الباقة ومعدل الحرق ومدرج البيانات" else "Predictive consumption runway, burn rate & depletion timeline"
    fun safeRunwayBudget(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الميزانية اليومية الآمنة" else "Safe Daily Runway"
    fun safeRunwayDesc(days: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "لإنهاء الـ $days يوماً المتبقية بأمان" else "To safely complete remaining $days days"
    fun burnVelocity(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "سرعة الاستهلاك" else "Consumption Velocity"
    fun burnVelocityFast(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "أسرع من المخطط" else "Above safe pace"
    fun burnVelocityOptimal(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ضمن الميزانية الآمنة" else "Optimal safe pace"
    fun cycleProgress(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مسار دورة الفاتورة" else "Billing Cycle Timeline"
    fun cycleDaysElapsed(elapsed: Int, total: Int, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "اليوم $elapsed من إجمالي $total يوماً" else "Day $elapsed of $total in cycle"
    fun recommendationsTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "توصيات ذكية لإطالة عمر الباقة" else "Smart Data Runway Tips"
    fun recCritical(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "خطر النفاد وشيك: قلل دقة تشغيل الفيديو عبر بيانات الجوال وأوقف التحديثات التلقائية في الخلفية." else "Critical risk: Lower streaming video quality and pause background auto-updates on cellular."
    fun recWarning(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تنبيه: سرعة الحرق الحالية ستؤدي لنفاد الباقة مبكراً، قم بتنزيل الملفات الكبيرة عبر شبكات Wi-Fi." else "Warning: Your current burn rate will deplete your cap early. Offload large downloads to Wi-Fi."
    fun recSafe(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "ممتاز! استهلاكك منضبط وضمن المسار الآمن حتى موعد التجديد القادم." else "Optimal! Your mobile consumption is comfortably on track to finish with data to spare."
    fun openSettingsMenu(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات العامة" else "Open Settings"
    fun backToPrevious(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "رجوع" else "Back"

    // Private DNS (DoT)
    fun privateDnsTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "نظام أسماء النطاقات الخاص (Private DNS)" else "Private DNS (DoT)"
    fun privateDnsSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تشفير حركة البيانات وحظر الإعلانات والتعقب على مستوى النظام" else "System-wide DNS encryption, ad-blocking & tracker prevention"
    fun privateDnsActive(hostname: String, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الوضع النشط: $hostname" else "Active: $hostname"
    fun privateDnsOff(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "افتراضي النظام (معطل)" else "System Default (Off)"
    fun privateDnsAdGuard(lang: AppLanguage) = "AdGuard DNS"
    fun privateDnsCloudflare(lang: AppLanguage) = "Cloudflare (1.1.1.1)"
    fun privateDnsGoogle(lang: AppLanguage) = "Google Public DNS"
    fun privateDnsQuad9(lang: AppLanguage) = "Quad9 DNS"
    fun privateDnsOpenDns(lang: AppLanguage) = "Cisco OpenDNS"
    fun privateDnsCustom(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مضيف مخصص" else "Custom Hostname"
    fun privateDnsAdGuardBadge(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "★ موصى به • حظر الإعلانات" else "★ RECOMMENDED • BLOCKS ADS"
    fun privateDnsAdGuardDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "يحظر الإعلانات وأدوات التعقب الضارة ويوفر استهلاك الباقة (dns.adguard.com)" else "Blocks ads, trackers & saves cellular quota (dns.adguard.com)"
    fun privateDnsCloudflareDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استجابة سريعة جداً وحماية الخصوصية وتشفير DoT (one.one.one.one)" else "Ultra-fast response with privacy DoT encryption (one.one.one.one)"
    fun privateDnsGoogleDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "خوادم غوغل العالمية عالية الاعتمادية والاستقرار (dns.google)" else "Global high-reliability Anycast DNS infrastructure (dns.google)"
    fun privateDnsQuad9Desc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حماية أمنية متقدمة ضد البرمجيات الخبيثة والتصيد (dns.quad9.net)" else "Threat-blocking security DNS against malware & phishing (dns.quad9.net)"
    fun privateDnsOpenDnsDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تصفية موثوقة للشبكات والحماية السحابية من سيسكو (dns.opendns.com)" else "Enterprise-grade cloud filtering & reliability by Cisco (dns.opendns.com)"
    fun privateDnsOffDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استخدام DNS الافتراضي التلقائي لشبكة الاتصال دون تشفير مخصص" else "Use network default DNS assigned by mobile carrier / Wi-Fi"
    fun privateDnsCustomDesc(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "إدخال اسم مضيف DoT مخصص من اختيارك" else "Enter your custom TLS DNS provider hostname"
    fun privateDnsCopiedNotice(hostname: String, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تم نسخ «$hostname» إلى الحافظة!" else "Copied \"$hostname\" to clipboard!"
    fun privateDnsGuide(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "افتح إعدادات أندرويد ➔ الشبكة والإنترنت ➔ نظام DNS الخاص ➔ اختر «اسم مضيف موفر DNS الخاص» والصق الرابط" else "Open Android Settings ➔ Network & internet ➔ Private DNS ➔ Select 'Private DNS provider hostname' and paste"
    fun privateDnsOpenSettingsBtn(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تطبيق في إعدادات أندرويد" else "Apply in Android Settings"
    fun privateDnsCustomPlaceholder(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مثال: dns.quad9.net" else "e.g. dns.quad9.net"
    fun privateDnsApplyBtn(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "تطبيق" else "Apply"

    // Local VPN DNS Shield & Quick Settings Tile
    fun dnsVpnShieldTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "درع DNS المحلي (VPN محلي)" else "Local DNS Shield (VPN)"
    fun dnsVpnActiveLabel(host: String, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "نشط الآن • $host" else "Active Shield • $host"
    fun dnsVpnInactiveLabel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "متوقف — فعّل الحماية الفورية بضغطة واحدة" else "Inactive — Tap switch for instant DNS protection"
    fun dnsVpnQueriesCount(count: Long, lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الاستعلامات المؤمّنة: $count" else "Queries secured: $count"
    fun dnsQuickTileHint(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "متاح أيضاً في لوحة الإعدادات السريعة للهاتف (Quick Settings Tile)" else "Also accessible from your phone's Quick Settings Tile"

    // Dedicated DNS Control Screen
    fun dnsScreenTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "درع DNS الذكي" else "Smart DNS Shield"
    fun dnsScreenSubtitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حظر الإعلانات وتشفير الاستعلامات بنفق محلي فائق السرعة" else "Local split-DNS tunnel for ad-blocking & privacy protection"
    fun dnsShieldActiveBadge(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الحماية نشطة" else "SHIELD ACTIVE"
    fun dnsShieldStandbyBadge(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "وضع الاستعداد" else "STANDBY"
    fun dnsTapOrbHint(lang: AppLanguage, active: Boolean) = if (lang == AppLanguage.ARABIC) {
        if (active) "اضغط على الدرع لإيقاف الحماية المحلية" else "اضغط على الدرع لتفعيل الحماية بضغطة واحدة"
    } else {
        if (active) "Tap shield core to pause local DNS tunnel" else "Tap shield core for instant 1-tap DNS protection"
    }
    fun dnsFilterEngineLabel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "محرك التصفية" else "Filter Engine"
    fun dnsTunnelModeLabel(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "بروتوكول النفق" else "Tunnel Mode"
    fun dnsAdFilterMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حظر الإعلانات والتعقب" else "Ads & Trackers Block"
    fun dnsFastPrivacyMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "خصوصية فائقة السرعة" else "Ultra-Fast Privacy"
    fun dnsGlobalSpeedMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "استجابة عالمية موثوقة" else "Global Anycast DNS"
    fun dnsThreatGuardMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حماية ضد التهديدات" else "Threat & Malware Shield"
    fun dnsOpenDnsMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "حماية سيسكو السحابية" else "Cisco Cloud Filter"
    fun dnsCustomMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "مضيف مخصص" else "Custom Resolver"
    fun dnsIspMode(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "افتراضي المزود" else "ISP Default"
    fun dnsProvidersSection(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "خوادم DNS المدمجة" else "INTEGRATED DNS SERVERS"
    fun dnsPingLatencyBtn(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "فحص السرعة" else "Ping"
    fun dnsSystemDotTitle(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "المزامنة مع DNS الخاص بالنظام (DoT)" else "System Private DNS (DoT) Sync"
    fun dnsOpenDedicatedPage(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "فتح صفحة DNS" else "Open DNS Page"
}



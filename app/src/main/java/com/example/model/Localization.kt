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
    fun navSettings(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإعدادات" else "Settings"

    // Connection
    fun wifiActive(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "واي فاي نشط" else "Wi-Fi Active"
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
    fun appVersionInfo(lang: AppLanguage) = if (lang == AppLanguage.ARABIC) "الإصدار 1.0.1 (v1.0.1) | آخر تحديث نوفمبر 2026" else "Version 1.0.1 (v1.0.1) | Last Updated November 2026"
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
}


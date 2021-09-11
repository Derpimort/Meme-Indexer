package com.legendbois.memeindexer

object ConstantsHelper {
    const val defaultText = "5PAC38AR_UN1ND3X3D_5P3C1F13R"
    const val defaultFailedText = "5PAC38AR_FA1L3D_5P3C1F13R"

    const val PERIODIC_WORKREQ_TAG = "MemeIndexer-Daily-Scan"
    const val WORKMANAGER_UID = "MemeIndexer-Daily-Scan-Worker"
    const val ONETIME_WORKMANAGER_UID = "MemeIndexer-Scan-Worker"
    const val ONETIME_WORKREQ_TAG = "MemeIndexer-Onetime-Scan"

    val THEMES = mapOf(
        "Legacy" to R.style.AppTheme_Legacy,
        "Dark" to R.style.AppTheme_Dark,
        "Light" to R.style.AppTheme_Light
    )

    const val ONE_MINUTE_MILLIS: Long = 60*1000

    val TIME_RANGES = mapOf(
        "Last 10 Minutes" to 10 * ONE_MINUTE_MILLIS,
        "Last Hour" to 60 * ONE_MINUTE_MILLIS,
        "Last 24 Hours" to 24 * 60 * ONE_MINUTE_MILLIS,
        "Last 30 Days" to 30 * 24 * 60 * ONE_MINUTE_MILLIS,
        "All time" to Long.MAX_VALUE
    )

    val USAGE_HISTORY_ACTIONS = mapOf(
        "scan" to 0,
        "search" to 1,
        "share" to 2
    )


}
package com.example.ui.browser

import android.graphics.Bitmap
import com.example.data.downloader.StorageDestination
import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "علامة تبويب جديدة",
    var url: String = "about:blank",
    var icon: Bitmap? = null,
    var isLoading: Boolean = false,
    var progress: Int = 0,
    var canGoBack: Boolean = false,
    var canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    var desktopMode: Boolean = false,
    var isSecure: Boolean = true,
    var detectedMediaCount: Int = 0
)

enum class SearchEngine(val displayName: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo (حماية الخصوصية)", "https://duckduckgo.com/?q="),
    BING("Bing", "https://www.bing.com/search?q="),
    BRAVE("Brave Search", "https://search.brave.com/search?q=")
}

data class AppThemeOption(
    val name: String,
    val primaryColorHex: Long
)

val ACCENT_THEMES = listOf(
    AppThemeOption("أزرق بحري ملكي", 0xFF0D47A1),
    AppThemeOption("زمردي عصري", 0xFF00796B),
    AppThemeOption("كهرماني مشرق", 0xFFE65100),
    AppThemeOption("أرجواني ملكي", 0xFF4A148C),
    AppThemeOption("قرمزي داكن", 0xFF880E4F)
)

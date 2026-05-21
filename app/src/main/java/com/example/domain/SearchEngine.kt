package com.example.domain

enum class SearchEngine(val title: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    BING("Bing", "https://www.bing.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=")
}

fun getSearchUrl(engine: String, query: String): String {
    val searchEngine = SearchEngine.values().find { it.title == engine } ?: SearchEngine.GOOGLE
    return "${searchEngine.searchUrl}${query.replace(" ", "+")}"
}

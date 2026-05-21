package com.example.ui.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToExtensions: () -> Unit
) {
    val currentUrl by viewModel.currentUrl.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loadProgress by viewModel.loadProgress.collectAsStateWithLifecycle()
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle()
    
    var urlInput by remember { mutableStateOf(currentUrl) }
    var webView: WebView? by remember { mutableStateOf(null) }
    
    val focusManager = LocalFocusManager.current

    // Sync input with actual URL changes (e.g. user follows link)
    LaunchedEffect(currentUrl) {
        if (currentUrl != webView?.url) {
            urlInput = currentUrl
            webView?.loadUrl(currentUrl)
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    viewModel.loadUrl(urlInput)
                                    focusManager.clearFocus()
                                }
                            ),
                            placeholder = { Text("Search or type web address") },
                            shape = MaterialTheme.shapes.extraLarge
                        )
                    },
                    actions = {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Extensions") },
                                onClick = {
                                    expanded = false
                                    onNavigateToExtensions()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Extension, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = {
                                    expanded = false
                                    onNavigateToSettings()
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.Settings, contentDescription = null)
                                }
                            )
                        }
                    }
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        progress = { loadProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar {
                IconButton(
                    onClick = { webView?.goBack() },
                    enabled = webView?.canGoBack() == true
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { webView?.goForward() },
                    enabled = webView?.canGoForward() == true
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    viewModel.loadUrl("") // will load Google or default search
                }) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { webView?.reload() }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        @SuppressLint("SetJavaScriptEnabled")
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                viewModel.isLoading.value = true
                                url?.let { 
                                    urlInput = it
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                viewModel.isLoading.value = false
                                url?.let {
                                    viewModel.currentUrl.value = it
                                }
                                view?.title?.let {
                                    viewModel.currentTitle.value = it
                                }
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                if (adBlockEnabled) {
                                    val urlToCheck = request?.url.toString().lowercase()
                                    if (urlToCheck.contains("googleads") || urlToCheck.contains("doubleclick") || urlToCheck.contains("ads.")) {
                                        // Block ad requests
                                        return WebResourceResponse("text/plain", "UTF-8", null)
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }
                        
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                viewModel.loadProgress.value = newProgress / 100f
                            }
                        }
                        
                        // Assigning ref
                        webView = this
                        loadUrl(currentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

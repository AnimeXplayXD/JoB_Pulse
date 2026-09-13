package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun openJobLink(context: Context, value: String?) {
    val uri = value?.trim()?.let(Uri::parse)
    if (uri == null || uri.scheme !in setOf("https", "http") || uri.host.isNullOrBlank()) {
        Toast.makeText(context, "This link is not available yet.", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No application can open this link.", Toast.LENGTH_SHORT).show()
    }
}

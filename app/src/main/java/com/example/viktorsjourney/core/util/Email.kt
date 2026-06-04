package com.example.viktorsjourney.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast


fun sendEmailIntent(context: Context, to: String, subject: String) {
    val encodedSubject = Uri.encode(subject)
    val mailtoUri = "mailto:$to?subject=$encodedSubject"

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse(mailtoUri)
    }

    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "No email application found.", Toast.LENGTH_SHORT).show()
    }
}

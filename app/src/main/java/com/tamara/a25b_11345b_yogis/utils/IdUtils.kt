package com.tamara.a25b_11345b_yogis.utils

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.Locale

object IdUtils {
    fun slugify(input: String, maxLen: Int = 80): String {
        val noAccents = Normalizer.normalize(input.trim(), Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        var slug = noAccents.lowercase(Locale.US)
            .replace("[^a-z0-9]+".toRegex(), "-")
            .trim('-')
        if (slug.length > maxLen) slug = slug.substring(0, maxLen).trim('-')
        if (slug.isBlank()) slug = "item"
        return slug
    }

    fun emailKey(email: String): String =
        email.trim().lowercase(Locale.US)
            .replace(".", ",").replace("#", "%23")
            .replace("$", "%24").replace("[", "%5B").replace("]", "%5D")

    suspend fun nextAvailableId(parent: DatabaseReference, base: String): String {
        var n = 1
        while (true) {
            val candidate = if (n == 1) base else "$base-$n"
            if (!parent.child(candidate).get().await().exists()) return candidate
            n++
        }
    }
}

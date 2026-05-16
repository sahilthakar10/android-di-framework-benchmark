package com.codeint.dibenchmark.runtime

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Auto-initializes the DiBenchmark SDK via manifest merge.
 * This ContentProvider runs before Application.onCreate() and sets up
 * the SDK with default configuration. No user code change needed.
 */
class DiBenchmarkInitializer : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context ?: return false
        val app = context.applicationContext as? Application ?: return false

        DiBenchmark.initialize(app)
        DiBenchmark.startSession()
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}

package com.show.launch

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri


class AppProvider : ContentProvider() {

    override fun insert(uri: Uri, values: ContentValues?): Uri?  = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int =- -1

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int =  -1

    override fun getType(uri: Uri): String?  = null

    override fun onCreate(): Boolean {
        context?.apply {
            Launch.attach(this)
                .doInit()
        }
        return false
    }
}
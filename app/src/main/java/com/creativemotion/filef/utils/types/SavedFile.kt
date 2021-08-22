package com.creativemotion.filef.utils.types

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.getAbsolutePath

/**
 * used in walkers and model part of the app
 * object for comparison of the found files and other extensions
 */
data class SavedFile(val path: String?, val uri: Uri?, val size: Long = -1)

fun SavedFile.isEmpty(): Boolean{
    return path == null && uri == null
}

fun SavedFile.getPath(context: Context): String{
    if (path != null) return path
    if (uri != null){
        DocumentFile.fromSingleUri(context, uri)?.let {
            return it.getAbsolutePath(context)
        }
    }
    return ""
}


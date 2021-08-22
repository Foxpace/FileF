package com.creativemotion.filef.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import com.anggrayudi.storage.file.getRootPath
import java.io.File

object CustomFileUtils {

    /**
     * builds tree uri from the previous uri and document ID of the child file
     */
    fun getNewUriFromId(childrenUri: Uri, documentId: String): Uri {
        return DocumentsContract.buildDocumentUriUsingTree(
            childrenUri,
            documentId
        )
    }

    /**
     * returns path to SD card - if path exists
     */
    fun getSdPath(context: Context): String? {
        context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES).forEach {
            if (Environment.isExternalStorageRemovable(it)) return it.getRootPath(context)
        }
        return null
    }


    /**
     * checks if the path is not child path of the already added paths
     * checks if the path is not parent path of the already added paths
     */
    fun addNewFileToSet(set: MutableSet<File>, file: File): Boolean {
        if (!isPathSubPath(
                set,
                file.absolutePath
            )
        ) {
            removeChildrenFiles(set, file)
            set.add(file)
            return true
        }
        return false
    }

    /**
     * checks if the path is not child path of the already added paths
     */
    private fun isPathSubPath(set: MutableSet<File>, path: String): Boolean {
        set.forEach { if (path.contains(it.absolutePath)) return true }
        return false
    }

    /**
     * checks if the path is not parent path of the already added paths
     */
    private fun removeChildrenFiles(set: MutableSet<File>, file: File){
        val parent: String = file.absolutePath
        val i: MutableIterator<File> = set.iterator()
        while (i.hasNext()) if(i.next().absoluteFile.startsWith(parent)) i.remove()
    }
}
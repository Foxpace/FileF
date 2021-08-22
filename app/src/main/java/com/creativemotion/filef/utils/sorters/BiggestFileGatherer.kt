package com.creativemotion.filef.utils.sorters

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.creativemotion.filef.utils.CustomFileUtils
import com.creativemotion.filef.utils.types.SavedFile
import com.creativemotion.filef.utils.types.isEmpty
import java.io.File
import java.nio.file.Files
import java.nio.file.Path


/**
 * aggregates all the files and their sizes - and saves them into the ordered array
 * to sort array - InsertionSort is used
 * takes File, Uri, Path objects -> they are converted to the SavedFile object
 * @param numberOfFiles - number of files to find
 */
class BiggestFileGatherer(numberOfFiles: Int) {

    private var minSize = -1L
    private var savedFiles: Array<SavedFile> = Array(numberOfFiles+1){SavedFile(null, null)}

    fun onFile(file: File) {
        if (file.length() > minSize) addFile(file.absolutePath, null, file.length())

    }

    fun onFileQuery(childrenUri: Uri, documentId: String, size: Long) {
        if (size > minSize) addFile(null, CustomFileUtils.getNewUriFromId(childrenUri, documentId), size)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPath(path: Path) {
        if (Files.size(path) > minSize) addFile(path.toString(), null, Files.size(path))

    }

    private fun addFile(filePath: String? = null, uri: Uri? = null, size: Long) {
        savedFiles[0] = SavedFile(filePath, uri, size)
        Sorting.insertionSort(savedFiles)
        minSize = savedFiles[1].size
        savedFiles[0] = SavedFile(null, null)
    }

    fun getFiles(): List<SavedFile> {
        return savedFiles.filter {
            !it.isEmpty()
        }.toList().reversed()
    }

}
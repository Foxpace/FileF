package com.creativemotion.filef.utils

import android.content.Context
import com.creativemotion.filef.utils.sorters.Sorting
import com.creativemotion.filef.utils.types.SavedFile
import java.io.File

/**
 * iterates through picked files and aggregates the biggest files,
 * which are sorted and picked to represent in UI
 */

class WalkerIterator(
    private val files: MutableSet<File>, // folders to iterate
    private val numberOfFiles: Int, // files to find
    private val walkerListener: WalkerListener, // callbacks for the UI
) {

    private val foundFiles: ArrayList<SavedFile> = ArrayList(numberOfFiles * files.size)

    suspend fun searchForFiles(
        context: Context,
        walkerType: WalkerFactory.WalkerType
    ): List<SavedFile> {

        // iterates through the folders
        files.forEach { file: File ->
            walkerListener.onFileChanged(file.absolutePath)
            WalkerFactory.getWalker(context, numberOfFiles, walkerType).let {
                it.walkFiles(file)
                foundFiles.addAll(it.getFiles())
            }
        }

        walkerListener.onFinishWalking()

        if (foundFiles.isEmpty()) return listOf()

        // sorting them and getting the picked sublist
        var from = foundFiles.size - numberOfFiles
        if (from < 0) from = 0
        Sorting.quickSort(foundFiles, 0, foundFiles.size - 1)
        return foundFiles.subList(from, foundFiles.size).toList()

    }


}
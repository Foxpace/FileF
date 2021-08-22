package com.creativemotion.filef.utils.walkers

import com.creativemotion.filef.utils.sorters.BiggestFileGatherer
import com.creativemotion.filef.utils.types.SavedFile
import java.io.File

/**
 * generalization of the different approaches to access storage
 * has field of BiggestFileGatherer - stores the biggest found files - comparator, just need to pass files
 */
abstract class Walker(numberOfFiles: Int) {

    val biggestFileGatherer: BiggestFileGatherer = BiggestFileGatherer(numberOfFiles)

    /**
     * place for custom implementation of the folder walker
     */
    open fun walkFiles(file: File){}

    fun getFiles(): List<SavedFile> {
        return biggestFileGatherer.getFiles()
    }

}
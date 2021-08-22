package com.creativemotion.filef.utils.walkers

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

/**
 * uses Apache Commons IO library to access files
 * the library is wrapper for nio.files library, which is accessible from Android O,
 * but with this library we can use it in lower versions of the Android
 */
class WalkerApacheIO(numberOfFiles: Int): Walker(numberOfFiles) {

    override fun walkFiles(file: File){
        val iterator = FileUtils.iterateFiles(file, object: IOFileFilter{
            override fun accept(file: File?): Boolean {
                return true
            }

            override fun accept(dir: File?, name: String?): Boolean {
                return true
            }
        }, TrueFileFilter.INSTANCE)

        while (iterator.hasNext()) biggestFileGatherer.onFile(iterator.next())
    }

}
package com.creativemotion.filef.utils.walkers

import com.anggrayudi.storage.file.isEmpty
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

/**
 * old fashioned Files Api, where we use list files method to access further files and folders
 * the case with symbolic link is also covered with apache.commons.io
 */

class WalkerFiles (numberOfFiles: Int) : Walker(numberOfFiles) {

    override fun walkFiles(file: File) {
        val stack = Stack<File>()
        stack.push(file)
        while (stack.isNotEmpty()){
            stack.pop().listFiles()?.let { fileList: Array<File> ->
                fileList.forEach{ tempFile: File ->
                    if (tempFile.isDirectory){
                        if (!FileUtils.isSymlink(tempFile) && !tempFile.isEmpty) stack.push(tempFile)
                    }else{
                        biggestFileGatherer.onFile(tempFile)
                    }
                }
            }
        }
    }


}
package com.creativemotion.filef.utils.walkers

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.file.Files
import java.nio.file.Path


/**
 * implementation of nio.file API for Android O and higher
 */

@RequiresApi(Build.VERSION_CODES.O)
class WalkerFilesOreo(numberOfFiles: Int) : Walker(numberOfFiles) {

    override fun walkFiles(file: File) {
        Files.walk(file.toPath(), Int.MAX_VALUE)
            .forEach { p: Path -> biggestFileGatherer.onPath(p) }
    }
}
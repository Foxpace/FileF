package com.creativemotion.filef.repository

import android.content.Context
import com.creativemotion.filef.uihandlers.types.SearchState
import com.creativemotion.filef.utils.WalkerFactory
import com.creativemotion.filef.utils.WalkerIterator
import com.creativemotion.filef.utils.WalkerListener
import com.creativemotion.filef.utils.sorters.Sorting
import com.creativemotion.filef.utils.types.SavedFile
import kotlinx.coroutines.*
import java.io.File

class MainRepository {

    // private scope for coroutines
    private var coroutineScope: CoroutineScope? = null

    suspend fun getSearchResult(
        context: Context,
        walkerListener: WalkerListener,
        walkerType: WalkerFactory.WalkerType,
        foldersToSearch: MutableSet<File>,
        foldersToSearchSD: MutableSet<File>,
        numberOfFiles: Int,
    ): SearchState {

        coroutineScope = CoroutineScope(Dispatchers.IO)
        var externalMemory: Deferred<List<SavedFile>>? = null
        var sdMemory: Deferred<List<SavedFile>>? = null

        // async for the folders in internal memory
        if (foldersToSearch.isNotEmpty()) {
            externalMemory = coroutineScope!!.async {
                return@async WalkerIterator(
                    foldersToSearch,
                    numberOfFiles,
                    walkerListener,
                ).searchForFiles(
                    context,
                    walkerType,
                )
            }
        }

        // async for the SD card
        if (foldersToSearchSD.isNotEmpty()) {
            sdMemory = coroutineScope!!.async {
                return@async WalkerIterator(
                    foldersToSearchSD,
                    numberOfFiles,
                    walkerListener,
                ).searchForFiles(
                    context,
                    walkerType
                )
            }
        }

        val allResults: ArrayList<SavedFile> =
            ArrayList(numberOfFiles * foldersToSearch.size * 2)

        // waits for both of them to finish
        sdMemory?.await()?.let {
            allResults.addAll(it)
        }
        externalMemory?.await()?.let {
            allResults.addAll(it)
        }

        // picking the biggest files
        if (sdMemory != null && externalMemory != null) Sorting.quickSort(allResults, 0, allResults.size - 1)

        var from = allResults.size - numberOfFiles
        if (from < 0) from = 0

        val result = allResults.subList(from, allResults.size).toList().reversed()

        return SearchState.OnFinish(result)
    }

    fun cancel() {
        coroutineScope?.cancel()
    }

}
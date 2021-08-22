package com.creativemotion.filef.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativemotion.filef.R
import com.creativemotion.filef.repository.MainRepository
import com.creativemotion.filef.utils.CustomFileUtils
import com.creativemotion.filef.utils.WalkerFactory
import com.creativemotion.filef.uihandlers.types.FileUI
import com.creativemotion.filef.uihandlers.types.SearchState
import com.creativemotion.filef.utils.WalkerListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel(), WalkerListener {

    // stores all the folders to walk
    val foldersToShow: ArrayList<FileUI> = ArrayList()
    // sets to prevent repetition of the files
    private val foldersToSearch: MutableSet<File> = mutableSetOf()
    private val foldersToSearchSD: MutableSet<File> = mutableSetOf()
    // path to sd
    private var sdPath: String? = null

    // livedata for the state of the app - searching, finished, ...
    private var _searchState: MutableLiveData<SearchState> = MutableLiveData()
    val searchState: LiveData<SearchState>
        get() = _searchState

    // actual state of the search - showing the actual path
    private var _pathToShow: MutableLiveData<String> = MutableLiveData()
    val pathToShow: LiveData<String>
        get() = _pathToShow

    /**
     * launches the event to search for the biggest files and updates the searchState with result
     */
    fun searchForFiles(context: Context, numberOfFiles: Int, walkerType: WalkerFactory.WalkerType) {

        _searchState.value = SearchState.OnStart

        viewModelScope.launch(Dispatchers.IO) {
            val result: SearchState = try {
                mainRepository.getSearchResult(
                    context,
                    this@MainViewModel,
                    walkerType,
                    foldersToSearch,
                    foldersToSearchSD,
                    numberOfFiles
                )
            } catch (e: Exception) {
                e.printStackTrace()
                SearchState.OnError(context.getString(R.string.search_error))
            }

            withContext(Dispatchers.Main) {
                if(result is SearchState.OnFinish && result.result.isEmpty()){
                    _searchState.value = SearchState.OnError(context.getString(R.string.search_no_files))
                }else{
                    _searchState.value = result
                }
            }
        }
    }

    /**
     * adding the folder to the stack - true if successful, false - duplicate or child
     */
    fun addFile(context: Context, file: File): Boolean {

        var newFileAdded: Boolean

        newFileAdded = isCardFile(context, file)

        if(!newFileAdded) newFileAdded = isLocalFile(file)

        if (newFileAdded) updateDataUI()
        else _searchState.value = SearchState.OnInvalidFolder

        return newFileAdded
    }

    /**
     * checks if the file is from SD card + it adds it to SD card set
     */
    private fun isCardFile(context: Context, file: File): Boolean {
        if (sdPath == null) sdPath = CustomFileUtils.getSdPath(context)
        if (sdPath != null) {
            if (file.absolutePath.contains(sdPath!!)) {
                if (CustomFileUtils.addNewFileToSet(foldersToSearchSD, file)) {
                     return true
                }
            }
        }
        return false
    }

    /**
     * adds file to the local storage set
     */
    private fun isLocalFile(file: File): Boolean {
        if (CustomFileUtils.addNewFileToSet(foldersToSearch, file)) {
            return true
        }
        return false
    }

    /**
     * FileUI are initialized after every change, so the UI can be modified
     */
    private fun updateDataUI(){
        foldersToShow.clear()
        foldersToSearchSD.forEach {
            foldersToShow.add(FileUI(true, it.absolutePath))
        }
        foldersToSearch.forEach {
            foldersToShow.add(FileUI(false, it.absolutePath))
        }
        _searchState.value = SearchState.OnFoldersChange
    }

    /**
     * removes file from all the memories
     */
    fun removeFile(position: Int) {
        val file = File(foldersToShow[position].path)
        foldersToShow.removeAt(position)
        foldersToSearch.remove(file)
        foldersToSearchSD.remove(file)
        _searchState.value = SearchState.OnFoldersChange
    }


    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }

    fun cancelSearch() {
        mainRepository.cancel()
    }

    /**
     * updates the textview under the progress bar for the search
     */
    override suspend fun onFileChanged(path: String) {
        withContext(Dispatchers.Main){
            _pathToShow.value = path
        }

    }

    /**
     * empties the text under the progress bar
     */
    override suspend fun onFinishWalking() {
        withContext(Dispatchers.Main){
            _pathToShow.value = ""
        }

    }

}
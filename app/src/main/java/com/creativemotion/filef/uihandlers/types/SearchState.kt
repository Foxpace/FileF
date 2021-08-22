package com.creativemotion.filef.uihandlers.types

import com.creativemotion.filef.utils.types.SavedFile

/**
 * basic states of the app
 */
sealed class SearchState {
    object OnStart: SearchState()
    class OnFinish(val result: List<SavedFile>): SearchState()
    class OnError(val error: String): SearchState()
    object OnFoldersChange: SearchState()
    object OnInvalidFolder: SearchState()
}
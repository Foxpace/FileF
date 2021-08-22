package com.creativemotion.filef.utils

/**
 * callbacks for the UI from WalkerIterator
 */
interface WalkerListener {
    suspend fun onFileChanged(path: String)
    suspend fun onFinishWalking()
}
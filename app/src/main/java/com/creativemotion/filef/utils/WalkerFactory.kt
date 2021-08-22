package com.creativemotion.filef.utils

import android.content.Context
import android.os.Build
import com.creativemotion.filef.utils.walkers.*

object WalkerFactory {

    enum class WalkerType { WalkerApache, WalkerContentResolver, WalkerFiles, WalkerFilesOreo }

    fun getWalker(context: Context, numberOfItems: Int, walkerType: WalkerType): Walker {
        return when (walkerType) {
            WalkerType.WalkerApache -> WalkerApacheIO(numberOfItems)
            WalkerType.WalkerContentResolver -> WalkerContentResolver(numberOfItems, context)
            WalkerType.WalkerFiles -> WalkerFiles(numberOfItems)
            WalkerType.WalkerFilesOreo -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WalkerFilesOreo(numberOfItems)
            } else {
                WalkerFiles(numberOfItems)
            }
        }
    }

    fun stringToWalkerType(value: String): WalkerType {
        return when (value) {
            "IO File API" -> WalkerType.WalkerFiles
            "NIO File API" -> WalkerType.WalkerFilesOreo
            "Content resolver" -> WalkerType.WalkerContentResolver
            "Apache IO" -> WalkerType.WalkerApache
            else -> WalkerType.WalkerFiles
        }
    }
}
package com.creativemotion.filef.utils.sorters

import com.creativemotion.filef.utils.types.SavedFile

object Sorting {

    fun insertionSort(arr: Array<SavedFile>) {
        val n = arr.size
        for (i in 1 until n) {
            val key = arr[i]
            var j = i - 1
            while (j >= 0 && arr[j].size > key.size) {
                arr[j + 1] = arr[j]
                j -= 1
            }
            arr[j + 1] = key
        }
    }

    /**
     * recursive call for the quicksort alg
     * for the first call: low = 0, high = array.size - 1
     */
    //
    fun quickSort(array: ArrayList<SavedFile>, low: Int, high: Int) {
        if (low < high) {
            val pivot = partition(array, low, high)
            quickSort(array, low, pivot - 1)
            quickSort(array, pivot, high)
        }
    }

    /**
     * This method finds the pivot index for an array
     *
     * @param array The array to be sorted
     * @param low The first index of the array
     * @param high The last index of the array
     *
     * */
    private fun partition(array: ArrayList<SavedFile>, low: Int, high: Int): Int {

        var left = low
        var right = high
        val mid = (left + right) / 2
        val pivot = array[mid].size

        while (left <= right) {
            while (array[left].size < pivot) {
                left++
            }

            while (array[right].size > pivot) {
                right--
            }

            if (left <= right) {
                swapElements(array, left, right)
                left++
                right--
            }
        }
        return left
    }

    private fun swapElements(array: ArrayList<SavedFile>, idx1: Int, idx2: Int) {
        array[idx1] = array[idx2].also {
            array[idx2] = array[idx1]
        }
    }
}
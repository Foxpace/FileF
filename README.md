# FileF - search for the biggest files in the phone

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<p align="center"><img src="https://github.com/Foxpace/FileF/blob/master/Images/icon.png" alt="loading" width="250" /></p>

<p align="center"><img src="https://github.com/Foxpace/FileF/blob/master/Images/merged.png" alt="merged_app" width="600" /></p>

With new versions of the Android, it is becomes harder and harder to access storage via some APIs. The FileF collects **4 different approaches** to access storage in Android, which work with fine until the Android 10. **Android 11 and higher requires MANAGE_APP_ALL_FILES_ACCESS_PERMISSION** to access all the files in the background via older APIs. If the app needs this permissions and is released on Google Play, it needs to serve specific purpose like file browser, antivirus, ... - more on that [here](https://developer.android.com/training/data-storage/manage-all-files). Otherwise, you should use scoped storage - more [here](https://developer.android.com/guide/topics/data).

The app takes folders as inputs and number of the number of the biggest files to be found. Also you can choose algorithm, which will search the folders. The app supports parallel search in SD card and external memory of the phone. 

## Approaches

- **File API** - The Android can use java old-fashioned File API, which is compatible with all the Android versions - in my tests the fastest
- **Nio Files API** - Compatible with Android Oreo and higher - have its own implementation to walk through the all folders 
- **Content resolver** - usage of queries to find files in the folder - unfortunately, the conditions for the query are ignored and is the slowest among the APIs
- [**Apache Commons IO**](https://commons.apache.org/) - wrapper for the Nio Files API, which can be used in lower versions of the Android [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## App organization

The main folder of the app is **'utils'**, where all the methods can be found:

- **sorters**
  - BiggestFileGatherer.kt - Keeps sorted array by size. Insertion is being done by InsertionSort. Accepts File, Uri, Path objects. 
  - Sorting.kt - InsertionSort and QuickSort implementations
- **types** - SavedFile.kt - basic object, which saves path, uri and size of the file
- **walkers** 
  - Walker.kt - abstract class, which has BiggestFileGatherer and method to customized, so the walkers are in organized manner
  - **implementations**: WalkerApacheIO.kt / WalkerContentResolver.kt / WalkerFiles.kt / WalkerFilesOreo.kt
- CustomFileUtils.kt - helper functions to get path to SD, adding to Sets, duplication preventions, ...
- WalkerFactory.kt - generates the required Walker
- WalkerIterator.kt - takes list of the folders to be searched in, iterates through them and merges the results of all folders into one
- WalkerListener.kt - interface, which the UI should have, so it can show the progress

## Used libraries

- [**Apache Commons IO**](https://commons.apache.org/) - Apache Commons IO is a library of utilities to assist with developing IO functionality. [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
- [**SimpleStorage**](https://github.com/anggrayudi/SimpleStorage) - 💾 Simplify Android Storage Access Framework for file management across API levels. [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
- [**Toasty**](https://github.com/GrenderG/Toasty) - The usual Toast, but with steroids 💪 [![License: GPL v3](https://camo.githubusercontent.com/400c4e52df43f6a0ab8a89b74b1a78d1a64da56a7848b9110c9d2991bb7c3105/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f4c6963656e73652d47504c76332d626c75652e737667)](https://www.gnu.org/licenses/gpl-3.0)








package com.creativemotion.filef.utils.walkers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.toTreeDocumentFile
import com.creativemotion.filef.utils.CustomFileUtils
import java.io.File
import java.util.*

/**
 * uses content resolver queries to access all the files with uri
 * the slowest of the all methods, however the shame is, that for the documents
 * the conditional search is not available - is ignored in deeper levels of the code
 * the query gets the document id, size and type, which are used to compare and search for further files
 */
class WalkerContentResolver(numberOfFiles: Int, private val context: Context) :
    Walker(numberOfFiles) {

    private val queryProjection = arrayOf(
        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_SIZE,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
//        DocumentsContract.Document.COLUMN_DISPLAY_NAME
    )

    // https://stackoverflow.com/questions/56263620/contentresolver-query-on-documentcontract-lists-all-files-disregarding-selection
//    private val sizeAndEmptyQuery = "${DocumentsContract.Document.COLUMN_MIME_TYPE} = ?"

    override fun walkFiles(file: File) {

        // stack to store folders
        val stack = Stack<Uri>()
        stack.push(DocumentFile.fromFile(file).toTreeDocumentFile(context)!!.uri)

        // placeholders for the search
        var uri: Uri
        var childrenTreeUri: Uri
        var documentId: String
        var c: Cursor?

        while (stack.isNotEmpty()) {
            uri = stack.pop()

            childrenTreeUri =
                DocumentsContract.buildChildDocumentsUriUsingTree(
                    uri,
                    if (DocumentsContract.isDocumentUri(context, uri)) {
                        DocumentsContract.getDocumentId(uri)
                    } else {
                        DocumentsContract.getTreeDocumentId(uri)
                    }
                )

            c = null
            try {
                // search query
                c = context.contentResolver.query(childrenTreeUri, queryProjection, null, null, null)

                while (c!!.moveToNext()) {
                    documentId = c.getString(0)
                    // val name = c.getString(3)
                    if (c.getString(2) != DocumentsContract.Document.MIME_TYPE_DIR) {
                        // for uri
                        biggestFileGatherer.onFileQuery(uri, documentId, c.getLong(1))
                    } else {
                        // for folder
                        stack.push(CustomFileUtils.getNewUriFromId(uri, documentId))
                    }
                }
            } catch (e: Exception) {
                Log.w("Search", "Failed query: $e")
            } finally {
                c?.close()
            }
        }
    }

}
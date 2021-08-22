package com.creativemotion.filef.uihandlers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.creativemotion.filef.R
import com.creativemotion.filef.uihandlers.UtilsUI
import com.creativemotion.filef.utils.types.SavedFile
import com.creativemotion.filef.utils.types.getPath

/**
 * shows the biggest found files in the picked folders
 * adapter for the SearchFragment's recycler
 */
class SearchAdapter(var foundFolders: List<SavedFile>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.row_folder_type)
        val textView: TextView = view.findViewById(R.id.row_folder_text)
        val textViewMemory: TextView = view.findViewById(R.id.row_folder_memory)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = if (UtilsUI.isNightMode(viewGroup.context)) {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_dark_memory, viewGroup, false)
        } else {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_light_memory, viewGroup, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = foundFolders[position].getPath(viewHolder.textView.context)
        viewHolder.imageView.setImageResource(R.drawable.ic_folder_icon)
        viewHolder.textViewMemory.text = getMemoryInKb(foundFolders[position].size)
    }

    private fun getMemoryInKb(size: Long): String {
        return "${(size / 1024L)} kB"
    }

    override fun getItemCount() = foundFolders.size

}
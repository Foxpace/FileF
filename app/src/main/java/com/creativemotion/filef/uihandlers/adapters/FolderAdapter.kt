package com.creativemotion.filef.uihandlers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.creativemotion.filef.R
import com.creativemotion.filef.uihandlers.UtilsUI
import com.creativemotion.filef.viewmodel.MainViewModel

/**
 * adapter for the recycler to store folders to be searched for
 */
class FolderAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    // holds references to views
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.row_folder_type)
        val textView: TextView = view.findViewById(R.id.row_folder_text)
        val buttonView: ImageButton = view.findViewById(R.id.row_folder_delete)
    }

    // change views for the dark and light theme
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = if (UtilsUI.isNightMode(viewGroup.context)) {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_dark, viewGroup, false)
        } else {
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.row_light, viewGroup, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView.text = viewModel.foldersToShow[position].path
        val id = if (viewModel.foldersToShow[position].sdCard) {
            R.drawable.ic_sd_icon
        } else {
            R.drawable.ic_folder_icon
        }
        viewHolder.imageView.setImageResource(id)
        viewHolder.buttonView.setOnClickListener { // removes view, if the button is clicked
            viewModel.removeFile(viewHolder.adapterPosition)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = viewModel.foldersToShow.size

}
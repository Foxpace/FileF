package com.creativemotion.filef.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creativemotion.filef.R
import com.creativemotion.filef.uihandlers.Notify
import com.creativemotion.filef.uihandlers.adapters.SearchAdapter
import com.creativemotion.filef.uihandlers.types.SearchState
import com.creativemotion.filef.viewmodel.MainViewModel


class SearchFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    // number of files and walker type is passed
    private val args: SearchFragmentArgs by navArgs()

    // list of the files to show
    private val searchAdapter: SearchAdapter = SearchAdapter(listOf())

    private val notificationId: Int = 6789

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // recyclerview init
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        view.findViewById<RecyclerView>(R.id.search_recycler).apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = searchAdapter
        }

        // cancel button
        view.findViewById<Button>(R.id.search_cancel).setOnClickListener {
            mainViewModel.cancelSearch()
            val action = SearchFragmentDirections.actionGoHome()
            Navigation.findNavController(view).navigate(action)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.searchState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is SearchState.OnInvalidFolder -> {}
                is SearchState.OnFoldersChange -> {}

                // show progressbar with text
                is SearchState.OnStart -> {
                    view.findViewById<ProgressBar>(R.id.search_progressBar).apply {
                        this.visibility = View.VISIBLE
                    }
                    view.findViewById<TextView>(R.id.search_path_to_show).apply {
                        this.visibility = View.VISIBLE
                    }
                }
                // hide progressbar with text, and show recycler with new data
                is SearchState.OnFinish -> {
                    Notify.createFinishingNotification(requireContext(), notificationId)
                    view.findViewById<ProgressBar>(R.id.search_progressBar).apply {
                        this.visibility = View.GONE
                    }
                    view.findViewById<TextView>(R.id.search_path_to_show).apply {
                        this.visibility = View.GONE
                    }
                    view.findViewById<RecyclerView>(R.id.search_recycler).apply {
                        searchAdapter.foundFolders = state.result
                        searchAdapter.notifyItemRangeChanged(0, state.result.size)
                        visibility = View.VISIBLE
                    }
                }

                // show error text
                is SearchState.OnError -> {
                    view.findViewById<ProgressBar>(R.id.search_progressBar).apply {
                        this.visibility = View.GONE
                    }
                    view.findViewById<TextView>(R.id.search_error_msg).apply {
                        this.visibility = View.VISIBLE
                        this.text = state.error
                    }
                }
            }
        })

        // updates text under progressbar - which folder is actually used
        mainViewModel.pathToShow.observe(viewLifecycleOwner){
            view.findViewById<TextView>(R.id.search_path_to_show).apply {
                this.text = it
            }
        }

        // starts search
        Notify.createStartingNotification(requireContext(), notificationId)
        mainViewModel.searchForFiles(requireContext(), args.numberOfFiles, args.walkerType)
    }
}
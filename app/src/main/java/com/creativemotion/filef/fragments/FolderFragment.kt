package com.creativemotion.filef.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggrayudi.storage.file.toRawFile
import com.creativemotion.filef.R
import com.creativemotion.filef.uihandlers.adapters.FolderAdapter
import com.creativemotion.filef.uihandlers.UtilsUI
import com.creativemotion.filef.uihandlers.types.SearchState
import com.creativemotion.filef.utils.WalkerFactory
import com.creativemotion.filef.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty


@AndroidEntryPoint
class FolderFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    // for recycler managmenet
    private lateinit var adapter: FolderAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    // picking dirs
    private lateinit var dirOpener: ActivityResultLauncher<Intent>

    // holder for spinner - to pick walker
    private lateinit var spinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // intro fragment - no permission
        if(!UtilsUI.isPermission(requireContext())){
            findNavController().navigate(FolderFragmentDirections.actionIntro())
            return
        }

        dirOpener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                processActivityResult(result)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    private fun initRecycler(view: View) {
        adapter = FolderAdapter(mainViewModel)
        linearLayoutManager = LinearLayoutManager(requireContext())

        val recycler = view.findViewById<RecyclerView>(R.id.folder_recycler)
        recycler.layoutManager = linearLayoutManager
        recycler.adapter = adapter
    }

    private fun initSpinner(view: View) {

        // NIO file walker available for Oreo and higher
        val spinnerValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.array.search_algos
        } else {
            R.array.search_algos_below_oreo
        }

        spinner = view.findViewById(R.id.folder_spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            spinnerValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    /**
     * trying to get file uri from the activity result -> DocumentFile -> File
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun processActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data!!.data?.let { uri ->
                DocumentFile.fromTreeUri(requireContext(), uri)?.let { document ->
                    document.toRawFile(requireContext())?.let { file ->
                        if (mainViewModel.addFile(requireContext(), file)) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                return
            }

            Toasty.warning(
                requireActivity().applicationContext,
                R.string.failed_to_add_string,
                Toasty.LENGTH_LONG
            ).show()

        }
    }

    private fun onAddFolder() {
//        if (Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT) {
//            Intent(Intent.ACTION_GET_CONTENT).let {
//                it.type = "*/*"
//                activityResultLauncher.launch(it)
//            }
//        } else {
            dirOpener.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
//        }
    }

    /**
     * checks if there are some folders to search, picked methods and number of files to search for
     */
    private fun onStartSearch() {
        if (mainViewModel.foldersToShow.isEmpty()) {
            Toasty.warning(
                requireActivity().applicationContext,
                R.string.list_is_empty,
                Toasty.LENGTH_SHORT
            ).show()
            return
        }

        val numberOfFilesText: String =
            requireView().findViewById<EditText>(R.id.folder_number_of_files_field).text.toString()

        if (numberOfFilesText.isBlank()) {
            Toasty.warning(
                requireActivity().applicationContext,
                getString(R.string.pick_number),
                Toasty.LENGTH_SHORT
            ).show()
            return
        }

        val type: WalkerFactory.WalkerType =
            WalkerFactory.stringToWalkerType(spinner.selectedItem.toString())
        val numberOfFiles = numberOfFilesText.toInt()

        if (numberOfFiles <= 0) {
            Toasty.warning(
                requireActivity().applicationContext,
                getString(R.string.small_number),
                Toasty.LENGTH_SHORT
            ).show()
            return
        }

        FolderFragmentDirections.actionStartSearch(numberOfFiles, type).also {
            findNavController().navigate(it)
        }

    }

    /**
     * recycler can be empty -> show textview with "list is empty"
     * otherwise make textview gone and show recyclerview with picked folders
     */
    private fun setEmptyView(view: View) {
        val emptyView = view.findViewById<TextView>(R.id.folder_empty)
        val recycler = view.findViewById<RecyclerView>(R.id.folder_recycler)
        if (adapter.itemCount > 0) {
            emptyView.visibility = View.GONE
            recycler.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.VISIBLE
            recycler.visibility = View.INVISIBLE
        }
    }

    /**
     * set up all the views - click listener, visibility and observers
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler(view)
        initSpinner(view)

        view.findViewById<Button>(R.id.folder_button_add_file).setOnClickListener { onAddFolder() }
        view.findViewById<Button>(R.id.folder_button_search_files)
            .setOnClickListener { onStartSearch() }

        setEmptyView(view)

        mainViewModel.searchState.observe(viewLifecycleOwner, { searchState: SearchState ->
            when (searchState) {
                SearchState.OnFoldersChange -> setEmptyView(view)
                SearchState.OnInvalidFolder -> Toasty.warning(
                    requireActivity().applicationContext,
                    getString(R.string.file_is_included),
                    Toasty.LENGTH_SHORT
                ).show()
                else -> {}
            }
        })
    }


}
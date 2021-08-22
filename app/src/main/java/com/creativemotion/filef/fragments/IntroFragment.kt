package com.creativemotion.filef.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.creativemotion.filef.R
import com.creativemotion.filef.uihandlers.UtilsUI
import es.dmoral.toasty.Toasty

@RequiresApi(Build.VERSION_CODES.M)
class IntroFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestManageFilesR: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // permission launchers for the permissions

        // under Android R
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    changeTextToNext(requireView())
                } else {
                    Toasty.error(
                        requireActivity().applicationContext,
                        getString(R.string.intro_permission_needed),
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }

        // Android R and higher
        requestManageFilesR =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (UtilsUI.isPermission(requireContext())) {
                    changeTextToNext(requireView())
                } else {
                    Toasty.error(
                        requireActivity().applicationContext,
                        getString(R.string.intro_permission_needed),
                        Toasty.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        container?.removeAllViews()

        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        changeTextToNext(view)
        return view
    }

    /**
     * changes the button's text to "Next", so the user clicks it again and is
     * navigated to FolderFragment
     */
    private fun changeTextToNext(view: View) {
        if (UtilsUI.isPermission(requireContext())) {
            view.findViewById<TextView>(R.id.intro_button).apply {
                text = requireContext().getText(R.string.next)
            }
        }
    }

    /**
     * sets up button click to check permission
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.intro_button).setOnClickListener {
            if (UtilsUI.isPermission(requireContext())) {
                findNavController().navigate(IntroFragmentDirections.actionGoHome())
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    askManageAppAllFiles()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        }
    }

    /**
     * checks the ALL_FILES_ACCESS_PERMISSION for Android R
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun askManageAppAllFiles(){
        try {
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                it.addCategory("android.intent.category.DEFAULT")
                it.data = Uri.parse(
                    String.format(
                        "package:%s",
                        requireContext().packageName
                    )
                )
                requestManageFilesR.launch(it)
            }
        } catch (e: Exception) {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).also {
                requestManageFilesR.launch(it)
            }
        }
    }
}
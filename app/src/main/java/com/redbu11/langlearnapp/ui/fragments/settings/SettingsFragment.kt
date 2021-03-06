/*
 *
 *  * Copyright (C) 2020 Viktor Bukovets
 *  *
 *  * This file is part of LangLearnApp.
 *  *
 *  * LangLearnApp is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * LangLearnApp is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */

package com.redbu11.langlearnapp.ui.fragments.settings

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.ui.dialogs.AboutTheAppDialogFragment
import com.redbu11.langlearnapp.ui.dialogs.EraseDatabaseDialog
import com.redbu11.langlearnapp.ui.dialogs.ExportPhrasesDialog
import com.redbu11.langlearnapp.ui.dialogs.ImportPhrasesDialog
import com.redbu11.langlearnapp.ui.dialogs.abstactions.ConfirmationDialogFragment
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener,
    ConfirmationDialogFragment.ConfirmationDialogListener, HasAndroidInjector {

    //@Inject
    //lateinit var dao: PhraseDAO
    @Inject
    lateinit var repository: PhraseRepository

    private lateinit var settingsViewModel: SettingsViewModel
    private val phrases: MutableSet<Phrase> = mutableSetOf()

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        //LangLearnApp.appComponent.inject(this)
        //PhraseDatabase.getInstance(requireActivity().applicationContext).phraseDAO
        //repository = PhraseRepository(dao)
        val factory = SettingsViewModelFactory(requireActivity().application, repository)
        settingsViewModel =
            ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        settingsViewModel.phrases.observe(viewLifecycleOwner, Observer {
            phrases.addAll(it)
        })
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key
        when {
            key.equals("clear_all") -> {
                showConfirmEraseDatabaseDialog()
            }
            key.equals("export_phrases") -> showConfirmExportDatabaseDialog()
            key.equals("import_phrases") -> showConfirmImportDatabaseDialog()
            key.equals("about") -> {
                val dialogFragment: DialogFragment = AboutTheAppDialogFragment()
                dialogFragment.show(childFragmentManager, "AboutTheAppDialogFragment")
            }
            else -> {
                //nothing
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return true
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        //
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    /**
     * Create an intent
     */
    private fun createOpenFileIntent(context: Context, uri: Uri): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        //val contentUri =
        //    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(uri)
        //intent.putExtra(Intent.EXTRA_TITLE, "Open your file with a .csv reader")
        intent.setDataAndType(uri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    /**
     * export phrases to CSV file on external storage
     */
    private fun exportPhrasesToCSVFile() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/csv"
                putExtra(Intent.EXTRA_TITLE, "export_phrases_${System.currentTimeMillis() / 1000}.csv")

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker before your app creates the document.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.EMPTY)
                }
            }
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent, CREATE_CSV_FILE)

        } else {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Start chooser to pick a .csv file(any text file,
     * because some file managers don't recognize text/csv)
     */
    private fun pickCsvToImport() {
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "text/*"
        chooseFile = Intent.createChooser(chooseFile, getString(R.string.settings_pick_csv_intent))
        //chooseFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivityForResult(chooseFile, REQUEST_CODE_GET_CSV_FILE)
    }

    //Dialogs-----------

    /**
     * Show confirmation dialog to erase database
     */
    fun showConfirmEraseDatabaseDialog() {
        val dialogFragment: DialogFragment = EraseDatabaseDialog()
        dialogFragment.show(childFragmentManager, "confirmEraseDatabase")
    }

    /**
     * Show confirmation dialog to export database
     */
    fun showConfirmExportDatabaseDialog() {
        val dialogFragment: DialogFragment = ExportPhrasesDialog()
        dialogFragment.show(childFragmentManager, "confirmExportDatabase")
    }

    /**
     * Show confirmation dialog to import database
     */
    fun showConfirmImportDatabaseDialog() {
        val dialogFragment: DialogFragment = ImportPhrasesDialog()
        dialogFragment.show(childFragmentManager, "confirmImportDatabase")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment?) {
        when (dialog) {
            is EraseDatabaseDialog -> {
                settingsViewModel.clearAllPhrases()
                dialog.dismiss()
            }
            is ExportPhrasesDialog -> {
                exportPhrasesToCSVFile()
                dialog.dismiss()
            }
            is ImportPhrasesDialog -> {
                pickCsvToImport()
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment?) {
        when (dialog) {
            is EraseDatabaseDialog,
            is ExportPhrasesDialog,
            is ImportPhrasesDialog -> {
                dialog.dismiss()
            }
        }
    }

    //Results---------------------

    companion object {
        const val REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 0
        const val REQUEST_CODE_GET_CSV_FILE = 1

        // Request code for creating a PDF document.
        const val CREATE_CSV_FILE = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //TODO? Permission has been denied by user
                } else {
                    exportPhrasesToCSVFile()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREATE_CSV_FILE -> {
                // The result data contains a URI for the document or directory that
                // the user selected.
                Log.i("createOpenFileIntent", "createOpenFileIntent1 ${data.toString()}")
                data?.data?.also { uri ->
                    Log.i("createOpenFileIntent", "createOpenFileIntent2")
                    val oStream = requireActivity().contentResolver.openOutputStream(uri)
                    settingsViewModel.exportFromRepositoryToCsv(oStream!!, phrases)
                    Snackbar.make(
                        requireView(),
                        R.string.settings_export_successful,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    val intent = createOpenFileIntent(requireContext(), uri)
                    startActivity(
                        Intent.createChooser(
                            intent,
                            String.format(getString(R.string.settings_view_filename, "phrases.csv"))
                        )
                    )
                    Log.i("createOpenFileIntent", "createOpenFileIntent3")
                }
            }
            REQUEST_CODE_GET_CSV_FILE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val fileUri = data.data
                    if (null != fileUri) {
                        settingsViewModel.importToRepositoryFromCsv(
                            requireContext(),
                            fileUri
                        )
                        Snackbar.make(
                            requireView(),
                            R.string.settings_import_successful,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        //fileUri is null
                        Snackbar.make(
                            requireView(),
                            "${getString(R.string.error_occurred)} [fileUri is null]",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    //NOT FOUND
                    Snackbar.make(
                        requireView(),
                        "${getString(R.string.error_occurred)} [DATA NOT FOUND]",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

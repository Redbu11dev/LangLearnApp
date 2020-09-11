package com.redbu11.langlearnapp.ui.fragments.settings

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseDatabase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.ui.dialogs.ConfirmationDialogFragment
import com.redbu11.langlearnapp.ui.dialogs.EraseDatabaseDialog
import com.redbu11.langlearnapp.utils.MyFileUtils
import java.io.File


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener,
    ConfirmationDialogFragment.ConfirmationDialogListener {

    private lateinit var settingsViewModel: SettingsViewModel
    private val phrases: MutableSet<Phrase> = mutableSetOf()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_pref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dao = PhraseDatabase.getInstance(requireContext().applicationContext).phraseDAO
        val repository = PhraseRepository(dao)
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
        Toast.makeText(context, "$key", Toast.LENGTH_SHORT).show()
        when {
            key.equals("clear_all") -> {
                showConfirmEraseDatabaseDialog()
            }
            key.equals("export_phrases") -> exportPhrasesToCSVFile()
            key.equals("import_phrases") -> pickCsvToImport()
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
        Toast.makeText(context, "$key", Toast.LENGTH_SHORT).show()
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
    private fun createOpenFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

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
            val fileName = "export_phrases_${System.currentTimeMillis() / 1000}.csv"
            val file = MyFileUtils.generateExternalFile(
                requireContext(),
                fileName
            )
            settingsViewModel.exportFromRepositoryToCsv(file, phrases)
            val intent = createOpenFileIntent(requireContext(), file)
            startActivity(Intent.createChooser(intent, String.format(getString(R.string.settings_view_filename, fileName))))
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
        startActivityForResult(chooseFile, REQUEST_CODE_GET_CSV_FILE)
    }

    //Dialogs-----------

    /**
     * Show confirmation dialog to erase database
     */
    fun showConfirmEraseDatabaseDialog() {
        val dialogFragment: DialogFragment = EraseDatabaseDialog()
        dialogFragment.show(childFragmentManager, "confirmEraseDatabase")

        //Usages reminder
//        dialogFragment.setTargetFragment(this, 0)
//        dialogFragment.show(requireActivity().supportFragmentManager, "confirmEraseDatabase")

        //Dismiss reminder
//        public void dismissDialog(){
//            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//            if (prev != null) {
//                DialogFragment df = (DialogFragment) prev;
//                df.dismiss();
//            }
//        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment?) {
        when (dialog) {
            is ConfirmationDialogFragment -> {
                //The dialog is ConfirmationDialogFragment
                settingsViewModel.clearAllPhrases()
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment?) {
        when (dialog) {
            is ConfirmationDialogFragment -> {
                //The dialog is ConfirmationDialogFragment
                dialog.dismiss()
            }
        }
    }

    //Results---------------------

    companion object {
        const val REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 0
        const val REQUEST_CODE_GET_CSV_FILE = 1
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
                    //TODO Permission has been denied by user
                } else {
                    exportPhrasesToCSVFile()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_GET_CSV_FILE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val fileUri = data.data
                    if (null != fileUri) {
                        settingsViewModel.importToRepositoryFromCsv(
                            requireContext(),
                            fileUri
                        )
                    } else {
                        //TODO fileUri is null
                    }
                } else {
                    //TODO NOT FOUND
                }
            }
        }
    }
}

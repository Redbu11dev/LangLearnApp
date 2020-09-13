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

package com.redbu11.langlearnapp.ui.fragments.dashboard

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.redbu11.langlearnapp.MainActivity
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.databinding.FragmentDashboardBinding
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseDatabase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.ui.dialogs.ConfirmationDialogFragment
import com.redbu11.langlearnapp.ui.dialogs.DeletePhraseConfirmationDialog
import com.redbu11.langlearnapp.ui.dialogs.UpdatePhraseConfirmationDialog
import com.redbu11.langlearnapp.utils.SoftUtils


class DashboardFragment : Fragment(), MainActivity.IActivityOnBackPressed, ConfirmationDialogFragment.ConfirmationDialogListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var adapter: PhraseRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        dashboardViewModel =
//                ViewModelProvider(this).get(DashboardViewModel::class.java)

        val dao = PhraseDatabase.getInstance(requireContext().applicationContext).phraseDAO
        val repository = PhraseRepository(dao)
        val factory = DashboardViewModelFactory(requireActivity().application, repository)

        dashboardViewModel =
            ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dashboard, container, false)
        binding.myViewModel = dashboardViewModel
        binding.myFragment = this
        binding.lifecycleOwner = this
        binding.queryInfoDisplayScrollview.visibility = View.GONE

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        dashboardViewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled().let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })

        dashboardViewModel.phraseCreatorContainerVisible.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phraseCreatorContainer.visibility = View.VISIBLE
                binding.fab.visibility = View.GONE
            } else {
                binding.phraseCreatorContainer.visibility = View.GONE
                binding.fab.visibility = View.VISIBLE
                SoftUtils.hideSoftKeyBoard(requireContext(), binding.phraseCreatorContainer)
            }
            TransitionManager.beginDelayedTransition(
                binding.root as ViewGroup,
                ChangeBounds().setDuration(200)
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the options menu from XML
        inflater.inflate(R.menu.options_menu, menu)

        val mSearchItem = menu.findItem(R.id.m_search)
        val mSearchView = mSearchItem.actionView as SearchView
        mSearchView.isIconified = true

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        mSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    mSearchView.clearFocus()
                    mSearchItem.collapseActionView()
                    return true
                }

                override fun onQueryTextChange(query: String): Boolean {
                    if (mSearchView.hasFocus()) {
                        dashboardViewModel.queryString.value = query
                    }
                    return true
                }
            }
        )

        dashboardViewModel.queryString.observe(viewLifecycleOwner, Observer {
            if (TextUtils.isEmpty(it)) {
                mSearchItem.collapseActionView()
                binding.queryInfoDisplayScrollview.visibility = View.GONE
            }
            else {
                binding.queryInfoDisplayScrollview.visibility = View.VISIBLE
            }
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup, AutoTransition().setDuration(100))
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initRecyclerView() {
        binding.phraseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter =
            PhraseRecyclerViewAdapter { selectedItem: Phrase -> listItemClicked(selectedItem) }
        binding.phraseRecyclerView.adapter = adapter
        displayPhrasesList()
    }

    private fun displayPhrasesList() {
        dashboardViewModel.phrases.observe(viewLifecycleOwner, Observer {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(phrase: Phrase) {
        dashboardViewModel.setAndShowInputFormAsUpdate(phrase)
    }

    override fun onBackPressed(): Boolean {
        //Toast.makeText(requireContext(),"onBackPressed",Toast.LENGTH_SHORT).show()
        return if (binding.phraseCreatorContainer.visibility == View.VISIBLE) {
            dashboardViewModel.phraseCreatorContainerVisible.value = false
            true
        } else {
            false
        }
    }

    /**
     * Show confirmation dialog to update phrase info
     */
    fun showConfirmUpdatePhraseDialog() {
        val dialogFragment: DialogFragment = UpdatePhraseConfirmationDialog()
        dialogFragment.show(childFragmentManager, "UpdatePhraseConfirmationDialog")
    }

    /**
     * Show confirmation dialog to delete phrase
     */
    fun showConfirmDeletePhraseDialog() {
        val dialogFragment: DialogFragment = DeletePhraseConfirmationDialog()
        dialogFragment.show(childFragmentManager, "DeletePhraseConfirmationDialog")
    }

    override fun onDialogPositiveClick(dialog: DialogFragment?) {
        when (dialog) {
            is UpdatePhraseConfirmationDialog -> {
                dashboardViewModel.updateCurrentPhrase()
                dialog.dismiss()
            }
            is DeletePhraseConfirmationDialog -> {
                dashboardViewModel.delete()
                dialog.dismiss()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment?) {
        when (dialog) {
            is DialogFragment -> {
                dialog.dismiss()
            }
        }
    }
}

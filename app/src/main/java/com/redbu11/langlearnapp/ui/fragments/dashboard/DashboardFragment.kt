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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.redbu11.langlearnapp.MainActivity
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.databinding.FragmentDashboardBinding
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseDatabase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.utils.SoftUtils

class DashboardFragment : Fragment(), MainActivity.IActivityOnBackPressed {

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

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dashboard, container, false)
        binding.myViewModel = dashboardViewModel
        binding.lifecycleOwner = this

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

        dashboardViewModel.isPhraseCreatorContainerVisible.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.phraseCreatorContainer.visibility = View.VISIBLE
                binding.fab.visibility = View.GONE
            }
            else {
                binding.phraseCreatorContainer.visibility = View.GONE
                binding.fab.visibility = View.VISIBLE
                SoftUtils.hideSoftKeyBoard(requireContext(), binding.phraseCreatorContainer)
            }
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup, ChangeBounds().setDuration(200))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initRecyclerView(){
        binding.phraseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PhraseRecyclerViewAdapter { selectedItem:Phrase->listItemClicked(selectedItem)}
        binding.phraseRecyclerView.adapter = adapter
        displayPhrasesList()
    }

    private fun displayPhrasesList(){
        dashboardViewModel.phrases.observe(viewLifecycleOwner, Observer {
            Log.i("MYTAG",it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(phrase: Phrase){
        //Toast.makeText(requireContext(),"selected phrase is ${phrase.phraseText}",Toast.LENGTH_LONG).show()
        dashboardViewModel.setAndShowInputFormAsUpdate(phrase)
    }

    override fun onBackPressed(): Boolean {
        //Toast.makeText(requireContext(),"onBackPressed",Toast.LENGTH_SHORT).show()
        return if (binding.phraseCreatorContainer.visibility == View.VISIBLE) {
            dashboardViewModel.isPhraseCreatorContainerVisible.value = false
            true
        }
        else {
            false
        }
    }
}

package com.redbu11.langlearnapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.databinding.FragmentDashboardBinding
import com.redbu11.langlearnapp.db.Phrase
import com.redbu11.langlearnapp.db.PhraseDatabase
import com.redbu11.langlearnapp.db.PhraseRepository
import com.redbu11.langlearnapp.utils.SoftUtils

class DashboardFragment : Fragment() {

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

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDashboardBinding.bind(view)
        binding.myViewModel = dashboardViewModel
        binding.lifecycleOwner = this

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
        dashboardViewModel.initUpdateAndDelete(phrase)
    }
}

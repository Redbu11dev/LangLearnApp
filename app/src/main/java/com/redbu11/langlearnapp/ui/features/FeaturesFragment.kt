package com.redbu11.langlearnapp.ui.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.redbu11.langlearnapp.R

class FeaturesFragment : Fragment() {

    private lateinit var featuresViewModel: FeaturesViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        featuresViewModel =
                ViewModelProvider(this).get(FeaturesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_features, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        featuresViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}

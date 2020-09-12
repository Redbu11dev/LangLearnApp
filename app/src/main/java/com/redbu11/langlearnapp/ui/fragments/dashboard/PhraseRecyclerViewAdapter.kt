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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.databinding.PhraseListItemBinding
import com.redbu11.langlearnapp.db.Phrase

class PhraseRecyclerViewAdapter (private val clicklistener:(Phrase)->Unit):RecyclerView.Adapter<MyViewHolder>() {

    private val phrasesList = ArrayList<Phrase>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : PhraseListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.phrase_list_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return phrasesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(phrasesList[position], clicklistener)
    }

    fun setList(phrases: List<Phrase>) {
        phrasesList.clear()
        phrasesList.addAll(phrases)
    }

}

class MyViewHolder(val binding: PhraseListItemBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(phrase: Phrase, clicklistener:(Phrase)->Unit) {
        binding.phraseTitleTextView.text = String.format(binding.phraseTitleTextView.context.getString(R.string.phrase_listItem_phraseLang_title), phrase.phraseLanguage)
        binding.phraseTextView.text = phrase.phraseText
        binding.translationTitleTextView.text = String.format(binding.phraseTitleTextView.context.getString(R.string.phrase_listItem_translationLang_title), phrase.translationLanguage)
        binding.translationTextView.text = phrase.translationText
        binding.phraseListItemLayout.setOnClickListener {
            clicklistener(phrase)
        }
        //binding.cardView.setCardBackgroundColor( Color.parseColor(ConvertUtils.stringToColor(phrase.phraseLanguage)) )
    }
}
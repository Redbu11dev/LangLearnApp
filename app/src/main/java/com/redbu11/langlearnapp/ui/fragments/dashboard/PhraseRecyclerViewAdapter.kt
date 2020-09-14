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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.redbu11.langlearnapp.R
import com.redbu11.langlearnapp.databinding.PhraseListItemBinding
import com.redbu11.langlearnapp.db.Phrase
import java.util.*
import kotlin.collections.ArrayList

class PhraseRecyclerViewAdapter(private val clicklistener: (Phrase) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {

    private val phrasesList = ArrayList<Phrase>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: PhraseListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.phrase_list_item, parent, false)
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
        phrasesList.reverse()
    }

    private var removedPosition: Int = 0
    private lateinit var removedItem: Phrase

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        removedItem = phrasesList[position]
        removedPosition = position

        phrasesList.removeAt(position)
        notifyItemRemoved(position)

        Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
            phrasesList.add(removedPosition, removedItem)
            notifyItemInserted(removedPosition)

        }.show()
    }

}

class MyViewHolder(val binding: PhraseListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(phrase: Phrase, clicklistener: (Phrase) -> Unit) {
        binding.phraseTitleTextView.text =
            String.format(binding.phraseTitleTextView.context.getString(R.string.phrase_listItem_phraseLang_title), phrase.phraseLanguage)
        binding.phraseTextView.text = phrase.phraseText
        binding.translationTitleTextView.text =
            String.format(binding.phraseTitleTextView.context.getString(R.string.phrase_listItem_translationLang_title), phrase.translationLanguage)
        binding.translationTextView.text = phrase.translationText
        binding.notesTitleTextView.text = binding.phraseTitleTextView.context.getString(R.string.phrase_listItem_notes_title)
        binding.notesTextView.text = phrase.notes
        if (TextUtils.isEmpty(binding.notesTextView.text)) {
            binding.notesTitleTextView.visibility = View.GONE
            binding.notesTextView.visibility = View.GONE
        } else {
            binding.notesTitleTextView.visibility = View.VISIBLE
            binding.notesTextView.visibility = View.VISIBLE
        }
        binding.phraseListItemClickLayout.setOnClickListener {
            clicklistener(phrase)
        }
        //binding.cardView.setCardBackgroundColor( Color.parseColor(ConvertUtils.stringToColor(phrase.phraseLanguage)) )
    }
}

class SwipeToDeleteCallback(private val context: Context, private var myAdapter: PhraseRecyclerViewAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private var colorDrawableBackground: ColorDrawable = ColorDrawable(Color.parseColor("#ff0000"))
    private var deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_forever_black_24dp)!!

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
        myAdapter.removeItem(viewHolder.adapterPosition, viewHolder)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
//        val itemView = viewHolder.itemView
//        val iconMarginVertical =
//            (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2
//
//        if (dX > 0) {
//            colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
//            deleteIcon.setBounds(
//                itemView.left + iconMarginVertical,
//                itemView.top + iconMarginVertical,
//                itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
//                itemView.bottom - iconMarginVertical
//            )
//        } else {
//            colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
//            deleteIcon.setBounds(
//                itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
//                itemView.top + iconMarginVertical,
//                itemView.right - iconMarginVertical,
//                itemView.bottom - iconMarginVertical
//            )
//            deleteIcon.level = 0
//        }
//
//        colorDrawableBackground.draw(canvas)
//
//        canvas.save()
//
//        if (dX > 0)
//            canvas.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
//        else
//            canvas.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
//
//        deleteIcon.draw(canvas)
//
//        canvas.restore()

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}
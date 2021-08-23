package com.demo.ws.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.demo.ws.R
import com.demo.ws.models.Word
import com.demo.ws.views.fragments.WordsFragment

class WordsAdapter(private val wordsList: ArrayList<Word>, val callback: WordsFragment)
    : RecyclerView.Adapter<WordsAdapter.WordViewHolder>(), Filterable {

    var wordsFilterList = ArrayList<Word>()

    init {
        wordsFilterList = wordsList
    }

    inner class WordViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.word_item, parent, false)) {

        private var wordName: TextView = itemView.findViewById(R.id.wordNameTxt)
        private var synonymsTable: TableLayout = itemView.findViewById(R.id.tableLayout)
        private val adapterContext = parent.context

        fun bindData(word: Word) {
            wordName.text = word.name
            synonymsTable.removeAllViews()
            val synonyms = word.synonyms
            for (item in synonyms) {
                val name = item.name
                val row = TableRow(adapterContext)
                row.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val textView = TextView(adapterContext)
                textView.text = name
                row.addView(textView)
                synonymsTable.addView(row)
            }
            setSynonymClickListener()
        }

        private fun setSynonymClickListener() {
            itemView.setOnClickListener {
                callback.getSelectedWordPosition(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    fun removeWord(index: Int) {
        wordsFilterList.removeAt(index)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WordViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return wordsFilterList.size
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val reward = wordsFilterList[position]
        holder.bindData(reward)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.length >= 3) {
                    if (charSearch.isEmpty()) {
                        wordsFilterList = wordsList
                    } else {
                        val resultList = ArrayList<Word>()
                        for (row in wordsList) {
                            if (row.name.lowercase().contains(charSearch.lowercase())) {
                                resultList.add(row)
                            }
                        }
                        if (resultList.isEmpty()) {
                            for (word in wordsList) {
                                for (synonym in word.synonyms) {
                                    if (synonym.name.lowercase().contains(charSearch.lowercase()))
                                        resultList.add(word)
                                }
                            }
                        }
                        wordsFilterList = resultList
                    }
                } else {
                    wordsFilterList = wordsList
                }
                val filterResults = FilterResults()
                filterResults.values = wordsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wordsFilterList = results?.values as ArrayList<Word>
                notifyDataSetChanged()
            }

        }
    }
}
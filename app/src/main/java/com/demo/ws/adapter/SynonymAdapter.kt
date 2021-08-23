package com.demo.ws.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.ws.R
import com.demo.ws.listeners.SynonymClickInterface
import com.demo.ws.models.Synonym
import com.demo.ws.views.fragments.AddWordFragment

class SynonymAdapter(
    private val synonymsList: ArrayList<Synonym>,
    val callback: AddWordFragment
) : RecyclerView.Adapter<SynonymAdapter.SynonymViewHolder>() {

    inner class SynonymViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.synonym_item, parent, false)) {

        private val adapterContext = parent.context
        private var synonymName: TextView = itemView.findViewById(R.id.synonymTxt)

        fun bindData(synonym: Synonym) {
            synonymName.text = synonym.name
            setSynonymClickListener()
        }

        private fun setSynonymClickListener() {
            itemView.setOnClickListener {
                callback.getSelectedSynonymPosition(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SynonymViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SynonymViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return synonymsList.size
    }

    override fun onBindViewHolder(holder: SynonymViewHolder, position: Int) {
        val synonym = synonymsList[position]
        holder.bindData(synonym)
    }

}
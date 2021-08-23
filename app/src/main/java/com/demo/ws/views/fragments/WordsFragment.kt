package com.demo.ws.views.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.ws.adapter.WordsAdapter
import com.demo.ws.databinding.WordsFragmentBinding
import com.demo.ws.listeners.SwipeGesture
import com.demo.ws.listeners.WordClickInterface
import com.demo.ws.viewmodel.ScreenType
import com.demo.ws.viewmodel.WordsViewModel
import com.demo.ws.views.activities.MainActivity

class WordsFragment : Fragment(), WordClickInterface {

    private lateinit var binding: WordsFragmentBinding
    private val viewModel: WordsViewModel by viewModels({ requireActivity() })
    private var holderActivity: MainActivity? = null
    private lateinit var wordsAdapter: WordsAdapter

    private val reloadItems = Observer<Boolean> { success ->
        if (success) {
            if (viewModel.words.isEmpty())
                binding.emptyLayout.visibility = View.VISIBLE
            else
                binding.emptyLayout.visibility = View.GONE
            wordsAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WordsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addWordBtn.setOnClickListener {
            viewModel.resetData()
            viewModel.screenType = ScreenType.ADD
            (activity as MainActivity).setFragment(AddWordFragment())
        }
        searchViewClickListener()
        wordsAdapter = WordsAdapter(viewModel.words, this)

        val swipeGesture = object : SwipeGesture() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteWord(viewHolder.adapterPosition)
//                        wordsAdapter.removeWord(viewHolder.adapterPosition)
                    }
                }
                super.onSwiped(viewHolder, direction)
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.recyclerView)
        setRecycler()

        viewModel.notify.observe(viewLifecycleOwner, reloadItems)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        holderActivity = activity as MainActivity
    }

    override fun onResume() {
        super.onResume()
        wordsAdapter.notifyDataSetChanged()
    }

    private fun searchViewClickListener() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                wordsAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun setRecycler() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = wordsAdapter
        }
    }

    override fun getSelectedWordPosition(position: Int) {
        viewModel.selectedWordIndex = position
        viewModel.screenType = ScreenType.EDIT
        (activity as MainActivity).setFragment(AddWordFragment())
    }
}
package com.demo.ws.views.fragments

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.ws.R
import com.demo.ws.adapter.SynonymAdapter
import com.demo.ws.databinding.FragmentAddWordBinding
import com.demo.ws.listeners.SynonymClickInterface
import com.demo.ws.viewmodel.ScreenType
import com.demo.ws.viewmodel.WordsViewModel
import com.demo.ws.views.activities.MainActivity
import androidx.lifecycle.Observer
import androidx.core.content.ContextCompat.getSystemService

import android.view.MotionEvent
import androidx.core.content.ContextCompat


class AddWordFragment : Fragment(), SynonymClickInterface {

    private lateinit var binding: FragmentAddWordBinding
    private val viewModel: WordsViewModel by viewModels({ requireActivity() })
    private var holderActivity: MainActivity? = null
    private lateinit var synonymAdapter: SynonymAdapter

    private val reloadItems = Observer<Boolean> { success ->
        if (success) {
            synonymAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddWordBinding.inflate(layoutInflater)
        setClickListeners()
        viewModel.notify.observe(viewLifecycleOwner, reloadItems)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenType()
        setFocusListener()
        synonymAdapter = SynonymAdapter(viewModel.synonyms, this)
        setRecycler()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        holderActivity = activity as MainActivity
    }

    override fun onDestroy() {
        viewModel.updateWord()
        super.onDestroy()
    }

    private fun setScreenType() {
        if (viewModel.screenType == ScreenType.EDIT) {
            viewModel.getSynonyms()
            binding.wordTxt.text = viewModel.wordName
        }
    }

    private fun setFocusListener() {
        binding.addWordEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                if (binding.addWordEditText.text.isNotEmpty()) {
                    viewModel.wordName = binding.addWordEditText.text.toString()
                    binding.wordTxt.text = binding.addWordEditText.text
                    binding.addWordEditText.text.clear()
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.addSynonymBtn.setOnClickListener {
            if (viewModel.isWordAvailable(binding.addWordEditText.text.toString())) {
                Toast.makeText(activity, resources.getString(R.string.word_exists), Toast.LENGTH_LONG).show()
                binding.addWordEditText.text.clear()
            } else {
                viewModel.screenType = ScreenType.ADD
                if (viewModel.wordName.isEmpty()) {
                    binding.wordTxt.text = binding.addWordEditText.text
                    viewModel.wordName = binding.addWordEditText.text.toString()
                    if (binding.wordTxt.text.isNullOrEmpty()) {
                        Toast.makeText(activity, resources.getString(R.string.error_word), Toast.LENGTH_LONG).show()
                    } else {
                        showDialog(null)
                    }
                } else {
                    if (viewModel.wordName.isNotEmpty())
                        showDialog(null)
                    else {
                        viewModel.wordName = binding.addWordEditText.text.toString()
                        binding.wordTxt.text = binding.addWordEditText.text
                        showDialog(null)
                    }
                }
            }
        }
    }

    private fun setRecycler() {
        binding.recylerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = synonymAdapter
        }
    }

    private fun showDialog(text: String?) {
        binding.addWordEditText.text.clear()
        val builder: AlertDialog.Builder? = activity?.let { AlertDialog.Builder(it) }

        val input = EditText(activity)
        input.imeOptions = EditorInfo.IME_ACTION_DONE
        input.hint = resources.getString(R.string.synonym)
        input.setText(text)
        input.requestFocus()
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder?.setView(input)

        var positiveButton = resources.getString(R.string.add)
        if (viewModel.screenType == ScreenType.EDIT) {
            builder?.setTitle(resources.getString(R.string.edit_synonym))
            positiveButton = resources.getString(R.string.edit)
        } else {
            builder?.setTitle(resources.getString(R.string.add_synonym))
        }
        builder?.setPositiveButton(positiveButton) { _, _ ->
            val synonym = input.text.toString()
            if (viewModel.isSynonymAvailable(synonym)) {
                Toast.makeText(activity, resources.getString(R.string.word_exists), Toast.LENGTH_LONG).show()
            } else {
                if (viewModel.screenType == ScreenType.EDIT) {
                    viewModel.editSynonym(synonym)
                } else {
                    viewModel.addSynonyms(synonym)
                }
            }

            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(input.applicationWindowToken, 0)
        }
        builder?.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(input.applicationWindowToken, 0)
            dialog.cancel()
        }

        builder?.show()
    }

    override fun getSelectedSynonymPosition(position: Int) {
        viewModel.screenType = ScreenType.EDIT
        showAlertDialog(position)
    }

    private fun showAlertDialog(position: Int) {
        val builder = activity?.let { AlertDialog.Builder(it) }
        builder?.setTitle("Options")
        builder?.setPositiveButton(resources.getString(R.string.edit)) { dialog, which ->
            val synonym = viewModel.getSynonym(position)
            viewModel.selectedSynonymIndex = position
            showDialog(synonym)
        }
        builder?.setNeutralButton(resources.getString(R.string.delete)) { dialog, which ->
            viewModel.selectedSynonymIndex = position
            val synonymsArray = viewModel.checkSynonyms()
            if (synonymsArray.size > 1) {
                showAlertDeleteDialog(synonymsArray)
            } else {
                viewModel.deleteSynonym(position)
            }
        }
        builder?.setNegativeButton(resources.getString(R.string.dismiss)) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder?.create()
        dialog?.show()
    }

    private fun showAlertDeleteDialog(idArray: ArrayList<String>) {
        val builder = activity?.let { AlertDialog.Builder(it) }
        builder?.setTitle(resources.getString(R.string.warning))
        builder?.setMessage(resources.getString(R.string.delete_error))
        builder?.setPositiveButton(resources.getString(R.string.delete)) { dialog, which ->
            viewModel.deleteAllLinked(idArray)
            dialog.dismiss()
        }
        builder?.setNegativeButton(resources.getString(R.string.dismiss)) { dialog, which ->
            viewModel.selectedSynonymIndex?.let { viewModel.deleteSynonym(it) }
            dialog.dismiss()
        }
        val dialog = builder?.create()
        dialog?.show()
    }
}
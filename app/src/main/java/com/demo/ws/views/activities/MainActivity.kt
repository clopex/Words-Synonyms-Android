package com.demo.ws.views.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.demo.ws.R
import com.demo.ws.viewmodel.WordsViewModel
import com.demo.ws.views.fragments.WordsFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var viewModel: WordsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WordsViewModel::class.java)
        if (savedInstanceState == null) {
            viewModel.words = viewModel.getDummyData()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WordsFragment>(R.id.fragment_container_view)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            window.currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    fun removeFragment() {
        supportFragmentManager.popBackStack()
    }
}
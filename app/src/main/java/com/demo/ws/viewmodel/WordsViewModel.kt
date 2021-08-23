package com.demo.ws.viewmodel

import android.text.BoringLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.ws.models.Synonym
import com.demo.ws.models.Word

class WordsViewModel : ViewModel() {

    var wordName = ""
    var notify = MutableLiveData<Boolean>()
    var synonyms = ArrayList<Synonym>()
    var words = ArrayList<Word>()
    var selectedWordIndex: Int? = null
    var selectedSynonymIndex: Int? = null
    var screenType: ScreenType = ScreenType.ADD

    private fun addAWord() {
        if (wordName.isEmpty())
            return
        words.add(Word(wordName, synonyms))
    }

    fun updateWord() {
        selectedWordIndex?.let {
            words[it].name = wordName
            words[it].synonyms = synonyms
        } ?: run {
            addAWord()
        }
        notify.postValue(true)
    }

    fun resetData() {
        wordName = ""
        synonyms.clear()
        selectedWordIndex = null
        selectedSynonymIndex = null
    }

    fun getSynonyms() {
        selectedWordIndex?.let { id ->
            val synonymsArray = words[id].synonyms
            wordName = words[id].name
            synonyms = synonymsArray
        }
    }

    fun addSynonyms(text: String) {
        val synonym = Synonym(text)
        synonyms.add(synonym)
        notify.postValue(true)
    }

    fun deleteSynonym(id: Int) {
        synonyms.removeAt(id)
        notify.postValue(true)
    }

    fun deleteWord(id: Int) {
        words.removeAt(id)
        notify.postValue(true)
    }

    fun editSynonym(text: String) {
        selectedSynonymIndex?.let { id ->
            synonyms[id].name = text
            notify.postValue(true)
        }
    }

    fun getSynonym(id: Int): String {
        return synonyms[id].name
    }

    fun isWordAvailable(word: String): Boolean {
        for (item in words) {
            if (item.name == word)
                return true
        }
        return false
    }

    fun isSynonymAvailable(synonym: String): Boolean {
        for (item in synonyms) {
            if (item.name == synonym)
                return true
        }
        return false
    }

    fun checkSynonyms(): ArrayList<String> {
        val tempStringArray = ArrayList<String>()
        selectedSynonymIndex?.let { id ->
            val name = synonyms[id].name
            for (item in words) {
                for (x in item.synonyms) {
                    if (x.name == name) {
                        tempStringArray.add(x.id.toString())
                    }
                }
            }
        }
        return tempStringArray
    }

    fun deleteAllLinked(synonymsIds: ArrayList<String>) {
        for (synonymsId in synonymsIds) {
            for ((index, word) in words.withIndex()) {
                val filteredList = word.synonyms.filter {
                    it.id.toString() != synonymsId
                }
                words[index].synonyms = filteredList as ArrayList<Synonym>
            }
        }
        getSynonyms()
        notify.postValue(true)
    }

    fun getDummyData(): ArrayList<Word> {
        return arrayListOf(
            Word("phone", arrayListOf(
                Synonym("sound"),
                Synonym("speech sound"),
                Synonym("telephone"),
                Synonym("earphone")
            )),
            Word("house", arrayListOf(
                Synonym("domiciliate"),
                Synonym("family"),
                Synonym("home"),
                Synonym("household")
            )),
            Word("travel", arrayListOf(
                Synonym("move"),
                Synonym("journey"),
                Synonym("trip"),
                Synonym("move around")
            ))
        )
    }
}

enum class ScreenType {
    ADD,
    EDIT
}
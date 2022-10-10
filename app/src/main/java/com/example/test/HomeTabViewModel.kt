package com.example.test

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.test.model.SpecialWebtoonItem

class HomeTabViewModel : ViewModel() {
    private val specialWebtoonItems = listOf(
        SpecialWebtoonItem("1", "1", "1"),
        SpecialWebtoonItem("2", "2", "2"),
        SpecialWebtoonItem("3", "3", "3"),
        SpecialWebtoonItem("4", "4", "4"),
        SpecialWebtoonItem("1", "1", "1"),
        SpecialWebtoonItem("2", "2", "2"),
        SpecialWebtoonItem("3", "3", "3"),
        SpecialWebtoonItem("4", "4", "4"),
    )
    private val _specialWebtoonList = specialWebtoonItems.toMutableStateList()
    val specialWebtoonList: List<SpecialWebtoonItem>
        get() = _specialWebtoonList

    fun loadMoreSpecialWebtoon() {
        _specialWebtoonList.addAll(specialWebtoonItems)
    }

    fun loadMoreSpecialWebtoonTop() {
        println(_specialWebtoonList.size)
        _specialWebtoonList.addAll(0, specialWebtoonItems)
    }
}
package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe

interface FilterRepository {

    val recipesData: LiveData<List<Recipe>>
    val categoryData: LiveData<FilterState>
    val queryData: LiveData<String>

    fun save(recipe: Recipe)
    fun update(recipe: Recipe)
    fun insert(recipe: Recipe)
    fun saveCategories(filterState: FilterState)
    fun saveQuery(query: String)
}
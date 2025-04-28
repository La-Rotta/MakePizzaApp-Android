package com.example.makepizza_android.data.repository

import com.example.makepizza_android.data.models.IngredientListModel
import com.example.makepizza_android.data.models.PizzaListModel
import com.example.makepizza_android.data.network.MakePizzaAPIService

class PizzaRepository {
    private val api  = MakePizzaAPIService()

    suspend fun getAllPizzas(): List<PizzaListModel> {
        val response = api.getAllPizzas()
        return response
    }

    suspend fun getAllIngredients(): List<IngredientListModel> {
        val response = api.getAllIngredients()
        return response
    }
}
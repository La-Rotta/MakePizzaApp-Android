package com.example.makepizza_android.data.network

import com.example.makepizza_android.data.models.IngredientListModel
import com.example.makepizza_android.data.models.PizzaListModel
import com.example.makepizza_android.data.models.UserModel
import retrofit2.Response
import retrofit2.http.GET

interface IMakePizzaAPIClient {
    @GET("/users/current")
    suspend fun getCurrentUser(): Response<UserModel?>

    @GET("/pizzas/fetch-all")
    suspend fun getAllPizzas(): Response<List<PizzaListModel>>

    @GET("/ingredients/fetch-all")
    suspend fun getAllIngredients(): Response<List<IngredientListModel>>
}
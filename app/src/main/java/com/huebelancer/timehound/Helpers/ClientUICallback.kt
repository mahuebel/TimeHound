package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO

/**
 * Created by mahuebel on 10/8/17.
 */
interface ClientUICallback {
    fun onLoadedCallback(client: ClientDTO)
    fun onNoDataFound()
    fun onError(error: Exception)
}
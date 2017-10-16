package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO

/**
 * Created by mahuebel on 10/14/17.
 */
interface ClientUpdateListener {
    fun onUpdate(client: ClientDTO)
}
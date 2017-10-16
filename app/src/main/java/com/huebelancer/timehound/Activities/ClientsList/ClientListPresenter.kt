package com.huebelancer.timehound.Activities.ClientsList

import android.util.Log
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.ModelLayer

/**
 * Created by mahuebel on 9/4/17.
 */
class ClientListPresenter(private val modelLayer: ModelLayer) {

    fun loadData(callback: ModelLayer.RealmLoadCallback, showHidden: Boolean) {
        modelLayer.loadClients(callback, showHidden)
    }

    fun addClient(clientDTO: ClientDTO, callback: ModelLayer.RealmLoadCallback, showHidden: Boolean) {
        modelLayer.addClient(clientDTO, callback, showHidden)
        Log.d(TAG, "adding client//////////////")
    }

    companion object {
        val TAG = this::class.java.simpleName
    }

}
package com.huebelancer.timehound.ModelLayer

import android.util.Log
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.ModelLayer.Database.DataLayer
import java.util.*

/**
 * Created by mahuebel on 9/4/17.
 */
class ModelLayer(private val dataLayer: DataLayer) {

    interface RealmLoadCallback {
        fun onLoadedCallback(clients: MutableList<ClientDTO>)
        fun onLoadedCallback(client: ClientDTO)
        fun onNoDataFound()
        fun onError(error: Exception)
    }

    fun loadClients(callback: RealmLoadCallback, showHidden: Boolean) {
        try {
            dataLayer.loadClients(callback, showHidden)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

    fun loadClient(clientName: String, callback: RealmLoadCallback) {
        try {
            dataLayer.loadClient(clientName, callback)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

    fun toggleClientClock(clientName: String, callback: RealmLoadCallback?) {
        try {
            dataLayer.toggleClientClock(clientName, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addClient(clientDTO: ClientDTO, callback: RealmLoadCallback, showHidden: Boolean) {
        try {
            dataLayer.addClient(showHidden, clientDTO, callback)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
            callback.onError(e)
        }
    }

    fun addClientNote(client: String, text: String, callback: RealmLoadCallback) {
        try {
            dataLayer.addClientNote(client, text, callback)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

    companion object {
        val TAG = this::class.java.simpleName
    }

    fun billLastPeriod(clientName: String, callback: RealmLoadCallback) {
        try {
            dataLayer.billLastPeriod(clientName, callback)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

    fun editEvent(event: EventDTO, date: Date, callback: RealmLoadCallback) {
        try {
            dataLayer.editEvent(event, date, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun editNote(client: ClientDTO?, note: NoteDTO, newText: String, callback: RealmLoadCallback) {
        try {
            dataLayer.editNote(client!!, note, newText, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleClientVisibility(clientName: String, callback: RealmLoadCallback) {
        try {
            dataLayer.toggleClientVisibility(clientName, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
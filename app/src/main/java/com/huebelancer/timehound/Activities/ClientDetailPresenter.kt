package com.huebelancer.timehound.Activities

import android.content.Context
import android.os.Handler
import android.support.v7.app.AlertDialog
import com.huebelancer.timehound.Helpers.ClientUICallback
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.ModelLayer

/**
 * Created by mahuebel on 10/14/17.
 */
class ClientDetailPresenter(val activity: ClientDetailActivity, private val modelLayer: ModelLayer) : ModelLayer.RealmLoadCallback  {

    lateinit var clientName: String
    private var clockHandler: Handler? = null
    lateinit var clockRunnable: Runnable

    var isOnTheClock = false
    private var uiCallback: ClientUICallback = activity

    private lateinit var clients: MutableList<ClientDTO>

    init {
        clockRunnable = Runnable {
            if (isOnTheClock) {
                loadData()
                clockHandler?.postDelayed(clockRunnable, 36000)
            } else {
                clockHandler = null
            }
        }

    }


    fun loadData() {
        modelLayer.loadClient(clientName, this)
        modelLayer.loadClients(this, false)
    }




    fun toggleClock(context: Context) {
        val otherClient = isOtherClientOnTheClock()

        if (otherClient != null) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure?")
                    .setMessage("Clocking in for $clientName will take ${otherClient.name} off the clock.")
                    .setPositiveButton("Yes", {view, position ->
                        modelLayer.toggleClientClock(otherClient.name!!, null)
                        activity.stopNotification(otherClient.name!!)
                        modelLayer.toggleClientClock(clientName, this)
                    })
                    .setNegativeButton("Cancel", null)
            builder.create().show()
        } else {
            modelLayer.toggleClientClock(clientName, this)
        }

    }

    private fun isOtherClientOnTheClock() : ClientDTO? {
        clients.forEach { client ->
            if (client.isOnTheClock() && client.name != clientName)
                return client
        }

        return null
    }

    private fun startChronometer() {
        isOnTheClock = true
        if(clockHandler == null) {
            clockHandler = Handler()

            clockHandler?.postDelayed(clockRunnable, 36000)
        }
    }

    private fun stopChronometer() {
        isOnTheClock = false
    }


    override fun onLoadedCallback(clients: MutableList<ClientDTO>) {
        this.clients = clients
    }

    override fun onLoadedCallback(client: ClientDTO) {
        uiCallback.onLoadedCallback(client)

        if (client.isOnTheClock())
            startChronometer()
        else
            stopChronometer()
    }

    override fun onNoDataFound() {
        uiCallback.onNoDataFound()
    }

    override fun onError(error: Exception) {
        uiCallback.onError(error)
    }

    fun destroy() {
        isOnTheClock = false
        clockHandler = null
    }

    fun toggleClientVisibility() {
        modelLayer.toggleClientVisibility(clientName, this)
    }


}
package com.huebelancer.timehound.Activities.ClientsList

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 9/4/17.
 */
class ClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var context : Context
    var name : TextView
    var icon : ImageView

    init {
        context = view.context
        name = view.findViewById(R.id.client_text)
        icon = view.findViewById(R.id.client_icon)
    }

    fun configureWith(client: ClientDTO, position: Int) {
        name.text = client.name

        if (client.isOnTheClock()) {
            icon.visibility = View.VISIBLE
        } else {
            icon.visibility = View.GONE
        }
    }
}
package com.huebelancer.timehound.Activities.ClientsList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.huebelancer.timehound.Helpers.CustomItemClickListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 9/4/17.
 */
class ClientViewAdapter(
        var clients: MutableList<ClientDTO>,
        var listener: CustomItemClickListener
    ) : RecyclerView.Adapter<ClientViewHolder>() {

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.configureWith(client, position)
    }

    override fun getItemCount(): Int {
        return clients.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientViewHolder {
        val clientView = LayoutInflater.from(parent?.context).inflate(R.layout.item_client, parent, false)

        val holder = ClientViewHolder(clientView)

        clientView.setOnClickListener({
            listener.onItemClick(clientView, holder.adapterPosition)
        })

        return holder
    }

}
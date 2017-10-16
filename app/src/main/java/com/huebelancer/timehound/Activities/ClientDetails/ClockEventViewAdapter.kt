package com.huebelancer.timehound.Activities.ClientDetails

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO

/**
 * Created by mahuebel on 9/4/17.
 */
class ClockEventViewAdapter(var events: MutableList<EventDTO>?) : RecyclerView.Adapter<ClockEventHolder>() {
    override fun getItemCount(): Int {
        return if (events != null) {
            events!!.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: ClockEventHolder?, position: Int) {
        val event: EventDTO = events?.get(position)!!
        holder?.configureWith(event, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClockEventHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
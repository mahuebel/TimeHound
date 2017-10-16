package com.huebelancer.timehound.Activities.ClientDetails

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.huebelancer.timehound.Helpers.ClockEventListener
import com.huebelancer.timehound.ModelLayer.Database.Models.Day
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 9/29/17.
 */
class WorkDayViewAdapter(var days: MutableList<Day>?, val listener: ClockEventListener) : RecyclerView.Adapter<WorkDayViewHolder>() {
    override fun onBindViewHolder(holder: WorkDayViewHolder?, position: Int) {
        val day = days!![position]
        holder?.configureWith(day)
    }

    override fun getItemCount(): Int {
        return days?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): WorkDayViewHolder {
        val dayView = LayoutInflater.from(parent?.context).inflate(R.layout.item_day, parent, false)

        val holder = WorkDayViewHolder(dayView, listener)

        return holder
    }

}
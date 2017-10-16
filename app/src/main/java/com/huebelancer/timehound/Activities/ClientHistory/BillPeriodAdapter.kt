package com.huebelancer.timehound.Activities.ClientHistory

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.huebelancer.timehound.Helpers.ClockEventListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.BillPeriodDTO
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 10/8/17.
 */
class BillPeriodAdapter(var periods: MutableList<BillPeriodDTO>?, val listener: ClockEventListener) : RecyclerView.Adapter<BillPeriodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BillPeriodHolder {
        val periodView = LayoutInflater.from(parent?.context).inflate(R.layout.item_period, parent, false)

        val holder = BillPeriodHolder(periodView, listener)
        return holder
    }

    override fun onBindViewHolder(holder: BillPeriodHolder?, position: Int) {
        val period = periods!![position]
        holder?.configureWith(period)
    }

    override fun getItemCount(): Int {
        return periods?.size ?: 0
    }
}
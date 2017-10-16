package com.huebelancer.timehound.Activities.ClientHistory

import android.app.TimePickerDialog
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.huebelancer.timehound.Helpers.ClockEventListener
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.DTOs.BillPeriodDTO
import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import com.huebelancer.timehound.R
import java.util.*

/**
 * Created by mahuebel on 10/8/17.
 */
class BillPeriodHolder(view: View, val listener: ClockEventListener) : RecyclerView.ViewHolder(view) {

    var context: Context = view.context
    private val periodStart: TextView = view.findViewById(R.id.periodStart)
    private val periodEnd: TextView = view.findViewById(R.id.periodEnd)
    private val totalHours: TextView = view.findViewById(R.id.periodHours)
    private val clockIns: TextView = view.findViewById(R.id.clockIns)
    private val clockOuts: TextView = view.findViewById(R.id.clockOuts)
    private val eventContainer: LinearLayout = view.findViewById(R.id.eventContainer)


    fun configureWith(period: BillPeriodDTO) {
        val format = "MMM dd, yy - hh:mma"
        periodStart.text = Helpers.displayDateFormat(period.events[0].time!!, format)
        periodEnd.text = Helpers.displayDateFormat(period.lastClockEvent().time!!, format)
        totalHours.text = "${Math.round(period.hoursWorked() * 100) / 100.0}"
        clockIns.text = "${period.clockInCount()}"
        clockOuts.text = "${period.clockOutCount()}"

        Log.i(TAG, period.spans().toString())
        period.spans().forEachIndexed({ index, span ->
            val clockEvent = LayoutInflater.from(context).inflate(R.layout.item_clock_event, eventContainer, false)
            val dateView = clockEvent.findViewById<TextView>(R.id.clockDate)
            val clockIcon = clockEvent.findViewById<ImageView>(R.id.clockIcon)
            clockEvent.findViewById<ImageButton>(R.id.imageButton).setOnClickListener({view ->
                listener.onSpanClick(span)
            })
            dateView.text = span.text()
            clockIcon.setImageResource(span.icon())
            Log.d(Span.TAG, "starthour: ${span.startHour()}, endHour ${span.endHour()}")
            eventContainer.addView(clockEvent)
        })

        Log.i(TAG, "${eventContainer.childCount} children")
    }



    companion object {
        val TAG = BillPeriodHolder::class.java.simpleName
    }
}
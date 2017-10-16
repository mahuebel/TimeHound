package com.huebelancer.timehound.Activities.ClientDetails

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.lzyzsd.circleprogress.ArcProgress
import com.huebelancer.timehound.Helpers.ClockEventListener
import com.huebelancer.timehound.ModelLayer.Database.Models.Day
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 9/29/17.
 */
class WorkDayViewHolder(val view: View, val listener: ClockEventListener) : RecyclerView.ViewHolder(view) {
    var context : Context
    var progressBar: ArcProgress
    var outCount : TextView
    var inCount : TextView
    var dayLabel : TextView
    var cardView : CardView
    var eventsContainer: LinearLayout

    init {
        context = view.context
        progressBar = view.findViewById(R.id.arcProgress)
        outCount = view.findViewById(R.id.outCount)
        inCount = view.findViewById(R.id.inCount)
        dayLabel = view.findViewById(R.id.dayLabel)
        cardView = view.findViewById(R.id.dayCard)
        eventsContainer = view.findViewById(R.id.eventContainer)

        progressBar.max = 24
//        cardView.setBackgroundResource(R.drawable.primary_gradient)
    }

    fun configureWith(day: Day) {

        Log.i(TAG, "Configuring WorkDayView")
        view.setOnClickListener({v ->
            when(eventsContainer.visibility) {
                View.GONE -> {
                    eventsContainer.visibility = View.VISIBLE
                    day.spans().forEach({ span ->
                        val clockEvent = LayoutInflater.from(context).inflate(R.layout.item_clock_event, eventsContainer, false)
                        val dateView = clockEvent.findViewById<TextView>(R.id.clockDate)
                        val clockIcon = clockEvent.findViewById<ImageView>(R.id.clockIcon)
                        dateView.text = span.text()
                        clockIcon.setImageResource(span.icon())
                        clockEvent.findViewById<ImageButton>(R.id.imageButton).setOnClickListener({ view ->
                            listener.onSpanClick(span)
                        })
                        eventsContainer.addView(clockEvent)
                    })
                }
                View.VISIBLE -> {
                    eventsContainer.removeAllViews()
                    eventsContainer.visibility = View.GONE
                }
            }

        })

        val count = day.clockInCount()
        inCount.text = count.toString()
        outCount.text = day.clockOutCount(count).toString()

        val hoursWorked = day.hoursWorked()

        progressBar.progress = Math.floor(hoursWorked).toInt()
        val remainder = Math.round((hoursWorked - progressBar.progress) * 100)
        val remainderText = if (remainder < 10) "0$remainder" else remainder

        progressBar.suffixText = ".$remainderText"

        dayLabel.text = day.getDayName()

        Log.d(TAG, "${Math.floor(hoursWorked).toInt()} Hours Worked: ${day.hoursWorked()}, ${hoursWorked}, or ${(Math.round(hoursWorked * 100) / 100.0)} \nDay Name: ${day.getDayName()}")
    }

    companion object {
        val TAG = WorkDayViewHolder::class.java.simpleName
    }
}
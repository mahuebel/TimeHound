package com.huebelancer.timehound.Helpers

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import com.huebelancer.timehound.ModelLayer.Enums.EventType
import com.huebelancer.timehound.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mahuebel on 9/4/17.
 */
class Helpers {
    companion object {

        val TAG = Helpers::class.java.simpleName

        val hourInMillis: Double = 1000 * 60 * 60.0

        //region EventType helpers

        fun eventTypeToString(type: EventType) : String {
            return when (type) {
                EventType.Clock_In -> {
                    "in"
                }
                EventType.Clock_Out -> {
                    "out"
                }
                else -> {
                    ""
                }
            }
        }

        fun stringToEventType(type: String?) : EventType? {
            return when (type) {
                "in" -> {
                    EventType.Clock_In
                }
                "out" -> {
                    EventType.Clock_Out
                }
                else -> {
                    null
                }
            }
        }

        //endregion

        //region Fragment Helpers

        fun addFragmentToActivity(manager: FragmentManager, fragment: Fragment, frameId: Int) {
//            fragment.enterTransition = Fade()
//            fragment.exitTransition = Fade()

            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            transaction.add(frameId, fragment)
            transaction.commit()
        }

        fun replaceFragmentInActivity(manager: FragmentManager, fragment: Fragment, frameId: Int, clearClient: Boolean) {
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.enterTransition = Slide()
                fragment.exitTransition = Slide()
            } else {
                fragment.enterTransition = Fade()
                fragment.exitTransition = Fade()
            }
*/



            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
            transaction.replace(frameId, fragment)
            if (!clearClient)
                transaction.addToBackStack(null)
            transaction.commit()
        }

        //endregion

        //region GradientDrawable Helper

        fun getGradientDrawable(intArray: IntArray) : GradientDrawable {
            val gd = GradientDrawable(
                    GradientDrawable.Orientation.BL_TR,
                    intArray
            )
            gd.cornerRadius = 0f

            return gd
        }

        //endregion


        //region Time Span Helpers

        fun round(number: Double) : Double {
            return Math.round(number * 100) / 100.0
        }


        //endregion


        //region ClockEvent Helpers

        fun hoursWorkedInEvents(events: MutableList<EventDTO>, isMeasuredInDays: Boolean) : Double {
            //this Long value will the be sum of the event durations in milliseconds
            val hourInMillis : Double = 1000.0 * 60.0 * 60.0
            var durationInMillis: Long = 0

            var clockInDate : Long? = null
            val cal = Calendar.getInstance()
            cal.time = midnightOfDay(Date())

            events.forEachIndexed { index, event ->
                    if (event.type == EventType.Clock_In) {
                        clockInDate = event.time!!.time

                        if (index == events.lastIndex ) {
                            if (isMeasuredInDays) {
                                //this is the last one and it's a clock in
                                //unless today is the 'day', this baby is crankin
                                //til midnight

                                cal.time = event.time!!
                                val eventDayOfyear = cal[Calendar.DAY_OF_YEAR]
                                val eventYear = cal[Calendar.YEAR]

                                val today = Calendar.getInstance()

                                durationInMillis += if (eventYear == today[Calendar.YEAR] && eventDayOfyear == today[Calendar.DAY_OF_YEAR]) {
                                    //this is today, so we track up to now.
                                    today.timeInMillis - clockInDate!!
                                } else {
                                    //this is not today, so we track to midnight
                                    cal.add(Calendar.DAY_OF_YEAR, 1)
                                    cal.time = midnightOfDay(cal.time)
                                    cal.timeInMillis - clockInDate!!
                                }
                            } else {
                                //This is when we're counting hours in Periods, not days
                                //so this will be up to the current 'now' time.
                                val today = Calendar.getInstance()
                                durationInMillis += (today.timeInMillis - clockInDate!!)
                            }

                        }
                    } else {
                        //it's a clock out, baby!
                        if (clockInDate == null) {
                            //the first event of the day is a clock out,
                            //so we were on the clock today since midnight

                            //This should never apply to period hour counting
                            cal.time = midnightOfDay(event.time!!)

                            //set hours to the duration between
                            durationInMillis = event.time!!.time - cal.timeInMillis
                        } else {
                            durationInMillis += (event.time!!.time - clockInDate!!)
                        }
                    }
            }



            return durationInMillis / hourInMillis
        }

        private fun midnightOfDay(date: Date): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0

            return cal.time
        }


        fun displayDateFormat(date: Date, format: String = "E h:mm:sa"): String {
            return SimpleDateFormat(format, Locale.US).format(date)
        }



        fun getTimeSpans(events: MutableList<EventDTO>, format: String): MutableList<Span> {
            val list = mutableListOf<Span>()

            var lastEvent: EventDTO? = null
            val cal = Calendar.getInstance()

            events.forEachIndexed { index, event ->
                when (event.type) {
                    EventType.Clock_In -> {
                        lastEvent = event

                        if (index == events.lastIndex) {
                            list.add(Span(event, EventDTO(EventType.Clock_Out, Date(), ""),format))
                        }
                    }
                    else -> {
                        if (index == 0) {
                            cal.time = event.time
                            cal[Calendar.HOUR_OF_DAY] = 0
                            cal[Calendar.MINUTE] = 0
                            cal[Calendar.SECOND] = 0
                            lastEvent = EventDTO(EventType.Clock_In, cal.time, "")
                        }
                        list.add(Span(lastEvent!!, event, format))
                    }
                }
            }

            list.sortBy {
                it.start.time
            }

            return list
        }


        fun handleSpanEdit(context: Context, span: Span, format: String, callback: OnClockEditDone) {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_span_edit, null, false)
            val currentStart: TextView = view.findViewById(R.id.currentStart)
            val currentEnd: TextView = view.findViewById(R.id.currentEnd)

            val btnStart: Button = view.findViewById(R.id.startButton)
            val btnEnd: Button = view.findViewById(R.id.endButton)

            val startCal = Calendar.getInstance()
            val endCal = Calendar.getInstance()
            startCal.time = span.start.time
            endCal.time = span.end.time


            currentStart.text = Helpers.displayDateFormat(span.start.time!!, format)
            currentEnd.text = Helpers.displayDateFormat(span.end.time!!, format)

            btnStart.setOnClickListener({view ->
                Log.d(TAG, "start HOD: ${startCal[Calendar.HOUR_OF_DAY]}, start Min: ${startCal[Calendar.MINUTE]}")


                val dateDialog = DatePickerDialog(
                        context,
                        {dialog, selectedYear, selectedMonth, selectedDay ->
                            startCal[Calendar.YEAR] = selectedYear
                            startCal[Calendar.MONTH] = selectedMonth
                            startCal[Calendar.DAY_OF_MONTH] = selectedDay
                        },
                        startCal[Calendar.YEAR],
                        startCal[Calendar.MONTH],
                        startCal[Calendar.DAY_OF_MONTH]
                )
                dateDialog.setOnDismissListener({dialog ->
                    val timeDialog = TimePickerDialog(
                            context,
                            {timePicker,selectedHour, selectedMinute ->
                                startCal[Calendar.HOUR_OF_DAY] = selectedHour
                                startCal[Calendar.MINUTE] = selectedMinute
                                currentStart.text = displayDateFormat(startCal.time, format)
                            },
                            startCal[Calendar.HOUR_OF_DAY],
                            startCal[Calendar.MINUTE],
                            false
                    )
                    timeDialog.show()
                })
                dateDialog.show()
            })
            btnEnd.setOnClickListener({view ->

                val dateDialog = DatePickerDialog(
                        context,
                        {dialog, selectedYear, selectedMonth, selectedDay ->
                            endCal[Calendar.YEAR] = selectedYear
                            endCal[Calendar.MONTH] = selectedMonth
                            endCal[Calendar.DAY_OF_MONTH] = selectedDay
                        },
                        endCal[Calendar.YEAR],
                        endCal[Calendar.MONTH],
                        endCal[Calendar.DAY_OF_MONTH]
                )
                dateDialog.setOnDismissListener({dialog ->
                    val timeDialog = TimePickerDialog(
                            context,
                            {timePicker,selectedHour, selectedMinute ->
                                endCal[Calendar.HOUR_OF_DAY] = selectedHour
                                endCal[Calendar.MINUTE] = selectedMinute
                                currentEnd.text = displayDateFormat(endCal.time, format)
                            },
                            endCal[Calendar.HOUR_OF_DAY],
                            endCal[Calendar.MINUTE],
                            false
                    )
                    timeDialog.show()
                })
                dateDialog.show()

            })

            val builder = AlertDialog.Builder(context)
            builder.setView(view)
                    .setPositiveButton("Save", {view, position ->
                        callback.onClockEditDone(span, startCal.time, endCal.time)
                    })
                    .setNegativeButton("Cancel", null)
            builder.create().show()
        }


        //endregion

    }
}
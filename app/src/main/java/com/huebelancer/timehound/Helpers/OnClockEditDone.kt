package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import java.util.*

/**
 * Created by mahuebel on 10/15/17.
 */
interface OnClockEditDone {
    fun onClockEditDone(span: Span, start: Date, end: Date)
}
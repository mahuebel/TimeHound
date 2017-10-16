package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.Models.Span

/**
 * Created by mahuebel on 10/15/17.
 */
interface ClockEventListener {
    fun onSpanClick(span: Span)
}
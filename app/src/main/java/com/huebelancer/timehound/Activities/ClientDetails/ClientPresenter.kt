package com.huebelancer.timehound.Activities.ClientDetails

import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.ModelLayer.Database.DTOs.BillPeriodDTO
import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import com.huebelancer.timehound.ModelLayer.ModelLayer
import java.util.*

/**
 * Created by mahuebel on 9/4/17.
 */
class ClientPresenter(val fragment: ClientFragment, private val modelLayer: ModelLayer) {

    lateinit var clientName: String

    val callback: ModelLayer.RealmLoadCallback = (fragment.activity as ClientDetailActivity).getRealmLoadCallback()

    fun bill(lastOpenPeriod: BillPeriodDTO) {
        if (!lastOpenPeriod.isBilled && lastOpenPeriod.events.size > 0)
            modelLayer.billLastPeriod(clientName, (fragment.activity as ClientDetailActivity).getRealmLoadCallback())
    }

    fun editSpan(span: Span, start: Date, end: Date) {
        val startEvent = span.start
        val endEvent = span.end

        if (startEvent.clientId != null && startEvent.clientId != "" && startEvent.time != start) {
            modelLayer.editEvent(startEvent, start, callback)
        }

        if (endEvent.clientId !=  null && endEvent.clientId != "" && endEvent.time != end) {
            modelLayer.editEvent(endEvent, end, callback)
        }
    }

}
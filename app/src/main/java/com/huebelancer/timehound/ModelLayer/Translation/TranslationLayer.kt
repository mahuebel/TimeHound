package com.huebelancer.timehound.ModelLayer.Translation

import android.util.Log
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.DTOs.BillPeriodDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.ModelLayer.Database.DataLayer
import com.huebelancer.timehound.ModelLayer.Database.Models.BillPeriod
import com.huebelancer.timehound.ModelLayer.Database.Models.Client
import com.huebelancer.timehound.ModelLayer.Database.Models.ClockEvent
import com.huebelancer.timehound.ModelLayer.Database.Models.Note
import io.realm.Realm

/**
 * Created by mahuebel on 9/6/17.
 */
class TranslationLayer {
    fun translate(/*realm: Realm, */clients: MutableList<Client>) : MutableList<ClientDTO> {
        val list = mutableListOf<ClientDTO>()

        clients.forEach { client ->
            list.add(
                    ClientDTO(
                            client.name,
                            client.hidden,
                            translatePeriods(
                                    client.periods
                            ),
                            translateNotes(
                                    client.notes
                            )
                    )
            )

            Log.d(TAG, list[list.lastIndex].toString())
        }

        clients.sortBy { client ->
            client.name
        }

        return list
    }

    fun translate(realm: Realm, client: Client) : ClientDTO {
        return ClientDTO(
                client.name,
                client.hidden,
                translatePeriods(
                        DataLayer.loadPeriodsFromRealm(client.name!!, realm)
                ),
                translateNotes(
                        client.notes
                )
        )
    }


    private fun translateEvents(events: MutableList<ClockEvent>) : MutableList<EventDTO> {
        val list = mutableListOf<EventDTO>()

        events.forEach { event ->
            list.add(
                    EventDTO(
                            Helpers.stringToEventType(event.type)!!,
                            event.time,
                            event.client
                    )
            )
        }

        list.sortBy { event ->
            event.time
        }

        return list
    }

    private fun translatePeriods(periods: MutableList<BillPeriod>) : MutableList<BillPeriodDTO> {
        val list = mutableListOf<BillPeriodDTO>()

        periods.forEach { period ->
            list.add(
                    BillPeriodDTO(
                            period.client!!,
                            period.isBilled,
                            translateEvents(
                                    period.clockEvents
                            )
                    )
            )
        }

        val message = if (list.size > 0) list[list.lastIndex].toString() else "No periods."

        Log.d(TAG, message)

        return list
    }

    private fun translateNotes(notes: MutableList<Note>) : MutableList<NoteDTO> {
        val list = mutableListOf<NoteDTO>()

        notes.forEach { note ->
            list.add(
                    NoteDTO(
                            note.date,
                            note.text
                    )
            )
        }

        val message = if (list.size > 0) list[list.lastIndex].toString() else "No notes."

        Log.d(TAG, message)

        return list
    }

    companion object {
        val TAG = TranslationLayer::class.java.simpleName
    }
}
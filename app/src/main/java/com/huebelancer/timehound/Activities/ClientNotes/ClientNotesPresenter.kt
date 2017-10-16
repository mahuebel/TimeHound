package com.huebelancer.timehound.Activities.ClientNotes

import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.ModelLayer.ModelLayer

/**
 * Created by mahuebel on 10/8/17.
 */
class ClientNotesPresenter(val fragment: ClientNotesFragment, val modelLayer: ModelLayer) {

    lateinit var clientName: String

    val callback = (fragment.activity as ClientDetailActivity).getRealmLoadCallback()

    fun addNote(text: String) {
        modelLayer.addClientNote(clientName, text, (fragment.activity as ClientDetailActivity).getRealmLoadCallback())
    }

    fun destroy() {

    }

    fun editNote(client: ClientDTO?, note: NoteDTO, newText: String) {
        modelLayer.editNote(client, note, newText, callback)
    }

}
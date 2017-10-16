package com.huebelancer.timehound.Activities.ClientNotes

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.huebelancer.timehound.Helpers.NoteClickListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 10/8/17.
 */
class ClientNotesAdapter(var notes: MutableList<NoteDTO>?, var listener: NoteClickListener) : RecyclerView.Adapter<ClientNotesHolder>() {
    override fun getItemCount(): Int {
        return notes?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientNotesHolder {
        val noteView = LayoutInflater.from(parent?.context).inflate(R.layout.item_note, parent, false)

        val holder = ClientNotesHolder(noteView, listener)
        return holder
    }

    override fun onBindViewHolder(holder: ClientNotesHolder?, position: Int) {
        val note = notes!![position]
        holder?.configureWith(note)
    }

}
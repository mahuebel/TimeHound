package com.huebelancer.timehound.Activities.ClientNotes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.Helpers.NoteClickListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 10/8/17.
 */
class ClientNotesHolder(view: View, var listener: NoteClickListener) : RecyclerView.ViewHolder(view) {
    var context: Context = view.context
    private var dateView: TextView = view.findViewById(R.id.date)
    private var noteView: TextView = view.findViewById(R.id.text)
    private var iconBtn: ImageButton = view.findViewById(R.id.editNoteButton)

    fun configureWith(note: NoteDTO) {
        dateView.text = Helpers.displayDateFormat(note.date, "MMM dd, yy - hh:mma")
        noteView.text = note.text
        iconBtn.setOnClickListener({v ->
            listener.onNoteClick(note)
        })
    }

    companion object {
        val TAG = ClientNotesHolder::class.java.simpleName
    }
}
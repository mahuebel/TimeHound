package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO

/**
 * Created by mahuebel on 10/15/17.
 */
interface OnNoteEditDone {
    fun onNoteEditeDone(note: NoteDTO, newText: String)
}
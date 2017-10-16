package com.huebelancer.timehound.Helpers

import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO

/**
 * Created by mahuebel on 10/15/17.
 */
interface NoteClickListener {
    fun onNoteClick(note: NoteDTO)
}
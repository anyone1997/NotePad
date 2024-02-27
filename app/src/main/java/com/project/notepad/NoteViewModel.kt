/* ViewModel would interact with the SQLite database via a repository */

package com.project.notepad

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class NoteViewModel(private val onNotesChanged: (List<Note>) -> Unit, private val repository: NoteRepository) : ViewModel() {

    var selectedNoteId by mutableStateOf(-1)
    var isDialogVisible by mutableStateOf(false)
    var newNoteName by mutableStateOf("")
    var newNoteDescription by mutableStateOf("")
    var existingNoteName by mutableStateOf("")
    var existingNoteDescription by mutableStateOf("")

    init {
        onNotesChanged(getAllNotes())
    }

    private fun getAllNotes(): List<Note> {
        return repository.getAllNotes()
    }

    fun addNewNote(note: Note) {
        viewModelScope.launch {
            repository.addNewNote(note)
            onNotesChanged(getAllNotes())
        }
    }

    fun removeNoteById(noteId: Int) {
        viewModelScope.launch {
            repository.removeNoteById(noteId)
            onNotesChanged(getAllNotes())
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            onNotesChanged(getAllNotes())
        }
    }
}
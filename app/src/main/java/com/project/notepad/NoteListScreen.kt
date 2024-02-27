package com.project.notepad

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

open class NoteListScreen {
    @Composable
    fun NotePad(context: Context) {
        CompositionLocalProvider {
            UpdatableList(context = context)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun UpdatableList(context: Context) {

        var notes by remember { mutableStateOf(emptyList<Note>()) }

        val noteViewModel = remember {
            NoteViewModel({ updateNotes ->
                notes = updateNotes
            }, NoteRepository(dbHelper = DbHelper(context)))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = noteViewModel.newNoteName,
                onValueChange = { noteViewModel.newNoteName = it },
                label = { Text(text = "Add note's title") },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = noteViewModel.newNoteDescription,
                onValueChange = { noteViewModel.newNoteDescription = it },
                label = { Text(text = "Add note's context") },
                textStyle = TextStyle(fontFamily = FontFamily.Serif)
            )

            Button(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                onClick = {
                    // this should be in the NoteViewModel
                    if (noteViewModel.newNoteName.isNotBlank() && noteViewModel.newNoteDescription.isNotBlank()) {
                        noteViewModel.addNewNote(
                            Note(
                                0,
                                noteViewModel.newNoteName,
                                noteViewModel.newNoteDescription
                            )
                        ) // SQLite will auto-increment the ID
                        Toast.makeText(context, "Note being saved", Toast.LENGTH_SHORT)
                            .show()
                        // resetting note's name and title
                        noteViewModel.newNoteName = ""
                        noteViewModel.newNoteDescription = ""
                    } else {
                        Toast.makeText(
                            context,
                            "Note's title or context shouldn't be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                Text(text = "Save note")
            }

            LazyColumn {
                items(notes) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = item.noteName,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = item.noteDescription
                        )
                        Row {
                            Button(
                                onClick = {
                                    noteViewModel.selectedNoteId = item.id
                                    noteViewModel.existingNoteName = item.noteName
                                    noteViewModel.existingNoteDescription = item.noteDescription
                                    noteViewModel.isDialogVisible = true
                                },
                                Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                            ) {
                                Text("Edit Note")
                            }
                            Button(
                                onClick = { noteViewModel.removeNoteById(item.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red,)
                                ) {
                                Text(text = "remove")
                            }
                        }
                    }
                }
            }

            // Dialog disappears when screen is rotated
            if (noteViewModel.isDialogVisible) {
                AlertDialog(
                    onDismissRequest = { noteViewModel.isDialogVisible = false },
                    title = {
                        Text(text = "Edit note")
                    },
                    text = {
                        Column {
                            // Example: Input field for editing the note name
                            TextField(value = noteViewModel.existingNoteName, onValueChange = {
                                noteViewModel.existingNoteName = it
                            }, label = { Text(text = "Note name") })
                            // Example: Input field for editing the note description
                            TextField(value = noteViewModel.existingNoteDescription, onValueChange = {
                                noteViewModel.existingNoteDescription = it
                            },
                                label = { Text(text = "Note Description") })
                        }
                    },
                    confirmButton = {
                        // Button to confirm the changes and dismiss the dialog
                        Button(onClick = {
                            if (noteViewModel.existingNoteName.isNotBlank() && noteViewModel.existingNoteDescription.isNotBlank()) {
                                val updatedNote = Note(
                                    id = noteViewModel.selectedNoteId,
                                    noteName = noteViewModel.existingNoteName,
                                    noteDescription = noteViewModel.existingNoteDescription
                                )
                                noteViewModel.updateNote(updatedNote)
                                noteViewModel.selectedNoteId = -1
                                noteViewModel.isDialogVisible = false
                            }
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        // Button to dismiss the dialog without saving changes
                        Button(onClick = {
                            // Reset values and dismiss the dialog
                            noteViewModel.selectedNoteId = -1
                            noteViewModel.isDialogVisible = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
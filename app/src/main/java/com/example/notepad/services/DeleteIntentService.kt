package com.example.notepad.services

import android.app.IntentService
import android.content.Intent
import com.example.notepad.room.NoteDatabase

class DeleteIntentService: IntentService("DeleteIntentService"){

    private val noteDatabase by lazy { NoteDatabase.getDatabase(applicationContext).noteDao() }

    override fun onHandleIntent(intent: Intent?) {
        noteDatabase.deleteNote(intent!!.extras!!.getParcelable("note")!!)
    }

}
package com.example.notepad.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import com.example.notepad.room.NoteDatabase

class DeleteService : Service() {

    private val noteDatabase by lazy { NoteDatabase.getDatabase(applicationContext).noteDao() }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        noteDatabase.deleteNote(intent.extras!!.getParcelable("note")!!)
        return START_STICKY
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
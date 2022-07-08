package com.example.notepad

import androidx.room.*
import io.reactivex.Single

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getNotes(): Single<List<Note>>

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)
}

package com.example.notepad.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.notepad.room.Note
import com.example.notepad.R
import java.text.SimpleDateFormat


class NoteAdapterListView(private val context: Context, var noteList: ArrayList<Note>) : BaseAdapter() {
    private lateinit var image: ImageView
    private lateinit var description: TextView
    private lateinit var date: TextView
    override fun getCount(): Int {
        return noteList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        image = view.findViewById(R.id.image)
        Glide.with(context)
            .load(noteList[position].image)
            .into(image)
        description = view.findViewById(R.id.description)
        description.text = noteList[position].description
        date = view.findViewById(R.id.date)
        date.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(noteList[position].date)

        return view
    }
}
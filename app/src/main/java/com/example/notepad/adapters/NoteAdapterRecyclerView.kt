package com.example.notepad.adapters
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notepad.AddNoteFragment
import com.example.notepad.EditNoteFragment
import com.example.notepad.room.Note
import com.example.notepad.R
import java.text.SimpleDateFormat

class NoteAdapterRecyclerView (var noteList: List<Note>) : RecyclerView.Adapter<NoteAdapterRecyclerView.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val description: TextView = itemView.findViewById(R.id.description)
        val date: TextView = itemView.findViewById(R.id.date)


        fun bind (note: Note) {
            description.text = note.description

            date.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(note.date)

            Glide.with(itemView.context)
                .load(note.image)
                .into(image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
        holder.itemView.setOnClickListener {
            (holder.itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, EditNoteFragment(noteList[position])).addToBackStack(null).commit()

        }
    }

    override fun getItemCount() = noteList.size

}
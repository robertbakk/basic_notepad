package com.example.notepad

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notepad.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val noteDatabase by lazy { NoteDatabase.getDatabase(requireActivity()).noteDao() }
    private lateinit var adapter: NoteAdapter
    private val binding get() = _binding!!

    private val newNoteResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val noteDate = Date()
                val noteDescription = result.data?.getStringExtra("note_description")
                //val noteImage = result.data?.getStringExtra("note_image")

                val newNote = Note(noteDate, noteDescription ?: "", "")
                lifecycleScope.launch {
                    noteDatabase.addNote(newNote)
                }
            }
        }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDatabase.getNotes().collect { notesList ->
                adapter = NoteAdapter(notesList)

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        observeNotes()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
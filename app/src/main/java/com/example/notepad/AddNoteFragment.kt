package com.example.notepad

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.example.notepad.databinding.FragmentAddNoteBinding
import com.example.notepad.databinding.FragmentFirstBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.*

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var description: TextInputLayout
    private lateinit var submitBtn: Button
    private lateinit var chooseImageBtn: Button
    private val noteDatabase by lazy { NoteDatabase.getDatabase(requireActivity()).noteDao() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun chooseFromCamera() {

    }

    private fun chooseFromGallery() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        description = binding.tilDescription
        submitBtn = binding.btnSubmit
        chooseImageBtn = binding.btnChooseImage

        chooseImageBtn.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Choose image source")
                .setPositiveButton("Camera") { _, _ ->
                    chooseFromCamera()
                }
                .setNegativeButton("Gallery") { _, _ ->
                    chooseFromGallery()
                }
                .setNeutralButton("Cancel", null)
                .show()
        }
        submitBtn.setOnClickListener {

            val descriptionText = description.editText?.text.toString()
            val newNote = Note(Date(), descriptionText, "")
            lifecycleScope.launch {
                noteDatabase.addNote(newNote)
            }
            activity!!.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
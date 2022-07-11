package com.example.notepad

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.notepad.databinding.FragmentEditNoteBinding
import com.example.notepad.room.Note
import com.example.notepad.room.NoteDatabase
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.*


class EditNoteFragment(private var note: Note) : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var description: TextInputLayout
    private lateinit var submitBtn: Button
    private lateinit var chooseImageBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var image: ImageView
    private val noteDatabase by lazy { NoteDatabase.getDatabase(requireActivity()).noteDao() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { result ->
        photoUri = result
        val contentResolver = requireActivity().applicationContext.contentResolver
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        photoUri?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }

        Glide.with(requireContext())
            .load(photoUri)
            .into(image)

    }

    private var photoUri: Uri? = null
    private var cameraUri: Uri? = null

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            photoUri = cameraUri
            Glide.with(requireContext())
                .load(photoUri)
                .into(image)
        }
    }

    private fun chooseFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        cameraUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        launcherCamera.launch(cameraIntent)

    }

    private fun chooseFromGallery() {
        launcherGallery.launch(arrayOf("image/*"))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        description = binding.tilDescription
        submitBtn = binding.btnSubmit
        chooseImageBtn = binding.btnChooseImage
        image = binding.image
        cancelBtn = binding.btnCancel

        description.editText?.setText(note.description)
        photoUri = note.image.toUri()
        image.setImageURI(photoUri)

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

        cancelBtn.setOnClickListener {
            activity!!.onBackPressed()
        }

        submitBtn.setOnClickListener {
            val descriptionText = description.editText?.text.toString()

            if(descriptionText.isEmpty()) {
                Toast.makeText(context, "Description can't be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(photoUri == null) {
                Toast.makeText(context, "Image can't be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            note.description = descriptionText
            note.image = photoUri.toString()
            lifecycleScope.launch {
                noteDatabase.updateNote(note)
            }
            activity!!.onBackPressed()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.notepad

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.FragmentFirstBinding
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
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
    private lateinit var recyclerView: RecyclerView

    val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
        ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            lifecycleScope.launch {
                noteDatabase.deleteNote(adapter.noteList[position])
            }
            (adapter.noteList as ArrayList).removeAt(position)
            adapter.notifyDataSetChanged()
        }
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDatabase.getNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(
                    {
                        adapter.noteList = it
                        adapter.notifyDataSetChanged()
                    }, {
                        Log.i("TAG_ERROR", it.message?:"")
                    }
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NoteAdapter(emptyList())
        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        observeNotes()
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
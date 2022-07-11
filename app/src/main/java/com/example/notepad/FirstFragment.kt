package com.example.notepad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.adapters.NoteAdapterGridView
import com.example.notepad.adapters.NoteAdapterListView
import com.example.notepad.adapters.NoteAdapterRecyclerView
import com.example.notepad.databinding.FragmentFirstBinding
import com.example.notepad.room.Note
import com.example.notepad.room.NoteDatabase
import com.example.notepad.services.DeleteIntentService
import com.example.notepad.services.DeleteService
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
    private lateinit var adapter: NoteAdapterRecyclerView
    private lateinit var adapterListView: NoteAdapterListView
    private lateinit var adapterGridView: NoteAdapterGridView
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = NoteAdapterRecyclerView(emptyList())
        recyclerView.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        registerForContextMenu(binding.listView)
        observeNotes()

        return binding.root
    }

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
            deleteNote(adapter.noteList[position])
            (adapter.noteList as ArrayList).removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, adapter.noteList.size)
        }
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDatabase.getNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(
                    {
                        adapterListView = NoteAdapterListView(requireContext(), it as ArrayList<Note>)
                        binding.listView.adapter = adapterListView

                        adapterGridView = NoteAdapterGridView(requireContext(), it)
                        binding.gridView.adapter = adapterGridView

                        adapter.noteList = it
                        adapter.notifyDataSetChanged()
                    }, {
                        Log.i("TAG_ERROR", it.message?:"")
                    }
                )
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(requireContext())
        inflater.inflate(R.menu.menu_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
                deleteNote(adapter.noteList[position])
                adapterListView.noteList.removeAt(position)
                adapterListView.notifyDataSetChanged()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteNote(note: Note) {
        lifecycleScope.launch {
            //val intent = Intent(context, DeleteService::class.java)
            val intent = Intent(context, DeleteIntentService::class.java)
            intent.putExtra("note", note)
            requireContext().startService(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
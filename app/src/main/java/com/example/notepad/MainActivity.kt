package com.example.notepad

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.notepad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Your notes"
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener {
            openAddNoteFragment()
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, FirstFragment()).commit()
    }

    private fun openAddNoteFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.container, AddNoteFragment()).addToBackStack(null).commit()
        binding.fab.visibility = View.GONE
        binding.toolbar.menu.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.add -> {
                openAddNoteFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
package com.example.mynote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynote.com.Constant
import com.example.mynote.com.Note
import com.example.mynote.com.NoteAdapter
import com.example.mynote.com.NoteDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

     val db by lazy { NoteDB (this)}
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListener()
        setupRecyclerView()
    }

    fun setupListener(){
        buttonCreate.setOnClickListener {
            intentEdit(0,Constant.TYPE_CREATE)
        }
    }

    fun intentEdit(noteId: Int, intentType: Int){
        startActivity(
            Intent(applicationContext,EditActivity::class.java)
                .putExtra("intent_id",noteId)
                .putExtra("intent_type",intentType)
        )
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }

    fun loadNote (){
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("MainActivity","dbResponse:$notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

   private fun setupRecyclerView(){
    noteAdapter = NoteAdapter(arrayListOf(),object : NoteAdapter.OnAdapterListener{
        override fun onClick(note: Note) {
            // read detail note
            intentEdit(note.id,Constant.TYPE_READ)
        }

        override fun onUpdate(note: Note) {
            intentEdit(note.id,Constant.TYPE_UPDATE)
        }
        override fun onDelete(note: Note) {
           deleteDialog( note )
        }
    })

       listData.apply {
           layoutManager = LinearLayoutManager(applicationContext)
           adapter = noteAdapter

       }
    }

    private fun deleteDialog(note: Note) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("konfirmasi")
            setMessage("yakin hapus ${note.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }

}
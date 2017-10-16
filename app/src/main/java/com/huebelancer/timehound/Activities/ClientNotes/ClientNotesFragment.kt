package com.huebelancer.timehound.Activities.ClientNotes


import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.AppbarCallback
import com.huebelancer.timehound.Helpers.ClientUpdateListener
import com.huebelancer.timehound.Helpers.NoteClickListener
import com.huebelancer.timehound.Helpers.OnNoteEditDone
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO

import com.huebelancer.timehound.R


class ClientNotesFragment : Fragment(), View.OnClickListener, ClientUpdateListener, NoteClickListener, OnNoteEditDone {
    private var clientName: String = ""
    private lateinit var presenter: ClientNotesPresenter
    private lateinit var coordinator: Coordinator
    private lateinit var appbarCallback: AppbarCallback

    private var recyclerView: RecyclerView? = null
    private lateinit var fab: FloatingActionButton

    private var client: ClientDTO? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        appbarCallback = activity as ClientDetailActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyRegistry.shared.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View? = inflater!!.inflate(R.layout.fragment_client_notes, container, false)

        attachUI(view)

        return view
    }

    private fun attachUI(view: View?) {
        fab = view!!.findViewById(R.id.addNoteFab)!!
        recyclerView    = view.findViewById(R.id.recyclerView)

        initializeListView()

        fab.setOnClickListener(this)
    }

    private fun initializeListView() {
        val manager = LinearLayoutManager(activity)


        if (recyclerView != null) {
            recyclerView?.layoutManager = manager
            recyclerView?.setHasFixedSize(true)

            val adapter = ClientNotesAdapter(
                    client?.notes,
                    this
            )
            try {
                val dividerItemDecoration = DividerItemDecoration(
                        recyclerView?.context,
                        manager.orientation
                )
                dividerItemDecoration.setDrawable(
                        ContextCompat.getDrawable(
                                context,
                                R.drawable.divider
                        )
                )
                recyclerView?.addItemDecoration(dividerItemDecoration)
            } catch(e: Exception) {
                e.printStackTrace()
            }

            recyclerView?.adapter = adapter
        }
    }


    fun configureWith(presenter: ClientNotesPresenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator

        presenter.clientName = clientName
    }



    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }



    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.addNoteFab -> {
                val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_text, null, false)
                val editText = layout.findViewById<TextInputEditText>(R.id.editText)
                editText.hint = "Note"

                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Add a Note")
                        .setView(layout)
                        .setPositiveButton("ADD", { dialog, which ->
                            val noteText = editText.text.toString()

                            if (noteText != "")
                                presenter.addNote(noteText)
                        })
                        .setNegativeButton("Cancel", null)

                builder.create().show()
            }
        }
    }

    override fun onUpdate(client: ClientDTO) {
        this.client = client
        initializeListView()
    }

    override fun onNoteClick(note: NoteDTO) {
        handleEditNote(note)
    }

    fun handleEditNote(note: NoteDTO) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_text, null, false)
        val editText = view.findViewById<TextInputEditText>(R.id.editText)
        editText.setText(note.text, TextView.BufferType.EDITABLE)
        editText.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        editText.hint = "Note"

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
                .setPositiveButton("Save", {v,pos ->
                    onNoteEditeDone(note, editText.text.toString())
                })
                .setNegativeButton("Cancel", null)
        builder.create().show()
    }

    override fun onNoteEditeDone(note: NoteDTO, newText: String) {
        presenter.editNote(client, note, newText)
    }

    companion object {
        private val CLIENT_NAME = "clientName"

        fun newInstance(client: ClientDTO): ClientNotesFragment {
            val fragment = ClientNotesFragment()
            fragment.client = client
            fragment.clientName = client.name!!
            /*
            val args = Bundle()
            args.putSerializable(CLIENT_NAME, client)
            fragment.arguments = args
            */
            return fragment
        }
    }
}// Required empty public constructor

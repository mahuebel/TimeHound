package com.huebelancer.timehound.Activities.ClientsList


import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.huebelancer.timehound.Activities.ClientsActivity
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.DetailActivityCallback
import com.huebelancer.timehound.Helpers.Constants
import com.huebelancer.timehound.Helpers.CustomItemClickListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.ModelLayer
import com.huebelancer.timehound.R

class ClientListFragment : Fragment(), CustomItemClickListener, ModelLayer.RealmLoadCallback, ClientsActivity.HiddenListener {

    override fun onChange(showHidden: Boolean) {
        presenter?.loadData(this, showHidden)
    }

    private var presenter: ClientListPresenter? = null
    private var coordinator: Coordinator? = null

    private var recyclerView: RecyclerView? = null

    private lateinit var cardView: CardView
    private lateinit var submitClient: Button
    private lateinit var cancelNewClient: Button
    private lateinit var addedClientName: EditText
    private lateinit var emptyListView: TextView
    private lateinit var fab: FloatingActionButton

    private var clients: MutableList<ClientDTO> = mutableListOf()


    private lateinit var detailActivityCallback: DetailActivityCallback

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        detailActivityCallback = activity as ClientsActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View? = inflater!!.inflate(R.layout.fragment_client_list, container, false)

        attachUI(view)

        DependencyRegistry.shared.inject(this)

        return view
    }

    override fun onResume() {
        super.onResume()

        detailActivityCallback.setAppTitle(Constants.APP_TITLE)

        loadClients()

    }

    private fun attachUI(view: View?) {
        if (view != null) {

            val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

            cardView        = view.findViewById(R.id.add_client_card)
            fab             = view.findViewById(R.id.floatingActionButton)
            submitClient    = view.findViewById(R.id.add_client_button)
            addedClientName = view.findViewById(R.id.add_client_name)
            cancelNewClient = view.findViewById(R.id.add_client_cancel)
            recyclerView    = view.findViewById(R.id.recyclerView)
            emptyListView   = view.findViewById(R.id.emptyListView)

            initializeListView()
            initializeButtons()
        }
    }


    private fun initializeListView() {
        val manager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = manager
        recyclerView?.setHasFixedSize(true)

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

        val adapter = ClientViewAdapter(clients, this)
        recyclerView?.adapter = adapter
    }


    private fun initializeButtons() {
        fab.setOnClickListener({
            toggleCardView()
        })

        submitClient.setOnClickListener({
            val name: String = addedClientName.text.toString()
            if (name != "") {
                createClient(name)
//                toggleCardView()
            }
        })

        cancelNewClient.setOnClickListener({
            addedClientName.setText("")
//            toggleCardView()
        })
    }


    private fun toggleCardView() {
        /*
        if (fab.visibility == View.VISIBLE) {
            cardView.visibility = View.VISIBLE
            fab.visibility = View.GONE
        } else {
            cardView.visibility = View.GONE
            fab.visibility = View.VISIBLE
        }
        */
        Log.d(TAG, "clicked!")
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_text, null, false)
        val editText = view.findViewById<TextInputEditText>(R.id.editText)
        editText.hint = "New Client Name"
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Add a Client")
                .setView(view)
                .setPositiveButton("Add", {view, position ->
                    val text = editText.text.toString()
                    if (text != "")
                        createClient(text)
                })
                .setNegativeButton("Cancel", null)
        builder.create().show()
    }


    private fun createClient(name: String) {
        presenter?.addClient(ClientDTO(
                name,
                false,
                mutableListOf()
        ), this, (activity as ClientsActivity).showHidden)
    }


    fun configureWith(presenter: ClientListPresenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator

        loadClients()
    }


    private fun loadClients() {
        presenter?.loadData(this, (activity as ClientsActivity).showHidden)
    }


    override fun onLoadedCallback(clients: MutableList<ClientDTO>) {
        this.clients = clients

        val adapter = recyclerView?.adapter as ClientViewAdapter
        adapter.clients = this.clients
        adapter.notifyDataSetChanged()

        if (clients.size == 0) {
            emptyListView.visibility = View.VISIBLE
        } else {
            emptyListView.visibility = View.GONE
        }

    }

    override fun onLoadedCallback(client: ClientDTO) {
        //not used on this fragment
    }

    override fun onNoDataFound() {
        Toast.makeText(activity, "no data found", Toast.LENGTH_SHORT).show()
//        createDummyClients()
        if (clients.size == 0) {
            emptyListView.visibility = View.VISIBLE
        } else {
            emptyListView.visibility = View.GONE
        }
    }

    override fun onError(error: Exception) {
        if (error.message!!.contains("already exists!")) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error!")
                    .setMessage(error.message)
                    .setNeutralButton("OK", null)
            builder.create().show()
        }
        error.printStackTrace()
    }

    override fun onItemClick(v: View, position: Int) {
        coordinator?.handleClientClick(
                activity as ClientsActivity,
                clients[position].name!!,
                R.id.frame, false
        )
    }

    private fun createDummyClients() {
/*
        val cli = mutableListOf(
                "WrapTX",
                "All Signs and Tint",
                "Service Now",
                "EasterNow",
                "CC Screener",
                "Lotto Pool",
                "Gather",
                "Swapp",
                "RosterDroid"
        )

        cli.forEach { client ->
            presenter?.addClient(
                    ClientDTO(
                            client,
                            false,
                            mutableListOf()
                    ),
                    this
            )
        }

*/
    }

    companion object {
        val TAG = this::class.java.simpleName
        fun newInstance(): ClientListFragment {
            return ClientListFragment()
        }
    }

}

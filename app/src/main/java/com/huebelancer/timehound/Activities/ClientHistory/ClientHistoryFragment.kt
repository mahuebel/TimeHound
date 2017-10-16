package com.huebelancer.timehound.Activities.ClientHistory

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.*
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.Models.Span

import com.huebelancer.timehound.R
import java.util.*


class ClientHistoryFragment : Fragment(), ClientUpdateListener, ClockEventListener, OnClockEditDone {


    private var clientName: String = ""
    private lateinit var presenter: ClientHistoryPresenter
    private lateinit var coordinator: Coordinator
    private lateinit var appbarCallback: AppbarCallback

    private var recyclerView: RecyclerView? = null

    private var client: ClientDTO? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        appbarCallback = activity as ClientDetailActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            clientName = arguments.getString(CLIENT_NAME)
        }

        DependencyRegistry.shared.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View? = inflater!!.inflate(R.layout.fragment_client_history, container, false)

        attachUI(view)

        return view
    }

    private fun attachUI(view: View?) {
        recyclerView    = view?.findViewById(R.id.recyclerView)!!

        initializeListView()
    }


    private fun initializeListView() {
        if (recyclerView != null) {
            val manager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = manager
            recyclerView?.setHasFixedSize(true)

            val adapter = BillPeriodAdapter(
                    client?.periodHistory(),
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
            } catch (e: Exception) {
                e.printStackTrace()
            }

            recyclerView?.adapter = adapter
        }
    }

    fun configureWith(presenter: ClientHistoryPresenter, coordinator: Coordinator) {
        this.presenter = presenter
        this.coordinator = coordinator

        presenter.clientName = clientName
    }


    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    override fun onUpdate(client: ClientDTO) {
        this.client = client
        initializeListView()
    }


    override fun onSpanClick(span: Span) {
        handleSpanEdit(span)
    }


    private fun handleSpanEdit(span: Span) {
        val format = "E MMM dd, h:mma"
        Helpers.handleSpanEdit(context, span, format, this)
    }

    override fun onClockEditDone(span: Span, start: Date, end: Date) {
        presenter.editSpan(span, start, end)
    }



    companion object {
        private val CLIENT_NAME = "clientName"

        fun newInstance(client: ClientDTO): ClientHistoryFragment {
            val fragment = ClientHistoryFragment()
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

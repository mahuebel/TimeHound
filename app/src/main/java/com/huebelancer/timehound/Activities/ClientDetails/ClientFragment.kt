package com.huebelancer.timehound.Activities.ClientDetails


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO

import com.huebelancer.timehound.R
import android.support.design.widget.FloatingActionButton
import android.util.Log
import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.Helpers.*
import com.huebelancer.timehound.ModelLayer.Database.Models.Span
import com.huebelancer.timehound.Utilities.Analytics
import java.util.*


class ClientFragment : Fragment(), View.OnClickListener, ClientUpdateListener, ClockEventListener, OnClockEditDone, FragmentShowCallback {
    override fun onFragmentShown() : View.OnClickListener? {
        return this
    }

    private var clientName: String = ""
    private var presenter: ClientPresenter? = null
    private lateinit var coordinator: Coordinator

    private var recyclerView: RecyclerView? = null
//    private lateinit var billFab: FloatingActionButton

    var client: ClientDTO? = null

    private lateinit var detailActivityCallback: DetailActivityCallback

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

        detailActivityCallback = activity as ClientDetailActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DependencyRegistry.shared.inject(this)
        Log.d(TAG, "onCreate")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View? = inflater!!.inflate(R.layout.fragment_client, container, false)

        attachUI(view)

        setBackgroundGradient()
        Log.d(TAG, "onCreateView")

        return view
    }

    private fun setBackgroundGradient() {
        /*
        clientLayout.background = Helpers.getGradientDrawable(
                intArrayOf(
                        ContextCompat.getColor(activity, R.color.colorPrimaryDark),
                        ContextCompat.getColor(activity, R.color.colorPrimary)
                )
        )
        */
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")

        setupUI()

    }

    private fun setupUI() {
        if (this.client != null) {
            val adapter = recyclerView?.adapter as WorkDayViewAdapter
            adapter.days = this.client!!.daysInPeriod()
            adapter.notifyDataSetChanged()
        }
    }


    private fun attachUI(view: View?) {
        val manager = LinearLayoutManager(activity)

//        billFab         = view!!.findViewById(R.id.billFab)
        recyclerView    = view?.findViewById(R.id.recyclerView)

        recyclerView?.layoutManager = manager
        recyclerView?.setHasFixedSize(true)

        initializeListView()

//        billFab.setOnClickListener(this)
    }


    private fun initializeListView() {
        Log.i(TAG, "Client current work is initializing")
        val adapter = WorkDayViewAdapter(
                client?.daysInPeriod(),
                this
        )
        recyclerView?.adapter = adapter
    }

    fun configureWith(prsntr: ClientPresenter, coordinator: Coordinator) {
        presenter = prsntr
        this.coordinator = coordinator

        presenter?.clientName = clientName
    }


    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.detailFAB -> {
                handleBillClick()
            }
        }
    }

    fun handleBillClick() {
        Log.d(TAG, "clicked bill fab, presenter is null? ${clientName} - ${presenter == null}")
        Analytics.getInstance().periodBilled(client!!.lastOpenPeriod())
        presenter?.bill(client!!.lastOpenPeriod())
    }

    override fun onUpdate(dto: ClientDTO) {
        client = dto
        if (recyclerView != null) {
            initializeListView()
        }
    }

    override fun onSpanClick(span: Span) {
        handleSpanEdit(span)
    }


    private fun handleSpanEdit(span: Span) {
        val format = "E MMM dd, h:mma"
        Helpers.handleSpanEdit(context, span, format, this)
    }

    override fun onClockEditDone(span: Span, start: Date, end: Date) {
        presenter?.editSpan(span, start, end)
    }


    companion object {
        val TAG = ClientFragment::class.java.simpleName
        private val CLIENT_NAME = "clientName"

        fun newInstance(client: ClientDTO): ClientFragment {
            val fragment = ClientFragment()
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

}

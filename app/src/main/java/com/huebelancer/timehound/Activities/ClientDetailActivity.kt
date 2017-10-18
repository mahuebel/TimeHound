package com.huebelancer.timehound.Activities

import android.animation.LayoutTransition
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.transition.Fade
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.Log
import android.view.*
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.huebelancer.timehound.Activities.ClientDetails.ClientFragment
import com.huebelancer.timehound.Activities.ClientHistory.ClientHistoryFragment
import com.huebelancer.timehound.Activities.ClientNotes.ClientNotesFragment
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.*
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.ModelLayer

import com.huebelancer.timehound.R
import com.huebelancer.timehound.Utilities.SubscriptionManager
import kotlinx.android.synthetic.main.activity_client_detail.*

class ClientDetailActivity : AppCompatActivity(), ClientUICallback, View.OnClickListener, AppbarCallback {

    override fun setAppTitle(title: String) {
        supportActionBar?.title = title
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var clientName: String

    private var presenter: ClientDetailPresenter? = null
    private lateinit var coordinator: Coordinator
    private lateinit var mSubscriptionManager: SubscriptionManager

    //region UI Widgets

    private lateinit var mChronometer: Chronometer

    private lateinit var clientTextView: TextView
    private lateinit var periodHoursView: TextView
    private lateinit var clientLayout: ConstraintLayout
    private lateinit var clockFab: FloatingActionButton
    private lateinit var headerImage: ImageView
    private lateinit var settingsBtn: ImageView
    private lateinit var headerCard: ConstraintLayout
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    //endregion


    private var client: ClientDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_detail)

        clientName = intent.getStringExtra(Constants.EXTRA_CLIENT_NAME)

        DependencyRegistry.shared.inject(this)

        attachUI()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }

    private fun setupPagerAdapter() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        if (mSectionsPagerAdapter != null) {
            container.removeAllViews()
            mSectionsPagerAdapter = null
        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    }


    private fun attachUI() {
        clientTextView  = findViewById(R.id.clientName)!!
        periodHoursView = findViewById(R.id.clientHours)
        clientLayout    = findViewById(R.id.clientLayout)
        clockFab        = findViewById(R.id.addClockFab)
        headerImage     = findViewById(R.id.headerImage)
        settingsBtn     = findViewById(R.id.settingsBtn)
        mChronometer    = findViewById(R.id.chronometer)
        headerCard      = findViewById(R.id.cardLayout)

//        ViewCompat.setTransitionName(findViewById(R.id.appbar), EXTRA_IMAGE)
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout)

        collapsingToolbarLayout.title = clientName
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))


        val trans = clientLayout.layoutTransition
        trans.enableTransitionType(LayoutTransition.CHANGING)

        clockFab.setOnClickListener(this)
        settingsBtn.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        setupUI()
    }

    private fun setupUI() {
        if (this.client != null) {
            setupUIforClockToggle(this.client!!)
            setPeriodHours()

            if (mSectionsPagerAdapter != null) {
                val current = container.currentItem
                setupPagerAdapter()
                container.currentItem = current
            } else {
                setupPagerAdapter()
            }

            setAppTitle(clientName)
        }
    }

    private fun setPeriodHours() {
        val hours = client?.lastOpenPeriod()?.hoursWorked()!!
        periodHoursView.text = "${Math.round(hours * 100) / 100.0}"
    }

    private fun setupUIforClockToggle(client: ClientDTO) {
        clientTextView.text = client.name
        var color = ContextCompat.getColor(this, R.color.colorPrimary)
        if (client.isOnTheClock()) {
            //ON THE CLOCK

            startNotification()

            mChronometer.visibility = View.VISIBLE

            val elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime()
            val period = client.lastOpenPeriod()
            val base = period.events[period.events.lastIndex].time?.time!! - elapsedRealtimeOffset
            Log.d(ClientFragment.TAG, "Chronometer base: $base")
            mChronometer.base = base
            mChronometer.start()
            headerImage.setImageResource(R.drawable.on_the_clock_header)
            clockFab.setImageDrawable(
                    ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_alarm_off_black_24dp
                    )
            )
            color = ContextCompat.getColor(this, R.color.colorOnTheClock)
        } else {
            //OFF THE CLOCK
            stopNotification()
            mChronometer.stop()
            mChronometer.visibility = View.GONE

            headerImage.setImageResource(R.drawable.off_the_clock_header)
            clockFab.setImageDrawable(
                    ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_alarm_black_24dp
                    )
            )
        }

        val colorDrawable = ColorDrawable(color)
        collapsingToolbarLayout.contentScrim = colorDrawable
        appbar.setBackgroundColor(color)
        tabs.setBackgroundColor(color)
    }


    private fun stopNotification() {
        if (client != null) {
            coordinator.handleStopNotification(client!!, this)

        }
    }

    fun stopNotification(client: String) {
        coordinator.handleStopNotification(client, this)
    }

    private fun startNotification() {
        if (client != null) {
            coordinator.handleStartNotification(client!!, this)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_client_detail, menu)

        val item = menu.getItem(0)
        item.title = if (client?.hidden == null || !client?.hidden!!)
            getString(R.string.show_client)
        else
            getString(R.string.hide_client)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.hide_client) {
            toggleClientVisibility(item)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toggleClientVisibility(item: MenuItem) {
        if (client?.hidden == null || !client?.hidden!!)
            item.title = getString(R.string.show_client)
        else
            item.title = getString(R.string.hide_client)

        presenter?.toggleClientVisibility()
    }


    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val pagerClient: ClientDTO = client ?: ClientDTO("", false, mutableListOf(), mutableListOf())

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return when(position) {
                1 -> {
                    val frag = ClientNotesFragment.newInstance(pagerClient)
                    mSubscriptionManager.addSub(frag)
                    frag
                }
                2 -> {
                    val frag = ClientHistoryFragment.newInstance(pagerClient)
                    mSubscriptionManager.addSub(frag)
                    frag
                }
                else -> {
                    val frag = ClientFragment.newInstance(pagerClient)
                    mSubscriptionManager.addSub(frag)
                    frag
                }
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }

        fun updateClient(client: ClientDTO) {
            pagerClient.name = client.name
            pagerClient.hidden = client.hidden
            pagerClient.periods = client.periods
            pagerClient.notes = client.notes

            for(i in 0..count) {
                try {
                    (getItem(i) as ClientUpdateListener).onUpdate(pagerClient)
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }

        }
    }



    override fun onClick(view: View?) {
        val set = TransitionSet()
        set.addTransition(Fade())
                .addTransition(Slide(Gravity.START))
        set.interpolator = FastOutSlowInInterpolator()

        when(view?.id) {
            R.id.addClockFab -> {
                set.addTarget(mChronometer)

                presenter?.toggleClock(this)
            }

        }
        TransitionManager.beginDelayedTransition(headerCard, set)
    }




    fun getRealmLoadCallback() : ModelLayer.RealmLoadCallback? {
        return presenter
    }

    fun configureWith(presenter: ClientDetailPresenter, coordinator: Coordinator, subscriptionManager: SubscriptionManager) {
        this.presenter = presenter
        this.coordinator = coordinator

        this.presenter?.clientName = clientName
        mSubscriptionManager= subscriptionManager

        presenter.loadData()
    }


    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }




    override fun onLoadedCallback(client: ClientDTO) {
        this.client = client
        setupUI()
        mSubscriptionManager.update(client)
//        mSectionsPagerAdapter?.updateClient(client)
    }

    override fun onNoDataFound() {
        Toast.makeText(this, "No Data Found!", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: Exception) {
        Toast.makeText(this, "Error Fetching Data!", Toast.LENGTH_SHORT).show()
    }

}

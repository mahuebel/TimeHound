package com.huebelancer.timehound.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.huebelancer.timehound.Activities.ClientDetails.ClientFragment
import com.huebelancer.timehound.Activities.ClientsList.ClientListFragment
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.Dependencies.DependencyRegistry
import com.huebelancer.timehound.Helpers.DetailActivityCallback
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.R
import com.huebelancer.timehound.Utilities.Analytics
import com.huebelancer.timehound.Utilities.NotificationService
import kotlinx.android.synthetic.main.activity_client_detail.*

class ClientsActivity : AppCompatActivity(), DetailActivityCallback {
    interface HiddenListener {
        fun onChange(showHidden: Boolean)
    }

    lateinit var coordinator: Coordinator

    var showHidden: Boolean = false
    var hiddenListener: HiddenListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Analytics.getInstance().viewedClientList()
        setContentView(R.layout.activity_clients)

        DependencyRegistry.shared.inject(this)

        setSupportActionBar(toolbar)
        toolbar.popupTheme

        val activeName : String? = intent.getStringExtra(NotificationService.ACTIVE_NAME)

        var fragment =
                supportFragmentManager
                    .findFragmentById(R.id.frame)


        if (fragment == null) {
            fragment = ClientListFragment.newInstance()

            Helpers.addFragmentToActivity(
                    supportFragmentManager,
                    fragment,
                    R.id.frame
            )
        }

        hiddenListener = fragment as ClientListFragment

        if (activeName != null && activeName != "") {
            val isClearClient = fragment.javaClass == ClientFragment::class.java

            coordinator.handleClientClick(this, activeName, R.id.frame, isClearClient)
        }

    }


    override fun setAppTitle(title: String) {
        supportActionBar?.title = title
    }



    fun configureWith(coordinator: Coordinator) {
        this.coordinator = coordinator
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_clients, menu)

        val item = menu.getItem(0)
        item.title = if (showHidden) {
            getString(R.string.hide_clients)
        } else {
            getString(R.string.show_clients)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.show_hidden) {
            toggleShownClients(item)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    fun toggleShownClients(item: MenuItem) {
        showHidden = !showHidden
        if (showHidden)
            item.title = getString(R.string.hide_clients)
        else
            item.title = getString(R.string.show_clients)

        hiddenListener?.onChange(showHidden)
    }

}

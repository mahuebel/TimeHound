package com.huebelancer.timehound.Activities.ClientDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.huebelancer.timehound.R

/**
 * Created by mahuebel on 10/7/17.
 */
class SettingsListAdapter(val adapterContext: Context, var settingsList: MutableList<String>)
    : ArrayAdapter<String>(adapterContext, R.layout.item_settings, settingsList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = if (convertView == null) {
            LayoutInflater.from(adapterContext).inflate(R.layout.item_settings, parent, false)
        } else {
            convertView
        }

        val settingsText: TextView = view.findViewById(R.id.text)

        settingsText.text = settingsList[position]

        return view
    }

    override fun getCount(): Int {
        return settingsList.size
    }
}
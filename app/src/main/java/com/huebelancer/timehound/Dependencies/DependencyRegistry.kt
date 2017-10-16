package com.huebelancer.timehound.Dependencies

import com.huebelancer.timehound.Activities.ClientDetailActivity
import com.huebelancer.timehound.Activities.ClientDetailPresenter
import com.huebelancer.timehound.Activities.ClientDetails.*
import com.huebelancer.timehound.Activities.ClientHistory.ClientHistoryFragment
import com.huebelancer.timehound.Activities.ClientHistory.ClientHistoryPresenter
import com.huebelancer.timehound.Activities.ClientNotes.ClientNotesFragment
import com.huebelancer.timehound.Activities.ClientNotes.ClientNotesPresenter
import com.huebelancer.timehound.Activities.ClientsActivity
import com.huebelancer.timehound.Activities.ClientsList.ClientListFragment
import com.huebelancer.timehound.Activities.ClientsList.ClientListPresenter
import com.huebelancer.timehound.Coordinators.Coordinator
import com.huebelancer.timehound.ModelLayer.Database.DataLayer
import com.huebelancer.timehound.ModelLayer.ModelLayer
import com.huebelancer.timehound.ModelLayer.Translation.TranslationLayer
import com.huebelancer.timehound.Utilities.SubscriptionManager
import io.realm.Realm

/**
 * Created by mahuebel on 9/4/17.
 */
class DependencyRegistry {

    companion object {
        val shared: DependencyRegistry = DependencyRegistry()
    }

    private val coordinator = Coordinator()

    private val realm = Realm.getInstance(Realm.getDefaultConfiguration())

    private val translationLayer = createTranslationLayer()
    private fun createTranslationLayer(): TranslationLayer {
        return TranslationLayer()
    }

    private val dataLayer: DataLayer = createDataLayer()
    private fun createDataLayer(): DataLayer {
        return DataLayer(realm, translationLayer)
    }

    private val modelLayer: ModelLayer = createModelLayer()
    private fun  createModelLayer(): ModelLayer {
        return ModelLayer(dataLayer)
    }

    private val subscriptionManager = SubscriptionManager()

    //region Injections

    fun inject(fragment: ClientListFragment) {
        val presenter = ClientListPresenter(modelLayer)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(fragment: ClientFragment) {
        val presenter = ClientPresenter(fragment, modelLayer)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(activity: ClientsActivity) {
        activity.configureWith(coordinator)
    }

    fun inject(fragment: ClientHistoryFragment) {
        val presenter = ClientHistoryPresenter(fragment, modelLayer)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(fragment: ClientNotesFragment) {
        val presenter = ClientNotesPresenter(fragment, modelLayer)
        fragment.configureWith(presenter, coordinator)
    }

    fun inject(activity: ClientDetailActivity) {
        val presenter = ClientDetailPresenter(activity, modelLayer)
        activity.configureWith(presenter, coordinator, subscriptionManager)
    }

    //endregion
}
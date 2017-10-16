package com.huebelancer.timehound.Utilities

import com.huebelancer.timehound.Helpers.ClientUpdateListener
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO

/**
 * Created by mahuebel on 10/15/17.
 */
class SubscriptionManager {
    val subscriptions: LinkedHashSet<ClientUpdateListener> = LinkedHashSet()

    fun addSub(subscription: ClientUpdateListener) {
        subscriptions.add(subscription)
    }

    fun update(client: ClientDTO) {
        subscriptions.forEach({ sub ->
            sub.onUpdate(client)
        })
    }
}
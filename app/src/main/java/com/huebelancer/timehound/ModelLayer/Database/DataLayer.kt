package com.huebelancer.timehound.ModelLayer.Database

import android.os.AsyncTask
import android.util.Log
import com.huebelancer.timehound.Helpers.ClientExistsException
import com.huebelancer.timehound.Helpers.Helpers
import com.huebelancer.timehound.ModelLayer.Database.DTOs.ClientDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.EventDTO
import com.huebelancer.timehound.ModelLayer.Database.DTOs.NoteDTO
import com.huebelancer.timehound.ModelLayer.Database.Models.BillPeriod
import com.huebelancer.timehound.ModelLayer.Database.Models.Client
import com.huebelancer.timehound.ModelLayer.Database.Models.ClockEvent
import com.huebelancer.timehound.ModelLayer.Database.Models.Note
import com.huebelancer.timehound.ModelLayer.Enums.EventType
import com.huebelancer.timehound.ModelLayer.ModelLayer
import com.huebelancer.timehound.ModelLayer.Translation.TranslationLayer
import com.huebelancer.timehound.Utilities.Analytics
import io.realm.Realm
import io.realm.RealmResults
import java.util.*

/**
 * Created by mahuebel on 9/4/17.
 */
class DataLayer(private val realm: Realm, private val translationLayer: TranslationLayer) {

    fun loadClient(clientName: String, callback: ModelLayer.RealmLoadCallback) {
        val clientTask = LoadClientTask(clientName, callback, translationLayer)
        clientTask.execute()
    }

    fun loadClients(callback: ModelLayer.RealmLoadCallback, showHidden: Boolean) {
        val clientsTask = LoadClientsTask(showHidden, callback, translationLayer)
        clientsTask.execute()
    }

    fun addClient(showHidden: Boolean, clientDto: ClientDTO, callback: ModelLayer.RealmLoadCallback) {
        realm.executeTransaction {
            val existing: Client? = realm.where(Client::class.java).equalTo("name", clientDto.name).findFirst()

            if (existing != null) {
                if (existing.hidden != null && existing.hidden!!) {
                    existing.hidden = false
                } else {
                    callback.onError(ClientExistsException("${clientDto.name} already exists!"))
                }
            } else {

                val client = realm.createObject(Client::class.java)
                client.hidden = clientDto.hidden
                client.name = clientDto.name
            }
        }
        Log.d(TAG, "Client added")

        loadClients(callback, showHidden)
        Analytics.getInstance().clientAdded(clientDto)
    }

    fun addClientNote(client: String, text: String, callback: ModelLayer.RealmLoadCallback) {
        val noteTask = AddClientNoteTask(text, client, callback, translationLayer)
        noteTask.execute()
        Analytics.getInstance().noteAdded()
    }

    fun toggleClientClock(clientName: String, callback: ModelLayer.RealmLoadCallback?) {
        val toggleTask = AddClientEventTask(clientName, callback, translationLayer)
        toggleTask.execute()
    }

    fun billLastPeriod(clientName: String, callback: ModelLayer.RealmLoadCallback) {
        val billOutTask = BillOutTask(clientName, callback, translationLayer)
        billOutTask.execute()
    }

    fun editEvent(event: EventDTO, date: Date, callback: ModelLayer.RealmLoadCallback) {
        val editEventTask = EditEventTask(event, date, callback, translationLayer)
        editEventTask.execute()
    }

    fun editNote(client: ClientDTO, note: NoteDTO, newText: String, callback: ModelLayer.RealmLoadCallback) {
        val editNoteTask = EditNoteTask(client, note, newText, callback, translationLayer)
        editNoteTask.execute()
    }

    fun toggleClientVisibility(clientName: String, callback: ModelLayer.RealmLoadCallback) {
        val toggleClientVisTask = ToggleClientVisibilityTask(clientName, callback, translationLayer)
        toggleClientVisTask.execute()
    }



    companion object {

        val TAG = this::class.java.simpleName

        fun loadClientsFromRealm(realm: Realm) : MutableList<Client> {
            val results: RealmResults<Client> = realm.where(Client::class.java).notEqualTo("hidden", true).findAll()
            return realm.copyFromRealm(results)
        }

        fun loadAllClientsFromRealm(realm: Realm) : MutableList<Client> {
            val results: RealmResults<Client> = realm.where(Client::class.java).findAll()
            return realm.copyFromRealm(results)
        }

        fun loadClientFromRealm(realm: Realm, clientName: String) : Client? {
            val result: Client? = realm.where(Client::class.java).equalTo("name", clientName).findFirst()
            return realm.copyFromRealm(result)
        }

         fun loadClientEventsFromRealm(clientId: String?, realm: Realm) : MutableList<ClockEvent> {
            return if (clientId != null) {
                val results: RealmResults<ClockEvent> = realm.where(ClockEvent::class.java).equalTo("client", clientId).findAll()
                realm.copyFromRealm(results)
            } else {
                mutableListOf()
            }
        }

        fun loadPeriodsFromRealm(clientName: String, realm: Realm): MutableList<BillPeriod> {
            return if (clientName != null) {
                val results: RealmResults<BillPeriod> = realm.where(BillPeriod::class.java).equalTo("client", clientName).findAll()
                realm.copyFromRealm(results)
            } else {
                mutableListOf()
            }
        }
    }




    class LoadClientsTask(val showHidden: Boolean, private var callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, MutableList<ClientDTO>>() {

        override fun doInBackground(vararg p0: Void): MutableList<ClientDTO> {
            val realm = Realm.getDefaultInstance()

            val clients: MutableList<Client> = if (showHidden) {
                DataLayer.loadAllClientsFromRealm(realm)
            } else {
                DataLayer.loadClientsFromRealm(realm)
            }


            val result = translationLayer.translate(clients)
            realm.close()

            result.sortBy { it.name }

            return result
        }

        override fun onPostExecute(result: MutableList<ClientDTO>?) {
            super.onPostExecute(result)
            callback.onLoadedCallback(result!!)
        }
    }


    class LoadClientTask(private var clientName: String, private var callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO>() {

        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientName)
            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)

            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }

    }


    class AddClientNoteTask(private var text: String, private var clientName: String, private var callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO>() {
        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val client: Client? = shadowRealm.where(Client::class.java).equalTo("name", clientName).findFirst()

                val note = shadowRealm.createObject(Note::class.java)
                note.date = Date()
                note.text = text

                client?.notes?.add(note)
            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientName)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)
            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }

    }


    class BillOutTask(private var clientName: String, private var callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO>() {
        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val client: Client? = shadowRealm.where(Client::class.java).equalTo("name", clientName).findFirst()

                val period = client?.lastOpenPeriod()

                if (period != null && !period.isBilled) {
                    period.isBilled = true
                }

                //I guess I'll jsut add a new empty period when the last was completed... probably overkill
                val p = shadowRealm.createObject(BillPeriod::class.java)
                p.client = clientName
                client?.periods?.add(p)
            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientName)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)
            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }

    }

    class AddClientEventTask(private var clientName: String, private var callback: ModelLayer.RealmLoadCallback?, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO>() {

        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val client: Client? = shadowRealm.where(Client::class.java).equalTo("name", clientName).findFirst()

                Log.d(TAG, "Client has periods: ${client?.periods?.size!! > 0}" )

                var period = if (client.periods.size > 0
                        && !client.periods[client.periods.lastIndex].isBilled)  {
                    client.periods[client.periods.lastIndex]
                } else {
                    val p = shadowRealm.createObject(BillPeriod::class.java)
                    p.client = clientName
                    client.periods.add(p)
                    p
                }

                Log.d(TAG, period.toString())

                val event = shadowRealm.createObject(ClockEvent::class.java)
                event.client = clientName
                event.time = Date()
                event.type = if (period?.clockEvents!!.size == 0
                        || period.clockEvents[period.clockEvents.lastIndex].type == Helpers.eventTypeToString(EventType.Clock_Out)) {
                    //last event was clock out or null
                    Log.d(TAG, "${clientName}'s last event was null(${period.clockEvents.size == 0}) or clock out")
                    Helpers.eventTypeToString(EventType.Clock_In)
                } else {
                    //last event was clock in
                    Log.d(TAG, "${clientName}'s last event was clock in")
                    Helpers.eventTypeToString(EventType.Clock_Out)
                }
                period.clockEvents.add(event)

                Log.d(TAG, "Client : ${client.name} \n Events: ${period.clockEvents.size}")
            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientName)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)

            if (result != null) {
                callback?.onLoadedCallback(result)
            } else {
                callback?.onNoDataFound()
            }
        }

    }

    class EditEventTask(private var event: EventDTO, private var date: Date, private var callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO?>() {
        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val clockEvent: ClockEvent? = shadowRealm.where(ClockEvent::class.java)
                        .equalTo("type", Helpers.eventTypeToString(event.type!!))
                        .equalTo("time", event.time)
                        .equalTo("client", event.clientId)
                        .findFirst()

                val existingEvent: ClockEvent? = shadowRealm.where(ClockEvent::class.java)
                        .equalTo("type", Helpers.eventTypeToString(event.type!!))
                        .equalTo("time", date)
                        .equalTo("client", event.clientId)
                        .findFirst()

                if (existingEvent != null){
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.add(Calendar.MINUTE, 1)
                    clockEvent?.time = cal.time
                }
                else {
                    clockEvent?.time = date
                }

                val period: BillPeriod? = shadowRealm.where(BillPeriod::class.java)
                        .equalTo("client", event.clientId)
                        .findFirst()

                period?.clockEvents?.sort("time")

            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, event.clientId!!)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)

            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }
    }

    class EditNoteTask(val clientDTO: ClientDTO, val noteDTO: NoteDTO, val newText: String, val callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO?>() {
        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val note: Note? = shadowRealm.where(Note::class.java)
                        .equalTo("date", noteDTO.date)
                        .equalTo("text", noteDTO.text)
                        .findFirst()

                if (note != null)
                    note.text = newText
            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientDTO.name!!)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)

            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }
    }

    class ToggleClientVisibilityTask(val clientName: String, val callback: ModelLayer.RealmLoadCallback, val translationLayer: TranslationLayer) : AsyncTask<Void, Void, ClientDTO?>() {
        override fun doInBackground(vararg p0: Void?): ClientDTO? {
            val realm = Realm.getDefaultInstance()

            realm.executeTransaction({ shadowRealm ->
                val client: Client? = shadowRealm.where(Client::class.java)
                        .equalTo("name", clientName)
                        .findFirst()

                if (client != null) {
                    if (client.hidden == null)
                        client.hidden = true
                    else
                        client.hidden = !client.hidden!!
                }
            })

            val client: Client? = DataLayer.loadClientFromRealm(realm, clientName)

            return if (client != null) {
                val result = translationLayer.translate(realm, client)
                realm.close()
                result
            } else {
                realm.close()
                null
            }
        }

        override fun onPostExecute(result: ClientDTO?) {
            super.onPostExecute(result)

            if (result != null) {
                callback.onLoadedCallback(result)
            } else {
                callback.onNoDataFound()
            }
        }
    }
}
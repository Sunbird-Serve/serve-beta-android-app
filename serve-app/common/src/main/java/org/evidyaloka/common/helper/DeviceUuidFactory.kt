package org.evidyaloka.common.helper

import android.content.Context
import java.util.*

object DeviceUuidFactory {
    private val PREFS_FILE = "device_id.xml"
    private val PREFS_DEVICE_ID = "device_id"
    fun getDeviceUuid(context: Context): UUID? {
        var uuid: UUID? = null
        val pref = context.getSharedPreferences(PREFS_FILE, 0)
        val id = pref.getString(PREFS_DEVICE_ID, null);
        if (id != null) {
            // Use the ids previously computed and stored in the
            // prefs file
            uuid = UUID.fromString(id);
        }else{
            uuid= UUID.randomUUID()
            pref.edit()
                .putString(PREFS_DEVICE_ID, uuid.toString())
                .commit()
        }
        return uuid
    }
}
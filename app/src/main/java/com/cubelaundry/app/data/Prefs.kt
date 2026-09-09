package com.cubelaundry.app.data

import android.content.Context

/** Minimal local storage — just enough to prefill the order form and look up
 * History with the customer's own details after they've placed at least one
 * order, or after they've filled in the Profile screen. No login/account
 * system; nothing here is sent anywhere except as part of a normal order. */
object Prefs {
    private const val PREFS_NAME = "cube_laundry_prefs"
    private const val KEY_MOBILE = "last_mobile_number"
    private const val KEY_NAME = "customer_name"
    private const val KEY_ADDRESS = "customer_address"

    fun saveMobileNumber(context: Context, mobile: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MOBILE, mobile)
            .apply()
    }

    fun getMobileNumber(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_MOBILE, null)
    }

    fun saveProfile(context: Context, name: String, mobile: String, address: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_NAME, name)
            .putString(KEY_MOBILE, mobile)
            .putString(KEY_ADDRESS, address)
            .apply()
    }

    fun getName(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME, null)
    }

    fun getAddress(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ADDRESS, null)
    }
}

package com.example.appgoimon.data.local

import android.content.Context
import com.example.appgoimon.data.remote.AuthUserDto

/**
 * Disk-backed persistence for the logged-in user, so a returning user is NOT shown the login
 * screen on cold start (AC1/AC2). Backed by SharedPreferences (no extra dependency). Reads are
 * synchronous and tiny, so they are safe to perform during composition without a visible flash.
 */
class AuthPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Persists the authenticated user's identity fields after a successful login (AC1). */
    fun saveUser(user: AuthUserDto) {
        prefs.edit()
            .putInt(KEY_ID, user.id)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_FULL_NAME, user.full_name)
            .putString(KEY_PHONE, user.phone)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_CREATED_AT, user.created_at)
            .apply()
    }

    /** Restores the saved user on cold start, or null if no one is logged in (AC2). */
    fun loadUser(): AuthUserDto? {
        val id = prefs.getInt(KEY_ID, 0)
        val username = prefs.getString(KEY_USERNAME, null)
        val role = prefs.getString(KEY_ROLE, null)
        if (id <= 0 || username == null || role == null) {
            return null
        }
        return AuthUserDto(
            id = id,
            username = username,
            full_name = prefs.getString(KEY_FULL_NAME, null),
            phone = prefs.getString(KEY_PHONE, null),
            role = role,
            created_at = prefs.getString(KEY_CREATED_AT, null)
        )
    }

    /** Clears the saved user on logout (AC3). Does NOT touch any running table session. */
    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "auth_prefs"
        const val KEY_ID = "user_id"
        const val KEY_USERNAME = "username"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_PHONE = "phone"
        const val KEY_ROLE = "role"
        const val KEY_CREATED_AT = "created_at"
    }
}

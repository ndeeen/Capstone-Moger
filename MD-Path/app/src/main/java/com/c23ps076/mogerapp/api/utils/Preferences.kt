package com.c23ps076.mogerapp.api.utils

import android.content.Context
import android.content.SharedPreferences
import com.c23ps076.mogerapp.api.AuthSession
import com.c23ps076.mogerapp.api.utils.Const.Companion.NAME_KEY
import com.c23ps076.mogerapp.api.utils.Const.Companion.TOKEN_KEY
import com.c23ps076.mogerapp.api.utils.Const.Companion.STILL_LOGIN_KEY
import com.c23ps076.mogerapp.BuildConfig.PREF_NAME
import com.c23ps076.mogerapp.api.utils.Const.Companion.EMAIL_KEY


class Preferences(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun setUserLogin(auth: AuthSession) {
        editor.apply {
            putString(NAME_KEY, auth.name)
            putString(TOKEN_KEY, auth.access_token)
            putBoolean(STILL_LOGIN_KEY, auth.still_login)
            putString(EMAIL_KEY, auth.email)
            apply()
        }
    }

    fun getLoginData(): AuthSession {
        return AuthSession(
            pref.getString(NAME_KEY, "").toString(),
            pref.getString(TOKEN_KEY, "").toString(),
            pref.getBoolean(STILL_LOGIN_KEY, false),
            pref.getString(EMAIL_KEY, "").toString()
        )
    }

    fun clearLoginData() {
        setUserLogin(AuthSession("", "", false, ""))
    }

    fun logout() {
        editor.apply {
            remove(NAME_KEY)
            remove(TOKEN_KEY)
            putBoolean(STILL_LOGIN_KEY, false)
            remove(EMAIL_KEY)
            apply()
        }

    }
}
package com.nitroxina.kanb.persistence

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.nitroxina.kan.DeCryptor
import com.nitroxina.kan.EnCryptor
import com.nitroxina.kanb.kanboardApi.APP_ALIAS


class SharedPreferenceKB(val context: Context) {
    private val PREFS_NAME = "Kanb"

    companion object {
        val SERVER_URL = "server_url"
        val USERNAME = "username"
        val TOKEN = "token"
    }

    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(keyName: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(keyName, text)
        editor!!.commit()
    }

    fun saveEncryption(keyName: String, text: String) {
        val encryptor = EnCryptor()
        val encryptedText = Base64.encodeToString(encryptor.encryptText(APP_ALIAS, text), Base64.DEFAULT)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(keyName, encryptedText)
        editor.putString("iv$keyName", Base64.encodeToString(encryptor.iv, Base64.DEFAULT))
        editor!!.commit()
    }

    fun save(keyName: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(keyName, value)
        editor.commit()
    }

    fun save(keyName: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(keyName, status!!)
        editor.commit()
    }

    fun getValueString(keyName: String): String? {
        return sharedPref.getString(keyName, null)
    }

    fun getDecriptValueString(keyName: String): String? {
        val ivText = sharedPref.getString("iv$keyName", null)
        val ecryptedText = sharedPref.getString(keyName, null)
        if(ivText.isNullOrBlank() || ecryptedText.isNullOrBlank()) {
            return null
        }
        val ivByteArray = Base64.decode(ivText, Base64.DEFAULT)
        val ecryptedByteArray = Base64.decode(ecryptedText, Base64.DEFAULT)
        val decryptor = DeCryptor()
        return decryptor.decryptData(APP_ALIAS, ecryptedByteArray, ivByteArray)
    }

    fun getValueInt(keyName: String): Int {
        return sharedPref.getInt(keyName, 0)
    }

    fun getValueBoolien(keyName: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(keyName, defaultValue)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor.clear()
        editor.commit()
    }

    fun removeValue(keyName: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(keyName)
        editor.commit()
    }
}

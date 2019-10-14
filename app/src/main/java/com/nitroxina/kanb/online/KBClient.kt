package com.nitroxina.kanb.online

import android.util.Base64
import com.nitroxina.kanb.extensions.getKBResponse
import com.nitroxina.kanb.kanboardApi.*
import com.nitroxina.kanb.persistence.SharedPreferenceKB
import okhttp3.*
import org.json.JSONObject
import java.lang.Exception


object KBClient {
    private val authenticator: Authenticator
    private val okHttpClient: OkHttpClient
    private val serverUrl: String = server_url!!

    init {

        val clientKbToken =  kbToken!!
        val clientUsername =  username!!
        val clientSSL = ssl!!

        authenticator =  object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
                if (response.request().header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }

                val credential = Credentials.basic(clientUsername, clientKbToken)
                return response.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
            }
        }

        okHttpClient = if(clientSSL) {
            OkHttpClient.Builder().authenticator(authenticator).build()
        } else {
            UnsfOkHttpClient.getUnsafeOkHttpClient(authenticator)
        }

        clearCredential()
    }

    fun execute(nameMethod: String, params: String? = null) : KBResponse {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        var paramStr = "}"
        if(params != null) {
            paramStr = ", \"params\": $params}"
        }
        val jsonString = "{\"jsonrpc\" : \"2.0\", \"method\": \"$nameMethod\", \"id\": 2 ${paramStr}"
        val body = RequestBody.create(JSON, jsonString)
        val request = Request.Builder()
            .url(serverUrl)
            .post(body)
            .build()
        val response = okHttpClient.newCall(request).execute()
        var jsonObj = JSONObject(response.body()!!.string())
        return jsonObj.getKBResponse()
    }

    private fun clearCredential() {
        kbToken = ""
        username = ""
    }
}

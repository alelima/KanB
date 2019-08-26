package com.nitroxina.kanb.online

import android.util.Base64
import com.nitroxina.kanb.getKBResponse
import com.nitroxina.kanb.kanboardApi.KB_URL
import com.nitroxina.kanb.model.KBResponse
import okhttp3.*
import org.json.JSONObject



object KBClient {
    val authenticator: Authenticator
    val okHttpClient: OkHttpClient

    init {
        authenticator =  object : Authenticator {
            override fun authenticate(route: Route, response: Response): Request? {
                if (response.request().header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }

                val apiToken = "083d0489d4c640910b1285a3f2c0842a15a7d10fc1fbee38aeb01c1b8416"
                val user = "jsonrpc"
                val userApiToken = arrayOf(user, apiToken).joinToString(":")
                val xApiAuthTokenBytes = userApiToken.toByteArray(charset("utf-8"))
                val xApiAuthToken = Base64.encodeToString(xApiAuthTokenBytes, Base64.NO_WRAP)

//                val credential = Credentials.basic("admin", "admin")
                val credential = Credentials.basic("admin", apiToken)
                return response.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
//                return response.request().newBuilder()
//                    .header("X-API-Auth", xApiAuthToken)
//                    .build()

            }
        }
        okHttpClient = OkHttpClient.Builder().authenticator(authenticator).build()
    }

    fun execute(nameMethod: String) : KBResponse {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val jsonString = "{\"jsonrpc\": \"2.0\", \"method\": \"$nameMethod\", \"id\": 2}"
        val body = RequestBody.create(JSON, jsonString)

        val request = Request.Builder()
            .url("$KB_URL")
            .post(body)
            .build()
        val response = okHttpClient.newCall(request).execute()
        val jsonObj = JSONObject(response.body()!!.string())
        return jsonObj.getKBResponse()
    }
}

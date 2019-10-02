package com.nitroxina.kanb.online

import android.util.Base64
import com.nitroxina.kanb.extensions.getKBResponse
import com.nitroxina.kanb.kanboardApi.KB_URL
import okhttp3.*
import org.json.JSONObject
import java.lang.Exception


object KBClient {
    val authenticator: Authenticator
    val okHttpClient: OkHttpClient

    init {
        authenticator =  object : Authenticator {
            override fun authenticate(route: Route, response: Response): Request? {
                if (response.request().header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }

                val apiToken = "944adf13de90d24538529bd470c08523893965e31ec998c3d10f684c863e"
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

    fun execute(nameMethod: String, params: String? = null) : KBResponse {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        var paramStr = "}"
        if(params != null) {
            paramStr = ", \"params\": $params}"
        }
        val jsonString = "{\"jsonrpc\" : \"2.0\", \"method\": \"$nameMethod\", \"id\": 2 ${paramStr}"
        val body = RequestBody.create(JSON, jsonString)

        val request = Request.Builder()
            .url("$KB_URL")
            .post(body)
            .build()

//        var jsonObj :JSONObject? = null
//        try {
//
//        } catch (e: Exception) {
//            //TODO: tratar erro aqui, realizar reconexão algumas vezes, dar mensagem para o usuário após tentar
//        }

        val response = okHttpClient.newCall(request).execute()
        var jsonObj = JSONObject(response.body()!!.string())
        return jsonObj.getKBResponse()
    }
}

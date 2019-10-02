package com.nitroxina.kanb.online

import com.nitroxina.kanb.extensions.getKBResponse
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.reflect.KFunction2

class TimesExecutor {

    var times: Int = 0


    fun times(times: Int) : TimesExecutor {
        this.times = times
        return this
    }

    fun execute(f : (String, String?)-> KBResponse, param1: String, param2: String? = null) : KBResponse {
        var exception: Exception = RuntimeException("Problem on execute this function")
        for (i in 1..times) {
            try {
                val response = f(param1, param2)
                if(response.successful) {
                    return response
                }
            } catch (e: Exception) {
                exception = e
            }
        }
        return KBResponse(0, "", null, null, ConectionError(exception))
    }

    fun execute(f: KFunction2<OkHttpClient, Request, Call>, client: OkHttpClient, request: Request):  KBResponse{
        var exception: Exception = RuntimeException("Problem on execute this function")
        for (i in 1..times) {
            try {
                val response = f(client, request).execute()
                val kbResponse = JSONObject(response.body()!!.string()).getKBResponse()
                if(response.isSuccessful) {
                    return kbResponse
                }
            } catch (e: Exception) {
                exception = e
            }
        }
        return KBResponse(0, "", null, null, ConectionError(exception))
    }
}
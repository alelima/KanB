package com.nitroxina.kanb

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nitroxina.kanb.kanboardApi.GET_ME
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.persistence.SharedPreferenceKB
import okhttp3.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var server_url:String
    private lateinit var username:String
    private lateinit var kbToken: String
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val buttonSave = findViewById<MaterialButton>(R.id.save_button)
        buttonSave.setOnClickListener {
            createTestAndSaveCredential()
        }
    }

    private fun createTestAndSaveCredential() {
        val usernameFromView = findViewById<TextInputEditText>(R.id.username).text.toString()
        val kbTokenFromView = findViewById<TextInputEditText>(R.id.kb_token).text.toString()
        val serverFromView = findViewById<TextInputEditText>(R.id.server_url).text.toString()

        if(usernameFromView.isNullOrBlank() || kbTokenFromView.isNullOrBlank() || serverFromView.isNullOrBlank()) {
            //mensagem de que é necessário preencher username e token
            return
        } else {
            this.username = usernameFromView
            this.kbToken = kbTokenFromView
            this.server_url = serverFromView
        }

        //val Credential = Credential(username, kbToken)

        testCredential()
    }

    private fun testCredential() {

        if(this::username.isInitialized && this::kbToken.isInitialized) {
            val authenticator = object : Authenticator {
                override fun authenticate(route: Route, response: Response): Request? {
                    if (response.request().header("Authorization") != null) {
                        return null // Give up, we've already attempted to authenticate.
                    }

                    val credential = Credentials.basic(username, kbToken)
                    return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build()
                }
            }
            val okHttpClient = OkHttpClient.Builder().authenticator(authenticator).build()
            val request = createRequestForProfile()
            val sharedPreference = SharedPreferenceKB(this)

            object : AsyncTask<Void, Void, Profile?>() {
                override fun doInBackground(vararg params: Void?): Profile? {
                    val response = okHttpClient.newCall(request).execute()
                    val kbResponse = JSONObject(response.body()!!.string()).getKBResponse()
                    return if (kbResponse.successful) {
                        val jsonObj = JSONObject(kbResponse.result)
                        jsonObj.toProfile()
                    } else {
                        val kbError = kbResponse.error
                        null
                        //TODO mapeia os erros aqui para retornar feedback ao usuario
                    }
                }

                override fun onPostExecute(profile: Profile?) {
                    if(profile != null) {
                        sharedPreference.save(SharedPreferenceKB.SERVER_URL, this@LoginActivity.server_url)
                        sharedPreference.saveEncryption(SharedPreferenceKB.TOKEN, this@LoginActivity.kbToken)
                        sharedPreference.save(SharedPreferenceKB.USERNAME, this@LoginActivity.username)
                        Toast.makeText(this@LoginActivity,"Configurações e Username salvos",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("profile", profile)
                        startActivity(intent)
                    }
                }
            }.execute()
        }
    }

    private fun createRequestForProfile(): Request {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val jsonString = "{\"jsonrpc\" : \"2.0\", \"method\": \"$GET_ME\", \"id\": 2 }"
        val body = RequestBody.create(JSON, jsonString)
        return Request.Builder()
            .url(server_url)
            .post(body)
            .build()
    }
}

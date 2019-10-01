package com.nitroxina.kanb

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nitroxina.kanb.extensions.toProfile
import com.nitroxina.kanb.kanboardApi.GET_ME
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.persistence.SharedPreferenceKB
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private var delayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000
    private lateinit var profile: Profile

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            var intent: Intent? = null
            if(this::profile.isInitialized) {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("profile", profile)
            } else {
                intent = Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        delayHandler = Handler()
        delayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        loadProfile()

    }

    public override fun onDestroy() {
        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    private fun loadProfile() {
        if(isUsernameAndTokenStored()) {
            object : AsyncTask<Void, Void, Profile>() {
                override fun doInBackground(vararg params: Void?): Profile? {
                    val kbResponse = KBClient.execute(GET_ME)
                    if(!kbResponse.successful) {
                        AlertDialog.Builder(this@SplashActivity)
                            .setMessage(kbResponse.error?.message)
                            .setNeutralButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        return null
                    }
                    val jsonObj = JSONObject(kbResponse.result)
                    return jsonObj.toProfile()
                }

                override fun onPostExecute(profile: Profile) {
                    this@SplashActivity.profile = profile
                    this@SplashActivity.delayHandler!!.postDelayed(mRunnable, 2000)
                }
            }.execute()
        }
    }

    private fun isUsernameAndTokenStored(): Boolean {
        val sharedPreference = SharedPreferenceKB(this)
        val server_url =  sharedPreference.getValueString(SharedPreferenceKB.SERVER_URL)
        val kbToken =  sharedPreference.getDecriptValueString(SharedPreferenceKB.TOKEN)
        val username =  sharedPreference.getValueString(SharedPreferenceKB.USERNAME)
        return !(server_url.isNullOrBlank() || kbToken.isNullOrBlank() || username.isNullOrBlank())
    }


}

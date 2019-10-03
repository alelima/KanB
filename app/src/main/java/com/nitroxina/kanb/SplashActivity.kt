package com.nitroxina.kanb

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nitroxina.kanb.extensions.toProfile
import com.nitroxina.kanb.kanboardApi.GET_ME
import com.nitroxina.kanb.kanboardApi.kbToken
import com.nitroxina.kanb.kanboardApi.server_url
import com.nitroxina.kanb.kanboardApi.username
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.online.KBResponse
import com.nitroxina.kanb.online.TimesExecutor
import com.nitroxina.kanb.persistence.SharedPreferenceKB
import org.json.JSONObject
import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {
    private var delayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 200000
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

        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        if (activeNetwork?.isConnected!!)  {
            loadProfile()
        } else {
            //TODO: dar uma mensagem de que não há internte e fechar o aplicativo
        }
    }

    public override fun onDestroy() {
        if (delayHandler != null) {
            delayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    private fun loadProfile() {
        if(isUsernameAndTokenStored()) {
            ProfileAsyncTask(this).execute()
        } else {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isUsernameAndTokenStored(): Boolean {
        val sharedPreference = SharedPreferenceKB(this)
        server_url =  sharedPreference.getValueString(SharedPreferenceKB.SERVER_URL)
        kbToken =  sharedPreference.getDecriptValueString(SharedPreferenceKB.TOKEN)
        username =  sharedPreference.getValueString(SharedPreferenceKB.USERNAME)
        return !(server_url.isNullOrBlank() || kbToken.isNullOrBlank() || username.isNullOrBlank())
    }


    private class ProfileAsyncTask internal constructor(context: SplashActivity) : AsyncTask<Void, Void, KBResponse>() {
        private val activityReference: WeakReference<SplashActivity> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): KBResponse {
            val activity = activityReference.get()!!
            val kbResponse = TimesExecutor().times(3).execute(KBClient::execute, GET_ME)
            if(kbResponse.successful) {
                val jsonObj = JSONObject(kbResponse.result)
                activity.profile = jsonObj.toProfile()
                activity.delayHandler!!.postDelayed(activity.mRunnable, 200000)
            }
            return kbResponse
        }

        override fun onPostExecute(response: KBResponse) {
            val activity = activityReference.get()!!
            if(response.successful) {
                var intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("profile", activity.profile)
                activity.startActivity(intent)
                activity.finish()
                return
            }
            var message = activity.getString(R.string.standard_erro_message)
            if (response.conectionError != null) message += "\n" + activity.getString(R.string.connection_error_message)
            AlertDialog.Builder(activity)
                .setMessage(message)
                .setNeutralButton("Ok") { dialog, _ ->
                    if(response.conectionError != null) {
                        activity.finish()
                    }
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

}

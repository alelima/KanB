package com.nitroxina.kanb

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nitroxina.kanb.kanboardApi.GET_ME
import com.nitroxina.kanb.model.Profile
import com.nitroxina.kanb.online.KBClient
import org.json.JSONObject

class ProfileFragment : Fragment(){

    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.profile_layout, null)
        getProfile()
        return rootView
    }

    fun getProfile() {
        object: AsyncTask<Void, Void, Profile>(){
            override fun doInBackground(vararg params: Void?) : Profile {
                val kbResponse = KBClient.execute(GET_ME)
                val jsonObj = JSONObject(kbResponse.result)
                return jsonObj.toProfile()
            }

            override fun onPostExecute(profile: Profile) {
                this@ProfileFragment.updateView(profile)
            }
        }.execute()
    }

    fun updateView(profile: Profile) {
        this.rootView.apply {
            findViewById<TextView>(R.id.profile_user_value).text = profile.username
            findViewById<TextView>(R.id.profile_name_value).text = profile.name
        }
    }

}
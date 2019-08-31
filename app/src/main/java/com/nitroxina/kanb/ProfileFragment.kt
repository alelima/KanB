package com.nitroxina.kanb

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
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
        val profile = arguments?.get("profile") as Profile
        profile?.let { updateView(it) }
        return rootView
    }

    private fun updateView(profile: Profile) {
        this.rootView.apply {
            findViewById<TextView>(R.id.profile_user_value).text = profile.username
            findViewById<TextView>(R.id.profile_name_value).text = profile.name
        }
    }

}
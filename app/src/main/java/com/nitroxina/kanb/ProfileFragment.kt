package com.nitroxina.kanb

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.nitroxina.kanb.kanboardApi.GET_ME
import com.nitroxina.kanb.kanboardApi.KBApplicationRole
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
            findViewById<TextView>(R.id.user_value).text = profile.username
            findViewById<TextView>(R.id.name_value).text = if(profile.name.isNullOrEmpty()) {
                getString(R.string.profile_none)
            } else {
                profile.name
            }
            findViewById<TextView>(R.id.email_value).text = if(profile.email.isNullOrEmpty()) {
                getString(R.string.profile_none)
            } else {
                profile.email
            }
            findViewById<TextView>(R.id.status_value).text = if(profile.is_active) {
                getString(R.string.profile_status_active)
            } else {
                getString(R.string.profile_status_inactive)
            }
            findViewById<TextView>(R.id.role_value).text = when (profile.role) {
                KBApplicationRole.MANAGER ->  getString(R.string.profile_app_role_manager)
                KBApplicationRole.ADMINISTRATOR ->  getString(R.string.profile_app_role_admin)
                KBApplicationRole.USER ->  getString(R.string.profile_app_role_user)
                else -> ""
            }
            findViewById<TextView>(R.id.account_type_value).text = if(profile.is_ldap_user) {
                getString(R.string.profile_accout_type_remote)
            } else {
                getString(R.string.profile_accout_type_local)
            }
            findViewById<TextView>(R.id.two_factor_auth_value).text = if(profile.twofactor_activated) {
                getString(R.string.profile_two_factor_authentication_enabled)
            } else {
                getString(R.string.profile_two_factor_authentication_disabled)
            }
            findViewById<TextView>(R.id.number_failed_login_value).text = profile.nb_failed_login
            findViewById<TextView>(R.id.timezone_value).text = if(profile.timezone.isNullOrEmpty()) {
                getString(R.string.profile_aplication_dafult)
            } else {
                profile.timezone
            }
            findViewById<TextView>(R.id.language_value).text = if(profile.language.isNullOrEmpty()) {
                getString(R.string.profile_aplication_dafult)
            } else {
                profile.language
            }
            findViewById<TextView>(R.id.custom_filter_value).text = profile.filter
            findViewById<TextView>(R.id.notifications_value).text = if(profile.notifications_enabled) {
                getString(R.string.profile_notifications_enabled)
            } else {
                getString(R.string.profile_notifications_disabled)
            }
        }
    }

}
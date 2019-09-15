package com.nitroxina.kanb

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nitroxina.kanb.kanboardApi.*
import com.nitroxina.kanb.model.Profile
import okhttp3.*
import okhttp3.Route

class MainActivity : AppCompatActivity() {

    private var profile: Profile? = null

    private val fragments = mapOf(PROJECT_LIST_FRAGMENT to ::ProjectListFragment,
        TASK_LIST_FRAGMENT to ::TasksListFragment, PROFILE_FRAGMENT to ::ProfileFragment,
        EDIT_TASK_FORM_FRAGMENT to ::EditTaskDialogFragment)

    companion object {
        val PROJECT_LIST_FRAGMENT = "projectListFragment"
        val TASK_LIST_FRAGMENT = "taskListFragment"
        val PROFILE_FRAGMENT = "profileFragment"
        val EDIT_TASK_FORM_FRAGMENT = "editTaskFormFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile =  intent.getSerializableExtra("profile") as Profile
        setContentView(R.layout.activity_main)
        navigateTo(PROJECT_LIST_FRAGMENT, getProfileBundle())
        configureBottomMenu()
    }

    fun navigateTo(item: String, bundle: Bundle? = null) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        bundle?.let { fragmentInstance.arguments = it }
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragmentInstance)
            .commit()
    }

    private fun configureBottomMenu() {

        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuitem_projects -> navigateTo(PROJECT_LIST_FRAGMENT, getProfileBundle())
                R.id.menuitem_tasks -> navigateTo(TASK_LIST_FRAGMENT)
                R.id.menuitem_perfil -> navigateTo(PROFILE_FRAGMENT, getProfileBundle())
            }
            true
        }
    }

    fun getProfileBundle() : Bundle {
        val bundle = Bundle()
        bundle.putSerializable("profile", profile)
        return bundle
    }

    fun loadList(callback: ()->Unit) {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val jsonString = "{\"jsonrpc\": \"2.0\", \"method\": \"$GET_MY_PROJECTS\", \"id\": 2}"
        val body = RequestBody.create(JSON, jsonString)

        val authenticator = object : Authenticator {
            override fun authenticate(route: Route, response: Response): Request? {
                if (response.request().header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }
                println("Authenticating for response: $response")
                val credential = Credentials.basic("admin", "admin")
                return response.request().newBuilder()
                    .header("Authorization", credential)
                    .build()
            }
        }

        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
//                val okHttpClient = OkHttpClient.Builder().authenticator(authenticator).build()
//                val request = Request.Builder()
//                    .url("$KB_URL")
//                    .post(body)
//                    .build()
//                val response = okHttpClient.newCall(request).execute()
//                val jsonObj = JSONObject(response.body()!!.string())
//                var kbResponse = jsonObj.getKBResponse()
//                println(kbResponse.toString())


//                val kbResponse = KBClient.execute(GET_MY_PROJECTS_LIST)
//                val jsonList = JSONArray(kbResponse.result)
//                this@ProjectAdapter.list = mutableListOf<Project>()
//                for(i in 1..jsonList.length()){
//                    val jsonObject = jsonList[i-1] as JSONObject
//                    val project = jsonObject.toSimpleProject()
//                    list.add(project)
//                }
            }


        }.execute()
    }
}


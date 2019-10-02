package com.nitroxina.kanb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.nitroxina.kanb.model.Profile

class MainActivity : AppCompatActivity() {

    private var profile: Profile? = null
    private var atualFragment: String = ""

    private val fragments = mapOf(PROJECT_LIST_FRAGMENT to ::ProjectListFragment,
        TASK_LIST_FRAGMENT to ::TasksListFragment, PROFILE_FRAGMENT to ::ProfileFragment,
        EDIT_TASK_FORM_FRAGMENT to ::EditTaskDialogFragment, TASK_DETAIL_FRAGMENT to ::TaskDetailFragment)

    companion object {
        val PROJECT_LIST_FRAGMENT = "projectListFragment"
        val TASK_LIST_FRAGMENT = "taskListFragment"
        val PROFILE_FRAGMENT = "profileFragment"
        val EDIT_TASK_FORM_FRAGMENT = "editTaskFormFragment"
        val TASK_DETAIL_FRAGMENT = "detailTaskFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profile =  intent.getSerializableExtra("profile") as Profile
        setContentView(R.layout.activity_main)
        configureBottomMenu()
        navigateTo(PROJECT_LIST_FRAGMENT, getProfileBundle())
    }

    fun navigateTo(item: String, bundle: Bundle? = null) {
        if(atualFragment == item) {
            return
        }
        this.atualFragment = item
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        bundle?.let { fragmentInstance.arguments = it }
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragmentInstance)
            .addToBackStack(null)
            .commit()
    }

    private fun configureBottomMenu() {
        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuitem_projects -> navigateTo(PROJECT_LIST_FRAGMENT, getProfileBundle())
                R.id.menuitem_tasks -> navigateTo(TASK_LIST_FRAGMENT, getProfileBundle())
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

}


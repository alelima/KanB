package com.nitroxina.kanb

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.allyants.boardview.BoardView
import com.allyants.boardview.Item
import com.nitroxina.kanb.adapter.KBoardAdapter
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import org.json.JSONArray
import java.util.ArrayList
import android.view.MotionEvent
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.nitroxina.kanb.extensions.toBoard
import com.nitroxina.kanb.kanboardApi.MOVE_TASK_POSITION
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.online.KBResponse


class BoardActivity : AppCompatActivity() {
    private var list = ArrayList<Item>()
    private lateinit var project : Project
    var actualFragment: String? = null

    private val fragments = mapOf(BOARD_FRAGMENT to ::BoardFragment
        , TASK_DETAIL_FRAGMENT to ::TaskDetailFragment)

    companion object {
        const val BOARD_FRAGMENT = "boardFragment"
        const val TASK_DETAIL_FRAGMENT = "detailTaskFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        project = intent.getSerializableExtra("project") as Project
        supportActionBar?.title = project.name
        if(savedInstanceState == null) {
            navigateTo(BOARD_FRAGMENT, getProjectBundle())
        }
    }

    fun navigateTo(item: String, bundle: Bundle? = null) {

        supportFragmentManager.popBackStack(actualFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        bundle?.let { fragmentInstance.arguments = it }
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragmentInstance)
            .addToBackStack(null)
            .commitAllowingStateLoss()
        actualFragment = item
    }

    fun reloadBoard(board: Board) {
        this.project.board = board
        this.actualFragment = null
        val fragmentInstance: Fragment = fragments[BOARD_FRAGMENT]?.invoke()!!
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,
                fragmentInstance)
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun getProjectBundle(): Bundle? {
        val bundle = Bundle()
        bundle.putSerializable("project", project)
        return bundle
    }
}

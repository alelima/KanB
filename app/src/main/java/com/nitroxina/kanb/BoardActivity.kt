package com.nitroxina.kanb

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.allyants.boardview.BoardView
import com.allyants.boardview.Item
import com.google.android.material.button.MaterialButton
import com.nitroxina.kanb.adapter.KBoardAdapter
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import org.json.JSONArray
import java.util.ArrayList

class BoardActivity : AppCompatActivity() {
    private var list = ArrayList<Item>()
    private lateinit var project : Project
    private lateinit var board: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        project = intent.getSerializableExtra("project") as Project
        supportActionBar?.title = project.name
        loadBoard()
    }

    fun createBoard() {
        val boardView = findViewById<View>(R.id.boardView) as BoardView
        boardView.SetColumnSnap(true)
        val data = ArrayList<KBoardAdapter.KBColumn>()

        //depois ver como fazer um board por swimlane
        board.swimlanes[0].columns.forEach {
                val list = mutableListOf<Task>()
                it.tasks.forEach {
                    list.add(it)
                }
                data.add(KBoardAdapter.KBColumn(it, list as ArrayList<Any>))
        }
        val boardAdapter = KBoardAdapter(this, data, project.role, project.name)
        boardView. setAdapter(boardAdapter)
        boardView.setOnDoneListener { Log.e("scroll", "done") }
    }

    fun loadBoard() {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val parameters = "[ ${project.id} ]"
                val kbResponse = KBClient.execute(GET_BOARD, parameters)
                val jsonArray = JSONArray(kbResponse.result)
                this@BoardActivity.board = jsonArray.toBoard()
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                createBoard()
            }
        }.execute()
    }
}

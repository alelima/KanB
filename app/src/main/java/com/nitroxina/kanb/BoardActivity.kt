package com.nitroxina.kanb

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.allyants.boardview.BoardView
import com.allyants.boardview.Item
import com.allyants.boardview.SimpleBoardAdapter
import com.nitroxina.kanb.adapter.KBoardAdapter
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.kanboardApi.GET_MY_PROJECTS_LIST
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Dashboard
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import org.json.JSONArray
import org.json.JSONObject
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
        getDashboard()
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
                data.add(KBoardAdapter.KBColumn(it.id, it.title, list as ArrayList<Any>))
        }
        val boardAdapter = KBoardAdapter(this, data)
        boardView. setAdapter(boardAdapter)
        boardView.setOnDoneListener { Log.e("scroll", "done") }

//        val data2 = ArrayList<SimpleBoardAdapter.SimpleColumn>()
//        list.add(Item("I am a very long list that is not the same size as the others. I am a multiline"))
//        list.add(Item("Item 1"))
//        val empty = ArrayList<Item>()
//        val test = ArrayList<Item>()
//        test.add(Item("Item 1"))
//        test.add(Item("Item 1"))
//        test.add(Item("Item 1"))
//        test.add(Item("Item 1"))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 1", list as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 2", test as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 3", empty as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 4", empty as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 5", empty as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 6", empty as ArrayList<Any>))
//        data.add(SimpleBoardAdapter.SimpleColumn("Column 7", empty as ArrayList<Any>))
//        val boardAdapter = SimpleBoardAdapter(this, data)
//        boardView.setAdapter(boardAdapter)
//        boardView.setOnDoneListener { Log.e("scroll", "done") }
    }

    fun getDashboard() {
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

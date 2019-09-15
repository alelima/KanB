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
import org.json.JSONArray
import java.util.ArrayList
import android.view.MotionEvent
import android.widget.TextView
import com.nitroxina.kanb.kanboardApi.MOVE_TASK_POSITION
import com.nitroxina.kanb.kanboardApi.UPDATE_TASK
import com.nitroxina.kanb.online.KBResponse


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
        val boardAdapter = KBoardAdapter(this, data, project.role, Project(project.id, project.name))
        boardView. setAdapter(boardAdapter)
        boardView.setOnDoneListener { Log.e("scroll", "done") }
        boardView.setOnDragItemListener(object : BoardView.DragItemStartCallback {
            override fun startDrag(view: View, startItemPos: Int, startColumnPos: Int) {
                //
            }

            override fun changedPosition(view: View, startItemPos: Int, startColumnPos: Int,
                newItemPos: Int, newColumnPos: Int) {
                //
            }

            override fun dragging(itemView: View, event: MotionEvent) {
                //
            }

            override fun endDrag(view: View, startItemPos: Int, startColumnPos: Int,
                endItemPos: Int, endColumnPos: Int) {
                if(startItemPos != endItemPos || startColumnPos != endColumnPos) {
                    val id = view.findViewById<TextView>(R.id.task_id).text.toString()
                    val task = Task("", project.id!!, id)
                    task.position = (endItemPos + 1).toString()
                    task.column_id = (endColumnPos + 1).toString()
                    task.swimlane_id = board.swimlanes[0].id
                    updateTaskPosition(task)
                }

                Log.d("BOARD", "Posicao inicial $startItemPos, Final: $endItemPos")
                Log.d("BOARD", "Coluna inicial $startColumnPos, Final: $endColumnPos")
            }
        })
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

    fun updateTaskPosition(task: Task) {
        object: AsyncTask<Void, Void, KBResponse?>(){
            override fun doInBackground(vararg params: Void?) : KBResponse? {
                val parameters = task.toJsonMovePositionParameters()
                return KBClient.execute(MOVE_TASK_POSITION, parameters)
            }

            override fun onPostExecute(result: KBResponse?) {
                super.onPostExecute(result)
                if(!result?.successful!!) {
                    //volta o quadro para o estado anterior (como?)
                }
            }
        }.execute()
    }

//    override fun onResume() {
//        super.onResume()
//        loadBoard()
//    }
}

package com.nitroxina.kanb

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.allyants.boardview.BoardView
import com.nitroxina.kanb.adapter.KBoardAdapter
import com.nitroxina.kanb.extensions.toBoard
import com.nitroxina.kanb.kanboardApi.GET_BOARD
import com.nitroxina.kanb.kanboardApi.MOVE_TASK_POSITION
import com.nitroxina.kanb.model.Board
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.online.KBResponse
import org.json.JSONArray
import java.util.*


class BoardFragment : Fragment() {

    lateinit var mActivity: Activity
    lateinit var rootView: View
    private lateinit var project : Project

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.board_fragment_layout, null)
        project = arguments?.get("project") as Project
        createBoard()
        return rootView
    }

    fun createBoard() {
        val boardView = rootView.findViewById<View>(R.id.boardView) as BoardView
        boardView.SetColumnSnap(true)
        val data = ArrayList<KBoardAdapter.KBColumn>()

        //depois ver como fazer um board por swimlane
        project.board!!.swimlanes[0].columns.forEach {
            val list = mutableListOf<Task>()
            it.tasks.forEach { task ->
                if(!task.is_active.isNullOrEmpty() && task.is_active.toBoolean()) {
                    list.add(task)
                }
            }
            data.add(KBoardAdapter.KBColumn(it, list as ArrayList<Any>))
        }
        val boardAdapter = KBoardAdapter(rootView.context, data, Project(project.id, project.name), this::reloadBoard)
        boardView.setAdapter(boardAdapter)
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
                    task.swimlane_id = project.board!!.swimlanes[0].id
                    updateTaskPosition(task)
                    (boardView.boardAdapter as KBoardAdapter).updateColumnHeadCount(startColumnPos, endColumnPos)
                }
            }
        })
        boardView.setOnItemClickListener { p0, p1, p2 ->
            val task = project.board!!.swimlanes[0].columns[p1].tasks[p2]
            (activity!! as BoardActivity).navigateTo(BoardActivity.TASK_DETAIL_FRAGMENT, task.toBundle())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context);
        if (context is Activity) {
            mActivity = context
        }
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

    fun reloadBoard() {
        object: AsyncTask<Void, Void, KBResponse>(){

            override fun doInBackground(vararg params: Void?) : KBResponse {
                val parameters = "[ ${project.id} ]"
                val kbResponse = KBClient.execute(GET_BOARD, parameters)
                val jsonArray = JSONArray(kbResponse.result)
                project.board = jsonArray.toBoard()
                return kbResponse;
            }

            override fun onPostExecute(result: KBResponse) {
                super.onPostExecute(result)
                if (result.successful) {
                    var context = this@BoardFragment.mActivity
                    val intent = Intent(context, BoardActivity::class.java)
                    intent.putExtra("project", project)
                    context.finish()
                    context.startActivity(intent)
                }
            }
        }.execute()
    }
}
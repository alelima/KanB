package com.nitroxina.kanb

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.nitroxina.kanb.kanboardApi.GET_MY_DASHBOARD
import com.nitroxina.kanb.kanboardApi.UPDATE_TASK
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import dev.sasikanth.colorsheet.ColorSheet
import org.json.JSONArray
import org.json.JSONObject

class EditTaskDialogFragment() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.task_new_edit_form_layout, null)
        populateView(rootView)
        return rootView
    }

    private fun populateView(view: View) {
        val task = ViewModelProviders.of(activity!!).get(EditTaskViewModel::class.java).dataTask.value

        val categories = arrayOf("Urgente", "Defeito", "Melhoria")
        val arrayAdapterCategory = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, categories)
        val categoriesDropdown = view.findViewById<MaterialBetterSpinner>(R.id.spinner_category)
        categoriesDropdown.setAdapter(arrayAdapterCategory)

        val assignees = arrayOf("Alessandro", "Sarah", "Perseu")
        val arrayAdapterAssignee = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, assignees)
        val assigneeSpinnerDropdown  = view.findViewById<MaterialBetterSpinner>(R.id.spinner_assignee)
        assigneeSpinnerDropdown.setAdapter(arrayAdapterAssignee)

        val priorities = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val arrayAdapterPriority = ArrayAdapter<String>(activity, R.layout.support_simple_spinner_dropdown_item, priorities)
        val prioritySpinnerDropdown  = view.findViewById<MaterialBetterSpinner>(R.id.spinner_priority)
        prioritySpinnerDropdown.setAdapter(arrayAdapterPriority)

        val dialogTitle = getString(R.string.task_new)
        view.findViewById<MaterialTextView>(R.id.text_view_title).text = "${task?.project_name} > $dialogTitle"


        val saveButton = view.findViewById<MaterialButton>(R.id.save_button)
        saveButton.setOnClickListener {
            updateTask(task)
        }

        val colorButton = view.findViewById<MaterialButton>(R.id.color_button)
        colorButton.setOnClickListener {
            ColorSheet().colorPicker(
                colors = intArrayOf(Color.parseColor("red"), Color.parseColor("blue"), Color.parseColor("yellow")),
                listener = { color ->
                    when (color) {
                        Color.parseColor("red") -> changeColor(it, "red", task!! )
                        Color.parseColor("blue") -> changeColor(it, "blue", task!!)
                        Color.parseColor("yellow") -> changeColor(it, "yellow", task!!)
                    }
                }
            ).show(fragmentManager!!)
        }

        task.let {
            view.findViewById<TextInputEditText>(R.id.task_title).setText(it!!.title)
            view.findViewById<TextInputEditText>(R.id.task_description).setText(it!!.description)
            view.findViewById<MaterialButton>(R.id.color_button).setBackgroundColor(Color.parseColor(it!!.color_id))
        }
    }

    private fun updateTask(task: Task?) {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val parameters = task?.toJsonParameters()
                val kbResponse = KBClient.execute(UPDATE_TASK, parameters)
                if(kbResponse.successful) {
                    if(kbResponse.result?.toBoolean() == false) {
                        // TODO: Tratar problema aqui
                        println("Deu problema")
                    }
                }
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)

            }
        }.execute()
    }

    private fun changeColor(view: View, colorName: String, task: Task) {
        view.setBackgroundColor(Color.parseColor(colorName))
        task.color_id = colorName
    }

    override fun onResume() {
        val params = dialog!!.window.attributes.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        dialog!!.window.attributes = params
        super.onResume()
    }

}
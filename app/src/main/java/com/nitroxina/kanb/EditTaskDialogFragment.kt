package com.nitroxina.kanb

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.nitroxina.kanb.adapter.ItemDropdown
import com.nitroxina.kanb.adapter.ItemDropdownAdapter
import com.nitroxina.kanb.extensions.isNumber
import com.nitroxina.kanb.extensions.toCategory
import com.nitroxina.kanb.kanboardApi.CREATE_TASK
import com.nitroxina.kanb.kanboardApi.GET_ALL_CATEGORIES
import com.nitroxina.kanb.kanboardApi.GET_ASSIGNABLE_USERS
import com.nitroxina.kanb.kanboardApi.UPDATE_TASK
import com.nitroxina.kanb.model.AssignableUser
import com.nitroxina.kanb.model.Category
import com.nitroxina.kanb.model.Task
import com.nitroxina.kanb.model.TaskColor
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.online.KBResponse
import com.nitroxina.kanb.viewcomponents.DateHourMask
import com.nitroxina.kanb.viewmodel.EditTaskViewModel
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner
import dev.sasikanth.colorsheet.ColorSheet
import org.json.JSONArray
import org.json.JSONObject

class EditTaskDialogFragment(var reloadFunction: (()->Unit)? = null) : DialogFragment() {

    private lateinit var task: Task
    private lateinit var categoryList: MutableList<Category>
    private lateinit var assigUserList: MutableList<AssignableUser>
    private lateinit var rootView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.task_form_layout, null)
        task = ViewModelProviders.of(activity!!).get(EditTaskViewModel::class.java).dataTask.value!!
        loadAssignableUsers()
        loadCategories()
        populatePriorities()
        configColorButton()
        configDatePick()
        populateView()
        return rootView
    }


    private fun populateView() {

        val dialogTitle = if (task.title.isNullOrEmpty()) { getString(R.string.task_new) } else { task.title }
        rootView.findViewById<MaterialTextView>(R.id.text_view_title).text = "${task.project_name} > $dialogTitle"

        val saveButton = rootView.findViewById<MaterialButton>(R.id.save_button)
        saveButton.setOnClickListener {
            populateTask()
            if(task.id == null) {
                createTask()
            } else {
                updateTask()
            }
            activity!!.recreate()
            dismiss()
        }

        if(task.id != null) {
            rootView.findViewById<TextInputEditText>(R.id.task_title).setText(task.title)
            rootView.findViewById<TextInputEditText>(R.id.task_description).setText(task.description)
            rootView.findViewById<MaterialButton>(R.id.color_button).setBackgroundColor(Color.parseColor(
                TaskColor.hexaBackgroundColorOf(task.color_id!!)))
            rootView.findViewById<TextInputEditText>(R.id.date_start).setText(task.date_started)
            rootView.findViewById<TextInputEditText>(R.id.date_due).setText(task.date_due)
            rootView.findViewById<TextInputEditText>(R.id.task_estimate_hours).setText(task.time_estimated)
            rootView.findViewById<TextInputEditText>(R.id.task_time_spent).setText(task.time_spent)
            rootView.findViewById<TextInputEditText>(R.id.task_complexity).setText(task.score.toString())
            rootView.findViewById<TextInputEditText>(R.id.task_reference).setText(task.reference)
        }
    }

    private fun configDatePick() {
        val dateStartTextInput = rootView.findViewById<TextInputEditText>(R.id.date_start)
        dateStartTextInput.addTextChangedListener(DateHourMask())
        val dateDueTextInput = rootView.findViewById<TextInputEditText>(R.id.date_due)
        dateDueTextInput.addTextChangedListener(DateHourMask())
    }

    private fun configColorButton() {
        val colorButton = rootView.findViewById<MaterialButton>(R.id.color_button)
        colorButton.setOnClickListener {
            it as MaterialButton
            ColorSheet().colorPicker(
                colors = intArrayOf(
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("yellow")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("blue")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("green")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("purple")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("red")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("orange")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("grey")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("brown")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("deep_orange")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("dark_grey")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("pink")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("teal")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("cyan")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("lime")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("light_green")),
                    Color.parseColor(TaskColor.hexaBackgroundColorOf("amber"))
                ),
                listener = { color ->
                    when (color) {
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("yellow")) -> changeColor(it, "yellow")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("blue")) -> changeColor(it, "blue")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("green")) -> changeColor(it, "green")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("purple")) -> changeColor(it, "purple")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("red")) -> changeColor(it, "red")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("orange")) -> changeColor(it, "orange")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("grey")) -> changeColor(it, "grey")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("brown")) -> changeColor(it, "brown")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("deep_orange")) -> changeColor(it, "deep_orange")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("dark_grey")) -> changeColor(it, "dark_grey")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("pink")) -> changeColor(it, "pink")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("teal")) -> changeColor(it, "teal")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("cyan")) -> changeColor(it, "cyan")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("lime")) -> changeColor(it, "lime")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("light_green")) -> changeColor(it, "light_green")
                        Color.parseColor(TaskColor.hexaBackgroundColorOf("amber")) -> changeColor(it, "amber")
                    }
                }
            ).show(fragmentManager!!)
        }
    }

    private fun populateCategories() {
        val categoriesDropdown = rootView.findViewById<MaterialBetterSpinner>(R.id.spinner_category)
        if(categoryList.isEmpty()) {
            categoriesDropdown.visibility = View.GONE
            return
        }
        val arrayAdapterCategory = ItemDropdownAdapter(activity!!,
            R.layout.support_simple_spinner_dropdown_item, categoryList as MutableList<ItemDropdown>)
        categoriesDropdown.setAdapter(arrayAdapterCategory)
        categoriesDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.adapter.getItem(position) as ItemDropdown?
            categoriesDropdown.setText(selectedItem?.name, false)
            task.category_id = selectedItem?.id?.toInt()
        }
        categoriesDropdown.setText(task.category_name, false)
    }

    private fun populateAssigneeUsers() {
        val assigneeSpinnerDropdown  = rootView.findViewById<MaterialBetterSpinner>(R.id.spinner_assignee)
        if(assigUserList.isEmpty()) {
            assigneeSpinnerDropdown.visibility = View.GONE
            return
        }
        val arrayAdapterAssignee = ItemDropdownAdapter(activity!!,
            R.layout.support_simple_spinner_dropdown_item, assigUserList as MutableList<ItemDropdown>)
        assigneeSpinnerDropdown.setAdapter(arrayAdapterAssignee)
        assigneeSpinnerDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.adapter.getItem(position) as ItemDropdown?
            assigneeSpinnerDropdown.setText(selectedItem?.name, false)
            task.owner_id = selectedItem?.id?.toInt()
        }
        assigneeSpinnerDropdown.setText(task.assignee_name, false)
    }

    private fun populatePriorities() {
        val priorities = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val arrayAdapterPriority = ArrayAdapter<String>(requireContext(), R.layout.support_simple_spinner_dropdown_item, priorities)
        val prioritySpinnerDropdown  = rootView.findViewById<MaterialBetterSpinner>(R.id.spinner_priority)
        prioritySpinnerDropdown.setAdapter(arrayAdapterPriority)
        prioritySpinnerDropdown.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.adapter.getItem(position) as String
            prioritySpinnerDropdown.setText(selectedItem, false)
            task.priority = selectedItem.toInt()
        }
        prioritySpinnerDropdown.setText(task.priority?.toString(), false)
    }

    private fun createTask() {
        object: AsyncTask<Void, Void, KBResponse>(){
            override fun doInBackground(vararg params: Void?) : KBResponse {
                val parameters = this@EditTaskDialogFragment.task.toJsonCreateParameters()
                val kbResponse = KBClient.execute(CREATE_TASK, parameters)
                if(kbResponse.successful) {
                    this@EditTaskDialogFragment.task.id = kbResponse.result
                }
                return kbResponse
            }

            override fun onPostExecute(result: KBResponse) {
                super.onPostExecute(result)
                if(result.successful) {
                    if(this@EditTaskDialogFragment.reloadFunction != null) {
                        this@EditTaskDialogFragment.reloadFunction!!()
                    }
                }
            }
        }.execute()
    }

    private fun updateTask() {
        object: AsyncTask<Void, Void, KBResponse>(){
            override fun doInBackground(vararg params: Void?) : KBResponse{
                val parameters = this@EditTaskDialogFragment.task.toJsonUpdateParameters()
                val kbResponse = KBClient.execute(UPDATE_TASK, parameters)
                if(kbResponse.successful) {
                    if(kbResponse.result?.toBoolean() == false) {
                        // TODO: Tratar problema aqui
                        println("Deu problema")
                    }
                }
                return kbResponse
            }

            override fun onPostExecute(result: KBResponse) {
                super.onPostExecute(result)
                if(result.successful) {
                    if(this@EditTaskDialogFragment.reloadFunction != null) {
                        this@EditTaskDialogFragment.reloadFunction!!()
                    }
                }
            }
        }.execute()
    }

    private fun loadCategories() {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val parameters = "{\"project_id\": ${task.project_id}}"
                val kbResponse = KBClient.execute(GET_ALL_CATEGORIES, parameters)
                if(kbResponse.successful) {
                    this@EditTaskDialogFragment.categoryList = mutableListOf<Category>()
                    val jsonList = JSONArray(kbResponse.result)
                    for(i in 1..jsonList.length()){
                        val jsonObject = jsonList[i-1] as JSONObject
                        val category = jsonObject.toCategory()
                        this@EditTaskDialogFragment.categoryList.add(category)
                        if(category.id == this@EditTaskDialogFragment.task.category_id?.toString()) {
                            task.category_name = category.name
                        }
                    }
                }
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                populateCategories()
            }
        }.execute()
    }

    private fun loadAssignableUsers() {
        object: AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                val parameters = "[\"${task.project_id}\"]"
                val kbResponse = KBClient.execute(GET_ASSIGNABLE_USERS, parameters)
                if(kbResponse.successful) {
                    val jsonObject = JSONObject(kbResponse.result)
                    var keysList = jsonObject.keys()
                    this@EditTaskDialogFragment.assigUserList = mutableListOf<AssignableUser>()
                    keysList.forEach {
                        val id = it
                        val name = jsonObject.getString(id)
                        this@EditTaskDialogFragment.assigUserList.add(AssignableUser(id, name))
                    }
                }
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)
                populateAssigneeUsers()
            }
        }.execute()
    }

    private fun changeColor(button: MaterialButton, colorName: String) {
        button.setBackgroundColor(Color.parseColor(TaskColor.hexaBackgroundColorOf(colorName)))
        task.color_id = colorName
    }

    override fun onResume() {
        if(dialog != null && dialog!!.window != null) {
            val params = dialog!!.window.attributes.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            dialog!!.window.attributes = params
        }
        super.onResume()
    }

    private fun populateTask() {
        task.title = rootView.findViewById<TextInputEditText>(R.id.task_title).text.toString()
        task.description = rootView.findViewById<TextInputEditText>(R.id.task_description).text.toString()
        task.date_started = rootView.findViewById<TextInputEditText>(R.id.date_start).text.toString()
        task.date_due = rootView.findViewById<TextInputEditText>(R.id.date_due).text.toString()
        task.time_estimated = rootView.findViewById<TextInputEditText>(R.id.task_estimate_hours).text.toString()
        task.time_spent = rootView.findViewById<TextInputEditText>(R.id.task_time_spent).text.toString()
        val score = rootView.findViewById<TextInputEditText>(R.id.task_complexity).text.toString()
        task.score = if (score.isNumber()) { score.toInt() } else { null }
        task.reference = rootView.findViewById<TextInputEditText>(R.id.task_reference).text.toString()
    }

}

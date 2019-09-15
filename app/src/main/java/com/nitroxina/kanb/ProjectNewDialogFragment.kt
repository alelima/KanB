package com.nitroxina.kanb

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.nitroxina.kanb.kanboardApi.CREATE_PROJECT
import com.nitroxina.kanb.model.Project
import com.nitroxina.kanb.online.KBClient
import com.nitroxina.kanb.online.KBResponse

class ProjectNewDialogFragment(private val ownerId: String) : DialogFragment() {
    lateinit var project: Project
    lateinit var rootView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.project_new_form_layout, null)
        val saveButton = rootView.findViewById<MaterialButton>(R.id.save_button)
        saveButton.setOnClickListener {
            populateProject(rootView)
            createProject()
            activity!!.recreate()
            dismiss()
        }

        return rootView
    }

    private fun populateProject(rootView: View) {
        project = Project()
        project.owner_id = this.ownerId
        project.name = rootView.findViewById<TextInputEditText>(R.id.project_name).text.toString()
        project.description = rootView.findViewById<TextInputEditText>(R.id.project_description).text.toString()
        project.identifier = rootView.findViewById<TextInputEditText>(R.id.project_identifier).text.toString()
    }

    private fun createProject() {
        object: AsyncTask<Void, Void, KBResponse?>(){
            override fun doInBackground(vararg params: Void?) : KBResponse? {
                val parameters = this@ProjectNewDialogFragment.project.toJsonCreateParameters()
                return KBClient.execute(CREATE_PROJECT, parameters)
            }

            override fun onPostExecute(result: KBResponse?) {
                super.onPostExecute(result)
                var message = "Projeto criado com sucesso"
                if(!result?.successful!!) {
                    message = "Erro ao tentar criar o projeto"
                }

                Snackbar
                    .make(
                        rootView,
                        message,
                        Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", {})
                    .show()
            }
        }.execute()
    }

    //most important to size the dialog
    override fun onResume() {
        val params = dialog!!.window.attributes.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        dialog!!.window.attributes = params
        super.onResume()
    }




}
package com.nitroxina.kanb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.google.android.material.textview.MaterialTextView

class ItemDropdownAdapter(context: Context, val resource: Int, objects: MutableList<ItemDropdown>) :
    ArrayAdapter<ItemDropdown>(context, resource, objects), Filterable {

    private var filteredObjects: List<ItemDropdown> = objects

    override fun getCount(): Int {
        return filteredObjects.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: MaterialTextView = convertView as MaterialTextView? ?: LayoutInflater.from(context)
            .inflate(resource, parent, false) as MaterialTextView
        view.text = filteredObjects[position].name
        return view
    }

    override fun getItem(position: Int): ItemDropdown? {
        return filteredObjects[position]
    }

    override fun getItemId(position: Int): Long {
        return filteredObjects[position].id.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    filteredObjects
                else
                    filteredObjects.filter {
                        it.name.toLowerCase().contains(queryString)
                    }
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredObjects = results?.values as List<ItemDropdown>
                notifyDataSetChanged()
            }
        }
    }
}
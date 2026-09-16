package com.medapp.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medtrixa.app.R

class DrugToolsFragment : Fragment(R.layout.fragment_drug_tools) {

    interface DrugToolClickListener {
        fun onDrugToolSelected(tool: DrugTool)
    }

    private var listener: DrugToolClickListener? = null

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        listener = context as? DrugToolClickListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.drugToolsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DrugToolAdapter(DrugToolsRepository.getTools()) { tool ->
            listener?.onDrugToolSelected(tool)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

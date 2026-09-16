package com.medapp.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medtrixa.app.R

class DrugToolAdapter(
    private val items: List<DrugTool>,
    private val onClick: (DrugTool) -> Unit
) : RecyclerView.Adapter<DrugToolAdapter.ToolViewHolder>() {

    inner class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iconView)
        val title: TextView = itemView.findViewById(R.id.titleView)
        val subtitle: TextView = itemView.findViewById(R.id.subtitleView)

        fun bind(tool: DrugTool) {
            icon.setImageResource(tool.iconRes)
            title.text = tool.title
            subtitle.text = tool.subtitle
            itemView.setOnClickListener { onClick(tool) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drug_tool, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

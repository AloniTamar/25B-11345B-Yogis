package com.tamara.a25b_11345b_yogis.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding

class FlowAdapter(
    private val items: List<Flow>,
    private val onClick: (Flow) -> Unit
) : RecyclerView.Adapter<FlowAdapter.FlowViewHolder>() {

    inner class FlowViewHolder(private val binding: ItemPoseBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(flow: Flow) {
            binding.tvLevelTitle.text = flow.flowName
            binding.root.setOnClickListener { onClick(flow) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowViewHolder {
        val binding = ItemPoseBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return FlowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
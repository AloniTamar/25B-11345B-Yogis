package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding
import com.tamara.a25b_11345b_yogis.data.model.Pose

class PoseAdapter(
    private var items: List<Pose>,
    private val onClick: (Pose) -> Unit
) : RecyclerView.Adapter<PoseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPoseBinding)
        : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(pose: Pose) {
            binding.tvLevelTitle.text = pose.name
            binding.tvLevelSubtitle.text = pose.level.toString() + " ● " + pose.category.toString()
            binding.root.setOnClickListener {
                onClick(pose)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPoseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<Pose>) {
        items = newItems
        notifyDataSetChanged()
    }
}

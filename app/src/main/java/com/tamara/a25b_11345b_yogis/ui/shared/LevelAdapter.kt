package com.tamara.a25b_11345b_yogis.ui.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding

class LevelAdapter(
    private val items: List<Pose.Level>,
    private val onClick: (Pose.Level) -> Unit
) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPoseBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(level: Pose.Level) {
            val text = when(level) {
                Pose.Level.beginner -> "Beginner"
                Pose.Level.intermediate -> "Intermediate"
                Pose.Level.advanced -> "Advanced"
            }
            binding.tvLevelTitle.text = text
            binding.tvLevelSubtitle.visibility = View.GONE
            binding.root.setOnClickListener { onClick(level) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPoseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}

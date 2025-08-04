package com.tamara.a25b_11345b_yogis.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class TimelineAdapter(
    private val items: List<Pose>,
    private val onClick: (Pose) -> Unit = {}
) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBasicTimelineBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(pose: Pose) {
            binding.tvTitle.text = pose.name
            binding.tvSub.text = pose.description
            binding.tvDuration.text = pose.duration?.let { "$it min" }
                ?: pose.repetitions?.let { "$it reps" } ?: ""
            binding.root.setOnClickListener { onClick(pose) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBasicTimelineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

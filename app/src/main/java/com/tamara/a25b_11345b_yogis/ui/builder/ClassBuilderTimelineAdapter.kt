package com.tamara.a25b_11345b_yogis.ui.builder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class ClassBuilderTimelineAdapter(
    private val poses: List<Pose>
) : RecyclerView.Adapter<ClassBuilderTimelineAdapter.TimelineViewHolder>() {

    inner class TimelineViewHolder(private val binding: ItemBasicTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pose: Pose) {
            binding.tvTitle.text = pose.name
            // Example: Show duration or repetitions
            binding.tvDuration.text = pose.duration?.let { "$it min" }
                ?: pose.repetitions?.let { "$it reps" }
                        ?: ""
            // You can add more here (category, etc)
            binding.tvSub.text = pose.category.name.replace('_', ' ').replaceFirstChar { it.uppercase() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemBasicTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(poses[position])
    }

    override fun getItemCount(): Int = poses.size
}

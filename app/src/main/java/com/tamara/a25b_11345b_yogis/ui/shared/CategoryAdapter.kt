package com.tamara.a25b_11345b_yogis.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding

class CategoryAdapter(
    private val items: List<Pose.Category>,
    private val onClick: (Pose.Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPoseBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Pose.Category) {
            binding.tvLevelTitle.text = category.name
                .replace(Regex("([a-z])([A-Z])"), "$1 $2")
                .replaceFirstChar { it.uppercase() }

            binding.root.setOnClickListener {
                onClick(category)
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

    override fun getItemCount() = items.size
}

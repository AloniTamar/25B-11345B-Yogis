package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding

class PlanListAdapter(
    private val plans: List<ClassPlan>,
    private val onClick: (ClassPlan) -> Unit
) : RecyclerView.Adapter<PlanListAdapter.PlanVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, vt: Int): PlanVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPoseBinding.inflate(inflater, parent, false)
        return PlanVH(binding)
    }
    override fun getItemCount() = plans.size

    override fun onBindViewHolder(holder: PlanVH, pos: Int) = holder.bind(plans[pos])

    inner class PlanVH(val b: ItemPoseBinding) : RecyclerView.ViewHolder(b.root) {
        @SuppressLint("SetTextI18n")
        fun bind(plan: ClassPlan) {
            b.tvLevelTitle.text = plan.planName
            b.tvLevelSubtitle.text = "${plan.duration} min • ${plan.level}"
            b.root.setOnClickListener { onClick(plan) }
        }
    }
}

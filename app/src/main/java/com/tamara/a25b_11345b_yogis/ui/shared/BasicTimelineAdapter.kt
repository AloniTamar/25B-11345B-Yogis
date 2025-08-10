package com.tamara.a25b_11345b_yogis.ui.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ItemBasicTimelineBinding

class BasicTimelineAdapter(
    private val items: List<ClassPlanElement>,
    private val onPoseClick: ((Pose) -> Unit)?,
    private val onFlowClick: ((Flow) -> Unit)?
) : RecyclerView.Adapter<BasicTimelineAdapter.ElementHolder>() {

    constructor(items: List<ClassPlanElement>, onPoseClick: (Pose) -> Unit)
            : this(items, onPoseClick, null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ElementHolder(ItemBasicTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onPoseClick, onFlowClick)

    override fun onBindViewHolder(holder: ElementHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    class ElementHolder(
        private val b: ItemBasicTimelineBinding,
        private val onPoseClick: ((Pose) -> Unit)?,
        private val onFlowClick: ((Flow) -> Unit)?
    ) : RecyclerView.ViewHolder(b.root) {
        @SuppressLint("SetTextI18n")
        fun bind(e: ClassPlanElement) {
            when (e) {
                is ClassPlanElement.PoseElement -> {
                    val d = e.pose.duration ?: 0
                    b.tvDuration.text = "$d seconds"
                    b.tvTitle.text    = e.pose.name
                    b.tvSub.text      = "${e.pose.level}"
                    b.root.setOnClickListener { onPoseClick?.invoke(e.pose) }
                }
                is ClassPlanElement.FlowElement -> {
                    b.tvDuration.text = "${e.flow.recommendedRounds} rounds"
                    b.tvTitle.text    = e.flow.flowName
                    b.tvSub.text      = "${e.flow.level}"
                    b.root.setOnClickListener { onFlowClick?.invoke(e.flow) }
                }
            }
        }
    }
}
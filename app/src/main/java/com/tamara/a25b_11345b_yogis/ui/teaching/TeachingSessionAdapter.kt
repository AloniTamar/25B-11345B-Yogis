package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tamara.a25b_11345b_yogis.data.model.TeachingSession
import com.tamara.a25b_11345b_yogis.databinding.ItemPoseBinding
import java.text.DateFormat
import java.util.*

class TeachingSessionAdapter(
    private val sessions: List<TeachingSession>,
    private val onClick: (TeachingSession) -> Unit
) : RecyclerView.Adapter<TeachingSessionAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemPoseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun getItemCount(): Int = sessions.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(sessions[position])
    }

    inner class Holder(
        private val binding: ItemPoseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(session: TeachingSession) {
            // Use tv_level_title / tv_level_subtitle from item_pose.xml
            binding.tvLevelTitle.text = DateFormat
                .getDateTimeInstance()
                .format(Date(session.startedAt))
            binding.tvLevelSubtitle.text = "Session ID: ${session.sessionId}"

            // Chevron is already in your layout; just wire click
            binding.root.setOnClickListener {
                onClick(session)
            }
        }
    }
}

package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import com.tamara.a25b_11345b_yogis.databinding.FlowViewBinding
import com.tamara.a25b_11345b_yogis.ui.shared.TimelineAdapter

class ClassPlanViewFragment : Fragment() {

    private var _binding: FlowViewBinding? = null
    private val binding get() = _binding!!
    private val repo = ClassPlanRepository()

    private var currentOrder = 0
    private lateinit var timelineAdapter: TimelineAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FlowViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val planId = requireArguments().getString("planId")
            ?: throw IllegalStateException("ClassPlanViewFragment needs a planId")

        binding.chipgroupCtInfo.visibility = View.GONE
        binding.rvTimeline.visibility = View.GONE

        repo.observePlan(planId,
            onPlanLoaded = { plan ->
                if (plan == null) {
                    Toast.makeText(requireContext(), "No such plan", Toast.LENGTH_SHORT).show()
                    return@observePlan
                }

                binding.tvCtTitle.text       = plan.planName
                binding.chipDuration.text    = "${plan.duration} min"
                binding.chipLevel.text       = plan.level
                binding.chipgroupCtInfo.visibility = View.VISIBLE

                binding.rvTimeline.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    timelineAdapter = TimelineAdapter(
                        items = plan.elements,
                        currentOrder = currentOrder,
                        onItemClick = { index ->
                            if (index < plan.elements.size - 1) {
                                currentOrder = index + 1
                                timelineAdapter.currentOrder = currentOrder
                                timelineAdapter.notifyItemChanged(currentOrder - 1)
                                timelineAdapter.notifyItemChanged(currentOrder)
                            } else {
                                Toast.makeText(requireContext(), "Class finished!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    binding.rvTimeline.adapter = timelineAdapter

                    visibility = View.VISIBLE
                }
            },
            onError = { e ->
                Toast.makeText(requireContext(),
                    "Failed to load plan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

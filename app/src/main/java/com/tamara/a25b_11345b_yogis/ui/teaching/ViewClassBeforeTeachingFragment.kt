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
import com.tamara.a25b_11345b_yogis.databinding.TeachingModeViewClassBinding
import com.tamara.a25b_11345b_yogis.ui.library.PoseDetailFragment
import com.tamara.a25b_11345b_yogis.ui.library.PoseLibraryFlowDetailFragment
import com.tamara.a25b_11345b_yogis.ui.shared.BasicTimelineAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class ViewClassBeforeTeachingFragment : Fragment() {
    private var _binding: TeachingModeViewClassBinding? = null
    private val binding get() = _binding!!
    private val repo = ClassPlanRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = TeachingModeViewClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val planId = requireArguments().getString("planId")
        if (planId == null) {
            Toast.makeText(requireContext(), "Class plan not found.", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return
        }

        repo.observePlan(planId,
            onPlanLoaded = { plan ->
                if (plan == null) {
                    Toast.makeText(requireContext(), "Class plan not found.", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                    return@observePlan
                }

                binding.tvCtTitle.text = plan.planName
                binding.chipDuration.text = "${plan.duration} min"
                binding.chipLevel.text = plan.level

                binding.rvTimeline.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = BasicTimelineAdapter(
                        plan.elements,
                        onPoseClick = { pose ->
                            val id = pose.id
                            if (id.isBlank()) {
                                Toast.makeText(requireContext(), "Missing pose id", Toast.LENGTH_SHORT).show()
                            } else {
                                navigateSmoothly(PoseDetailFragment.newInstance(id))
                            }
                        },
                        onFlowClick = { flow ->
                            val fid = flow.flowId
                            if (fid.isBlank()) {
                                Toast.makeText(requireContext(), "Missing flow id", Toast.LENGTH_SHORT).show()
                            } else {
                                navigateSmoothly(PoseLibraryFlowDetailFragment.newInstance(fid))
                            }
                        }
                    )
                    visibility = View.VISIBLE
                }
            },
            onError = { e ->
                Toast.makeText(requireContext(), "Failed to load class plan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )

        binding.btnSave.setOnClickListener {
            val planId = requireArguments().getString("planId")
            val fragment = TeachingModeFragment()
            fragment.arguments = Bundle().apply { putString("planId", planId) }
            navigateSmoothly(fragment)
        }

        binding.tvBackMenu.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnCtBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

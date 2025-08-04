package com.tamara.a25b_11345b_yogis.ui.builder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddFlowBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class ClassBuilderAddFlowFragment : Fragment() {

    companion object {
        private const val ARG_FLOW_ID = "flow_id"

        fun newInstance(flowId: String): ClassBuilderAddFlowFragment {
            val fragment = ClassBuilderAddFlowFragment()
            val args = Bundle()
            args.putString(ARG_FLOW_ID, flowId)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: ClassBuilderAddFlowBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val flowId = arguments?.getString(ARG_FLOW_ID) ?: return
        val flow = FlowRepository.getById(flowId)
        if (flow == null) {
            // TODO: handle error
            return
        }

        // Set up the timeline with your custom adapter
        val rv = binding.rvTimeline
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = ClassBuilderTimelineAdapter(flow.poses)

        binding.btnCtBack.setOnClickListener {
            navigateSmoothly(ClassBuilderFlowListFragment())
        }
        binding.btnAddFlow.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        // Show basic info
        binding.tvCtTitle.text = flow.flowName
        binding.chipDuration.text = "${flow.recommendedRounds} Rounds"
        binding.chipLevel.text = when (flow.level) {
            Pose.Level.beginner     -> "Beginner"
            Pose.Level.intermediate -> "Intermediate"
            Pose.Level.advanced     -> "Advanced"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

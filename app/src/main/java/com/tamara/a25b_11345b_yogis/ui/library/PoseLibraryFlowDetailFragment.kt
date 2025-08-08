package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.databinding.FlowViewBinding
import com.tamara.a25b_11345b_yogis.ui.shared.BasicTimelineAdapter
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PoseLibraryFlowDetailFragment : Fragment() {

    companion object {
        private const val ARG_FLOW_ID = "flow_id"

        fun newInstance(flowId: String) = PoseLibraryFlowDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_FLOW_ID, flowId)
            }
        }
    }

    private var _binding: FlowViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FlowViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnCtBack)

        val flowId = requireArguments().getString(ARG_FLOW_ID)
        val flow = FlowRepository.getAll().firstOrNull { it.flowId == flowId }

        if (flow == null) {
            Toast.makeText(requireContext(), "Flow not found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Set fields
        binding.tvCtTitle.text = flow.flowName

        // Show poses inside the flow, if available
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext())
        val poseElements: List<ClassPlanElement> = flow.poses.map { pose ->
            ClassPlanElement.PoseElement(pose)
        }
        binding.rvTimeline.adapter = BasicTimelineAdapter(poseElements)    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddFlowBinding
import com.tamara.a25b_11345b_yogis.ui.shared.BasicTimelineAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel

class ClassBuilderAddFlowFragment : Fragment() {

    companion object {
        private const val ARG_FLOW_ID = "flow_id"
        fun newInstance(flowId: String) = ClassBuilderAddFlowFragment().apply {
            arguments = Bundle().apply { putString(ARG_FLOW_ID, flowId) }
        }
    }

    private var _binding: ClassBuilderAddFlowBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wireBack(binding.btnCtBack)

        val flowId = requireArguments().getString(ARG_FLOW_ID)
        if (flowId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Missing flow id", Toast.LENGTH_SHORT).show()
            return
        }

        val flow = FlowRepository.getById(flowId)
        if (flow == null) {
            Toast.makeText(requireContext(), "Flow not found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Title/metadata
        binding.tvCtTitle.text = flow.flowName

        // ✅ Show the actual poses inside the flow (like the library screen)
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext())
        val poseElements = flow.poses.map { ClassPlanElement.PoseElement(it) }
        binding.rvTimeline.adapter = BasicTimelineAdapter(poseElements)

        // Add flow to current class plan
        binding.btnAddFlow.setOnClickListener {
            viewModel.addFlow(flow)
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

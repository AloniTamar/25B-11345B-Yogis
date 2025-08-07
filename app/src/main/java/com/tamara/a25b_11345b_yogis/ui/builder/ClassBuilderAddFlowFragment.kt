package com.tamara.a25b_11345b_yogis.ui.builder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddFlowBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.ui.shared.ClassPlanAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.viewmodel.ClassPlanBuilderViewModel

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

    private val viewModel: ClassPlanBuilderViewModel by activityViewModels()
    private var _binding: ClassBuilderAddFlowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val flowId = arguments?.getString(ARG_FLOW_ID) ?: return
        val flow: Flow = FlowRepository.getById(flowId)
            ?: run {
                // TODO: handle missing flow
                navigateSmoothly(ClassBuilderActionsFragment())
                return
            }

        // show the flow details in the timeline
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ClassPlanAdapter(listOf(ClassPlanElement.FlowElement(flow)))
        }

        // display basic info
        binding.tvCtTitle.text = flow.flowName
        binding.chipDuration.text = "${flow.recommendedRounds} rounds"
        binding.chipLevel.text = when (flow.level) {
            Pose.Level.beginner     -> "Beginner"
            Pose.Level.intermediate -> "Intermediate"
            Pose.Level.advanced     -> "Advanced"
        }

        // back button
        binding.btnCtBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        // add flow button
        binding.btnAddFlow.setOnClickListener {
            viewModel.addFlow(flow)
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        binding.tvBackMenu.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    navigateSmoothly(MainLoggedInFragment())
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderTempViewBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import android.widget.Toast
import com.tamara.a25b_11345b_yogis.data.firebase.ClassPlanBuilderManager
import com.tamara.a25b_11345b_yogis.data.model.ClassPlanElement
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel

class ClassBuilderTempViewFragment : Fragment() {

    private var _binding: ClassBuilderTempViewBinding? = null
    private val binding get() = _binding!!

    private val builderManager = ClassPlanBuilderManager()

    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderTempViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnCtBack)
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext())

        viewModel.elements.observe(viewLifecycleOwner) { elementList ->
            binding.rvTimeline.adapter = ClassBuilderTimelineAdapter(elementList)
        }

        binding.btnSave.setOnClickListener {
            val name     = viewModel.className.value ?: "Untitled class"
            val duration = viewModel.durationMinutes.value ?: 0
            val level    = viewModel.level.value ?: Pose.Level.beginner


            if (name.isBlank() || duration <= 0) {
                Toast.makeText(requireContext(), "Missing name or duration", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            builderManager.setClassInfo(name, duration, level)

            val elements = viewModel.elements.value.orEmpty()
            elements.forEach { el ->
                when (el) {
                    is ClassPlanElement.PoseElement -> builderManager.addPose(el.pose)
                    is ClassPlanElement.FlowElement -> builderManager.addFlow(el.flow)
                }
            }

            builderManager.savePlan { err ->
                if (err == null) {
                    Toast.makeText(requireContext(), "Class saved ✅", Toast.LENGTH_SHORT).show()
                    navigateSmoothly(MainLoggedInFragment())
                } else {
                    Toast.makeText(requireContext(), "Save failed: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvBackMenu.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.clearPlan()
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

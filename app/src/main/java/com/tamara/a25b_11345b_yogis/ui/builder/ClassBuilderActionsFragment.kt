package com.tamara.a25b_11345b_yogis.ui.builder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderActionsBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel
import kotlin.getValue

class ClassBuilderActionsFragment : Fragment() {
    private var _binding: ClassBuilderActionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCaBack.setOnClickListener {
            navigateSmoothly(ClassBuilderFragment())
        }

        viewModel.className.observe(viewLifecycleOwner) { name ->
            binding.tvCaTitle.text = name?.trim().orEmpty()
        }
        viewModel.durationMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.tvCaDuration.text = "${minutes ?: 0} min ● ${viewModel.level.value?.name ?: "Any Level"}"
        }

        binding.tvBackMain.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    navigateSmoothly(MainLoggedInFragment())
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
        binding.cardAddPose.setOnClickListener {
            navigateSmoothly(ClassBuilderAddPoseFragment())
        }

        binding.cardAddFlow.setOnClickListener {
            navigateSmoothly(ClassBuilderFlowListFragment())
        }

        binding.btnViewPlan.setOnClickListener {
            navigateSmoothly(ClassBuilderTempViewFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
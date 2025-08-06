package com.tamara.a25b_11345b_yogis.ui.builder

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
import com.tamara.a25b_11345b_yogis.viewmodel.ClassPlanBuilderViewModel
import kotlin.getValue

class ClassBuilderActionsFragment : Fragment() {

    private val viewModel: ClassPlanBuilderViewModel by activityViewModels()
    private var _binding: ClassBuilderActionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCaBack.setOnClickListener {
            navigateSmoothly(ClassBuilderFragment())
        }

        binding.tvBackMain.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.resetAll()
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
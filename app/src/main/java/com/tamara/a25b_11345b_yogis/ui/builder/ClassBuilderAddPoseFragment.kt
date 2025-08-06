package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddNewPoseBinding
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddPoseContainerBinding
import com.tamara.a25b_11345b_yogis.ui.library.PosesByLevelsFragment
import com.tamara.a25b_11345b_yogis.ui.library.PosesByTypesFragment
import com.tamara.a25b_11345b_yogis.ui.library.PosesListFragment
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.viewmodel.ClassPlanBuilderViewModel
import java.util.UUID

class ClassBuilderAddPoseFragment : Fragment() {

    private val viewModel: ClassPlanBuilderViewModel by activityViewModels()

    private var _binding: ClassBuilderAddPoseContainerBinding? = null
    private val binding get() = _binding!!

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        // TODO: handle selected image URI later
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddPoseContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnApBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
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

        fun loadPage(@LayoutRes layoutRes: Int) {
            binding.flAddPoseContent.removeAllViews()

            if (layoutRes == R.layout.class_builder_add_new_pose) {
                val newBinding = ClassBuilderAddNewPoseBinding
                    .inflate(layoutInflater, binding.flAddPoseContent, false)
                binding.flAddPoseContent.addView(newBinding.root)
                initNewPosePageWithBinding(newBinding)
            } else {
                val page = layoutInflater.inflate(
                    R.layout.class_builder_add_pose,
                    binding.flAddPoseContent,
                    false
                ).also { binding.flAddPoseContent.addView(it) }

                page.findViewById<MaterialCardView>(R.id.card_ap_view_by_levels)
                    .setOnClickListener {
                        navigateSmoothly(PosesByLevelsFragment.newInstanceForBuilder())
                    }
                page.findViewById<MaterialCardView>(R.id.card_ap_view_by_types)
                    .setOnClickListener {
                        navigateSmoothly(PosesByTypesFragment.newInstanceForBuilder())
                    }
                page.findViewById<MaterialCardView>(R.id.card_ap_all_poses)
                    .setOnClickListener {
                        navigateSmoothly(PosesListFragment.newInstanceAllForBuilder())
                    }
            }
        }

        loadPage(R.layout.class_builder_add_pose)
        binding.tlAddPoseTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadPage(
                    if (tab.position == 0)
                        R.layout.class_builder_add_pose
                    else
                        R.layout.class_builder_add_new_pose
                )
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun initNewPosePageWithBinding(nb: ClassBuilderAddNewPoseBinding) {
        nb.scrollRegion.isFillViewport = true

        fun selectDuration() = with(nb) {
            rbDuration.isChecked = true
            rbRepetitions.isChecked = false
            etDuration.isEnabled = true
            etRepetitions.isEnabled = false
            etDuration.requestFocus()
            etRepetitions.text?.clear()
        }
        fun selectReps() = with(nb) {
            rbDuration.isChecked = false
            rbRepetitions.isChecked = true
            etDuration.isEnabled = false
            etRepetitions.isEnabled = true
            etRepetitions.requestFocus()
            etDuration.text?.clear()
        }
        nb.rbDuration.setOnClickListener { selectDuration() }
        nb.rbRepetitions.setOnClickListener { selectReps() }
        selectDuration()

        val levels = resources.getStringArray(R.array.class_levels)
        nb.acLevel.setAdapter(
            ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, levels)
        )

        nb.btnRegister.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        nb.btnAddPose.setOnClickListener {
            // Read and normalize inputs
            val name = nb.etPoseName.text.toString().trim()
            val levelText = nb.acLevel.text.toString().trim().lowercase()
            val level = Pose.Level.valueOf(levelText)
            val duration = if (nb.rbDuration.isChecked) {
                nb.etDuration.text.toString().toIntOrNull() ?: 0
            } else 0
            val reps = if (nb.rbRepetitions.isChecked) {
                nb.etRepetitions.text.toString().toIntOrNull() ?: 0
            } else 0
            val description = nb.etDescription.text.toString().trim()

            // Create and add the Pose
            val newPose = Pose(
                id          = UUID.randomUUID().toString(),
                name        = name,
                level       = level,
                category    = Pose.Category.standingPoses,
                duration    = duration.takeIf { it > 0 },
                repetitions = reps.takeIf { it > 0 },
                description = description,
                notes       = null,
                image       = ""
            )
            viewModel.addPose(newPose)

            // Log to verify it was added
            Log.d("AddPoseFragment", "After addPose, total items = ${viewModel.items.value?.size}")

            // Return to the actions menu
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

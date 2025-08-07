package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
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
            // 1) Read inputs
            val name = nb.etPoseName.text.toString().trim()
            val levelText = nb.acLevel.text.toString().trim().lowercase()
            val level = try {
                Pose.Level.valueOf(levelText)
            } catch (e: Exception) {
                null
            }
            val duration = nb.etDuration.text.toString().toIntOrNull() ?: 0
            val reps = nb.etRepetitions.text.toString().toIntOrNull() ?: 0
            val description = nb.etDescription.text.toString().trim()

            // 2) Local validations
            when {
                name.isBlank() -> {
                    Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                level == null -> {
                    Toast.makeText(requireContext(), "Select a valid level", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                duration <= 0 && reps <= 0 -> {
                    Toast.makeText(requireContext(),
                        "Specify either duration or repetitions (>0)",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                description.isBlank() -> {
                    Toast.makeText(requireContext(),
                        "Description is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // 3) Build & upload
            val newPose = Pose(
                id          = UUID.randomUUID().toString(),
                name        = name,
                level       = level,
                category    = Pose.Category.standingPoses, // or let user pick
                duration    = duration.takeIf { it > 0 },
                repetitions = reps.takeIf { it > 0 },
                description = description,
                notes       = null,
                image       = ""
            )

            PoseRepository.savePose(newPose) { error ->
                if (error == null) {
                    viewModel.addPose(newPose)
                    navigateSmoothly(ClassBuilderActionsFragment())
                } else {
                    Toast.makeText(requireContext(),
                        "Failed to upload pose: ${error.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

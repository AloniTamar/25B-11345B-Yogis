package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddNewPoseBinding
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddPoseContainerBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class ClassBuilderAddPoseFragment : Fragment() {
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
        _binding = ClassBuilderAddPoseContainerBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnApBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        fun loadPage(@LayoutRes layoutRes: Int) {
            binding.flAddPoseContent.removeAllViews()

            if (layoutRes == R.layout.class_builder_add_new_pose) {
                val newBinding = ClassBuilderAddNewPoseBinding
                    .inflate(layoutInflater, binding.flAddPoseContent, false)
                binding.flAddPoseContent.addView(newBinding.root)
                initNewPosePageWithBinding(newBinding)
            }
            else {
                val page = layoutInflater.inflate(
                    layoutRes,
                    binding.flAddPoseContent,
                    false
                )
                binding.flAddPoseContent.addView(page)
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

        val levels = listOf(
            getString(R.string.beginners),
            getString(R.string.intermediate),
            getString(R.string.advanced)
        )
        nb.acLevel.setAdapter(
            ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, levels)
        )

        nb.btnRegister.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        nb.btnAddPose.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

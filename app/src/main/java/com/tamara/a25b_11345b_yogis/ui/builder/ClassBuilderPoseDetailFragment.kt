// ClassBuilderPoseDetailFragment.kt
package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderPosePageBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel

class ClassBuilderPoseDetailFragment : Fragment() {

    companion object {
        private const val ARG_POSE_ID = "pose_id"

        fun newInstance(id: String) = ClassBuilderPoseDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_POSE_ID, id)
            }
        }
    }

    private var _binding: ClassBuilderPosePageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()
    private val poseId: String by lazy {
        requireArguments().getString(ARG_POSE_ID)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ClassBuilderPosePageBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnPdBack)

        val pose: Pose = PoseRepository.getAll()
            .firstOrNull { it.id == poseId }
            ?: return

        // TODO - later, use poseId to load images/text from Firebase

        binding.btnAddPose.setOnClickListener {
            viewModel.addPose(pose)
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

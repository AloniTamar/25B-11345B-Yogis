package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderPosePageBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class ClassBuilderPoseDetailFragment : Fragment() {

    companion object {
        private const val ARG_POSE_ID = "pose_id"

        /** Call this to show the detail for a given pose */
        fun newInstance(id: String) = ClassBuilderPoseDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_POSE_ID, id) }
        }
    }

    private var _binding: ClassBuilderPosePageBinding? = null
    private val binding get() = _binding!!

    private val poseId: String by lazy {
        requireArguments().getString(ARG_POSE_ID)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ClassBuilderPosePageBinding
        .inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) back arrow
        wireBack(binding.btnPdBack)

        // 2) TODO: later, use `poseId` to load images/text from Firebase

        // 3) “Add Pose” button goes back to the ClassBuilder flow
        binding.btnAddPose.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

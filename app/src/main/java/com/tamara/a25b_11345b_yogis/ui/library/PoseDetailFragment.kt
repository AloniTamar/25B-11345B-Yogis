package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.PosePageBinding
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PoseDetailFragment : Fragment() {

    companion object {
        private const val ARG_POSE_ID = "pose_id"

        /** call this to show detail for the given Pose ID */
        fun newInstance(poseId: String) = PoseDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_POSE_ID, poseId)
            }
        }
    }

    private var _binding: PosePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PosePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // back arrow
        wireBack(binding.btnPdBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        // grab the ID
        val poseId = requireArguments().getString(ARG_POSE_ID)
            ?: error("PoseDetailFragment requires a pose_id argument")

        // TODO 1: fetch this pose’s data from Firebase by poseId
        // TODO 2: once you’ve loaded the model, set:
        //   • binding.tvPdTitle.text
        //   • setup binding.vpPdImages adapter + indicator
        //   • fill in level, family, duration/reps, description, notes, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

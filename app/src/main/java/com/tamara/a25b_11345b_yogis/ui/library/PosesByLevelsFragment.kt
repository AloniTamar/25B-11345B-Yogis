package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryLevelsBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PosesByLevelsFragment : Fragment() {

    companion object {
        private const val ARG_FOR_CLASS_BUILDER = "for_class_builder"

        /** Library mode: show levels list in the public library */
        fun newInstance() = PosesByLevelsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_FOR_CLASS_BUILDER, false)
            }
        }

        /** Builder mode: show levels list coming from the ClassBuilder flow */
        fun newInstanceForBuilder() = PosesByLevelsFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_FOR_CLASS_BUILDER, true)
            }
        }
    }

    private var _binding: PoseLibraryLevelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryLevelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // read the flag
        val forBuilder = arguments?.getBoolean(ARG_FOR_CLASS_BUILDER, false) == true

        // wire back arrow
        wireBack(binding.btnPblBack)

        // tap “Beginners”
        binding.cardBeginners.setOnClickListener {
            if (forBuilder) {
                navigateSmoothly(PosesListFragment.newInstanceForBuilder(Pose.Level.beginner))
            } else {
                navigateSmoothly(PosesListFragment.newInstance(Pose.Level.beginner))
            }
        }

        // tap “Intermediate”
        binding.cardIntermediate.setOnClickListener {
            if (forBuilder) {
                navigateSmoothly(PosesListFragment.newInstanceForBuilder(Pose.Level.intermediate))
            } else {
                navigateSmoothly(PosesListFragment.newInstance(Pose.Level.intermediate))
            }
        }

        // tap “Advanced”
        binding.cardAdvanced.setOnClickListener {
            if (forBuilder) {
                navigateSmoothly(PosesListFragment.newInstanceForBuilder(Pose.Level.advanced))
            } else {
                navigateSmoothly(PosesListFragment.newInstance(Pose.Level.advanced))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

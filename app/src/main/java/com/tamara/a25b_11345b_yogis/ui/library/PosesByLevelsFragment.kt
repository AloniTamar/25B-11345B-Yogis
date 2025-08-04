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

        // Back‐arrow wiring
        wireBack(binding.btnPblBack)

        // When the user taps the “Beginners” card:
        binding.cardBeginners.setOnClickListener {
            navigateSmoothly(PosesListFragment.newInstance(Pose.Level.beginner))
        }

        // “Intermediate” card:
        binding.cardIntermediate.setOnClickListener {
            navigateSmoothly(PosesListFragment.newInstance(Pose.Level.intermediate))
        }

        // “Advanced” card:
        binding.cardAdvanced.setOnClickListener {
            navigateSmoothly(PosesListFragment.newInstance(Pose.Level.advanced))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

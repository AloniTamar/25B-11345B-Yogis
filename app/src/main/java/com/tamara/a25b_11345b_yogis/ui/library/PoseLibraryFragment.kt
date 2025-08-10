package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryBinding
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class PoseLibraryFragment : Fragment() {

    private var _binding: PoseLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlBack.setOnClickListener {
            navigateBackToMain()
        }
        binding.tvBackMain.setOnClickListener {
            navigateBackToMain()
        }

        binding.cardViewByLevels.setOnClickListener {
            navigateSmoothly(PosesByLevelsFragment())
        }

        binding.cardViewByTypes.setOnClickListener {
            navigateSmoothly(PosesByTypesFragment())
        }

        binding.cardAllPoses.setOnClickListener {
            navigateSmoothly(PosesListFragment.newInstanceAll())
        }

        binding.cardFlows.setOnClickListener {
            navigateSmoothly(PoseLibraryFlowListFragment())
        }

        binding.btnPlAdd.setOnClickListener {
            navigateSmoothly(PoseLibraryAddNewPoseFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

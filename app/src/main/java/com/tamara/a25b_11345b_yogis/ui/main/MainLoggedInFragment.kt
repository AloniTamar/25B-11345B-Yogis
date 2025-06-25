package com.tamara.a25b_11345b_yogis.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.MainLoggedInBinding
import com.tamara.a25b_11345b_yogis.ui.builder.ClassBuilderFragment
import com.tamara.a25b_11345b_yogis.ui.library.PoseLibraryFragment
import com.tamara.a25b_11345b_yogis.ui.profile.ProfileFragment
import com.tamara.a25b_11345b_yogis.ui.teaching.TeachingModeFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class MainLoggedInFragment : Fragment() {

    private var _binding: MainLoggedInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainLoggedInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // “Class Builder” card
        binding.cardClassBuilder.setOnClickListener {
            navigateSmoothly(ClassBuilderFragment())
        }

        // “Pose Library” card
        binding.cardPoseLibrary.setOnClickListener {
            navigateSmoothly(PoseLibraryFragment())
        }

        // “Teaching Mode” card
        binding.cardTeacingMode.setOnClickListener {
            navigateSmoothly(TeachingModeFragment())
        }

        // “View My Profile” button (footer)
        binding.btnRegister.setOnClickListener {
            navigateSmoothly(ProfileFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.tamara.a25b_11345b_yogis.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.MainGuestBinding

class MainMenuFragment : Fragment() {

    private var _binding: MainGuestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainGuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: hook up each card/button:
        // binding.btnClassBuilder.setOnClickListener { navigateSmoothly(ClassBuilderFragment()) }
        // binding.btnPoseLibrary.setOnClickListener { navigateSmoothly(PoseLibraryFragment()) }
        // etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

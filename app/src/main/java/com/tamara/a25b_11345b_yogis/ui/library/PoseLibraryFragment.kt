package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryBinding
import com.tamara.a25b_11345b_yogis.utils.wireBack

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
        // TODO: wire up your level/type/all filters and pose list clicks
        wireBack(binding.btnPlBack)
        binding.tvBackMain.setOnClickListener {
            navigateBackToMain()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

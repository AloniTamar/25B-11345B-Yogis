package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddFlowBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class ClassBuilderAddFlowFragment : Fragment() {
    private var _binding: ClassBuilderAddFlowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // wire the back arrow
        wireBack(binding.btnCtBack)  // update to the actual ID in your layout

        binding.btnAddFlow.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

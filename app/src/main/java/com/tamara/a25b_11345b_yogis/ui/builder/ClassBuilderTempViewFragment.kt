package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderTempViewBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel

class ClassBuilderTempViewFragment : Fragment() {

    private var _binding: ClassBuilderTempViewBinding? = null
    private val binding get() = _binding!!

    // Shared builder ViewModel
    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderTempViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnCtBack)
        binding.rvTimeline.layoutManager = LinearLayoutManager(requireContext())

        // Observe and show both poses and flows
        viewModel.elements.observe(viewLifecycleOwner) { elementList ->
            binding.rvTimeline.adapter = ClassBuilderTimelineAdapter(elementList)
        }

        binding.btnSave.setOnClickListener {
            // TODO: Save logic (Firebase, etc)
            navigateSmoothly(MainLoggedInFragment())
        }

        binding.tvBackMenu.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.clearPlan()
                    navigateSmoothly(MainLoggedInFragment())
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

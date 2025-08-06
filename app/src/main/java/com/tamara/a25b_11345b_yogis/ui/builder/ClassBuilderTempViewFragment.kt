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
import com.tamara.a25b_11345b_yogis.ui.shared.ClassPlanAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassPlanBuilderViewModel

class ClassBuilderTempViewFragment : Fragment() {

    private var _binding: ClassBuilderTempViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassPlanBuilderViewModel by activityViewModels()

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

        // wire the back arrow
        wireBack(binding.btnCtBack)

        // RecyclerView setup (use the ID from your XML: rv_timeline)
        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ClassPlanAdapter(emptyList())
        }

        // Observe the shared items (poses + flows) and update UI
        viewModel.items.observe(viewLifecycleOwner) { elements ->
            binding.rvTimeline.adapter = ClassPlanAdapter(elements)
            binding.btnSave.isEnabled = viewModel.canSave
        }

        // Save locally: log + toast
        binding.btnSave.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }

        // Discard changes dialog
        binding.tvBackMenu.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    viewModel.resetAll()
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

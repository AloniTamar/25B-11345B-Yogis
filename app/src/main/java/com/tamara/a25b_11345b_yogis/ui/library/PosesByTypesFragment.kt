package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryPosesFamListBinding
import com.tamara.a25b_11345b_yogis.ui.shared.CategoryAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class PosesByTypesFragment : Fragment() {

    companion object {
        private const val ARG_FOR_CLASS_BUILDER = "for_class_builder"

        fun newInstance() = PosesByTypesFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_FOR_CLASS_BUILDER, false) }
        }
        fun newInstanceForBuilder() = PosesByTypesFragment().apply {
            arguments = Bundle().apply { putBoolean(ARG_FOR_CLASS_BUILDER, true) }
        }
    }

    private var _binding: PoseLibraryPosesFamListBinding? = null
    private val binding get() = _binding!!

    private var forBuilder = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryPosesFamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        forBuilder = arguments?.getBoolean(ARG_FOR_CLASS_BUILDER, false) == true
        super.onViewCreated(view, savedInstanceState)
        binding.btnPblBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val recycler = binding.rvPblLevels
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val families = Pose.Category.entries
        recycler.adapter = CategoryAdapter(families) { category ->
            if (forBuilder) {
                // Launch in builder mode
                navigateSmoothly(PosesListFragment.newInstanceForBuilder(category))
            } else {
                // Launch in public library mode
                navigateSmoothly(PosesListFragment.newInstance(category))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

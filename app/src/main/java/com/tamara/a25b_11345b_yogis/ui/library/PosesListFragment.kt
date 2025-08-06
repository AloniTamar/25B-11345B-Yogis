package com.tamara.a25b_11345b_yogis.ui.library

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryPosesListBinding
import com.tamara.a25b_11345b_yogis.ui.builder.ClassBuilderPoseDetailFragment
import com.tamara.a25b_11345b_yogis.ui.shared.PoseAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PosesListFragment : Fragment() {

    companion object {
        private const val ARG_LEVEL             = "level"
        private const val ARG_CATEGORY          = "category"
        private const val ARG_FOR_CLASS_BUILDER = "for_class_builder"

        fun newInstance(level: Pose.Level): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LEVEL, level.name)
                    putBoolean(ARG_FOR_CLASS_BUILDER, false)
                }
            }

        fun newInstanceForBuilder(level: Pose.Level): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LEVEL, level.name)
                    putBoolean(ARG_FOR_CLASS_BUILDER, true)
                }
            }

        fun newInstance(category: Pose.Category): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category.name)
                    putBoolean(ARG_FOR_CLASS_BUILDER, false)
                }
            }

        fun newInstanceForBuilder(category: Pose.Category): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category.name)
                    putBoolean(ARG_FOR_CLASS_BUILDER, true)
                }
            }

        fun newInstanceAll(): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_FOR_CLASS_BUILDER, false)
                }
            }

        fun newInstanceAllForBuilder(): PosesListFragment =
            PosesListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_FOR_CLASS_BUILDER, true)
                }
            }
    }

    private var _binding: PoseLibraryPosesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var allPoses: List<Pose>
    private var filterLevel: Pose.Level? = null
    private var filterCategory: Pose.Category? = null
    private var forClassBuilder: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PoseLibraryPosesListBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnPblBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        arguments?.getString(ARG_LEVEL)?.let { filterLevel = Pose.Level.valueOf(it) }
        arguments?.getString(ARG_CATEGORY)?.let { filterCategory = Pose.Category.valueOf(it) }
        forClassBuilder = arguments?.getBoolean(ARG_FOR_CLASS_BUILDER, false) == true

        val titleRes = when {
            filterLevel != null -> when (filterLevel!!) {
                Pose.Level.beginner     -> R.string.beginners_poses
                Pose.Level.intermediate -> R.string.intermediate_poses
                Pose.Level.advanced     -> R.string.advanced_poses
            }
            else -> R.string.all_poses
        }
        binding.tvPblTitle.setText(titleRes)

        allPoses = PoseRepository.getAll()
        binding.rvPblLevels.layoutManager = LinearLayoutManager(requireContext())

        fun show(list: List<Pose>) {
            binding.rvPblLevels.adapter = PoseAdapter(list) { pose ->
                if (forClassBuilder) {
                    navigateSmoothly(
                        ClassBuilderPoseDetailFragment.newInstance(pose.id)
                    )
                } else {
                    navigateSmoothly(
                        PoseDetailFragment.newInstance(pose.id)
                    )
                }
            }
        }

        val baseline = allPoses.filter {
            (filterLevel == null || it.level == filterLevel!!)
                    && (filterCategory == null || it.category == filterCategory!!)
        }

        show(baseline)

        binding.etLpSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim().lowercase()
                show(baseline.filter { it.name.lowercase().contains(q) })
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

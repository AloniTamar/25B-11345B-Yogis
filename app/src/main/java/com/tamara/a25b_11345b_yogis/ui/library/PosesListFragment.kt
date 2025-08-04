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
        private const val ARG_LEVEL         = "level"
        private const val ARG_CATEGORY      = "category"
        private const val ARG_FOR_CLASS_BUILDER = "for_class_builder"

        /** show only poses of a given Level, in regular library mode */
        fun newInstance(level: Pose.Level) = PosesListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_LEVEL, level.name)
                putBoolean(ARG_FOR_CLASS_BUILDER, false)
            }
        }

        /** show only poses of a given Level, inside ClassBuilder */
        fun newInstanceForBuilder(level: Pose.Level) = PosesListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_LEVEL, level.name)
                putBoolean(ARG_FOR_CLASS_BUILDER, true)
            }
        }

        fun newInstance(category: Pose.Category) = PosesListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category.name)
                putBoolean(ARG_FOR_CLASS_BUILDER, false)
            }
        }
        fun newInstanceForBuilder(category: Pose.Category) = PosesListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_CATEGORY, category.name)
                putBoolean(ARG_FOR_CLASS_BUILDER, true)
            }
        }
        fun newInstanceAll() = PosesListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_FOR_CLASS_BUILDER, false)
            }
        }
        fun newInstanceAllForBuilder() = PosesListFragment().apply {
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

        // back / footer
        wireBack(binding.btnPblBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        // read filters
        arguments?.getString(ARG_LEVEL)?.let { filterLevel = Pose.Level.valueOf(it) }
        arguments?.getString(ARG_CATEGORY)?.let { filterCategory = Pose.Category.valueOf(it) }

        forClassBuilder = arguments?.getBoolean("for_class_builder", false) ?: false

        // set title
        val titleRes = filterLevel?.let { lvl ->
            when (lvl) {
                Pose.Level.beginner     -> R.string.beginners_poses
                Pose.Level.intermediate -> R.string.intermediate_poses
                Pose.Level.advanced     -> R.string.advanced_poses
            }
        } ?: (filterCategory?.let { _ ->
            R.string.all_poses
        } ?: R.string.all_poses)
        binding.tvPblTitle.setText(titleRes)

        // data + recycler
        allPoses = PoseRepository.getAll()
        val rv = binding.rvPblLevels
        rv.layoutManager = LinearLayoutManager(requireContext())

        fun show(list: List<Pose>) {
            rv.adapter = PoseAdapter(list) { pose ->
                if (forClassBuilder) {
                    // Go to ClassBuilder version
                    navigateSmoothly(ClassBuilderPoseDetailFragment.newInstance(pose.id))
                } else {
                    // Go to regular pose detail
                    navigateSmoothly(PoseDetailFragment.newInstance(pose.id))
                }
            }
        }

        // initial filter
        val initial = allPoses
            .filter { filterLevel?.let { lvl -> it.level == lvl } ?: true }
            .filter { filterCategory?.let { cat -> it.category == cat } ?: true }
        show(initial)

        // search
        binding.etLpSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim().lowercase()
                show(initial.filter { it.name.lowercase().contains(q) })
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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
import com.tamara.a25b_11345b_yogis.ui.shared.PoseAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PosesListFragment : Fragment() {

    companion object {
        private const val ARG_LEVEL    = "level"
        private const val ARG_CATEGORY = "category"

        /** show only poses of a given Level */
        fun newInstance(level: Pose.Level) = PosesListFragment().apply {
            arguments = Bundle().apply { putString(ARG_LEVEL, level.name) }
        }

        /** show only poses of a given Category */
        fun newInstance(category: Pose.Category) = PosesListFragment().apply {
            arguments = Bundle().apply { putString(ARG_CATEGORY, category.name) }
        }

        /** show *all* poses */
        fun newInstance() = PosesListFragment()
    }

    private var _binding: PoseLibraryPosesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var allPoses: List<Pose>
    private var filterLevel: Pose.Level? = null
    private var filterCategory: Pose.Category? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = PoseLibraryPosesListBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) wire up back / footer
        wireBack(binding.btnPblBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        // 2) figure out whether we're showing a level or a category
        arguments?.let {
            it.getString(ARG_LEVEL)?.let { lvl ->
                filterLevel = Pose.Level.valueOf(lvl)
            }
            it.getString(ARG_CATEGORY)?.let { cat ->
                filterCategory = Pose.Category.valueOf(cat)
            }
        }

        // 3) set the title
        val titleRes = filterLevel?.let { lvl ->
            when (lvl) {
                Pose.Level.beginner     -> R.string.beginners_poses
                Pose.Level.intermediate -> R.string.intermediate_poses
                Pose.Level.advanced     -> R.string.advanced_poses
            }
        }
            ?: filterCategory?.let { cat ->
                when (cat) {
                    Pose.Category.standingPoses  -> R.string.standing_poses
                    Pose.Category.forwardBends   -> R.string.forward_bends
                    Pose.Category.backendArches  -> R.string.backbends_arches
                    Pose.Category.twists         -> R.string.twists
                    Pose.Category.inversions     -> R.string.inversions
                    Pose.Category.seatedPoses    -> R.string.seated_poses
                    Pose.Category.boatPoses      -> R.string.boat_poses
                    Pose.Category.balancingPoses -> R.string.balancing_poses
                }
            } ?: R.string.all_poses

        binding.tvPblTitle.setText(titleRes)


        binding.tvPblTitle.setText(titleRes)

        // 4) grab your source and RecyclerView
        allPoses = PoseRepository.getAll()
        val rv = binding.rvPblLevels
        rv.layoutManager = LinearLayoutManager(requireContext())

        // 5) helper to show a filtered list
        fun show(list: List<Pose>) {
            rv.adapter = PoseAdapter(list) { pose ->
                // TODO: when tapped, nav into detail screen
            }
        }

        // 6) initial filter
        val initial = allPoses.filter {
            filterLevel?.let { lvl -> it.level == lvl } ?: true
        }.filter {
            filterCategory?.let { cat -> it.category == cat } ?: true
        }
        show(initial)

        // 7) wire up search
        binding.etLpSearch.addTextChangedListener(object : TextWatcher {
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

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

    private var _binding: PoseLibraryPosesFamListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryPosesFamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPblBack.setOnClickListener {
            navigateSmoothly(PoseLibraryFragment())
        }
        val recycler = binding.rvPblLevels

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val families = Pose.Category.entries
        recycler.adapter = CategoryAdapter(families) { category ->
            // TODO: when tapped, navigate into the list of poses for this family
            navigateSmoothly( PosesListFragment.newInstance(category) )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

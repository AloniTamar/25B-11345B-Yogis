package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Flow
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryFlowListBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.ui.shared.FlowAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly


class ClassBuilderFlowListFragment : Fragment() {
    private var _binding: PoseLibraryFlowListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryFlowListBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPblTitle.text = getString(R.string.Flows)

        binding.btnPblBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        binding.tvBackMain.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }

        val dummyFlows = listOf(
            Flow("Surya Namaskar A"),
            Flow("Surya Namaskar B"),
            Flow("Standing Sequence"),
            Flow("Primary Series")
        )

        val adapter = FlowAdapter(dummyFlows) { flow ->
            // TODO: when you have real data, navigate to the flow detail screen.
            // For now: bubble up to your “class timeline” fragment
            navigateSmoothly(ClassBuilderAddFlowFragment())
        }
        binding.rvPblLevels.apply {
              layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
              this.adapter = adapter
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

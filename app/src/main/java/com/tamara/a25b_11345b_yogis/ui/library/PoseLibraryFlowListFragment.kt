package com.tamara.a25b_11345b_yogis.ui.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryFlowListBinding
import com.tamara.a25b_11345b_yogis.ui.shared.FlowAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PoseLibraryFlowListFragment : Fragment() {

    private var _binding: PoseLibraryFlowListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryFlowListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPblTitle.text = "Flows"
        wireBack(binding.btnPblBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }
        binding.btnAddFlow.visibility = View.GONE

        binding.rvPblLevels.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val flows = withContext(Dispatchers.IO) {
                FlowRepository.getAll()
            }

            val adapter = FlowAdapter(flows) { flow ->
                navigateSmoothly(
                    PoseLibraryFlowDetailFragment.newInstance(flow.flowId)
                )
            }
            binding.rvPblLevels.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

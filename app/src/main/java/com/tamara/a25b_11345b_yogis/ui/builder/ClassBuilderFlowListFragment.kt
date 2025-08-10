package com.tamara.a25b_11345b_yogis.ui.builder

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.data.repository.FlowRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryFlowListBinding
import com.tamara.a25b_11345b_yogis.ui.shared.FlowAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassBuilderFlowListFragment : Fragment() {

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

        binding.tvPblTitle.text = "Select a Flow"
        binding.btnPblBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }
        binding.btnAddFlow.visibility = View.GONE

        binding.rvPblLevels.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val flows = withContext(Dispatchers.IO) { FlowRepository.getAll() }
            binding.rvPblLevels.adapter = FlowAdapter(flows) { flow ->
                navigateSmoothly(ClassBuilderAddFlowFragment.newInstance(flow.flowId))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

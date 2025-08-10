package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.tamara.a25b_11345b_yogis.data.model.ClassPlan
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryPosesListBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class PlanListFragment : Fragment() {

    private var _binding: PoseLibraryPosesListBinding? = null
    private val binding get() = _binding!!
    private val repo = ClassPlanRepository()

    private lateinit var adapter: PlanListAdapter
    private var baseline: List<ClassPlan> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PoseLibraryPosesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPblTitle.text = "Select Teaching Class"

        binding.rvPblLevels.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlanListAdapter(emptyList()) { plan ->
            val fragment = ViewClassBeforeTeachingFragment().apply {
                arguments = Bundle().apply { putString("planId", plan.planId) }
            }
            navigateSmoothly(fragment)
        }
        binding.rvPblLevels.adapter = adapter
        binding.rvPblLevels.visibility = View.GONE

        val emailKey = FirebaseAuth.getInstance().currentUser?.email?.let { IdUtils.emailKey(it) }
        if (emailKey == null) {
            Toast.makeText(requireContext(), "You are not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        repo.listForUser(
            uid = emailKey,
            onLoaded = { plans ->
                baseline = plans

                val q = binding.etLpSearch.text?.toString()?.trim().orEmpty()
                val shown = filterPlans(baseline, q)

                adapter.update(shown)
                binding.rvPblLevels.visibility = if (shown.isNotEmpty()) View.VISIBLE else View.GONE
                if (plans.isEmpty()) {
                    Toast.makeText(requireContext(), "No class plans found!", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { err ->
                Toast.makeText(requireContext(), "Failed to load plans: ${err.message}", Toast.LENGTH_LONG).show()
            }
        )

        binding.etLpSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim().orEmpty()
                val filtered = filterPlans(baseline, q)
                adapter.update(filtered)
                binding.rvPblLevels.visibility = if (filtered.isNotEmpty()) View.VISIBLE else View.GONE
            }
        })

        binding.btnPblBack.setOnClickListener { navigateSmoothly(MainLoggedInFragment()) }
        binding.tvBackMain.setOnClickListener { navigateSmoothly(MainLoggedInFragment()) }
    }

    private fun filterPlans(list: List<ClassPlan>, query: String): List<ClassPlan> {
        if (query.isBlank()) return list
        return list.filter { it.planName.contains(query, ignoreCase = true) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

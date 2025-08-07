package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import com.tamara.a25b_11345b_yogis.databinding.TeachingModeViewClassBinding
import com.tamara.a25b_11345b_yogis.ui.shared.TimelineAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class TeachingModeFragment : Fragment() {

    private var _binding: TeachingModeViewClassBinding? = null
    private val binding get() = _binding!!
    private val planRepo    = ClassPlanRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TeachingModeViewClassBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, saved: Bundle?) {
        super.onViewCreated(view, saved)

        // read the planId argument
        val planId = requireArguments().getString("planId")
            ?: return showPlaceholder("No class plan specified.")

        binding.tvCtTitle.text = "Teaching Class"
        binding.rvTimeline.visibility = View.GONE

        // load the ClassPlan and show its timeline
        planRepo.observePlan(
            planId,
            onPlanLoaded = { plan ->
                plan?.let {
                    binding.rvTimeline.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = TimelineAdapter(
                            items = it.elements,
                            currentOrder = 0,
                            onFinish = { /*…*/ }
                        )
                        visibility = View.VISIBLE
                    }
                }
            },
            onError = { err ->
                Toast.makeText(
                    requireContext(),
                    "Could not load class: ${err.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        // back nav
        wireBack(binding.btnCtBack)
        binding.tvBackMenu.visibility = View.GONE

        binding.btnSave.text = "Finish Teaching"
        binding.btnSave.setOnClickListener {
            navigateSmoothly(PlanListFragment())
        }
    }

    private fun showPlaceholder(msg: String) {
        binding.rvTimeline.visibility = View.GONE
        val empty = TextView(requireContext()).apply {
            text = msg
            setTypeface(typeface, Typeface.ITALIC)
            textSize = 16f
            gravity = Gravity.CENTER
        }
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = binding.tvCtTitle.id
            startToStart = binding.root.id
            endToEnd   = binding.root.id
            topMargin  = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        }
        binding.root.addView(empty, lp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

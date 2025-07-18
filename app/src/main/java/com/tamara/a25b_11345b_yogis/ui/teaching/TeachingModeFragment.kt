package com.tamara.a25b_11345b_yogis.ui.teaching

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryFlowsOrPosesFamListBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class TeachingModeFragment : Fragment() {

    private var _binding: PoseLibraryFlowsOrPosesFamListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryFlowsOrPosesFamListBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPblTitle.text = "Select Teaching Class"

        binding.rvPblLevels.visibility = View.GONE

        val empty = TextView(requireContext()).apply {
            text = "No classes have been created yet in your account."
            setTypeface(typeface, Typeface.ITALIC)
            textSize = 16f
            gravity = Gravity.CENTER
        }
        val lp = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topToBottom = binding.tvPblTitle.id
            startToStart = binding.root.id
            endToEnd   = binding.root.id
            topMargin  = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        }
        binding.root.addView(empty, lp)

        binding.btnPblBack.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }
        binding.tvBackMain.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment(), addToBackStack = false)
        }

        wireBack(binding.btnPblBack)
        binding.tvBackMain.setOnClickListener {
            navigateBackToMain()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

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

        // 1) Change the title at runtime
        binding.tvPblTitle.text = "Select Teaching Class"

        // 2) For now: no classes exist → hide the list and show placeholder
        binding.rvPblLevels.visibility = View.GONE

        // Create and add an “empty” TextView
        val empty = TextView(requireContext()).apply {
            text = "No classes have been created yet in your account."
            setTypeface(typeface, Typeface.ITALIC)
            textSize = 16f
            gravity = Gravity.CENTER
        }
        // LayoutParams to center under the title
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

        // 3) Wire up the back-arrow and “back to main” link
        binding.btnPblBack.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }
        binding.tvBackMain.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment(), addToBackStack = false)
        }

        wireBack(binding.btnPblBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

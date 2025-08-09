package com.tamara.a25b_11345b_yogis.ui.teaching

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.tamara.a25b_11345b_yogis.data.repository.ClassPlanRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryPosesListBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class PlanListFragment : Fragment() {
    private var _binding: PoseLibraryPosesListBinding? = null
    private val binding get() = _binding!!
    private val repo = ClassPlanRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PoseLibraryPosesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide until loaded
        binding.rvPblLevels.visibility = View.GONE

        // Get current user ID from FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.i("PlanListFragment", "Current user ID: $userId")
        if (userId == null) {
            Toast.makeText(requireContext(), "You are not logged in.", Toast.LENGTH_SHORT).show()
            return
        }
        repo.listForUser(
            uid = userId,
            onLoaded = { plans ->
                val visible = plans.isNotEmpty()
                if (!visible) {
                    Toast.makeText(requireContext(), "No class plans found!", Toast.LENGTH_SHORT).show()
                }
                binding.rvPblLevels.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = PlanListAdapter(plans) { plan ->
                        val fragment = ViewClassBeforeTeachingFragment().apply {
                            arguments = Bundle().apply { putString("planId", plan.planId) }
                        }
                        navigateSmoothly(fragment)
                    }
                    visibility = if (visible) View.VISIBLE else View.GONE
                }
            },
            onError = { err ->
                Toast.makeText(requireContext(), "Failed to load plans: ${err.message}", Toast.LENGTH_LONG).show()
            }
        )


        wireBack(binding.btnPblBack)

        binding.tvBackMain.setOnClickListener {
            navigateSmoothly(MainLoggedInFragment())
        }

        binding.tvPblTitle.text = "Select Teaching Class"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.tamara.a25b_11345b_yogis.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.tamara.a25b_11345b_yogis.data.repository.UserRepository
import com.tamara.a25b_11345b_yogis.databinding.ProfileBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import createTextAvatar

class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!
    private val userRepo = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wireBack(binding.btnProfileBack)

        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        userRepo.getUserByEmail(
            email = email,
            onLoaded = onLoaded@{ profile ->
                if (profile == null) {
                    Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show()
                    return@onLoaded
                }

                binding.tvProfileName.text = profile.username
                binding.tvValueEmail.text  = profile.email
                binding.tvValueYoga.text   = profile.yogaType
                binding.tvValueLevel.text  = profile.yearsExperience.toString()

                val first = (profile.username.firstOrNull() ?: 'U').uppercaseChar()
                binding.ivProfilePic.setImageDrawable(createTextAvatar(requireContext(), first, 128))
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        binding.btnRegister.setOnClickListener {
            navigateSmoothly(EditProfileFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

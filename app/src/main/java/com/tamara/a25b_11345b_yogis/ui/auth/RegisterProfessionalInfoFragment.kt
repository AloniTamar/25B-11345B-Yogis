package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.data.firebase.RegistrationManager
import com.tamara.a25b_11345b_yogis.data.model.UserProfile
import com.tamara.a25b_11345b_yogis.data.repository.UserRepository
import com.tamara.a25b_11345b_yogis.databinding.SignUpProfInfoBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class RegisterProfessionalInfoFragment : Fragment() {

    private var _binding: SignUpProfInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignUpProfInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnProfessionalBack.setOnClickListener {
            navigateSmoothly(RegisterFragment())
        }

        binding.btnProfessionalRegister.setOnClickListener {
            // — collect prof fields —
            val years = binding.etLevel.text
                .toString().trim().toIntOrNull() ?: 0
            val yogaType = binding.etYoga.text.toString().trim()

            RegistrationManager.yearsExperience = years
            RegistrationManager.yogatype        = yogaType

            // — perform sign-up now that we have EVERYTHING —
            AuthManager.signUp(
                RegistrationManager.email,
                RegistrationManager.password
            ) { success, exception ->
                if (!success) {
                    Toast.makeText(
                        requireContext(),
                        "Signup failed: ${exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    return@signUp
                }

                // — we’re now signed in, grab the user —
                val firebaseUser = AuthManager.currentUser()
                if (firebaseUser == null) {
                    Toast.makeText(
                        requireContext(),
                        "Unexpected error: no Firebase user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@signUp
                }

                // — build & save the profile object —
                val profile = UserProfile(
                    uid = firebaseUser.uid,
                    email = RegistrationManager.email,
                    username = RegistrationManager.username,
                    certification = RegistrationManager.yogatype,
                    yearsExperience = RegistrationManager.yearsExperience
                )

                UserRepository().saveUser(profile) { dbError ->
                    if (dbError != null) {
                        Toast.makeText(
                            requireContext(),
                            "Couldn’t save profile: ${dbError.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        RegistrationManager.clear()
                        navigateSmoothly(
                            MainLoggedInFragment(),
                            addToBackStack = false
                        )
                    }
                }
            }
        }



        binding.tvProfessionalLogin.setOnClickListener {
            navigateSmoothly(LoginFragment(), addToBackStack = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

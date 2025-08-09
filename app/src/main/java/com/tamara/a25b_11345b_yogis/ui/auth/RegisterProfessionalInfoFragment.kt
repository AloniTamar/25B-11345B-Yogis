package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.data.firebase.RegistrationManager
import com.tamara.a25b_11345b_yogis.data.repository.UserRepository
import com.tamara.a25b_11345b_yogis.databinding.SignUpProfInfoBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import kotlinx.coroutines.launch

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
            val years = binding.etLevel.text.toString().trim().toIntOrNull() ?: 0
            val yogaType = binding.etYoga.text.toString().trim()

            RegistrationManager.yearsExperience = years
            RegistrationManager.yogaType = yogaType

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

                val firebaseUser = AuthManager.currentUser()
                if (firebaseUser == null) {
                    Toast.makeText(
                        requireContext(),
                        "Unexpected error: no Firebase user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@signUp
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        UserRepository().createOrUpdateUser(
                            uid = firebaseUser.uid,
                            email = RegistrationManager.email,
                            data = mapOf(
                                "uid" to firebaseUser.uid,
                                "email" to RegistrationManager.email,
                                "username" to RegistrationManager.username,
                                "yogaType" to RegistrationManager.yogaType,
                                "yearsExperience" to RegistrationManager.yearsExperience
                            )
                        )

                        RegistrationManager.clear()
                        navigateSmoothly(
                            MainLoggedInFragment(),
                            addToBackStack = false
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Couldn’t save profile: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
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

package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.manager.RegistrationManager
import com.tamara.a25b_11345b_yogis.databinding.SignUpBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class RegisterFragment : Fragment() {

    private var _binding: SignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: form validation…

        // “Next” button on sign_up.xml
        binding.btnSignUpNext.setOnClickListener {
            RegistrationManager.apply {
                this.username = binding.etUsername.text.toString().trim()
                this.email = binding.etEmailReg.text.toString().trim()
                if (binding.etPasswordReg.text.toString().trim() !=
                    binding.etConfirmPasswordReg.text.toString().trim()) {
                    binding.etConfirmPasswordReg.error = "Passwords do not match"
                    return@setOnClickListener
                }
                this.password = binding.etPasswordReg.text.toString().trim()
            }
            navigateSmoothly(RegisterProfessionalInfoFragment())
        }

        // “Already have an account? Log in” text
        binding.tvSignUpLogin.setOnClickListener {
            // for now skip straight to Main
            navigateSmoothly(LoginFragment())
        }

        binding.btnSignUpBack.setOnClickListener { navigateSmoothly(WelcomeFragment()) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

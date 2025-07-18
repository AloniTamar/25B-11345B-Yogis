package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            // for now skip straight to Main
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

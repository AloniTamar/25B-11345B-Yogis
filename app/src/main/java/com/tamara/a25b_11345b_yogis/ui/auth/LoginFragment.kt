package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.databinding.LoginBinding
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class LoginFragment : Fragment() {

    private var _binding: LoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass  = binding.etPassword.text.toString().trim()

            AuthManager.signIn(email, pass) { success, ex ->
                if (success) {
                    navigateSmoothly(MainLoggedInFragment())
                } else {
                    Toast.makeText(requireContext(),
                        "Login failed: ${ex?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            navigateSmoothly(ForgotPasswordFragment())
        }

        // if you have a “Register Now” link in your login.xml:
        binding.tvRegisterNow.setOnClickListener {
            navigateSmoothly(RegisterFragment())
        }

        binding.ivBack.setOnClickListener { navigateSmoothly(WelcomeFragment()) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.databinding.ForgotPasswordBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class ForgotPasswordFragment : Fragment() {

    private var _binding: ForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFpSendCode.setOnClickListener {
            val email = binding.etFpEmail.text.toString().trim()
            if (email.isBlank()) {
                Toast.makeText(requireContext(),
                    "Please enter your email address",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AuthManager.sendPasswordResetEmail(email) { success, exception ->
                if (success) {
                    Toast.makeText(requireContext(),
                        "Password reset link sent to $email",
                        Toast.LENGTH_LONG).show()
                    navigateSmoothly(LoginFragment())
                } else {
                    Log.e("ForgotPwd", "Reset failed", exception)
                    Toast.makeText(requireContext(),
                        "Error sending reset email: ${exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvFpLogin.setOnClickListener {
            navigateSmoothly(LoginFragment())
        }

        binding.btnFpBack.setOnClickListener {
            navigateSmoothly(LoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

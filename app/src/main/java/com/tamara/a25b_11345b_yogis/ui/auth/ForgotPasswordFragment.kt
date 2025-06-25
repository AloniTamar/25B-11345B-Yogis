package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.ForgotPasswordBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

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
            if (email.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Please enter your email",
                    Toast.LENGTH_SHORT).show()
            } else {
                // TODO: AuthManager.sendPasswordReset(email)
                navigateSmoothly(OtpVerificationFragment())
            }
        }

        binding.tvFpLogin.setOnClickListener {
            navigateSmoothly(LoginFragment())
        }

        wireBack(binding.btnFpBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

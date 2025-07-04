package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.OtpVerificationBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class OtpVerificationFragment : Fragment() {

    private var _binding: OtpVerificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerify.setOnClickListener {
            val code = """
        ${binding.etCode1.text}${binding.etCode2.text}
        ${binding.etCode3.text}${binding.etCode4.text}
      """.trim()
            if (code.length < 4) {
                Toast.makeText(requireContext(),
                    "Enter the 4-digit code",
                    Toast.LENGTH_SHORT).show()
            } else {
                // TODO: verify code with Firebase
                navigateSmoothly(CreateNewPasswordFragment())
            }
        }

        binding.tvResend.setOnClickListener {
            // TODO: resend OTP
            Toast.makeText(requireContext(),
                "Resend code not yet implemented",
                Toast.LENGTH_SHORT).show()
        }

        wireBack(binding.btnOtpBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

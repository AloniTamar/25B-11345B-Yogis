package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager
import com.tamara.a25b_11345b_yogis.databinding.CreateNewPasswordBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class CreateNewPasswordFragment : Fragment() {

    private var _binding: CreateNewPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCpReset.setOnClickListener {
            if (binding.etNewPassword.text.isNullOrBlank() ||
                binding.etConfirmPassword.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newPass = binding.etNewPassword.text.toString().trim()
            AuthManager.updatePassword(newPass) { success, ex ->
                if (success) {
                    navigateSmoothly(PasswordChangedFragment())
                } else {
                    Toast.makeText(requireContext(),
                        "Could not update password: ${ex?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.btnCpBack.setOnClickListener {
            navigateSmoothly(OtpVerificationFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

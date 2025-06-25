package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.CreateNewPasswordBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

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
            val p1 = binding.etNewPassword.text.toString().trim()
            val p2 = binding.etConfirmPassword.text.toString().trim()
            if (p1.isEmpty() || p2.isEmpty() || p1 != p2) {
                Toast.makeText(requireContext(),
                    "Passwords must match and not be empty",
                    Toast.LENGTH_SHORT).show()
            } else {
                // TODO: actually update password in Firebase
                navigateSmoothly(PasswordChangedFragment())
            }
        }

        wireBack(binding.btnCpBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

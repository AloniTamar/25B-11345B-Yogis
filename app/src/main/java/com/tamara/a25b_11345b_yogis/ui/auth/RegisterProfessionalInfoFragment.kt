package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            navigateSmoothly(MainLoggedInFragment(), addToBackStack = false)
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

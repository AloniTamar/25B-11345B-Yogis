package com.tamara.a25b_11345b_yogis.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.WelcomeBinding
import com.tamara.a25b_11345b_yogis.ui.auth.LoginFragment
import com.tamara.a25b_11345b_yogis.ui.auth.RegisterFragment
import com.tamara.a25b_11345b_yogis.ui.main.MainMenuFragment
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly

class WelcomeFragment : Fragment() {

    private var _binding: WelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            navigateSmoothly(LoginFragment())
        }

        binding.btnRegister.setOnClickListener {
            navigateSmoothly(RegisterFragment())
        }

        binding.tvContinueGuest.setOnClickListener {
            navigateSmoothly(MainMenuFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

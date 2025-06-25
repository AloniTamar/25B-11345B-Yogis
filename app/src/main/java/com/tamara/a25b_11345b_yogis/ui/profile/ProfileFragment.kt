package com.tamara.a25b_11345b_yogis.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tamara.a25b_11345b_yogis.databinding.ProfileBinding
import com.tamara.a25b_11345b_yogis.utils.wireBack

class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: load user profile data and hook up “Edit Profile” and “Change Password”

        wireBack(binding.btnProfileBack)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

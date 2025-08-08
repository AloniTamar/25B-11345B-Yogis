package com.tamara.a25b_11345b_yogis.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager.updatePassword
import com.tamara.a25b_11345b_yogis.data.repository.UserRepository
import com.tamara.a25b_11345b_yogis.data.model.UserProfile
import com.tamara.a25b_11345b_yogis.databinding.EditProfileBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack

class EditProfileFragment : Fragment() {

    private var _binding: EditProfileBinding? = null
    private val binding get() = _binding!!

    private val userRepo = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wireBack(binding.btnBack)

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val createdAt = user?.metadata?.creationTimestamp ?: System.currentTimeMillis()

        if (uid == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Pre-fill fields with current data
        userRepo.getUser(
            uid,
            onLoaded = onLoaded@{ profile ->
                if (profile == null) {
                    Toast.makeText(requireContext(), "Profile not found.", Toast.LENGTH_SHORT).show()
                    return@onLoaded
                }
                binding.etUserName.setText(profile.username)
                binding.etYogaType.setText(profile.yogaType)
                binding.etYearsExperience.setText(profile.yearsExperience.toString())
                binding.etEmail.setText(profile.email)
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.etUserName.text.toString().trim()
            val newYogaType = binding.etYogaType.text.toString().trim()
            val newExperience = binding.etYearsExperience.text.toString().trim().toIntOrNull() ?: 0

            if (newName.isBlank() || newYogaType.isBlank()) {
                Toast.makeText(requireContext(), "Name and yoga type are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedProfile = UserProfile(
                uid = uid,
                username = newName,
                email = user.email ?: "",
                yogaType = newYogaType,
                yearsExperience = newExperience,
                createdAt = createdAt,
                updatedAt = System.currentTimeMillis()
            )

            userRepo.saveUser(
                updatedProfile,
                onComplete = { error ->
                    if (error == null) {
                        navigateSmoothly(ProfileFragment())
                    } else {
                        Toast.makeText(requireContext(), "Update failed: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Views from the dialog
        val btnClose = dialogView.findViewById<MaterialButton>(R.id.btn_close)
        val btnUpdate = dialogView.findViewById<MaterialButton>(R.id.btn_update_password)
        val etCurrent = dialogView.findViewById<TextInputEditText>(R.id.et_current)
        val etNew = dialogView.findViewById<TextInputEditText>(R.id.et_new)
        val etVerify = dialogView.findViewById<TextInputEditText>(R.id.et_verify)
        val loaderOverlay = dialogView.findViewById<View>(R.id.dialog_loader_overlay)

        fun setFieldsEnabled(enabled: Boolean) {
            etCurrent.isEnabled = enabled
            etNew.isEnabled = enabled
            etVerify.isEnabled = enabled
            btnClose.isEnabled = enabled
            btnUpdate.isEnabled = enabled
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        btnUpdate.setOnClickListener {
            val currentPass = etCurrent.text?.toString()?.trim() ?: ""
            val newPass = etNew.text?.toString()?.trim() ?: ""
            val verifyPass = etVerify.text?.toString()?.trim() ?: ""

            // Basic validation
            when {
                currentPass.isBlank() || newPass.isBlank() || verifyPass.isBlank() -> {
                    etCurrent.error = if (currentPass.isBlank()) "Required" else null
                    etNew.error = if (newPass.isBlank()) "Required" else null
                    etVerify.error = if (verifyPass.isBlank()) "Required" else null
                    return@setOnClickListener
                }
                newPass != verifyPass -> {
                    etVerify.error = "Passwords do not match"
                    return@setOnClickListener
                }
                newPass.length < 6 -> {
                    etNew.error = "Password must be at least 6 characters"
                    return@setOnClickListener
                }
            }

            // Show loader, disable fields
            loaderOverlay.visibility = View.VISIBLE
            setFieldsEnabled(false)

            // Perform password update
            updatePassword(currentPass, newPass) { success, message ->
                loaderOverlay.visibility = View.GONE
                setFieldsEnabled(true)
                if (success) {
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), message ?: "Failed to update password", Toast.LENGTH_LONG).show()
                }
            }
        }

        dialog.show()
    }
}

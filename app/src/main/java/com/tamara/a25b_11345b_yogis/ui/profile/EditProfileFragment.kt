package com.tamara.a25b_11345b_yogis.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.firebase.AuthManager.updatePassword
import com.tamara.a25b_11345b_yogis.data.model.UserProfile
import com.tamara.a25b_11345b_yogis.data.repository.UserRepository
import com.tamara.a25b_11345b_yogis.databinding.EditProfileBinding
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import createTextAvatar

class EditProfileFragment : Fragment() {

    private var _binding: EditProfileBinding? = null
    private val binding get() = _binding!!
    private val userRepo = UserRepository()

    private var loadedProfile: UserProfile? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = EditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wireBack(binding.btnBack)

        val authUser = FirebaseAuth.getInstance().currentUser
        val email = authUser?.email
        val uid = authUser?.uid

        if (email.isNullOrBlank() || uid.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Load existing profile by email
        userRepo.getUserByEmail(
            email = email,
            onLoaded = onLoaded@{ profile ->
                if (profile == null) {
                    // Pre-fill with auth values if no profile exists yet
                    loadedProfile = UserProfile(
                        uid = uid,
                        email = email,
                        username = authUser.displayName ?: "",
                        yogaType = "",
                        yearsExperience = 0
                    )
                } else {
                    loadedProfile = profile
                }

                val p = loadedProfile!!
                binding.etUserName.setText(p.username)
                binding.etYogaType.setText(p.yogaType)
                binding.etYearsExperience.setText(p.yearsExperience.toString())
                binding.etEmail.setText(p.email)

                val first = (p.username.firstOrNull() ?: 'U').uppercaseChar()
                binding.ivPhoto.setImageDrawable(createTextAvatar(requireContext(), first, 128))
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        binding.btnChangePassword.setOnClickListener { showChangePasswordDialog() }

        binding.btnSave.setOnClickListener {
            val p = loadedProfile
            if (p == null) {
                Toast.makeText(requireContext(), "Profile not loaded yet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newName  = binding.etUserName.text.toString().trim()
            val newType  = binding.etYogaType.text.toString().trim()
            val newYears = binding.etYearsExperience.text.toString().trim().toIntOrNull() ?: 0

            if (newName.isBlank() || newType.isBlank()) {
                Toast.makeText(requireContext(), "Name and yoga type are required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = p.copy(
                username = newName,
                yogaType = newType,
                yearsExperience = newYears
                // createdAt kept; updatedAt handled in repository
            )

            userRepo.saveUserByEmail(updated) { err ->
                if (err == null) {
                    navigateSmoothly(ProfileFragment())
                } else {
                    Toast.makeText(requireContext(), "Update failed: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
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

        val btnClose  = dialogView.findViewById<MaterialButton>(R.id.btn_close)
        val btnUpdate = dialogView.findViewById<MaterialButton>(R.id.btn_update_password)
        val etCurrent = dialogView.findViewById<TextInputEditText>(R.id.et_current)
        val etNew     = dialogView.findViewById<TextInputEditText>(R.id.et_new)
        val etVerify  = dialogView.findViewById<TextInputEditText>(R.id.et_verify)
        val loader    = dialogView.findViewById<View>(R.id.dialog_loader_overlay)

        fun setEnabled(en: Boolean) {
            etCurrent.isEnabled = en; etNew.isEnabled = en; etVerify.isEnabled = en
            btnClose.isEnabled = en;  btnUpdate.isEnabled = en
        }

        btnClose.setOnClickListener { dialog.dismiss() }

        btnUpdate.setOnClickListener {
            val currentPass = etCurrent.text?.toString()?.trim().orEmpty()
            val newPass     = etNew.text?.toString()?.trim().orEmpty()
            val verifyPass  = etVerify.text?.toString()?.trim().orEmpty()

            when {
                currentPass.isBlank() -> { etCurrent.error = "Required"; return@setOnClickListener }
                newPass.isBlank()     -> { etNew.error     = "Required"; return@setOnClickListener }
                verifyPass.isBlank()  -> { etVerify.error  = "Required"; return@setOnClickListener }
                newPass != verifyPass -> { etVerify.error  = "Passwords do not match"; return@setOnClickListener }
                newPass.length < 6    -> { etNew.error     = "At least 6 characters"; return@setOnClickListener }
            }

            loader.visibility = View.VISIBLE
            setEnabled(false)

            updatePassword(currentPass, newPass) { success, message ->
                loader.visibility = View.GONE
                setEnabled(true)
                if (success) dialog.dismiss()
                else Toast.makeText(requireContext(), message ?: "Failed to update password", Toast.LENGTH_LONG).show()
            }
        }

        dialog.show()
    }
}

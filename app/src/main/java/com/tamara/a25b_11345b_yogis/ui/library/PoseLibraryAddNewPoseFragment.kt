package com.tamara.a25b_11345b_yogis.ui.library

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
import com.tamara.a25b_11345b_yogis.databinding.PoseLibraryAddNewPoseBinding
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PoseLibraryAddNewPoseFragment : Fragment() {

    private var _binding: PoseLibraryAddNewPoseBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var selectedImageMime: String? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            selectedImageMime = requireContext().contentResolver.getType(uri)
            Toast.makeText(requireContext(), "Photo selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PoseLibraryAddNewPoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageUploadOverlay.bringToFront()

        binding.btnPlBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.tvBackMain.setOnClickListener { requireActivity().onBackPressed() }

        val catLabels = resources.getStringArray(R.array.pose_categories_labels)
        val catValues = resources.getStringArray(R.array.pose_categories_values)
        binding.acType.setAdapter(ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, catLabels))
        var selectedCategory: Pose.Category? = null
        binding.acType.setOnItemClickListener { _, _, pos, _ ->
            selectedCategory = Pose.Category.valueOf(catValues[pos])
        }

        val levels = resources.getStringArray(R.array.class_levels)
        binding.acLevel.setAdapter(ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, levels))
        fun parseLevel(text: String): Pose.Level? = when (text.trim().lowercase()) {
            "beginner", "beginners" -> Pose.Level.beginner
            "intermediate" -> Pose.Level.intermediate
            "advanced" -> Pose.Level.advanced
            else -> null
        }

        binding.btnRegister.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.btnPlAdd.setOnClickListener {
            val name = binding.etPoseName.text.toString().trim()
            val level = parseLevel(binding.acLevel.text.toString())
            val durationSec = binding.evDuration.text.toString().toIntOrNull() ?: 0
            val description = binding.etDescription.text.toString().trim()
            val notes = binding.etNote.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
            val category = selectedCategory ?: run {
                val typed = binding.acType.text?.toString()?.trim()
                val idx = catLabels.indexOfFirst { it.equals(typed, ignoreCase = true) }
                if (idx >= 0) Pose.Category.valueOf(catValues[idx]) else null
            }

            when {
                name.isBlank() -> { toast("Name is required"); return@setOnClickListener }
                category == null -> { toast("Select a yoga type"); return@setOnClickListener }
                level == null -> { toast("Select a valid level"); return@setOnClickListener }
                durationSec <= 0 -> { toast("Duration (seconds) must be > 0"); return@setOnClickListener }
                selectedImageUri == null -> { toast("Please select an image"); return@setOnClickListener }
                description.isBlank() -> { toast("Description is required"); return@setOnClickListener }
            }

            binding.btnPlAdd.isEnabled = false
            binding.imageUploadOverlay.visibility = View.VISIBLE
            binding.tvLoaderText.text = "Uploading image… 0%"

            lifecycleScope.launch {
                try {
                    val db = FirebaseDatabase.getInstance(
                        "https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/"
                    )
                    val posesRef = db.getReference("poses")
                    val mediaRef = db.getReference("mediaAssets")

                    val base = IdUtils.slugify(name)
                    val poseId = IdUtils.nextAvailableId(posesRef, base)

                    val storage = FirebaseStorage.getInstance()
                    val fileRef = storage.reference.child("pose_images/$poseId.jpg")
                    val localUri = selectedImageUri!!

                    val uploadTask = fileRef.putFile(localUri)
                    uploadTask.addOnProgressListener { snap ->
                        val pct = if (snap.totalByteCount > 0)
                            (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
                        else 0
                        if (isAdded) binding.tvLoaderText.text = "Uploading image… $pct%"
                    }
                    uploadTask.await()

                    if (isAdded) binding.tvLoaderText.text = "Finalizing…"

                    val downloadUrl = fileRef.downloadUrl.await().toString()

                    val now = System.currentTimeMillis()
                    val meta = mapOf(
                        "assetId" to poseId,
                        "url" to downloadUrl,
                        "mimeType" to (selectedImageMime ?: "image/jpeg"),
                        "width" to 0,
                        "height" to 0,
                        "createdAt" to now,
                        "updatedAt" to now
                    )
                    mediaRef.child(poseId).setValue(meta).await()

                    val newPose = Pose(
                        id = poseId,
                        name = name,
                        level = level,
                        category = category,
                        duration = durationSec,
                        description = description,
                        notes = notes,
                        image = downloadUrl
                    )

                    PoseRepository.savePose(newPose) { error ->
                        binding.btnPlAdd.isEnabled = true
                        binding.imageUploadOverlay.visibility = View.GONE
                        if (error == null) {
                            navigateSmoothly(PoseDetailFragment.newInstance(poseId), addToBackStack = false)
                        } else {
                            toast("Failed to upload pose: ${error.message}")
                        }
                    }
                } catch (e: Exception) {
                    binding.btnPlAdd.isEnabled = true
                    binding.imageUploadOverlay.visibility = View.GONE
                    toast("Failed: ${e.message}")
                }
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

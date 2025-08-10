package com.tamara.a25b_11345b_yogis.ui.builder

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddNewPoseBinding
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderAddPoseContainerBinding
import com.tamara.a25b_11345b_yogis.ui.library.PosesByLevelsFragment
import com.tamara.a25b_11345b_yogis.ui.library.PosesByTypesFragment
import com.tamara.a25b_11345b_yogis.ui.library.PosesListFragment
import com.tamara.a25b_11345b_yogis.ui.main.MainLoggedInFragment
import com.tamara.a25b_11345b_yogis.utils.IdUtils
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ClassBuilderAddPoseFragment : Fragment() {

    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()
    private var _binding: ClassBuilderAddPoseContainerBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var selectedImageWidth: Int? = null
    private var selectedImageHeight: Int? = null
    private var selectedImageMime: String? = null

    private val DB_URL =
        "https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/"

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            captureLocalImageInfo(uri)
            Toast.makeText(requireContext(), "Image selected ✓", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLoaderText(msg: String) {
        // works whether tvLoaderText is on the container overlay or the inner page
        binding.root.findViewById<TextView>(R.id.tvLoaderText)?.let { it.text = msg }
    }

    private fun captureLocalImageInfo(localUri: Uri) {
        selectedImageUri = localUri

        binding.imageUploadOverlay.visibility = View.VISIBLE
        setLoaderText("Processing photo…")

        val ctx = requireContext()
        val mime = ctx.contentResolver.getType(localUri) ?: "image/jpeg"
        selectedImageMime = mime

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            ctx.contentResolver.openInputStream(localUri)?.use { stream ->
                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(stream, null, opts)
                selectedImageWidth = opts.outWidth
                selectedImageHeight = opts.outHeight
            }
            withContext(Dispatchers.Main) {
                binding.imageUploadOverlay.visibility = View.GONE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ClassBuilderAddPoseContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // make sure overlay sits on top if present
        binding.imageUploadOverlay.bringToFront()

        binding.btnApBack.setOnClickListener {
            navigateSmoothly(ClassBuilderActionsFragment())
        }

        binding.tvBackMain.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Discard changes?")
                .setMessage("Your changes won’t be saved. Continue?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    navigateSmoothly(MainLoggedInFragment())
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
        }

        fun loadPage(@LayoutRes layoutRes: Int) {
            binding.flAddPoseContent.removeAllViews()

            if (layoutRes == R.layout.class_builder_add_new_pose) {
                val newBinding = ClassBuilderAddNewPoseBinding
                    .inflate(layoutInflater, binding.flAddPoseContent, false)
                binding.flAddPoseContent.addView(newBinding.root)
                initNewPosePageWithBinding(newBinding)
            } else {
                val page = layoutInflater.inflate(
                    R.layout.class_builder_add_pose,
                    binding.flAddPoseContent,
                    false
                ).also { binding.flAddPoseContent.addView(it) }

                page.findViewById<MaterialCardView>(R.id.card_ap_view_by_levels)
                    .setOnClickListener {
                        navigateSmoothly(PosesByLevelsFragment.newInstanceForBuilder())
                    }
                page.findViewById<MaterialCardView>(R.id.card_ap_view_by_types)
                    .setOnClickListener {
                        navigateSmoothly(PosesByTypesFragment.newInstanceForBuilder())
                    }
                page.findViewById<MaterialCardView>(R.id.card_ap_all_poses)
                    .setOnClickListener {
                        navigateSmoothly(PosesListFragment.newInstanceAllForBuilder())
                    }
            }
        }

        loadPage(R.layout.class_builder_add_pose)
        binding.tlAddPoseTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadPage(
                    if (tab.position == 0)
                        R.layout.class_builder_add_pose
                    else
                        R.layout.class_builder_add_new_pose
                )
            }
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
    }

    private fun initNewPosePageWithBinding(nb: ClassBuilderAddNewPoseBinding) {
        nb.scrollRegion.isFillViewport = true

        val catLabels = resources.getStringArray(R.array.pose_categories_labels)
        val catValues = resources.getStringArray(R.array.pose_categories_values)
        nb.acType.setAdapter(ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, catLabels))

        var selectedCategory: Pose.Category? = null
        nb.acType.setOnItemClickListener { _, _, pos, _ ->
            selectedCategory = try { Pose.Category.valueOf(catValues[pos]) } catch (_: Exception) { null }
        }

        val levels = resources.getStringArray(R.array.class_levels)
        nb.acLevel.setAdapter(ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, levels))
        fun parseLevel(text: String): Pose.Level? = when (text.trim().lowercase()) {
            "beginner", "beginners" -> Pose.Level.beginner
            "intermediate"          -> Pose.Level.intermediate
            "advanced"              -> Pose.Level.advanced
            else -> null
        }

        nb.btnRegister.setOnClickListener { pickImageLauncher.launch("image/*") }

        nb.btnAddPose.setOnClickListener {
            val name         = nb.etPoseName.text.toString().trim()
            val level        = parseLevel(nb.acLevel.text.toString())
            val durationSec  = nb.evDuration.text.toString().toIntOrNull() ?: 0
            val description  = nb.etDescription.text.toString().trim()
            val notes        = nb.etNote.text?.toString()?.trim().takeUnless { it.isNullOrBlank() }
            val category     = selectedCategory
                ?: run {
                    val typed = nb.acType.text?.toString()?.trim()
                    val idx = catLabels.indexOfFirst { it.equals(typed, ignoreCase = true) }
                    if (idx >= 0) Pose.Category.valueOf(catValues[idx]) else null
                }

            when {
                name.isBlank() -> { Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                category == null -> { Toast.makeText(requireContext(), "Select a yoga type", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                level == null -> { Toast.makeText(requireContext(), "Select a valid level", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                durationSec <= 0 -> { Toast.makeText(requireContext(), "Duration (seconds) must be > 0", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                selectedImageUri == null -> { Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
                description.isBlank() -> { Toast.makeText(requireContext(), "Description is required", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            }

            // show overlay with initial percentage
            binding.imageUploadOverlay.visibility = View.VISIBLE
            setLoaderText("Uploading image… 0%")

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val db        = FirebaseDatabase.getInstance(DB_URL)
                    val posesRef  = db.getReference("poses")
                    val mediaRef  = db.getReference("mediaAssets")

                    val base   = IdUtils.slugify(name)
                    val poseId = IdUtils.nextAvailableId(posesRef, base)

                    val storage = FirebaseStorage.getInstance()
                    val fileRef = storage.reference.child("pose_images/$poseId.jpg")
                    val localUri = selectedImageUri!!

                    // upload with progress
                    val uploadTask = fileRef.putFile(localUri)
                    uploadTask.addOnProgressListener { snap ->
                        val pct = if (snap.totalByteCount > 0)
                            (100.0 * snap.bytesTransferred / snap.totalByteCount).toInt()
                        else 0
                        if (isAdded) setLoaderText("Uploading image… $pct%")
                    }
                    uploadTask.await()

                    if (isAdded) setLoaderText("Finalizing…")

                    val downloadUrl = fileRef.downloadUrl.await().toString()

                    val width  = selectedImageWidth ?: 0
                    val height = selectedImageHeight ?: 0
                    val mime   = selectedImageMime ?: "image/jpeg"
                    val now    = System.currentTimeMillis()

                    val meta = mapOf(
                        "assetId"   to poseId,
                        "url"       to downloadUrl,
                        "mimeType"  to mime,
                        "width"     to width,
                        "height"    to height,
                        "createdAt" to now,
                        "updatedAt" to now
                    )
                    mediaRef.child(poseId).setValue(meta).await()

                    val newPose = Pose(
                        id          = poseId,
                        name        = name,
                        level       = level,
                        category    = category,
                        duration    = durationSec,
                        description = description,
                        notes       = notes,
                        image       = downloadUrl
                    )

                    PoseRepository.savePose(newPose) { error ->
                        binding.imageUploadOverlay.visibility = View.GONE
                        if (error == null) {
                            viewModel.addPose(newPose)
                            navigateSmoothly(ClassBuilderActionsFragment())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload pose: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    binding.imageUploadOverlay.visibility = View.GONE
                    Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

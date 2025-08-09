package com.tamara.a25b_11345b_yogis.ui.builder

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

    // user-picked image (we upload it only when saving)
    private var selectedImageUri: Uri? = null
    // local info we read from the selected image
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

    /** Read width/height/mime locally; do NOT upload yet */
    private fun captureLocalImageInfo(localUri: Uri) {
        selectedImageUri = localUri
        binding.imageUploadOverlay.visibility = View.VISIBLE

        val ctx = requireContext()
        val mime = ctx.contentResolver.getType(localUri) ?: "image/jpeg"
        selectedImageMime = mime

        lifecycleScope.launch(Dispatchers.IO) {
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

        fun selectDuration() = with(nb) {
            rbDuration.isChecked = true
            rbRepetitions.isChecked = false
            etDuration.isEnabled = true
            etRepetitions.isEnabled = false
            etDuration.requestFocus()
            etRepetitions.text?.clear()
        }
        fun selectReps() = with(nb) {
            rbDuration.isChecked = false
            rbRepetitions.isChecked = true
            etDuration.isEnabled = false
            etRepetitions.isEnabled = true
            etRepetitions.requestFocus()
            etDuration.text?.clear()
        }
        nb.rbDuration.setOnClickListener { selectDuration() }
        nb.rbRepetitions.setOnClickListener { selectReps() }
        selectDuration()

        val levels = resources.getStringArray(R.array.class_levels)
        nb.acLevel.setAdapter(
            ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, levels)
        )

        nb.btnRegister.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        nb.btnAddPose.setOnClickListener {
            // 1) Read inputs
            val name = nb.etPoseName.text.toString().trim()
            val levelText = nb.acLevel.text.toString().trim().lowercase()
            val level = try { Pose.Level.valueOf(levelText) } catch (_: Exception) { null }
            val duration = nb.etDuration.text.toString().toIntOrNull() ?: 0
            val reps = nb.etRepetitions.text.toString().toIntOrNull() ?: 0
            val description = nb.etDescription.text.toString().trim()

            // 2) Validations
            when {
                name.isBlank() -> {
                    Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                level == null -> {
                    Toast.makeText(requireContext(), "Select a valid level", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                duration <= 0 && reps <= 0 -> {
                    Toast.makeText(requireContext(),
                        "Specify either duration or repetitions (>0)",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                description.isBlank() -> {
                    Toast.makeText(requireContext(),
                        "Description is required", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                selectedImageUri == null -> {
                    Toast.makeText(requireContext(),
                        "Please select an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // 3) Save flow (id + image) atomically
            binding.imageUploadOverlay.visibility = View.VISIBLE

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val db = FirebaseDatabase.getInstance(DB_URL)
                    val posesRef = db.getReference("poses")
                    val mediaRef = db.getReference("mediaAssets")

                    // poseId = readable & unique
                    val base = IdUtils.slugify(name)
                    val poseId = IdUtils.nextAvailableId(posesRef, base)

                    // upload image using the same id
                    val storage = FirebaseStorage.getInstance()
                    val fileRef = storage.reference.child("pose_images/$poseId.jpg")

                    val localUri = selectedImageUri!!
                    fileRef.putFile(localUri).await()
                    val downloadUrl = fileRef.downloadUrl.await().toString()

                    val width = selectedImageWidth ?: 0
                    val height = selectedImageHeight ?: 0
                    val mime = selectedImageMime ?: "image/jpeg"
                    val now = System.currentTimeMillis()

                    // write media metadata under the SAME id
                    val meta = mapOf(
                        "assetId" to poseId,
                        "url" to downloadUrl,
                        "mimeType" to mime,
                        "width" to width,
                        "height" to height,
                        "createdAt" to now,
                        "updatedAt" to now
                    )
                    mediaRef.child(poseId).setValue(meta).await()

                    // build and save Pose (repo will keep the provided id)
                    val newPose = Pose(
                        id = poseId,
                        name = name,
                        level = level,
                        category = Pose.Category.standingPoses,
                        duration = duration.takeIf { it > 0 },
                        repetitions = reps.takeIf { it > 0 },
                        description = description,
                        notes = null,
                        image = downloadUrl
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
                    Toast.makeText(
                        requireContext(),
                        "Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

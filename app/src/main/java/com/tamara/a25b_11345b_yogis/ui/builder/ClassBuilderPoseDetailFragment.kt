package com.tamara.a25b_11345b_yogis.ui.builder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.data.repository.PoseRepository
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderPosePageBinding
import com.tamara.a25b_11345b_yogis.ui.shared.ImagePagerAdapter
import com.tamara.a25b_11345b_yogis.utils.navigateSmoothly
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.viewmodel.ClassBuilderClassPlanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.core.net.toUri

class ClassBuilderPoseDetailFragment : Fragment() {

    companion object {
        private const val ARG_POSE_ID = "pose_id"
        fun newInstance(id: String) = ClassBuilderPoseDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_POSE_ID, id) }
        }
    }

    private var _binding: ClassBuilderPosePageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClassBuilderClassPlanViewModel by activityViewModels()
    private val poseId: String by lazy { requireArguments().getString(ARG_POSE_ID)!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ClassBuilderPosePageBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wireBack(binding.btnPdBack)

        val pose: Pose = PoseRepository.getAll().firstOrNull { it.id == poseId } ?: return

        // Title
        binding.tvPdTitle.text = pose.name

        // Background fill so FIT_CENTER images look nice
        binding.vpPdImages.setBackgroundResource(R.color.background_text_field)

        // Silent “no adapter attached” warning
        binding.vpPdImages.adapter = ImagePagerAdapter(emptyList()) {}

        // Resolve image path/gs/https -> fresh https URLs with token
        val sources = listOfNotNull(pose.image).filter { it.isNotBlank() }
        viewLifecycleOwner.lifecycleScope.launch {
            val https = toHttpsUrls(sources)

            val pagerAdapter = ImagePagerAdapter(https) { /* no loader here */ }
            binding.vpPdImages.adapter = pagerAdapter

            val single = pagerAdapter.itemCount <= 1
            binding.btnPdPrev.visibility = if (single) View.GONE else View.VISIBLE
            binding.btnPdNext.visibility = if (single) View.GONE else View.VISIBLE
            binding.tlPdIndicator.visibility = if (single) View.GONE else View.VISIBLE
            if (!single) {
                TabLayoutMediator(binding.tlPdIndicator, binding.vpPdImages) { _, _ -> }.attach()
                binding.btnPdPrev.setOnClickListener {
                    binding.vpPdImages.currentItem =
                        (binding.vpPdImages.currentItem - 1).coerceAtLeast(0)
                }
                binding.btnPdNext.setOnClickListener {
                    binding.vpPdImages.currentItem =
                        (binding.vpPdImages.currentItem + 1)
                            .coerceAtMost((pagerAdapter.itemCount - 1).coerceAtLeast(0))
                }
            }
        }

        // Add to plan
        binding.btnAddPose.setOnClickListener {
            viewModel.addPose(pose)
            navigateSmoothly(ClassBuilderActionsFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // -------- helpers (same logic you used in PoseDetail) --------

    private fun storage(): FirebaseStorage {
        val bucket = FirebaseApp.getInstance().options.storageBucket // e.g. yogis-xxx.firebasestorage.app
        return if (!bucket.isNullOrBlank()) FirebaseStorage.getInstance("gs://$bucket")
        else FirebaseStorage.getInstance()
    }

    private suspend fun toHttpsUrls(sources: List<String>): List<String> = withContext(Dispatchers.IO) {
        sources.mapNotNull { raw ->
            try {
                when {
                    raw.isBlank() -> null

                    raw.startsWith("gs://", true) -> {
                        storage().getReferenceFromUrl(raw).downloadUrl.await().toString()
                    }

                    raw.startsWith("http", true) -> {
                        val uri = raw.toUri()
                        val seg = uri.pathSegments // [v0, b, <bucket>, o, <encodedPath>]
                        val bucketFromUrl = seg.getOrNull(2) ?: return@mapNotNull null
                        val encodedPath   = seg.getOrNull(4) ?: return@mapNotNull null
                        val objectPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.name())
                        val normalizedBucket = bucketFromUrl
                            .replace(".appspot.com", ".firebasestorage.app", ignoreCase = true)
                        FirebaseStorage.getInstance("gs://$normalizedBucket")
                            .reference.child(objectPath).downloadUrl.await().toString()
                    }

                    else -> storage().reference.child(raw).downloadUrl.await().toString()
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}

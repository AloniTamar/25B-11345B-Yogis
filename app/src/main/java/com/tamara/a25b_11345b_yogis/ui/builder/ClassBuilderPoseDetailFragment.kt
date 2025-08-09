package com.tamara.a25b_11345b_yogis.ui.builder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.ClassBuilderPosePageBinding
import com.tamara.a25b_11345b_yogis.ui.shared.ImagePagerAdapter
import com.tamara.a25b_11345b_yogis.utils.wireBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.core.net.toUri
import com.google.firebase.database.FirebaseDatabase
import com.tamara.a25b_11345b_yogis.ui.library.PoseDetailFragment
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain

class ClassBuilderPoseDetailFragment : Fragment() {

    companion object {
        private const val ARG_POSE_ID = "pose_id"
        fun newInstance(id: String) = ClassBuilderPoseDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_POSE_ID, id) }
        }
    }

    private var _binding: ClassBuilderPosePageBinding? = null
    private val binding get() = _binding!!
    private val poseId: String by lazy { requireArguments().getString(ARG_POSE_ID)!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ClassBuilderPosePageBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnPdBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        binding.pdImageLoaderOverlay.visibility = View.VISIBLE
        binding.vpPdImages.setBackgroundResource(R.color.background_text_field)

        binding.vpPdImages.adapter = ImagePagerAdapter(emptyList()) { }

        lifecycleScope.launch {
            val pose = withContext(Dispatchers.IO) {
                FirebaseDatabase
                    .getInstance("https://yogis-e26d1-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("poses")
                    .child(poseId)
                    .get()
                    .await()
                    .getValue(Pose::class.java)
            }
            if (pose == null) {
                Toast.makeText(requireContext(), "Pose not found", Toast.LENGTH_SHORT).show()
                binding.pdImageLoaderOverlay.visibility = View.GONE
                return@launch
            }

            binding.tvPdTitle.text = pose.name

            val imageSources = listOfNotNull(pose.image).filter { it.isNotBlank() }
            val httpsUrls = toHttpsUrls(imageSources)
            Log.d("PoseDetail", "resolved urls=$httpsUrls")

            if (httpsUrls.isEmpty()) {
                binding.pdImageLoaderOverlay.visibility = View.GONE
            }

            val pagerAdapter = ImagePagerAdapter(httpsUrls) {
                binding.pdImageLoaderOverlay.post { binding.pdImageLoaderOverlay.visibility = View.GONE }
            }
            binding.vpPdImages.adapter = pagerAdapter

            binding.tvPdLevel.text       = pose.level.name
            binding.tvPdFamily.text      = pose.category.name
            binding.tvPdDescription.text = pose.description
            if (pose.notes.isNullOrBlank()) {
                binding.tvPdNotes.visibility = View.GONE
                binding.tvPdNotesContent.visibility = View.GONE
            } else {
                binding.tvPdNotes.visibility = View.VISIBLE
                binding.tvPdNotesContent.visibility = View.VISIBLE
                binding.tvPdNotesContent.text = pose.notes
            }

            val duration = pose.duration ?: 0
            val reps     = pose.repetitions ?: 0
            binding.tvPdDuration.text    = "$duration seconds"
            binding.tvPdDurationLabel.text = "$reps reps"

            val colorBlack    = ContextCompat.getColor(requireContext(), R.color.black)
            val colorSubtitle = ContextCompat.getColor(requireContext(), R.color.sub_titles_text)
            if (duration > 0) {
                binding.tvPdDuration.setTextColor(colorBlack)
                binding.tvPdRepsLabel.setTextColor(colorSubtitle)
            } else {
                binding.tvPdDuration.setTextColor(colorSubtitle)
                binding.tvPdRepsLabel.setTextColor(colorBlack)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun storage(): FirebaseStorage {
        val bucket = FirebaseApp.getInstance().options.storageBucket
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

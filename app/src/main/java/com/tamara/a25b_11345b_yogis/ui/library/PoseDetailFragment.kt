package com.tamara.a25b_11345b_yogis.ui.library

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tamara.a25b_11345b_yogis.R
import com.tamara.a25b_11345b_yogis.data.model.Pose
import com.tamara.a25b_11345b_yogis.databinding.PosePageBinding
import com.tamara.a25b_11345b_yogis.utils.navigateBackToMain
import com.tamara.a25b_11345b_yogis.utils.wireBack
import com.tamara.a25b_11345b_yogis.ui.shared.ImagePagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.core.net.toUri

class PoseDetailFragment : Fragment() {

    private var _binding: PosePageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_POSE_ID = "pose_id"
        fun newInstance(poseId: String) = PoseDetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_POSE_ID, poseId) }
        }
    }

    /** ViewHolder for the pager */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PosePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wireBack(binding.btnPdBack)
        binding.tvBackMain.setOnClickListener { navigateBackToMain() }

        val poseId = requireArguments().getString(ARG_POSE_ID)
            ?: error("PoseDetailFragment requires a pose_id argument")

        binding.pdImageLoaderOverlay.visibility = View.VISIBLE
        binding.vpPdImages.setBackgroundResource(R.color.background_text_field)

        // attach an empty adapter to silence "No adapter attached" logs
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

            // Title
            binding.tvPdTitle.text = pose.name

            // Build list from DB and resolve EVERYTHING to fresh HTTPS URLs (with token)
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

            // Text fields
            binding.tvPdValueLevel.text       = pose.level.name
            binding.tvPdValueFamily.text      = pose.category.name
            binding.tvPdValueDescription.text = pose.description
            if (pose.notes.isNullOrBlank()) {
                binding.tvPdLabelNotes.visibility = View.GONE
                binding.tvPdValueNotes.visibility = View.GONE
            } else {
                binding.tvPdLabelNotes.visibility = View.VISIBLE
                binding.tvPdValueNotes.visibility = View.VISIBLE
                binding.tvPdValueNotes.text = pose.notes
            }

            // Duration & Reps texts + highlight
            val duration = pose.duration ?: 0
            val reps     = pose.repetitions ?: 0
            binding.tvPdValueDuration.text    = "$duration seconds"
            binding.tvPdValueRepetitions.text = "$reps reps"

            val colorBlack    = ContextCompat.getColor(requireContext(), R.color.black)
            val colorSubtitle = ContextCompat.getColor(requireContext(), R.color.sub_titles_text)
            if (duration > 0) {
                binding.tvPdValueDuration.setTextColor(colorBlack)
                binding.tvPdValueRepetitions.setTextColor(colorSubtitle)
            } else {
                binding.tvPdValueDuration.setTextColor(colorSubtitle)
                binding.tvPdValueRepetitions.setTextColor(colorBlack)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ---------------- helpers ----------------

    /** FirebaseStorage instance using your configured bucket (google-services.json) */
    private fun storage(): FirebaseStorage {
        val bucket = FirebaseApp.getInstance().options.storageBucket // e.g. yogis-e26d1.firebasestorage.app
        return if (!bucket.isNullOrBlank()) {
            FirebaseStorage.getInstance("gs://$bucket")
        } else {
            FirebaseStorage.getInstance()
        }
    }

    /**
     * Convert whatever strings are stored (plain path / gs:// / https)
     * into fresh HTTPS download URLs with access tokens.
     */
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

                        val st = FirebaseStorage.getInstance("gs://$normalizedBucket")
                        st.reference.child(objectPath).downloadUrl.await().toString()
                    }

                    else -> storage().reference.child(raw).downloadUrl.await().toString()
                }
            } catch (e: Exception) {
                Log.e("PoseDetail", "toHttpsUrls failed for: $raw", e)
                null
            }
        }
    }
}

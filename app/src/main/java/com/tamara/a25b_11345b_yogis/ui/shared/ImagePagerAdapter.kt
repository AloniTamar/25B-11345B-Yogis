package com.tamara.a25b_11345b_yogis.ui.shared

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * Tiny, reusable ViewPager2 adapter that shows a list of image URLs.
 * - Uses FIT_CENTER so images aren’t cropped.
 * - Calls [onFirstImageSettled] once (success/fail) so caller can hide loaders.
 */
class ImagePagerAdapter(
    private val images: List<String>,
    private val onFirstImageSettled: () -> Unit
) : RecyclerView.Adapter<ImagePagerAdapter.VH>() {

    class VH(val iv: ImageView) : RecyclerView.ViewHolder(iv)

    private var notified = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val iv = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }
        return VH(iv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = images[position]
        Glide.with(holder.iv.context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!notified) { notified = true; onFirstImageSettled() }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!notified) { notified = true; onFirstImageSettled() }
                    return false
                }
            })
            .into(holder.iv)
    }

    override fun getItemCount(): Int = images.size
}

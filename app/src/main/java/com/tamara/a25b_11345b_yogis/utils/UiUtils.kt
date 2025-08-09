import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable

fun colorForLetter(letter: Char): Int {
    val colors = listOf(
        0xFFFFF59D.toInt(), // Light yellow
        0xFF81D4FA.toInt(), // Light blue
        0xFFC8E6C9.toInt(), // Light green
        0xFFFFCCBC.toInt(), // Light orange
        0xFFD1C4E9.toInt(), // Light purple
        0xFFFFF9C4.toInt(), // Light lemon
        0xFFB39DDB.toInt(), // Pastel purple
        0xFFFFAB91.toInt(), // Pastel coral
        0xFFB2DFDB.toInt(), // Pastel teal
        0xFFF8BBD0.toInt()  // Pastel pink
    )
    val idx = (letter.uppercaseChar().code - 'A'.code) % colors.size
    return colors[idx]
}

fun createTextAvatar(context: Context, letter: Char, size: Int = 128): BitmapDrawable {
    val bgColor = colorForLetter(letter)
    val bmp = createBitmap(size, size)
    val canvas = Canvas(bmp)
    val paint = Paint().apply {
        color = bgColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    paint.color = Color.WHITE
    paint.textSize = size * 0.55f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.textAlign = Paint.Align.CENTER

    val fm = paint.fontMetrics
    val textY = size / 2f - (fm.ascent + fm.descent) / 2

    canvas.drawText(letter.uppercaseChar().toString(), size / 2f, textY, paint)
    return bmp.toDrawable(context.resources)
}
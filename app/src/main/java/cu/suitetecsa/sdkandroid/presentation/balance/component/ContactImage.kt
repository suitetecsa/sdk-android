package cu.suitetecsa.sdkandroid.presentation.balance.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.sdkandroid.R

private const val Percent = 50

@Composable
fun ContactImage(
    photoUriString: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = loadPicture(context, photoUriString)

    bitmap?.asImageBitmap()?.let {
        Image(
            modifier = modifier.size(64.dp).clip(RoundedCornerShape(Percent)),
            bitmap = it,
            contentDescription = "Contact Photo")
    } ?: run {
        Image(
            modifier = modifier.clip(RoundedCornerShape(Percent)),
            bitmap = ImageBitmap.imageResource(id = R.drawable.default_user_image),
            contentDescription = null
        )
    }
}

private fun loadPicture(context: Context, photoUriString: String?): Bitmap? {
    photoUriString ?: return null
    val photoUri = Uri.parse(photoUriString)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, photoUri)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, photoUri)
    }
}

@Preview
@Composable
private fun ContactImagePreview() {
    ContactImage(photoUriString = null)
}

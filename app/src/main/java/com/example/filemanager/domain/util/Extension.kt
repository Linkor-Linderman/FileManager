package com.example.filemanager.domain.util

import com.example.filemanager.R

sealed class Extension(
    val name: String,
    val iconId: Int
) {
    object Txt : Extension("txt", R.drawable.txt_icon)
    object Png : Extension("png", R.drawable.png_icon)
    object Jpeg : Extension("jpg", R.drawable.jpeg_icon)
    object Pdf : Extension("pdf", R.drawable.pdf_icon)
    object Epub : Extension("epub", R.drawable.epub_icon)
    object Mp3 : Extension("mp3", R.drawable.mp3_icon)
    object Mp4 : Extension("mp4", R.drawable.mp4_icon)
    object Zip : Extension("zip", R.drawable.zip_icon)
}

package com.example.videoplayer_and_downloader.Utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.RelativeLayout
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.MyFileUtils
import com.example.videoplayer_and_downloader.databinding.DeleteDialogBinding

class DeleteDialog(
    context: Context,
    private val existingFile: MediaModel? = null, private val newUri: Uri? = null,
    private val onResult: ((MediaModel?) -> Unit)
) :
    Dialog(context) {
    private lateinit var fileUtils: MyFileUtils
    private lateinit var binding: DeleteDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }

    private fun initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DeleteDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.decorView.setBackgroundColor(0)
        window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)
        fileUtils = MyFileUtils( "DeleteFile",context)
        binding.btnDone.setOnClickListener {
            if (fileUtils.deleteFile(existingFile, newUri)) {
                existingFile?.let { uMedia ->
                    Log.d("deletedUri2", uMedia.uri)
                    onResult.invoke(uMedia)
                }
                newUri?.let { uUri ->
                    Log.d("deletedUri1", uUri.toString())
                    onResult.invoke(existingFile)
                }
            }
            dismiss()
        }
        binding.ivCancel.setOnClickListener {
            dismiss()
        }
        binding.btnNo.setOnClickListener {
            dismiss()
        }
    }


    override fun dismiss() {
        if (isShowing)
            super.dismiss()
    }


}
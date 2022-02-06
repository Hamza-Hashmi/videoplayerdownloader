package com.example.videoplayer_and_downloader.Utils

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.RelativeLayout
import com.example.videoplayer_and_downloader.models.CustomFiles
import com.example.videoplayer_and_downloader.models.MyFileUtils
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.databinding.RenameDialogBinding

class RenameDialog(
    context: Context,
    private val existingFile: CustomFiles,
    private val iRenamed: IRenamed
): Dialog(context) {
    private lateinit var fileUtils: MyFileUtils
    private lateinit var binding: RenameDialogBinding
    private lateinit var finalName: String
    private lateinit var finalExtension: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }

    private fun initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = RenameDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.decorView.setBackgroundColor(0)
        window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)
        fileUtils = MyFileUtils("RenameFile",context)
        binding.btnDone.setOnClickListener {
            if (binding.edName.text.isEmpty())
                showToast(context, R.string.enter_name_first)
            else {
                val renamedFile =
                    fileUtils.renameFile(
                        existingFile.realPath,
                        "${binding.edName.text}$finalExtension"
                    )
                renamedFile?.let {
                    iRenamed.onRenamed(
                        existingFile = existingFile,
                        newFile = CustomFiles(
                            name = it.name,
                            realPath = it.absolutePath,
                            isVideo = existingFile.isVideo,
                            uri = Uri.fromFile(it).toString()
                        )
                    )
                }
                dismiss()
            }
        }
        binding.ivCancel.setOnClickListener {
            dismiss()
        }
        existingFile.name.apply {

            val pos = lastIndexOf(".")
            if (pos > 0 && pos < length - 1) {
                finalName = substring(0, pos)
                finalExtension = substring(pos, length)
            }
            binding.edName.setText(finalName)
        }

    }


    override fun dismiss() {
        if (isShowing)
            super.dismiss()
    }

    interface IRenamed {
        fun onRenamed(existingFile: CustomFiles, newFile: CustomFiles)
    }


}
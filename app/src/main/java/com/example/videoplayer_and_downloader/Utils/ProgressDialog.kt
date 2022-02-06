package com.example.videoplayer_and_downloader.Utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.RelativeLayout
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.databinding.ProgressDialogBinding

class ProgressDialog(context: Context) : Dialog(context) {
    private lateinit var progressDialogBinding: ProgressDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }

    private fun initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialogBinding = ProgressDialogBinding.inflate(layoutInflater)
        setContentView(progressDialogBinding.root)
        window!!.decorView.setBackgroundColor(0)
        window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)
        progressDialogBinding.tvMessage.text = context.resources.getString(R.string.wait)
    }

    fun showWithMessage(message: String) {
        try {
            show()
            progressDialogBinding.tvMessage.text = message
        } catch (ignored: Exception) {
        }
    }

    override fun dismiss() {
        try {
            if (isShowing)
                super.dismiss()
        } catch (ignored: Exception) { }
    }
}
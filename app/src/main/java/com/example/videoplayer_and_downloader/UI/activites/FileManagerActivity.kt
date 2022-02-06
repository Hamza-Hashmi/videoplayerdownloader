package com.example.videoplayer_and_downloader.UI.activites

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.Utils.*
import com.example.videoplayer_and_downloader.adapters.FileManagerAdapter
import com.example.videoplayer_and_downloader.databinding.ActivityFileManagerBinding
import com.example.videoplayer_and_downloader.models.MediaModel
import com.example.videoplayer_and_downloader.models.SelectedListInfo
import java.io.File
import java.lang.Exception

class FileManagerActivity : AppCompatActivity() {
    lateinit var binding:ActivityFileManagerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFileManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val path = intent.getStringExtra("path")
        val root = File(path)
        val filesAndFolders = root.listFiles()
        if (filesAndFolders == null || filesAndFolders.size == 0) {
            binding.apply {
                this.nodatafountv.visibility = View.VISIBLE
                 this.ivNoMedia.visibility = View.VISIBLE
            }
            return
        }



        //FileManagerAdapter(filesAndFolders)
       // recyclerView.layoutManager = LinearLayoutManager(this)
        binding.filesManagerRv.adapter = FileManagerAdapter(filesAndFolders) { selectedFile ->
            if (selectedFile.isDirectory()) {
                val intent: Intent = Intent(this@FileManagerActivity, FileManagerActivity::class.java)
                val path: String = selectedFile.getAbsolutePath()
                intent.putExtra("path", path)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {

                val media = MediaModel(selectedFile.name,selectedFile.absolutePath,true,false,false,false,"unknown","unknown","",
                    getUriPath(selectedFile.absolutePath).toString())

                var list : ArrayList<MediaModel> = ArrayList()
                list.add(media)

                val info=   SelectedListInfo(
                    listType = deviceItemVideo,
                    selectedParent = none,
                    media.isVideo
                )

                val f = File(media.realPath)
                if (f.exists()) {
                    my_listType=info.listType
                    openPlayerActivity(
                        context = this@FileManagerActivity,
                        position = 0,
                        info = info,
                        realPath = media.realPath
                    )
                } else {
                    showToast(this@FileManagerActivity, R.string.error_files_missing)
                }

                my_listType = history

                //open the file
                //try {
                  //  val intent = Intent()
                    //intent.action = Intent.ACTION_VIEW
                  //  val type = "image/*"//
                    //intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    //startActivity(intent)
                /*} catch (e: Exception) {
                    Toast.makeText(
                        getApplicationContext(),
                        "Cannot open the file",
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
            }

        }


    }
}
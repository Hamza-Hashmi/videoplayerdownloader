package com.example.videodownload.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.NestedScrollWebDashboardActivity
import com.example.videodownload.allViewModel.MainViewModel
import com.example.videodownload.appUtils.*
import com.example.videoplayer_and_downloader.R
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.MyClipboardManager.readFromClipboard
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.appUtils.TinyDB
import com.example.videoplayer_and_downloader.SocialVideosDonwloader.downloderActivities.ShowWhatsappStatus
import com.example.videoplayer_and_downloader.databinding.FragmentSearchVideoUrlBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SearchVideoUrlFragment : BaseFargment(),View.OnClickListener {
    val mainViewModel: MainViewModel by sharedViewModel()
    var defaultEngine = 0
    var _binding : FragmentSearchVideoUrlBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchVideoUrlBinding.inflate(layoutInflater)
        binding.apply {
            btnBack.setOnClickListener {
            }
            btnDownload?.setOnClickListener {
                edttextUrlField?.let {
                    if (!it.text.isNullOrEmpty()){
                        edttextUrlField?.let {
                            searchOfWebBox(it)
                            getMainActivity()?.hideKeybaord(it)
                        }
                    }else{
                        getMainActivity()?.showShortMessage("Invalid Url")
                    }
                }
            }

            btnPaste?.setOnClickListener {
                edttextUrlField?.let {
                    it.setText(readFromClipboard(context))
                }
            }

            edttextUrlField?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edttextUrlField?.let {
                        searchOfWebBox(it)
                        getMainActivity()?.hideKeybaord(it)
                    }
                    return@OnEditorActionListener true
                }
                false
            })

        }
        activity?.let { activity ->

            val onBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.e(
                        Constants.LOG_TAG,
                        "handleOnBackPressed: stack count ${activity?.supportFragmentManager!!.backStackEntryCount}"
                    )
                }
            }
            activity?.let {
                it.getOnBackPressedDispatcher().addCallback(requireActivity(), onBackPressedCallback)
            }
        }
        return binding.root

    }
    val permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            //permission is granted
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            TedPermission.with(getMainActivity())
                .setPermissionListener(this)
                .setDeniedTitle("Permission denied")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Setting")
                .setPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearLayoutInstagram?.setOnClickListener(this)
        binding.linearLayoutTwitter?.setOnClickListener(this)
        binding.linearLayoutFacebook?.setOnClickListener(this)
        binding.linearLayoutVimeo?.setOnClickListener(this)
        binding.linearLayoutFBWatch?.setOnClickListener(this)
        binding.linearLayoutLikee?.setOnClickListener(this)
        binding.linearLayoutDailymotion?.setOnClickListener(this)
        binding.linearLayoutWhatsapp?.setOnClickListener(this)

        defaultEngine = TinyDB.getInstance(getMainActivity()).getInt(Constants.defaultSearchEngine)
        registerObserver()

        TedPermission.with(getMainActivity())
            .setPermissionListener(permissionlistener)
            .setDeniedTitle("Permission denied")
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setGotoSettingButtonText("Setting")
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()

    }
    private fun registerObserver() {
        mainViewModel.goToBroswerLiveData.observe(viewLifecycleOwner, Observer { url ->
            if (url != null && !url.first.equals("whatsapp")) {
                val intent = Intent(activity, NestedScrollWebDashboardActivity::class.java)
                intent.putExtra(Constants.KEY_URL, url.first)
                intent.putExtra(Constants.KEY_SITE_NAME, url.second)
                startActivity(intent)
            } else if (url.first.equals("whatsapp")) {

                val intent = Intent(activity, ShowWhatsappStatus::class.java)
                intent.putExtra(Constants.KEY_URL, url.first)
                startActivity(intent)
            }
        })
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linearLayoutInstagram -> {
                getMainActivity()?.hideKeybaord(binding.linearLayoutInstagram)
                mainViewModel.goToNestedScrollWebFragment("https://www.instagram.com", "Insta")
            }
            R.id.linearLayoutTwitter -> {


                getMainActivity()?.hideKeybaord(binding.linearLayoutTwitter)
                mainViewModel.goToNestedScrollWebFragment("https://mobile.twitter.com", "twitter")
            }
            R.id.linearLayoutFacebook -> {


                getMainActivity()?.hideKeybaord(binding.linearLayoutFacebook)
                mainViewModel.goToNestedScrollWebFragment("https://m.facebook.com", "facebook")
            }
            R.id.linearLayoutVimeo -> {

                getMainActivity()?.hideKeybaord(binding.linearLayoutVimeo)
                mainViewModel.goToNestedScrollWebFragment(getString(R.string.vimeourl), "vimeo")
            }
            R.id.linearLayoutFBWatch -> {
                Timber.tag("FBWatch_Btn_Click").d("FBWatch button is clicked")

                getMainActivity()?.hideKeybaord(binding.linearLayoutFBWatch)
                mainViewModel.goToNestedScrollWebFragment(getString(R.string.fbWatchurl), "facebook.watch")
                // }
            }
            R.id.linearLayoutLikee -> {

                getMainActivity()?.hideKeybaord(binding.linearLayoutLikee)
                mainViewModel.goToNestedScrollWebFragment(getString(R.string.likeeUrl), "likee")
            }
            R.id.linearLayoutDailymotion -> {

                getMainActivity()?.hideKeybaord(binding.linearLayoutDailymotion)
                mainViewModel.goToNestedScrollWebFragment(getString(R.string.dailymotion_url), "dailymotion")
            }
            R.id.linearLayoutWhatsapp -> {
                Timber.tag("Whatsapp_Btn_Click").d("Whatsapp button is clicked")
                mainViewModel.goToNestedScrollWebFragment("whatsapp", "whatsapp")

            }
        }
    }
    private fun searchOfWebBox(web: EditText) {
        val text1 = web.text.toString()
        if (text1.isEmpty())
            when (Constants.defSearchEngineValue) {
                0 -> {
                    openLink("https://www.google.com/")
                }
                1 -> {
                    openLink("https://search.yahoo.com/")
                }
                2 -> {
                    openLink("https://www.bing.com/")
                }
                3 -> {
                    openLink("https://www.baidu.com/")
                }
            }
        else if (!getMainActivity()?.isNetworkAvailable()!!)
            getMainActivity()?.showShortMessage("Internet not available")
        else if (text1.contains("youtube")) {
            getMainActivity()?.showyoutubeNotSupported()
            web.setText("")
        } else {
            openLink(text1)
            web.setText("")
        }
    }

    private fun openLink(text: String) {
        var url1 = text
        if (Patterns.WEB_URL.matcher(url1).matches()) {
            if (!url1.startsWith("http")) {
                url1 = "http://$url1"
            }
        } else {
            url1 = "https://google.com/search?q=$url1"
        }
        when {
            getMainActivity()?.isNetworkAvailable()!! -> {
                mainViewModel.goToNestedScrollWebFragment(url1, "Site")
            }
            else -> getMainActivity()?.showShortMessage("Internet not available")
        }
    }

}
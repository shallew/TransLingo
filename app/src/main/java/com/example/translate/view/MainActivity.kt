package com.example.translate.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.collection.LruCache
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.translate.R
import com.example.translate.databinding.ActivityMainBinding
import com.example.translate.tools.GlobalApplication
import com.example.translate.viewModel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    /**
     * 跳转到历史页面的请求码
     */
    private val REQUEST_CODE_FOR_HISTORY = 1

    /**
     * 录音权限请求码
     */
    private val REQUEST_AUDIO_PERMISSION_CODE = 2

    private lateinit var mContext: Context

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //初始化Context
        mContext = this

        activityBinding.lifecycleOwner = this

        //初始化UI时隐藏播放音频按钮
        activityBinding.playAudioBtn.visibility = View.GONE
        clickToPlayAudioListener()

        //监听文本改变后将MediaPlayer清除
        inputTextChangedListener()

        //初始化语言选择弹窗
        initLanguageSelectMenu()

        //开始翻译的监听
        clickToStartTranslateListener()

        //点击历史记录按钮跳转历史记录页面
        activityBinding.historicalRecordIv.setOnClickListener {
            val intent = Intent(this, HistoricalRecordActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_FOR_HISTORY)
        }

        activityBinding.audioRecorderBtn.setOnClickListener {
            val intent = Intent(this, EnglishAphorismActivity::class.java)
            startActivity(intent)
        }


//        /**
//         * 录音、停止录音的监听
//         */
//        activityBinding.audioRecorderBtn.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    //按下按钮开始录音
//                    checkAudioPermission()
//                    Log.d("RecordingDebug", "onCreate: 开始录音")
//                    true
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    //松开按钮停止录音
//                    mainViewModel.stopRecording()
//                    lottieAnimationHide()
//                    true
//                }
//
//                else -> false
//            }
//        }

//        activityBinding.audioRecorderBtn.setOnClickListener {
//            PictureSelector.create(this)
//                .openSystemGallery(MediaType.IMAGE)
//                .forResult(object : OnResultCallbackListener {
//                    override fun onCancel() {
//
//                    }
//
//                    override fun onResult(result: List<LocalMedia>) {
//                        if (result.isNotEmpty()) {
//                            mainViewModel.requestImageTranslate(result[0].absolutePath)
//                        }
//                    }
//
//                })
//        }

        //观察MainViewModel中的文本翻译结果
        mainViewModel.textTransLiveData.observe(this) { result ->
            //UI更新
            val showText = result.toString()
            val size = showText.length
            activityBinding.resultForTranslate.text = showText.substring(1, size - 1)
            //顺带显示播放音频按钮
            activityBinding.playAudioBtn.visibility = View.VISIBLE
        }

        //观察录音过程失败的LiveData
        mainViewModel.audioInterruptLiveData.observe(this) {
            if (it) {
                Toast.makeText(this, "录音过程异常中断！", Toast.LENGTH_SHORT).show()
                lottieAnimationHide()
            }
        }

//        Glide.with(this).load("https://cn.bing.com/images/search?view=detailV2&ccid=fqR1PWdz&id=1D949D3B9E88B2E309CFCC4ADB9C96F27168D5BD&thid=OIP.fqR1PWdzvaYcs2jRHhPPggHaE8&mediaurl=https%3a%2f%2fsc.68design.net%2fphotofiles%2f201805%2fcIRFCGVXdE.jpg&exph=2000&expw=3000&q=%e5%9b%be%e7%89%87&simid=607998440275529886&FORM=IRPRST&ck=CAB5689A6ECF9D1BC06E2825CA444157&selectedIndex=0&itb=0")
//            .into()

//        //观察录音转换字符串Base64结果的LiveData
//        mainViewModel.audioStrLiveData.observe(this) {
//            //转化成功则直接调用语音翻译
//            val fl = GlobalApplication.languages.find { pair ->
//                pair.first == activityBinding.fromLanguage.text.toString()
//            }?.second.toString()
//            val tl = GlobalApplication.languages.find { pair ->
//                pair.first == activityBinding.toLanguage.text.toString()
//            }?.second.toString()
//            //将字符串Base64转换成音频
//            Log.d("RecordingDebug", ":录音成功转成Base64字符串 $it")
//            if (it != null) {
//                mainViewModel.requestAudioTranslate(it, "zh", "en")
//            }
//        }

        //观察语言翻译结果的LiveData
        mainViewModel.audioTransLiveData.observe(this) { it ->
            if (it != null) {
                if (it.result != null) {
                    activityBinding.textInputLayout.editText?.setText(it.result.source)
                    activityBinding.resultForTranslate.text = it.result.target
                }
            }
        }


    }

    /**
     * 监听文本改变后清除音频和翻译结果
     */
    private fun inputTextChangedListener() {
        //监听文本改变后将MediaPlayer清除
        activityBinding.textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //文本变化前
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //文本变化时
            }

            override fun afterTextChanged(s: Editable?) {
                //文本变化后
                mainViewModel.cancelAudioPrepared()
                activityBinding.playAudioBtn.visibility = View.GONE
                activityBinding.resultForTranslate.text = ""
            }

        })
    }

    /**
     * 设置点击音频按钮开始播放音频的监听
     */
    private fun clickToPlayAudioListener() {
        activityBinding.playAudioBtn.setOnClickListener {
            try {
                mainViewModel.startPlayAudio()
            } catch (e: Exception) {
                Toast.makeText(this, "音频播放异常！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 点击开始翻译的监听
     */
    private fun clickToStartTranslateListener() {
        activityBinding.performTranslationBtn.setOnClickListener {
            val text = activityBinding.textInputLayout.editText?.text.toString().trim()
            if (text != "") {
                var fl = activityBinding.fromLanguage.text.toString()
                var tl = activityBinding.toLanguage.text.toString()
                fl = GlobalApplication.languages.find { it.first == fl }?.second.toString()
                tl = GlobalApplication.languages.find { it.first == tl }?.second.toString()
                Toast.makeText(mContext, "$fl, $tl", Toast.LENGTH_SHORT).show()
                //触发网络请求开始翻译文本
                mainViewModel.requestTextTranslate(text, fl, tl)
                //尚未处理翻译失败的情况
                mainViewModel.translateFliedLiveData.observe(this) {
                    if (it) {
                        Toast.makeText(this, "翻译失败！", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * 生成语言选择的PopupMenu
     */
    private fun AppCompatButton.initDialogMenu(isToLanguage: Boolean = false) {
        var languageList = if (isToLanguage) {
            GlobalApplication.languages.subList(1, GlobalApplication.languages.size)
        } else {
            GlobalApplication.languages
        }
        //目前选择的选项的下标
        val selectedIndex = languageList.indexOfFirst { it.first == this.text.toString() }
        val dialog = AlertDialog.Builder(mContext)
            .setTitle("选择语言")
            .setSingleChoiceItems(
                languageList.map { it.first }.toTypedArray(),
                selectedIndex
            ) { dialog, which ->
                this.text = languageList[which].first
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)

        setOnClickListener {
            dialog.show()
        }

    }

    /**
     * 初始化语言选择弹窗
     */
    private fun initLanguageSelectMenu() {
        activityBinding.fromLanguage.initDialogMenu()
        activityBinding.toLanguage.initDialogMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_FOR_HISTORY -> if (resultCode == RESULT_OK) {
                activityBinding.textInputLayout.editText?.setText(data?.getStringExtra("originalText"))
                activityBinding.fromLanguage.text = GlobalApplication.languages.find {
                    it.second == data?.getStringExtra("fromLanguage")
                }?.first
                activityBinding.toLanguage.text = GlobalApplication.languages.find {
                    it.second == data?.getStringExtra("toLanguage")
                }?.first
                activityBinding.performTranslationBtn.performClick()
            }
        }
    }

//    private fun checkAudioPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.RECORD_AUDIO,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                ),
//                REQUEST_AUDIO_PERMISSION_CODE
//            )
//        } else {
//            mainViewModel.startRecording()
//            lottieAnimationShow()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            REQUEST_AUDIO_PERMISSION_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 开始录音
//                    Toast.makeText(this, "您已授权录音", Toast.LENGTH_SHORT).show()
//                    mainViewModel.startRecording()
//                    lottieAnimationShow()
//                } else {
//                    Toast.makeText(this, "录音权限被拒绝", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }


    private fun lottieAnimationShow() {
        activityBinding.overlayView.visibility = View.VISIBLE
        activityBinding.lottieRecordingAnimation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    private fun lottieAnimationHide() {
        activityBinding.overlayView.visibility = View.GONE
        activityBinding.lottieRecordingAnimation.apply {
            visibility = View.GONE
            cancelAnimation()
        }
    }

//    private var mediaPlayer: MediaPlayer? = null
//
//    private fun playRecordedAudio() {
//        mediaPlayer = MediaPlayer().apply {
//            try {
//                setDataSource(AudioRecorder.pcmFile.absolutePath)
//                prepare()
//                start()
//            } catch (e: IOException) {
//                Log.d("mediaPlayer", "playRecordedAudio: $e")
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()
        if (englishMottoList.isEmpty()) {
            //如果集合为空，则预加载数据
            mainViewModel.preloadEnglishMottoAndWallpaper()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (isRecording) {
//            stopRecording()
//        }
//        mediaPlayer?.release()
    }
}
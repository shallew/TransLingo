package com.example.translate.viewModel

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.translate.model.network.EnglishNetwork
import com.example.translate.model.repository.TranslateResultRepository
import com.example.translate.model.retrofit.audio.AudioTransResponse
import com.example.translate.model.retrofit.english_motto.EnglishMottoResponse
import com.example.translate.view.EnglishMottoAndWallPaper
import com.example.translate.view.dictionaryLruCache
import com.example.translate.view.englishMottoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import leakcanary.ObjectWatcher
import org.greenrobot.eventbus.EventBus
import kotlin.Exception
import kotlin.jvm.Throws

/**
 *   文件名: com.example.translate.viewModel.MainViewModel
 *   @author: shallew
 *   @data: 2024/7/24 16:04
 *   @about: TODO
 */
class MainViewModel : ViewModel() {

    /**
     * 获取的文本翻译结果
     */
    private val _textTransLiveData = MutableLiveData<List<String>>()

    /**
     * View层的可读LiveData
     */
    val textTransLiveData: LiveData<List<String>> = _textTransLiveData

    /**
     * 翻译失败的LiveData
     */
    private val _translateFailedLiveData = MutableLiveData<Boolean>()

    /**
     * View层可读的LiveData
     */
    val translateFliedLiveData: LiveData<Boolean> = _translateFailedLiveData

    /**
     * 音频翻译的LiveData
     */
    private val _audioTransLiveData = MutableLiveData<AudioTransResponse?>()
    val audioTransLiveData: MutableLiveData<AudioTransResponse?> = _audioTransLiveData

    /**
     * 录音转Base64字符串的LiveData
     */
    private val _audioStrLiveData = MutableLiveData<String?>()
    val audioStrLiveData: MutableLiveData<String?> = _audioStrLiveData

    /**
     * 录音过程异常中断的LiveData
     */
    private val _audioInterruptLiveData = MutableLiveData<Boolean>()
    val audioInterruptLiveData: LiveData<Boolean> = _audioInterruptLiveData

//    /**
//     * 图片翻译的LiveData
//     */
//    private val _imageTransLiveData = MutableLiveData<PictureTransResponse?>()
//    val imageTransLiveData: MutableLiveData<PictureTransResponse?> = _imageTransLiveData


    /**
     * 全局音频播放器
     */
    private lateinit var mediaPlayer: MediaPlayer


    companion object {
        /**
         * 静态常量： 预加载已完成 通过Handler通知EnglishActivity更新UI
         */
        const val PRELOAD_COMPLETED: String = "0"

        /**
         * 静态常量： 响应单词点击事件，获取单词释义
         */
        const val GET_DEFINITION: Int = 1

    }


    /**
     * 请求文本翻译
     */
    fun requestTextTranslate(
        originText: String, fromLanguage: String,
        toLanguage: String
    ) {
        //开启协程
        viewModelScope.launch(Dispatchers.IO) {
            //从Model获取翻译结果的Response
            val translateResponse = TranslateResultRepository.searchTranslatedResult(
                originText,
                "${fromLanguage}2${toLanguage}"
            )
            if (translateResponse != null) {
                _textTransLiveData.postValue(translateResponse.translation)
                val tSpeakUrl = translateResponse.tSpeakUrl
                if (tSpeakUrl != null && tSpeakUrl != "") {
                    //准备音频
                    prepareAudio(tSpeakUrl)
                }
            } else {
                //结果为空获取失败
                _translateFailedLiveData.postValue(true)
            }
        }
    }

//    /**
//     * 请求语音翻译
//     */
//    fun requestAudioTranslate(
//        audioStr: String,
//        fromLanguage: String,
//        toLanguage: String
//    ) {
//        Log.d("RecordingDebug", "ViewModel层调用网络请求$audioStr")
//
//        //开启协程
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.d("RecordingDebug", "ViewModel开始协程 $fromLanguage, $toLanguage")
//
//
//            val audioTransResult = TranslateNetwork.audioTranslateRequest(
//                audioStr,
//                fromLanguage,
//                toLanguage
//            )
//            if (audioTransResult != null) {
//                _audioTransLiveData.postValue(audioTransResult)
//            } else {
//                //结果为空，回调失败结果
//                _translateFailedLiveData.postValue(true)
//            }
//        }
//    }

//    /**
//     * 开始录音
//     */
//    fun startRecording() {
//        //调用Model层
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                AudioRecorder.startRecording()
//            } catch (e: Exception) {
//                _audioInterruptLiveData.postValue(true)
//            }
//        }
//    }
//
//    /**
//     * 停止录音，返回转化的Base64字符串
//     */
//    fun stopRecording() {
//        //开启协程
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val audioStr = AudioRecorder.stopRecording()
//                Log.d("RecordingDebug", "停止stopRecording: $audioStr")
//                if (audioStr != null) {
//                    _audioStrLiveData.postValue(audioStr)
//                } else {
//                    _audioInterruptLiveData.postValue(true)
//                }
//            } catch (e: Exception) {
////                Log.d("RecordingDebug", "startRecording:ViewModel录音失败 $e")
//                _audioInterruptLiveData.postValue(true)
//            }
//        }
//    }

//    /**
//     * 调用图片翻译
//     */
//    fun requestImageTranslate(path: String?) {
//        //开启协程
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.d("pictureDebug", "requestImageTranslate: ViewModel")
//            if (path != null) {
//                val file = File(path)
//                val response = TranslateNetwork.pictureTranslateRequest(file, "zh", "en")
//                if (response != null) {
//                    _imageTransLiveData.postValue(response)
//                }
//            } else {
//                Log.d("pictureDebug", "requestImageTranslate: path == null")
//            }
//        }
//    }

    /**
     * 预加载英语格言和壁纸
     */
    fun preloadEnglishMottoAndWallpaper() {
        //开启协程
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var i = 0
                //获取壁纸
                val wallPaperList = EnglishNetwork.getWallPaper()
                repeat(7) {
                    //获取英语格言
//                    val anEnglishMotto = EnglishNetwork.getAnEnglishMotto()
                    val anEnglishMotto = EnglishMottoResponse(
                        200,
                        "success",
                        EnglishMottoResponse.Result(
                            "Better be blamed by our kith and kin,than be kissed by the enemy.",
                            "宁可让亲人责备，切勿让敌人亲吻。"
                        )
                    )
                    if (anEnglishMotto?.code == 200 && anEnglishMotto.msg == "success" &&
                        wallPaperList?.isNotEmpty() == true
                    ) {
                        englishMottoList.add(
                            EnglishMottoAndWallPaper(
                                mottoEn = anEnglishMotto.result.en,
                                mottoZh = anEnglishMotto.result.zh,
                                wallPaper = wallPaperList[i++].urls.regular,
                                spannableString = initClickableWordsString(anEnglishMotto.result.en)
                            )
                        )
                    }
                }
                //通知预加载已完成
//                handler.sendEmptyMessage(PRELOAD_COMPLETED)
                EventBus.getDefault().postSticky(PRELOAD_COMPLETED)
            } catch (e: Exception) {
                Log.e("preloadEnglish", "preloadEnglishMottoAndWallpaper: 预加载失败")
            }
        }


    }

    /**
     * 生成可点击单词字符串
     */
    private suspend fun initClickableWordsString(text: String): SpannableString =
        withContext(Dispatchers.IO) {
            //创建可点击的字符串
            val spannableString = SpannableString(text)
            //将文本分割成单词，并对每个单词应用可点击样式
            var startIndex = 0
            var endIndex = 0
            val pattern = Regex("[a-zA-Z-]+")
            val list = pattern.findAll(text).map { it.value }.toList()
            Log.d("preload", "preloadEnglishMottoAndWallpaper: $list")
            for (s in list) {
                endIndex = startIndex + s.length
                spannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        //点击单词触发
                        Log.d("wordClickEvent", "onClick: 触发了监听")
                        val message = Message()
                        message.what = GET_DEFINITION
                        message.obj = s
//                        handler.sendMessage(message)
                        EventBus.getDefault().post(message)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color = Color.WHITE
                    }
                }, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                startIndex = endIndex + 1
            }
            spannableString
        }

    /**
     * 准备音频，只能在MainViewModel中调用，每次获取到翻译结果时将资源准备好
     */
    private fun prepareAudio(url: String) {
        //每次准备的时候初始化实例
        mediaPlayer = MediaPlayer()
        //设置资源路径
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    fun cancelAudioPrepared() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }


    @Throws(Exception::class)
    fun startPlayAudio() {
        mediaPlayer.start()
    }

    override fun onCleared() {
        super.onCleared()
        //在ViewModel销毁时取消所有未完成的协程
        viewModelScope.cancel()
        //注意MediaPlayer的内存释放，避免内存泄漏
        cancelAudioPrepared()
//        stopRecording()
    }

}
package com.example.translate.viewModel

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translate.model.network.EnglishNetwork
import com.example.translate.model.retrofit.english_motto.EnglishMottoResponse
import com.example.translate.model.retrofit.english_motto.EnglishWordResponse
import com.example.translate.model.retrofit.wallpaper.WPResponse
import com.example.translate.model.retrofit.wallpaper.WallPaperResponse
import com.example.translate.view.EnglishMottoAndWallPaper
import com.example.translate.view.dictionaryLruCache
import com.example.translate.view.englishMottoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *   文件名: com.example.translate.viewModel.EnglishViewModel
 *   @author: shallew
 *   @data: 2024/7/30 10:20
 *   @about: TODO
 */
class EnglishViewModel : ViewModel() {
    /**
     * 获取英语格言的LiveData
     */
    private val _englishMottoLiveData = MutableLiveData<EnglishMottoResponse?>()
    val englishMottoLiveData: LiveData<EnglishMottoResponse?> = _englishMottoLiveData

    /**
     * 获取失败的LiveData
     */
    private val _errorLiveData = MutableLiveData<Boolean>()
    val errorLiveData: LiveData<Boolean> = _errorLiveData

    /**
     * 获取随机壁纸的LiveData
     */
    private val _randomWallPaperLiveData = MutableLiveData<WPResponse?>()
    val randomWallPaperLiveData: LiveData<WPResponse?> = _randomWallPaperLiveData

    private val _spannableStringLiveData = MutableLiveData<SpannableString>()
    val spannableStringLiveData: LiveData<SpannableString> = _spannableStringLiveData

    /**
     * 获取单词释义的LiveData
     */
    private val _wordDefinitionLiveData = MutableLiveData<EnglishWordResponse?>()
    val wordDefinitionLiveData: MutableLiveData<EnglishWordResponse?> = _wordDefinitionLiveData

    private val _refreshResultLiveData = MutableLiveData<Boolean>()
    val refreshResultLiveData: LiveData<Boolean> = _refreshResultLiveData

    fun refreshEnglishMottoList() {
        //开启协程
        viewModelScope.launch(Dispatchers.IO) {
            val temList = mutableListOf<EnglishMottoAndWallPaper>()
            try {
                var i = 0
                //获取壁纸
                val wallPaperList = EnglishNetwork.getWallPaper()
                repeat(7) {
                    //获取英语格言
                    val anEnglishMotto = EnglishNetwork.getAnEnglishMotto()
                    if (anEnglishMotto?.code == 200 && anEnglishMotto.msg == "success" &&
                        wallPaperList?.isNotEmpty() == true
                    ) {
                        temList.add(
                            EnglishMottoAndWallPaper(
                                mottoEn = anEnglishMotto.result.en,
                                mottoZh = anEnglishMotto.result.zh,
                                wallPaper = wallPaperList[i++].urls.regular,
                                spannableString = initClickableWordsString(anEnglishMotto.result.en)
                            )
                        )
                    }
                }
                if (temList.isNotEmpty()) {
                    englishMottoList.clear()
                    englishMottoList.addAll(temList)
                    //刷新成功，通知UI见面使用englishMottoList更新
                    _refreshResultLiveData.postValue(true)
                } else {
                    //刷新失败
                    _refreshResultLiveData.postValue(false)
                }
            } catch (e: Exception) {
                //刷新失败
                _refreshResultLiveData.postValue(false)
                Log.d("refreshError", "refreshEnglishMottoList: $e")
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
            val pattern = Regex("[a-zA-Z]+")
            val list = pattern.findAll(text).map { it.value }.toList()
            for (s in list) {
                endIndex = startIndex + s.length
                spannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        //点击单词触发
                        getWordDefinition(s)
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

    fun getWordDefinition(word: String) {
        //现在LruCache中查找
        val s = dictionaryLruCache.get(word)
        if (s != null) {
            //如果存在，直接返回
            _wordDefinitionLiveData.postValue(EnglishWordResponse(0, "success", EnglishWordResponse.Result(s, word)))
            return
        }
        //如果不存在，则开启协程进行网络请求
        viewModelScope.launch(Dispatchers.IO) {
            val wordDefinition = EnglishNetwork.getWordDefinition(word)
            if (wordDefinition?.result != null) {
                //请求完毕保存到LruCache中
                dictionaryLruCache.put(word, wordDefinition.result.content)
            }
            _wordDefinitionLiveData.postValue(wordDefinition)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
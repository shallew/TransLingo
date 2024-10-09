package com.example.translate.model.repository

import android.util.Log
import com.example.translate.model.network.TranslateNetwork
import com.example.translate.model.network.getCurrTime
import com.example.translate.model.retrofit.text.TextTransResponse
import com.example.translate.tools.GlobalApplication

/**
 *   文件名: com.example.translate.model.repository.TranslateResultRepository
 *   @author: shallew
 *   @data: 2024/7/25 15:50
 *   @about: TODO 调用本地查询和网络请求的单例类
 */
object TranslateResultRepository {
    /**
     * 查找单个翻译结果
     */
    suspend fun searchTranslatedResult(originalText: String, language: String): TextTransResponse? {
        val dao = TranslateResultDatabase.getDatabase(GlobalApplication.context)
            .translateResultDao()
        val result = dao.getTextTranslateResult(originalText, language)
        val autoFlag = language.startsWith("auto")

        if (result != null) {
            //本地查询到结果 将翻译时间更新
            result.translatedTime = getCurrTime()
            dao.updateTextTranslateResult(result)
            //转换为TextTransResponse再返回
            return TextTransResponse(
                dict = TextTransResponse.Dict(result.dict),
                l = result.l,
                query = result._query,
                tSpeakUrl = result.tSpeakUrl,
                translation = listOf(result.translation),
                webdict = TextTransResponse.Webdict(result.webdict)
            )
        } else {
            //本地没有查询到结果，需要网络请求
            val params = language.split("2")
            val transResponse = TranslateNetwork.textTranslateRequest(
                originalText,
                params[0], params[1]
            )
            if (autoFlag) transResponse?.l = language
            //将请求数据加入到数据库中
            if (transResponse != null && transResponse.errorCode == "0") {
                Log.d("requestTextTranslate", "searchTranslatedResult:iii $transResponse")
                putTranslateResult(transResponse)
            }
            //返回
            return transResponse
        }
    }

    /**
     * 将TextTransResponse转换并插入到数据库中
     */
    private suspend fun putTranslateResult(transResponse: TextTransResponse) {
        val translation = transResponse.translation.toString()
        val result = TranslateResultEntity(
            dict = transResponse.dict?.url ?: null,
            l = transResponse.l,
            _query = transResponse.query,
            tSpeakUrl = transResponse.tSpeakUrl,
            translation = translation.substring(1, translation.length - 1),
            webdict = transResponse.webdict?.url ?: null,
            translatedTime = getCurrTime()
        )
        TranslateResultDatabase.getDatabase(GlobalApplication.context)
            .translateResultDao()
            .insertTextTranslateResult(result)
    }

    /**
     * 返回全部翻译结果
     */
    suspend fun searchAllTranslatedRecords(): List<TranslateResultEntity> {
        //不论有没有记录都返回，没有记录也要在View层显示
        return TranslateResultDatabase.getDatabase(GlobalApplication.context)
            .translateResultDao()
            .getAllTextTranslateResult()
    }

    /**
     * 删除单个翻译结果
     * @return 是否成功删除
     */
    suspend fun deleteTranslatedResult(transResult: TranslateResultEntity): Boolean {
        val i =
            TranslateResultDatabase.getDatabase(GlobalApplication.context)
                .translateResultDao()
                .deleteTextTranslateResult(transResult)
        return i != 0
    }
}
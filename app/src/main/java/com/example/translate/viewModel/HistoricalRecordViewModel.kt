package com.example.translate.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.translate.model.repository.TranslateResultEntity
import com.example.translate.model.repository.TranslateResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 *   文件名: com.example.translate.viewModel.HistoricalRecordViewModel
 *   @author: shallew
 *   @data: 2024/7/25 21:56
 *   @about: TODO
 */
class HistoricalRecordViewModel : ViewModel() {
    /**
     * 获取全部历史记录的LiveData
     */
    private val _historicalRecordLiveData = MutableLiveData<List<TranslateResultEntity>>()
    val historicalRecordLiveData: LiveData<List<TranslateResultEntity>> = _historicalRecordLiveData

    /**
     * 删除历史记录的LiveData
     */
    private val _deleteHistoricalRecordLiveData = MutableLiveData<TranslateResultEntity?>()
    val deleteHistoricalRecordLiveData: LiveData<TranslateResultEntity?> =
        _deleteHistoricalRecordLiveData


    fun getHistoricalRecords() {
        //TODO:从数据库中获取历史记录

        //开启协程
        viewModelScope.launch(Dispatchers.IO) {
            val historicalRecords = TranslateResultRepository.searchAllTranslatedRecords()
            Log.d("requestTextTranslate", "getHistoricalRecords: ${historicalRecords.toString()}")
            _historicalRecordLiveData.postValue(historicalRecords)

        }
    }

    fun deleteSingleRecord(translateResultEntity: TranslateResultEntity) {

        //开启协程
        viewModelScope.launch(Dispatchers.IO) {
            val b =
                TranslateResultRepository.deleteTranslatedResult(translateResultEntity)
            if (b) {
                _deleteHistoricalRecordLiveData.postValue(translateResultEntity)
            } else {
                _deleteHistoricalRecordLiveData.postValue(null)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        //在ViewModel销毁时取消所有未完成的协程
        viewModelScope.cancel()
    }


}
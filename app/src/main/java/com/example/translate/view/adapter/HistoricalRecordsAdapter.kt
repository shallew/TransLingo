package com.example.translate.view.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.translate.R
import com.example.translate.databinding.ItemHistoricalRecordBinding
import com.example.translate.model.repository.TranslateResultEntity
import com.example.translate.tools.DataBindingConverter
import com.example.translate.tools.GlobalApplication

/**
 *   文件名: com.example.translate.view.adapter.HistoricalRecordsAdapter
 *   @author: shallew
 *   @data: 2024/7/25 20:59
 *   @about: TODO
 */
class HistoricalRecordsAdapter(
    private val list: List<TranslateResultEntity>,
    private val onItemClickListener: (TranslateResultEntity) -> Unit,
    private val onItemDeleteClick: (TranslateResultEntity) -> Unit
) :
    RecyclerView.Adapter<HistoricalRecordsAdapter.ViewHolder>() {


    class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemHistoricalRecordBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_historical_record, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val translatedResult = list[position]
        if (holder.binding is ItemHistoricalRecordBinding) {
            holder.binding.translatedResult = translatedResult
            Log.d("requestTextTranslate", "onBindViewHolder: ${translatedResult.l}")
            holder.binding.apply {
                historicalDeleteIv.setOnClickListener {
                    //从数据库中删除并更新列表
                    //试试看能不能在回调到HistoricalRecordActivity中处理
                    // ，否则不好获得ViewModel的实例
                    onItemDeleteClick(translatedResult)
                }
                root.setOnClickListener {
                    onItemClickListener(translatedResult)
                }
                historicalLanguageTv.text = translateLanguageConverter(translatedResult.l)
            }
        }
    }

    private fun translateLanguageConverter(l: String?): String {
        val p = l?.split("2")
        if (p?.size == 2) {
            val sb = StringBuilder().apply {
                append("[")
                append(GlobalApplication.languages.find {
                    it.second == p[0]
                }?.first)
                append("-")
                append(GlobalApplication.languages.find {
                    it.second == p[1]
                }?.first)
                append("]")
            }
            return sb.toString()
        } else {
            return "Error!"
        }
    }

}
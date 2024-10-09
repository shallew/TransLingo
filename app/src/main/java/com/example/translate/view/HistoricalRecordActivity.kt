package com.example.translate.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.BindingConversion
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.translate.R
import com.example.translate.databinding.ActivityHistoricalRecordBinding
import com.example.translate.model.repository.TranslateResultEntity
import com.example.translate.view.adapter.HistoricalRecordsAdapter
import com.example.translate.viewModel.HistoricalRecordViewModel
import com.example.translate.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date

class HistoricalRecordActivity : AppCompatActivity() {
    /**
     * DataBinding
     */
    private lateinit var activityBinding: ActivityHistoricalRecordBinding

    /**
     * ViewModel
     */
    private val historicalViewModel: HistoricalRecordViewModel by lazy {
        ViewModelProvider(this)[HistoricalRecordViewModel::class.java]
    }

    private lateinit var mContext: Context

    private var historicalRecordsList: MutableList<TranslateResultEntity> = mutableListOf()

    private lateinit var historicalAdapter: HistoricalRecordsAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_historical_record)
        if (!::activityBinding.isInitialized) {
            activityBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_historical_record)
        }
        //获取Context
        if (!::mContext.isInitialized) {
            mContext = this
        }

        //设置点击返回键监听
        activityBinding.historicalBackIv.setOnClickListener {
            finish()
        }

        //获取list 主动调用ViewModel中的方法
        historicalViewModel.getHistoricalRecords()
        //使用LiveData接收
        historicalViewModel.historicalRecordLiveData.observe(this) {
            historicalRecordsList = it.toMutableList()
            if (historicalRecordsList.isNotEmpty()) {
                historicalRecordsList.sortWith(Comparator { o1, o2 ->
                    (o2.translatedTime - o1.translatedTime).toInt()
                })
                //获取Adapter
                historicalAdapter = HistoricalRecordsAdapter(historicalRecordsList,
                    onItemClickListener = { clickedItem ->
                        //点击了子项
                        val ls = clickedItem.l.split("2")
                        val intent = intent
                        intent.putExtra("originalText", clickedItem._query)
                        intent.putExtra("fromLanguage", ls[0])
                        intent.putExtra("toLanguage", ls[1])
                        setResult(RESULT_OK, intent)
                        finish()
                    },
                    onItemDeleteClick = { toDeleteItem ->
                        //在这里调用ViewModel的删除方法
                        historicalViewModel.deleteSingleRecord(toDeleteItem)
                    })
                //设置Adapter
                activityBinding.historicalRecordsRecycler.adapter = historicalAdapter
                activityBinding.historicalRecordsRecycler.layoutManager =
                    LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            } else {
                Toast.makeText(mContext, "历史记录为空！", Toast.LENGTH_SHORT).show()
            }
        }
        //LiveData接收删除单条历史记录的结果
        historicalViewModel.deleteHistoricalRecordLiveData.observe(this) {
            if (it == null) {
                Toast.makeText(mContext, "删除失败！", Toast.LENGTH_SHORT).show()
            } else {
                //删除成功
                historicalRecordsList.remove(it)
                historicalAdapter.notifyDataSetChanged()
            }
        }
    }


}
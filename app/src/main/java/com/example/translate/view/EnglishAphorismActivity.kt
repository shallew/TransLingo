package com.example.translate.view

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.translate.R
import com.example.translate.databinding.ActivityEnglishAphorismBinding
import com.example.translate.databinding.PopupWordExplanationBinding
import com.example.translate.view.adapter.BannerAdapter
import com.example.translate.viewModel.EnglishViewModel
import com.example.translate.viewModel.MainViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class EnglishAphorismActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityEnglishAphorismBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[EnglishViewModel::class.java]
    }

    private lateinit var bannerAdapter: BannerAdapter

    private val dotList = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //注册EventBus
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_english_aphorism)
        activityBinding = DataBindingUtil.setContentView(this, R.layout.activity_english_aphorism)

        Log.d("eventBus", "onCreate: ${EventBus.getDefault().isRegistered(this)}")
        //显示加载中
        lottieAnimationShow()
        if (englishMottoList.isNotEmpty()) {
            showPreloadDataOnUi(MainViewModel.PRELOAD_COMPLETED)
        }

        //监听刷新点击事件
        activityBinding.refreshIv.setOnClickListener {
            lottieAnimationShow()
            viewModel.refreshEnglishMottoList()
        }

        //观察刷新结果的LiveData
        viewModel.refreshResultLiveData.observe(this) {
            if (it) {
                //刷新成功
                showPreloadDataOnUi(MainViewModel.PRELOAD_COMPLETED)
            } else {
                Toast.makeText(this, "刷新失败，请稍后重试", Toast.LENGTH_SHORT).show()
            }
        }

        //观察获取单词释义成功的LiveData
        viewModel.wordDefinitionLiveData.observe(this) {
            if (it?.result != null) {
                val currentItem = activityBinding.bannerViewpager2.currentItem
                val itemBannerBinding = bannerAdapter.getBindingList()[currentItem]
                showPopupWindow(itemBannerBinding.englishText, it.result.word, it.result.content)
            } else {
                Toast.makeText(this, "暂无此单词释义", Toast.LENGTH_SHORT).show()
            }
        }

    }

    /**
     * 将预加载的数据显示在Ui上
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun showPreloadDataOnUi(flag: String) {
        Log.d("eventBus", "showPreloadDataOnUi: ")
        lottieAnimationHide()
        Log.d("eventBus", "showPreloadDataOnUi: OK")

        //初始化ViewPager
        bannerAdapter = BannerAdapter(englishMottoList, this)
        activityBinding.bannerViewpager2.adapter = bannerAdapter
        activityBinding.bannerViewpager2.offscreenPageLimit = 7

        for (i in 0 until englishMottoList.size) {
            val imageView = ImageView(this)
            if (i == 0) {
                imageView.setImageResource(R.drawable.icon_dot_selected)
            } else {
                imageView.setImageResource(R.drawable.icon_dot_not_selected)
            }
            dotList.add(imageView)
            activityBinding.indexDot.addView(imageView)
        }

        //注册轮播图的滚动事件监听器
        activityBinding.bannerViewpager2.registerOnPageChangeCallback(object :
            OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                //当滚动到该页面
                //遍历点集合，将除了该页面以外的点设置为未选中，当前页设置为选中的点
                for (i in 0 until englishMottoList.size) {
                    if (i == position % englishMottoList.size) {
                        dotList[i].setImageResource(R.drawable.icon_dot_selected)
                    } else {
                        dotList[i].setImageResource(R.drawable.icon_dot_not_selected)
                    }
                }
            }
        })
    }

    @Subscribe
    fun getWordDefinition(message: Message) {
        Log.d("eventBus", "getWordDefinition: ")
        if (message.what == MainViewModel.GET_DEFINITION) {
            Log.d("eventBus", "getWordDefinition: OK")
            viewModel.getWordDefinition(message.obj as String)
        }
    }


    private fun showPopupWindow(anchorView: View, word: String, meaning: String) {
        // 创建布局
        val inflater = LayoutInflater.from(this)
        val binding: PopupWordExplanationBinding = DataBindingUtil.inflate(
            inflater, R.layout.popup_word_explanation, null, false
        )
        binding.word = word
        binding.definition = meaning.replace("|", "\n")

        //创建PopupWindow
        val popupWindow = PopupWindow(
            binding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // 显示 PopupWindow
        popupWindow.showAsDropDown(anchorView, 0, -400)
    }

    private fun lottieAnimationShow() {
        activityBinding.overlayView.visibility = View.VISIBLE
        activityBinding.lottieLoadingAnimation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    private fun lottieAnimationHide() {
        activityBinding.overlayView.visibility = View.GONE
        activityBinding.lottieLoadingAnimation.apply {
            visibility = View.GONE
            cancelAnimation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().removeAllStickyEvents()
            EventBus.getDefault().unregister(this)
        }
    }
}
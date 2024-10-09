package com.example.translate.view.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.translate.R
import com.example.translate.databinding.ItemBannerBinding
import com.example.translate.view.EnglishMottoAndWallPaper

/**
 *   文件名: com.example.translate.view.adapter.BannerAdapter
 *   @author: shallew
 *   @data: 2024/7/31 21:48
 *   @about: TODO
 */
class BannerAdapter(
    val list: List<EnglishMottoAndWallPaper>,
    private val mContext: Context
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {
    private val bindingList = mutableListOf<ItemBannerBinding>()

    inner class BannerViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            bindingList.add(binding as ItemBannerBinding)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = DataBindingUtil.inflate<ItemBannerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_banner,
            parent,
            false
        )
        return BannerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val data = list[position]
        if (holder.binding is ItemBannerBinding) {
            holder.binding.apply {
                Log.d("bannerAdapter", "onBindViewHolder: $data")
                var retryTimes = 0
                Glide.with(mContext).load(data.wallPaper).into(englishBackground)
                english = data.spannableString
                englishText.movementMethod = LinkMovementMethod.getInstance()
                chinese = data.mottoZh
            }
        }
    }

    fun getBindingList(): List<ItemBannerBinding> = bindingList
}
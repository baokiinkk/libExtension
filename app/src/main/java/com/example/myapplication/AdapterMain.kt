package com.example.myapplication

import android.widget.ImageView
import android.widget.VideoView
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.ViewDataBinding
import com.example.myapplication.databinding.ItemMainBinding
import com.vnpay.bases.adapter.BaseRclvAdapter
import com.vnpay.bases.adapter.BaseRclvHolder
import java.util.*

data class ModelImage(@DrawableRes val image: Int, val transaction: String,val url:String,val id:String)
class AdapterMain : BaseRclvAdapter<ModelImage>() {
    private var listener: ((ImageView,ModelImage) -> Unit)? = null
    private val cacheData:Stack<MutableList<ModelImage>> = Stack()
    private val cacheDataLast:Stack<MutableList<ModelImage>> = Stack()
    override fun getLayoutResource(viewType: Int): Int {
        return R.layout.item_main
    }

    override fun onCreateVH(
        itemView: ViewDataBinding,
        viewType: Int
    ): BaseRclvHolder<*, *> {
        return CardViewHolder(itemView as ItemMainBinding)
    }

    fun updateList(list: MutableList<ModelImage>?) {
        list?.let {
            val tmp = it
            reset(tmp)
        }
    }
    fun insertList(list: MutableList<ModelImage>?){
        list?.let {
            val mutableListCache = mutableListOf<ModelImage>()
            for (i in 0..7) {
                mutableListCache.add(dataSet.first())
                dataSet.removeFirst()
            }
            cacheData.push(mutableListCache)
            notifyItemRangeRemoved(0,8)
            addAllItems(it)
        }
    }
    fun insertLastCache(){
        cacheDataLast.pop()?.let {
            addAllItems(it)
        }
    }
    fun isEmptyCacheLast() = cacheDataLast.isEmpty()

    fun insertFirst(){
        val mutableListCache = mutableListOf<ModelImage>()
        val indexRemove = dataSet.size
        for (i in 0..7) {
            mutableListCache.add(dataSet.last())
            dataSet.removeLast()
        }
        notifyItemRangeRemoved(indexRemove-7,indexRemove)
        cacheDataLast.push(mutableListCache.reversed().toMutableList())

        if(cacheData.isNotEmpty())
            addItemsAtIndexAndNotify(0,cacheData.pop())

    }

    fun getItemPosition(pos: Int): ModelImage {
        return getItemDataAtPosition(pos)
    }

    fun setOnListener(action: (ImageView,ModelImage) -> Unit) {
        this.listener = action
    }

    inner class CardViewHolder(
        val binding: ItemMainBinding
    ) : BaseRclvHolder<ItemMainBinding, ModelImage>(binding) {

        init {
            binding.apply {
                itemView.setOnClickListener {
                    listener?.invoke(image,getItemPosition(adapterPosition))
                }
            }
        }

        override fun onBind(
            vhData: ModelImage
        ) {
            binding.apply {
                image.setImageResource(vhData.image)
                image.transitionName = vhData.transaction
                text.text = vhData.id
            }
        }
    }
}
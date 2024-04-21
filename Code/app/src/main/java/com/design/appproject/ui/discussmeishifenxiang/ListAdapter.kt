package com.design.appproject.ui.discussmeishifenxiang
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.DiscussmeishifenxiangItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 美食分享评论表适配器列表
 */
class ListAdapter : LoadMoreAdapter<DiscussmeishifenxiangItemBean>(R.layout.discussmeishifenxiang_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: DiscussmeishifenxiangItemBean) {
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("discussmeishifenxiang","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("discussmeishifenxiang","Delete"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("discussmeishifenxiang","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("discussmeishifenxiang","Delete"))
        }
    }
}
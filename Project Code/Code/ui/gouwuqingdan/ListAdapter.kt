package com.design.appproject.ui.gouwuqingdan
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.GouwuqingdanItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 购物清单适配器列表
 */
class ListAdapter : LoadMoreAdapter<GouwuqingdanItemBean>(R.layout.gouwuqingdan_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: GouwuqingdanItemBean) {
        holder.setText(R.id.jihuamingcheng_tv, item.jihuamingcheng.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("gouwuqingdan","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("gouwuqingdan","Delete"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("gouwuqingdan","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("gouwuqingdan","Delete"))
        }
    }
}
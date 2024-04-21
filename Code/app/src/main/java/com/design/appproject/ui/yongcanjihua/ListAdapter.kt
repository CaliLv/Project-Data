package com.design.appproject.ui.yongcanjihua
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.YongcanjihuaItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 用餐计划适配器列表
 */
class ListAdapter : LoadMoreAdapter<YongcanjihuaItemBean>(R.layout.yongcanjihua_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: YongcanjihuaItemBean) {
        holder.setText(R.id.jihuamingcheng_tv, item.jihuamingcheng.toString())
        holder.setText(R.id.jihualeixing_tv, item.jihualeixing.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("yongcanjihua","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("yongcanjihua","Delete"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("yongcanjihua","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("yongcanjihua","Delete"))
        }
    }
}
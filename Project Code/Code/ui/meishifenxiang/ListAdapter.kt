package com.design.appproject.ui.meishifenxiang
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.MeishifenxiangItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 美食分享适配器列表
 */
class ListAdapter : LoadMoreAdapter<MeishifenxiangItemBean>(R.layout.meishifenxiang_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: MeishifenxiangItemBean) {
        holder.setText(R.id.meishimingcheng_tv,"Food Name:"+ item.meishimingcheng.toString())
        holder.setText(R.id.meishileixing_tv, item.meishileixing.toString())
        val img = item.meishitupian.split(",")[0]
        holder.getView<ImageView>(R.id.picture_iv).load(context,img, needPrefix = !img.startsWith("http"))
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("meishifenxiang","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("meishifenxiang","Delete"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("meishifenxiang","Modify"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("meishifenxiang","Delete"))
        }
    }
}
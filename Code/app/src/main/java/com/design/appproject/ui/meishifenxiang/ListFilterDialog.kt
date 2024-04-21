package com.design.appproject.ui.meishifenxiang

import com.design.appproject.databinding.MeishifenxiangFilterDialogLayoutBinding
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.dylanc.viewbinding.inflate
import com.design.appproject.R
import com.design.appproject.logic.repository.UserRepository
import com.lxj.xpopup.core.DrawerPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.union.union_basic.ext.toConversion
import android.app.Activity
import com.blankj.utilcode.util.ScreenUtils
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.yes

class ListFilterDialog(context: Context) : DrawerPopupView(context) {

    lateinit var binding: MeishifenxiangFilterDialogLayoutBinding

    override fun addInnerContent() {
        binding = drawerContentContainer.inflate()
    }

    private val params by lazy { /*请求参数*/
        mutableMapOf<String,String>()
    }
    var callBackListener: ((isEnsure:Boolean,params:MutableMap<String,String>?) -> Unit)? =null

    override fun onCreate() {
        super.onCreate()
        binding.apply {
            dialogNsv.layoutParams = dialogNsv.layoutParams.apply {
                width = ScreenUtils.getScreenWidth()
                height = ScreenUtils.getScreenHeight()
            }
            dialogFl.setOnClickListener { dismiss() }
            boxMll.setOnClickListener {  }
            "Stir fry,Steam,Stew,Grill,Fry,Boil".split(",").forEachIndexed {index, s ->
                pengrenfangshiFbl.addView(createCustomItemView(index,s,pengrenfangshiFbl))
            }
            "Acid,Sweet,Bitter,Hot,Salty".split(",").forEachIndexed {index, s ->
                fengweiFbl.addView(createCustomItemView(index,s,fengweiFbl))
            }
            "Lu,Chuan,Yue,Su,Min,Zhe,Xiang,Hui".split(",").forEachIndexed {index, s ->
                caixiFbl.addView(createCustomItemView(index,s,caixiFbl))
            }
            resetBtn.setOnClickListener { //重置
                pengrenfangshiFbl.children.forEach { it.isSelected=false }
                fengweiFbl.children.forEach { it.isSelected=false }
                caixiFbl.children.forEach { it.isSelected=false }
                callBackListener?.invoke(false,null)
            }
            ensureBtn.setOnClickListener { //确定
                params.clear()
                val pengrenfangshiString = pengrenfangshiFbl.children.filter { it.isSelected }.map { it.toConversion<TextView>()?.text.toString() }.joinToString()
                pengrenfangshiString.isNotNullOrEmpty().yes {
                    params.put("pengrenfangshi",pengrenfangshiString)
                }
                val fengweiString = fengweiFbl.children.filter { it.isSelected }.map { it.toConversion<TextView>()?.text.toString() }.joinToString()
                fengweiString.isNotNullOrEmpty().yes {
                    params.put("fengwei",fengweiString)
                }
                val caixiString = caixiFbl.children.filter { it.isSelected }.map { it.toConversion<TextView>()?.text.toString() }.joinToString()
                caixiString.isNotNullOrEmpty().yes {
                    params.put("caixi",caixiString)
                }
                callBackListener?.invoke(true,params)
                dismiss()
            }
        }
    }

    private fun createCustomItemView(index:Int,title:String,viewGroup:ViewGroup,isSelect:Boolean=false):View= LayoutInflater.from(context).inflate(if (isSelect) R.layout.item_custom_active_layout else R.layout.item_custom_layout, viewGroup, false).apply{
        toConversion<TextView>()?.text = title
        isSelected=isSelect
        setOnClickListener {
            if (isSelected){
                return@setOnClickListener
            }else{
                val oldIndex= viewGroup.children.indexOfFirst { it.isSelected }
                if (oldIndex>=0){
                    val itemView = viewGroup.children.find { it.isSelected }
                    val oldTitle = itemView?.toConversion<TextView>()?.text.toString()?:""
                    viewGroup.removeViewAt(oldIndex)
                    val view = createCustomItemView(oldIndex,oldTitle,viewGroup)
                    viewGroup.addView(view,oldIndex)
                }
                viewGroup.removeViewAt(index)
                viewGroup.addView(createCustomItemView(index,title,viewGroup,true),index)
            }
        }
    }
}
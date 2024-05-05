package com.design.appproject.ui.yongcanjihua

import android.Manifest
import com.union.union_basic.permission.PermissionUtil
import com.design.appproject.ext.UrlPrefix
import androidx.core.widget.addTextChangedListener
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.view.children
import com.design.appproject.utils.Utils
import com.design.appproject.bean.BaiKeBean
import androidx.core.app.ActivityCompat.startActivityForResult
import com.blankj.utilcode.util.UriUtils
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.internal.LinkedTreeMap
import com.union.union_basic.ext.*
import com.blankj.utilcode.util.RegexUtils
import com.union.union_basic.utils.StorageUtil
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.design.appproject.widget.BottomSpinner
import com.design.appproject.base.CommonBean
import com.blankj.utilcode.util.TimeUtils
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter
import java.text.SimpleDateFormat
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.repository.UserRepository
import com.union.union_basic.image.selector.SmartPictureSelector
import java.io.File
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.bean.YongcanjihuaItemBean
import com.blankj.utilcode.constant.TimeConstants
import com.design.appproject.databinding.YongcanjihuaaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 用餐计划新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_YONGCANJIHUA)
class AddOrUpdateActivity:BaseBindingActivity<YongcanjihuaaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mYongcanjihuaItemBean = YongcanjihuaItemBean()

    override fun initEvent() {
        setBarTitle("Meal Plan")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mYongcanjihuaItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mYongcanjihuaItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mYongcanjihuaItemBean,mRefid)
                }
            }
            if (mYongcanjihuaItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mYongcanjihuaItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mYongcanjihuaItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mYongcanjihuaItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mYongcanjihuaItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mYongcanjihuaItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun YongcanjihuaaddorupdateLayoutBinding.initView(){
            jihualeixingBs.setOptions("Week,Month".split(","),"Please select a plan type",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    jihualeixingBs.text = content
                    mYongcanjihuaItemBean.jihualeixing =content
                }
            })
            yongcanshijianBs.setOptions("Breakfast, Lunch, Dinner".split(","),"Please choose a dining time",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    yongcanshijianBs.text = content
                    mYongcanjihuaItemBean.yongcanshijian =content
                }
            })
            mYongcanjihuaItemBean.jihuashijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            jihuashijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            val mjihuashijianPicker = DatimePicker(this@AddOrUpdateActivity).apply {
            wheelLayout.setDateFormatter(BirthdayFormatter())
            wheelLayout.setTimeFormatter(UnitTimeFormatter())
            wheelLayout.setRange(DatimeEntity.yearOnFuture(-100), DatimeEntity.yearOnFuture(50), DatimeEntity.now())
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                jihuashijianTv.text = "$year-$month-$day $hour:$minute:$second"
                mYongcanjihuaItemBean.jihuashijian="$year-$month-$day $hour:$minute:$second"
            }
        }
            jihuashijianTv.setOnClickListener {
            mjihuashijianPicker.show()
        }

            submitBtn.setOnClickListener{/*提交*/
                submit()
            }
            setData()
    }

    lateinit var mUserBean:LinkedTreeMap<String, Any>/*当前用户数据*/

    override fun initData() {
        super.initData()
        UserRepository.session<Any>().observeKt {
            it.getOrNull()?.let {
                it.data.toConversion<LinkedTreeMap<String, Any>>()?.let {
                    mUserBean = it
                    it["touxiang"]?.let { it1 -> StorageUtil.encode(CommonBean.HEAD_URL_KEY, it1) }
                    /**ss读取*/
                    if (mYongcanjihuaItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mYongcanjihuaItemBean.yonghuzhanghao = it["yonghuzhanghao"]?.toString()?:""
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mYongcanjihuaItemBean.yonghuxingming.isNullOrEmpty()){
                        mYongcanjihuaItemBean.yonghuxingming = it["yonghuxingming"]?.toString()?:""
                    }
                    binding.yonghuxingmingEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<YongcanjihuaItemBean>("yongcanjihua",mId).observeKt {
                it.getOrNull()?.let {
                    mYongcanjihuaItemBean = it.data
                    mYongcanjihuaItemBean.id = mId
                    binding.setData()
                }
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun YongcanjihuaaddorupdateLayoutBinding.submit() {
        mYongcanjihuaItemBean.jihuamingcheng = jihuamingchengEt.text.toString()
        mYongcanjihuaItemBean.jihuajianjie = jihuajianjieEt.text.toString()
        mYongcanjihuaItemBean.shiwudapei = shiwudapeiEt.text.toString()
        mYongcanjihuaItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mYongcanjihuaItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mYongcanjihuaItemBean.id>0){
            UserRepository.update("yongcanjihua",mYongcanjihuaItemBean).observeKt{
            it.getOrNull()?.let {
                "Submitted successfully".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<YongcanjihuaItemBean>("yongcanjihua",mYongcanjihuaItemBean).observeKt{
            it.getOrNull()?.let {
                "Submitted successfully".showToast()
                finish()
            }
        }
        }
    }


    private fun YongcanjihuaaddorupdateLayoutBinding.setData(){
        if (mYongcanjihuaItemBean.jihuamingcheng.isNotNullOrEmpty()){
            jihuamingchengEt.setText(mYongcanjihuaItemBean.jihuamingcheng.toString())
        }
        if (mYongcanjihuaItemBean.jihualeixing.isNotNullOrEmpty()){
            jihualeixingBs.text =mYongcanjihuaItemBean.jihualeixing
        }
        if (mYongcanjihuaItemBean.jihuajianjie.isNotNullOrEmpty()){
            jihuajianjieEt.setText(mYongcanjihuaItemBean.jihuajianjie.toString())
        }
        if (mYongcanjihuaItemBean.yongcanshijian.isNotNullOrEmpty()){
            yongcanshijianBs.text =mYongcanjihuaItemBean.yongcanshijian
        }
        if (mYongcanjihuaItemBean.shiwudapei.isNotNullOrEmpty()){
            shiwudapeiEt.setText(mYongcanjihuaItemBean.shiwudapei.toString())
        }
        if (mYongcanjihuaItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mYongcanjihuaItemBean.yonghuzhanghao.toString())
        }
        if (mYongcanjihuaItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mYongcanjihuaItemBean.yonghuxingming.toString())
        }
    }
}
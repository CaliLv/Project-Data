package com.design.appproject.ui.meishifenxiang

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
import com.design.appproject.bean.MeishifenxiangItemBean
import com.blankj.utilcode.constant.TimeConstants
import com.design.appproject.databinding.MeishifenxiangaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 美食分享新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_MEISHIFENXIANG)
class AddOrUpdateActivity:BaseBindingActivity<MeishifenxiangaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mMeishifenxiangItemBean = MeishifenxiangItemBean()

    override fun initEvent() {
        setBarTitle("Food sharing")
        setBarColor("#FFFFFF","black")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mMeishifenxiangItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mMeishifenxiangItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mMeishifenxiangItemBean,mRefid)
                }
            }
            if (mMeishifenxiangItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mMeishifenxiangItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mMeishifenxiangItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mMeishifenxiangItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mMeishifenxiangItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mMeishifenxiangItemBean,Utils.getUserId())
            }
        }
        binding.initView()

        binding.zhizuoguochengRichLayout.apply{
            actionBold.setOnClickListener {
                richEt.setBold()
            }
            actionItalic.setOnClickListener {
                richEt.setItalic()
            }
            actionStrikethrough.setOnClickListener {
                richEt.setStrikeThrough()
            }
            actionUnderline.setOnClickListener {
                richEt.setUnderline()
            }
            actionHeading1.setOnClickListener {
                richEt.setHeading(1)
            }
            actionHeading2.setOnClickListener {
                richEt.setHeading(2)
            }
            actionHeading3.setOnClickListener {
                richEt.setHeading(3)
            }
            actionHeading4.setOnClickListener {
                richEt.setHeading(4)
            }
            actionHeading5.setOnClickListener {
                richEt.setHeading(5)
            }
            actionIndent.setOnClickListener {
                richEt.setIndent()
            }
            actionOutdent.setOnClickListener {
                richEt.setOutdent()
            }
            actionAlignCenter.setOnClickListener {
                richEt.setAlignCenter()
            }
            actionAlignLeft.setOnClickListener {
                richEt.setAlignLeft()
            }
            actionAlignRight.setOnClickListener {
                richEt.setAlignRight()
            }
            actionInsertBullets.setOnClickListener {
                richEt.setBullets()
            }
            actionInsertNumbers.setOnClickListener {
                richEt.setNumbers()
            }
            actionInsertImage.setOnClickListener {
                SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                    val path = it[0]
                    UserRepository.upload(File(path),"").observeKt {
                        it.getOrNull()?.let {
                            richEt.insertImage(UrlPrefix.URL_PREFIX+"file/" + it.file, "dachshund", 320)
                        }
                    }
                }
            }
        }
    }

    fun MeishifenxiangaddorupdateLayoutBinding.initView(){
            meishileixingBs.let { spinner ->
            spinner.setOnItemSelectedListener(object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    spinner.text = content
                    mMeishifenxiangItemBean.meishileixing =content
                }
            })
            spinner.setOnClickListener {
                spinner.options.isNullOrEmpty().yes {
                    UserRepository.option("meishileixing", "meishileixing", "",null,"",false).observeKt{
                        it.getOrNull()?.let {
                            spinner.setOptions(it.data, "Please select a food type", false)
                            spinner.dialogShow()
                        }
                    }
                }.otherwise {
                    spinner.dialogShow()
                }
            }
        }
             meishitupianLl.setOnClickListener {
            SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                val path = it[0]
                showLoading("Uploading...")
                UserRepository.upload(File(path), "meishitupian").observeKt{
                    it.getOrNull()?.let {
                        meishitupianIfv.load(this@AddOrUpdateActivity, "file/"+it.file)
                        mMeishifenxiangItemBean.meishitupian = "file/" + it.file
                    }
                }
            }
        }
        //炒，蒸，炖，烤，炸，煮
            pengrenfangshiBs.setOptions("Stir fry,Steam,Stew,Grill,Fry,Boil".split(","),"Please choose the cooking method",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    pengrenfangshiBs.text = content
                    mMeishifenxiangItemBean.pengrenfangshi =content
                }
            })
        //酸，甜，苦，辣，咸
            fengweiBs.setOptions("Acid,Sweet,Bitter,Hot,Salty".split(","),"Please select the flavor",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    fengweiBs.text = content
                    mMeishifenxiangItemBean.fengwei =content
                }
            })
            caixiBs.setOptions("Lu,Chuan,Yue,Su,Min,Zhe,Xiang,Hui".split(","),"Please select a cuisine",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    caixiBs.text = content
                    mMeishifenxiangItemBean.caixi =content
                }
            })
            mMeishifenxiangItemBean.fenxiangshijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            fenxiangshijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            val mfenxiangshijianPicker = DatimePicker(this@AddOrUpdateActivity).apply {
            wheelLayout.setDateFormatter(BirthdayFormatter())
            wheelLayout.setTimeFormatter(UnitTimeFormatter())
            wheelLayout.setRange(DatimeEntity.yearOnFuture(-100), DatimeEntity.yearOnFuture(50), DatimeEntity.now())
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                fenxiangshijianTv.text = "$year-$month-$day $hour:$minute:$second"
                mMeishifenxiangItemBean.fenxiangshijian="$year-$month-$day $hour:$minute:$second"
            }
        }
            fenxiangshijianTv.setOnClickListener {
            mfenxiangshijianPicker.show()
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
                    if (mMeishifenxiangItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mMeishifenxiangItemBean.yonghuzhanghao = it["yonghuzhanghao"]?.toString()?:""
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mMeishifenxiangItemBean.yonghuxingming.isNullOrEmpty()){
                        mMeishifenxiangItemBean.yonghuxingming = it["yonghuxingming"]?.toString()?:""
                    }
                    binding.yonghuxingmingEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<MeishifenxiangItemBean>("meishifenxiang",mId).observeKt {
                it.getOrNull()?.let {
                    mMeishifenxiangItemBean = it.data
                    mMeishifenxiangItemBean.id = mId
                    binding.setData()
                }
            }
        }
        mMeishifenxiangItemBean.storeupnum = 0
        mMeishifenxiangItemBean.thumbsupnum = 0
        mMeishifenxiangItemBean.crazilynum = 0
        mMeishifenxiangItemBean.clicknum = 0
        mMeishifenxiangItemBean.sfsh = "Yes"
        binding.setData()
    }

    /**验证*/
    private fun MeishifenxiangaddorupdateLayoutBinding.submit() {
        mMeishifenxiangItemBean.meishimingcheng = meishimingchengEt.text.toString()
        mMeishifenxiangItemBean.shicai = shicaiEt.text.toString()
        mMeishifenxiangItemBean.peiliao = peiliaoEt.text.toString()
        mMeishifenxiangItemBean.zhizuoguocheng = zhizuoguochengRichLayout.richEt.html
        storeupnumEt.inputType = InputType.TYPE_CLASS_NUMBER
        mMeishifenxiangItemBean.storeupnum = storeupnumEt.text.toString().toInt()
        mMeishifenxiangItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mMeishifenxiangItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        if(mMeishifenxiangItemBean.meishimingcheng.isNullOrEmpty()){
            "Food name cannot be empty".showToast()
            return
        }
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mMeishifenxiangItemBean.id>0){
            UserRepository.update("meishifenxiang",mMeishifenxiangItemBean).observeKt{
            it.getOrNull()?.let {
                "Submitted successfully".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<MeishifenxiangItemBean>("meishifenxiang",mMeishifenxiangItemBean).observeKt{
            it.getOrNull()?.let {
                "Submitted successfully".showToast()
                finish()
            }
        }
        }
    }


    private fun MeishifenxiangaddorupdateLayoutBinding.setData(){
        if (mMeishifenxiangItemBean.meishimingcheng.isNotNullOrEmpty()){
            meishimingchengEt.setText(mMeishifenxiangItemBean.meishimingcheng.toString())
        }
        if (mMeishifenxiangItemBean.meishileixing.isNotNullOrEmpty()){
            meishileixingBs.text =mMeishifenxiangItemBean.meishileixing
        }
        if (mMeishifenxiangItemBean.meishitupian.isNotNullOrEmpty()){
            meishitupianIfv.load(this@AddOrUpdateActivity, mMeishifenxiangItemBean.meishitupian)
        }
        if (mMeishifenxiangItemBean.shicai.isNotNullOrEmpty()){
            shicaiEt.setText(mMeishifenxiangItemBean.shicai.toString())
        }
        if (mMeishifenxiangItemBean.pengrenfangshi.isNotNullOrEmpty()){
            pengrenfangshiBs.text =mMeishifenxiangItemBean.pengrenfangshi
        }
        if (mMeishifenxiangItemBean.fengwei.isNotNullOrEmpty()){
            fengweiBs.text =mMeishifenxiangItemBean.fengwei
        }
        if (mMeishifenxiangItemBean.caixi.isNotNullOrEmpty()){
            caixiBs.text =mMeishifenxiangItemBean.caixi
        }
        if (mMeishifenxiangItemBean.peiliao.isNotNullOrEmpty()){
            peiliaoEt.setText(mMeishifenxiangItemBean.peiliao.toString())
        }
        if (mMeishifenxiangItemBean.storeupnum>=0){
            storeupnumEt.setText(mMeishifenxiangItemBean.storeupnum.toString())
        }
        if (mMeishifenxiangItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mMeishifenxiangItemBean.yonghuzhanghao.toString())
        }
        if (mMeishifenxiangItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mMeishifenxiangItemBean.yonghuxingming.toString())
        }
        zhizuoguochengRichLayout.richEt.setHtml(mMeishifenxiangItemBean.zhizuoguocheng)
    }
}
package com.design.appproject.ui.meishifenxiang

import com.design.appproject.logic.repository.UserRepository
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import android.annotation.SuppressLint
import com.union.union_basic.utils.StorageUtil
import com.design.appproject.utils.ArouterUtils
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.design.appproject.base.*
import com.design.appproject.ext.postEvent
import android.media.MediaPlayer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import com.google.gson.Gson
import android.view.Gravity
import android.view.ViewGroup
import com.design.appproject.widget.DetailBannerAdapter
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.union.union_basic.ext.*
import androidx.core.view.setMargins
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.*
import com.union.union_basic.network.DownloadListener
import com.union.union_basic.network.DownloadUtil
import java.io.File
import androidx.core.view.setPadding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.utils.Utils
import java.util.*
import kotlin.concurrent.timerTask
import com.design.appproject.ext.load
import com.design.appproject.logic.viewmodel.meishifenxiang.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.MeishifenxiangcommonDetailsLayoutBinding
import com.design.appproject.databinding.MeishifenxiangcommonDetailsHeaderLayoutBinding
import com.dylanc.viewbinding.getBinding
import com.design.appproject.bean.*
import com.design.appproject.ui.CommentsAdatper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import android.text.Html
import com.design.appproject.widget.MyTextView
import com.design.appproject.widget.MyFlexBoxLayout
import com.design.appproject.widget.MyImageView
import android.view.ContextThemeWrapper
import com.google.android.flexbox.FlexWrap
import com.union.union_basic.image.loader.GlideLoader.load
/**
 * 美食分享详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_MEISHIFENXIANG)
class DetailsFragment : BaseBindingFragment<MeishifenxiangcommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mMeishifenxiangItemBean=MeishifenxiangItemBean()/*详情内容*/

    private val mCommentsAdatper by lazy {
        CommentsAdatper().apply {
            pageLoadMoreListener {
                mDetailsViewModel.comments("discussmeishifenxiang", mapOf("page" to it.toString(),"limit" to "10","refid" to mId.toString()))
            }
        }
    }

    private val mHeader by lazy {
        LayoutInflater.from(requireActivity()).inflate(R.layout.meishifenxiangcommon_details_header_layout, null,false)
    }
    private val mHeaderBinding:MeishifenxiangcommonDetailsHeaderLayoutBinding by lazy {
        mHeader.getBinding()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("Food sharing details page")
        setBarColor("#FFFFFF","black")
        binding.apply{
            mCommentsAdatper.addHeaderView(mHeader)
            srv.setAdapter(mCommentsAdatper)
            srv.setOnRefreshListener {
                loadData()
                mDetailsViewModel.comments("discussmeishifenxiang", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
            }
            mHeaderBinding.addCommentBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_DISCUSSMEISHIFENXIANG)
                .withLong("mRefid",mId)
                .navigation(requireActivity(),101)
            }
            mIsBack.yes {
                sfshBtn.isVisible = Utils.isAuthBack("meishifenxiang","Auditing")
            }.otherwise {
                sfshBtn.isVisible = Utils.isAuthFront("meishifenxiang","Auditing")
            }
            sfshBtn.setOnClickListener {
                XPopup.Builder(context).asInputConfirm("",""){
                    if (mMeishifenxiangItemBean.sfsh.isNullOrEmpty()){
                        "Please select the review status".showToast()
                        return@asInputConfirm
                    }
                    when(it){
                        "Pass"-> mMeishifenxiangItemBean.sfsh = "Yes"
                        "Not passed"->mMeishifenxiangItemBean.sfsh = "No"
                        else->mMeishifenxiangItemBean.sfsh = it
                    }
                    if (mMeishifenxiangItemBean.sfsh.isNullOrEmpty()){
                        "Please fill in the review reply".showToast()
                    }else{
                        mDetailsViewModel.update("meishifenxiang",mMeishifenxiangItemBean,"shhf")
                    }
                }.show()
            }
    }
    }

    override fun initData() {
        super.initData()
        showLoading()
        loadData()
        mDetailsViewModel.infoLiveData.observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let { info->
                binding.srv.isRefreshing =false
                mMeishifenxiangItemBean = info.data
                mHeaderBinding.setInfo()
            }
        }
        mDetailsViewModel.comments("discussmeishifenxiang", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
        mDetailsViewModel.commentsLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }
        mDetailsViewModel.updateLiveData.observeKt {
            it.getOrNull()?.let {
                if (it.callBackData=="shhf"){
                    "Review successful".showToast()
                }
            }

        }
    }

    private fun loadData(){
        mDetailsViewModel.info("meishifenxiang",mId.toString())
    }


    private fun MeishifenxiangcommonDetailsHeaderLayoutBinding.setInfo(){
        meishimingchengTv.text = "${mMeishifenxiangItemBean.meishimingcheng}"
        meishileixingTv.text = "${mMeishifenxiangItemBean.meishileixing}"
        banner.setAdapter(DetailBannerAdapter(mMeishifenxiangItemBean.meishitupian.split(","))).setOnBannerListener { data, position ->
            data.toConversion<String>()?.let {
                it.showToast()
            }
        }
        shicaiTv.text = "${mMeishifenxiangItemBean.shicai}"
        pengrenfangshiTv.text = "${mMeishifenxiangItemBean.pengrenfangshi}"
        fengweiTv.text = "${mMeishifenxiangItemBean.fengwei}"
        caixiTv.text = "${mMeishifenxiangItemBean.caixi}"
        peiliaoTv.text = "${mMeishifenxiangItemBean.peiliao}"
        zhizuoguochengCw.setHtml(mMeishifenxiangItemBean.zhizuoguocheng.trim())
        storeupnumTv.text = "${mMeishifenxiangItemBean.storeupnum}"
        clicknumTv.text = "${mMeishifenxiangItemBean.clicknum}"
        yonghuzhanghaoTv.text = "${mMeishifenxiangItemBean.yonghuzhanghao}"
        yonghuxingmingTv.text = "${mMeishifenxiangItemBean.yonghuxingming}"
        fenxiangshijianTv.text = "${mMeishifenxiangItemBean.fenxiangshijian}"
        var sfshStatus = if(mMeishifenxiangItemBean.sfsh =="Yes"){
            "Pass"
        }else if(mMeishifenxiangItemBean.sfsh =="No"){
            "Not pass"
        }else{
            "Pending review"
        }
        sfshTv.isVisible = mIsBack
        sfshFbl.isVisible = mIsBack
        sfshContentTv.isVisible = mIsBack
        sfshContentFbl.isVisible = mIsBack
        sfshTv.text = "${sfshStatus}"
        sfshContentTv.text =mMeishifenxiangItemBean.shhf
        initThumbsUp()
        initCollection()
    }



    private fun MeishifenxiangcommonDetailsHeaderLayoutBinding.initCollection(){/*收藏关注*/
        HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1","refid" to mId.toString(),
            "tablename" to "meishifenxiang","userid" to Utils.getUserId().toString(),"type" to "1")).observeKt {
            it.getOrNull()?.let {
                collectionIbtn.isSelected = it.data.list.isNotEmpty()/*true为已收藏*/
                collectionIbtn.text = Html.fromHtml(if (collectionIbtn.isSelected) "&#xe668;" else "&#xe669;")
            }
        }
        collectionIbtn.setOnClickListener {
            XPopup.Builder(requireActivity()).asConfirm("Prompt",if (collectionIbtn.isSelected)"Whether to cancel" else "Whether to bookmark") {
                if (collectionIbtn.isSelected){/*取消收藏或关注*/
                    HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1",
                        "refid" to mId.toString(), "tablename" to "meishifenxiang", "userid" to Utils.getUserId().toString(), "type" to "1" )).observeKt {
                        it.getOrNull()?.let {
                            if (it.data.list.isNotEmpty()){
                                HomeRepository.delete<StoreupItemBean>("storeup", listOf(it.data.list[0].id)).observeKt {
                                    it.getOrNull()?.let {
                                        mMeishifenxiangItemBean.storeupnum-=1
                                        UserRepository.update("meishifenxiang",mMeishifenxiangItemBean).observeKt {
                                            it.getOrNull()?.let {
                                                storeupnumTv.text = mMeishifenxiangItemBean.storeupnum.toString()
                                            }
                                         }
                                        "Cancel successful".showToast()
                                        collectionIbtn.isSelected =false
                                        collectionIbtn.text = Html.fromHtml("&#xe669;")
                                    }
                                }
                            }
                        }
                    }
                }else{/*收藏或关注*/
                    HomeRepository.add<StoreupItemBean>("storeup",StoreupItemBean(
                        userid = Utils.getUserId(),
                        name = mMeishifenxiangItemBean.meishimingcheng.toString(),
                        picture=mMeishifenxiangItemBean.meishitupian.split(",")[0],
                        refid = mMeishifenxiangItemBean.id,
                        tablename="meishifenxiang",
                        type="1"                    )).observeKt {
                        it.getOrNull()?.let {
                            mMeishifenxiangItemBean.storeupnum+=1
                            UserRepository.update("meishifenxiang",mMeishifenxiangItemBean).observeKt {
                                it.getOrNull()?.let {
                                    storeupnumTv.text = mMeishifenxiangItemBean.storeupnum.toString()
                                }
                            }
                            "Collection successful".showToast()
                            collectionIbtn.isSelected = true
                            collectionIbtn.text = Html.fromHtml("&#xe668;")
                        }
                    }
                }
            }.show()
        }
    }

    private fun MeishifenxiangcommonDetailsHeaderLayoutBinding.initThumbsUp(){/*赞和踩*/
        HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1","refid" to mId.toString(),
            "tablename" to "meishifenxiang","userid" to Utils.getUserId().toString(),"type" to "%2%")).observeKt {
            it.getOrNull()?.let {
                if (it.data.list.isNotEmpty()){
                    if (it.data.list[0].type=="21"){
                        zanTv.isSelected = true
                        caiFbl.isVisible = false
                        zanTv.text = "Liked"
                        zanIconTv.text = Html.fromHtml("&#xe8c4;")
                    }else{
                        caiTv.isSelected = true
                        zanFbl.isVisible = false
                        caiTv.text = "Disliked"
                        caiIconTv.text = Html.fromHtml("&#xe814;")
                    }
                }
            }
        }
        zanFbl.setOnClickListener {
            XPopup.Builder(requireActivity()).asConfirm("Prompt",if (zanTv.isSelected)"Do you want to cancel liking？" else "Do you like it？") {
                if (zanTv.isSelected){/*取消点赞*/
                    HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1","refid" to mId.toString(),
                        "tablename" to "meishifenxiang","userid" to Utils.getUserId().toString(),"type" to "%2%")).observeKt {
                        it.getOrNull()?.let {
                            if (it.data.list.isNotEmpty()){
                                HomeRepository.delete<Any>("storeup",listOf(it.data.list[0].id)).observeKt {
                                    it.getOrNull()?.let {
                                        zanTv.isSelected = false
                                        caiFbl.isVisible = true
                                        mMeishifenxiangItemBean.thumbsupnum -=1
                                        zanTv.text = "Like:"
                                        zanIconTv.text = Html.fromHtml("&#xe7e1;")
                                        zanNumTv.text = "${mMeishifenxiangItemBean.thumbsupnum}"
                                        mDetailsViewModel.update("meishifenxiang",mMeishifenxiangItemBean)
                                        "Cancel successful".showToast()
                                    }
                                }
                            }
                        }
                    }
                }else{/*点赞*/
                    HomeRepository.add<Any>("storeup", StoreupItemBean(
                        userid = Utils.getUserId(),
                        name = mMeishifenxiangItemBean.meishimingcheng,
                        picture =mMeishifenxiangItemBean.meishitupian.split(",")[0],
                        refid = mId,
                        tablename="meishifenxiang",
                        type= "21"
                    )).observeKt {
                        it.getOrNull()?.let {
                            zanTv.isSelected = true
                            caiFbl.isVisible = false
                            mMeishifenxiangItemBean.thumbsupnum +=1
                            zanTv.text = "Like:"
                            zanIconTv.text = Html.fromHtml("&#xe8c4;")
                            zanNumTv.text = "${mMeishifenxiangItemBean.thumbsupnum}"
                            mDetailsViewModel.update("meishifenxiang",mMeishifenxiangItemBean)
                            "Like successful".showToast()
                        }
                    }
                }
            }.show()
        }

        caiFbl.setOnClickListener {
            XPopup.Builder(requireActivity()).asConfirm("Prompt",if (caiTv.isSelected)"是否取消点踩" else "是否点踩") {
                if (caiTv.isSelected){/*取消点踩*/
                    HomeRepository.list<StoreupItemBean>("storeup", mapOf("page" to "1","limit" to "1","refid" to mId.toString(),
                        "tablename" to "meishifenxiang","userid" to Utils.getUserId().toString(),"type" to "%2%")).observeKt {
                        it.getOrNull()?.let {
                            if (it.data.list.isNotEmpty()){
                                HomeRepository.delete<Any>("storeup", listOf(it.data.list[0].id)).observeKt {
                                    it.getOrNull()?.let {
                                        caiTv.isSelected = false
                                        zanFbl.isVisible = true
                                        mMeishifenxiangItemBean.crazilynum -=1
                                        caiTv.text = "Dislike:"
                                        caiIconTv.text = Html.fromHtml("&#xe815;")
                                        caiNumTv.text = "${mMeishifenxiangItemBean.crazilynum}"
                                        mDetailsViewModel.update("meishifenxiang",mMeishifenxiangItemBean)
                                        "Cancel successful".showToast()
                                    }
                                }
                            }
                        }
                    }
                }else{/*点踩*/
                    HomeRepository.add<Any>("storeup", StoreupItemBean(
                        userid = Utils.getUserId(),
                        name = mMeishifenxiangItemBean.meishimingcheng,
                    picture =mMeishifenxiangItemBean.meishitupian.split(",")[0],
                    refid = mId,
                    tablename="meishifenxiang",
                    type= "22"
                    )).observeKt {
                        it.getOrNull()?.let {
                            caiTv.isSelected = true
                            zanFbl.isVisible = false
                            mMeishifenxiangItemBean.crazilynum +=1
                            caiTv.text = "Disliked:"
                            caiIconTv.text = Html.fromHtml("&#xe814;")
                            caiNumTv.text = "${mMeishifenxiangItemBean.crazilynum}"
                            mDetailsViewModel.update("meishifenxiang",mMeishifenxiangItemBean)
                            "Tap successfully".showToast()
                        }
                    }
                }
            }.show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
            mDetailsViewModel.comments("discussmeishifenxiang", mapOf("page" to "1","limit" to "10","refid" to mId.toString()))
        }
    }

}
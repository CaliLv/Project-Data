package com.design.appproject.ui.meishifenxiang

import com.design.appproject.base.NetRetrofitClient
import com.design.appproject.utils.ArouterUtils
import androidx.fragment.app.viewModels
import com.design.appproject.base.BaseBindingFragment
import com.alibaba.android.arouter.launcher.ARouter
import android.widget.*
import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.design.appproject.ext.load
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.blankj.utilcode.util.ColorUtils
import androidx.core.view.children
import androidx.core.view.isVisible
import com.design.appproject.R
import com.design.appproject.ui.meishifenxiang.ListFilterDialog
import android.view.ViewGroup
import android.view.Gravity
import androidx.core.view.setPadding
import androidx.core.view.setMargins
import com.design.appproject.widget.SpacesItemDecoration
import com.union.union_basic.ext.*
import net.lucode.hackware.magicindicator.MagicIndicator
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.MeishifenxiangcommonListLayoutBinding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.meishifenxiang.ListModel
import com.design.appproject.ui.meishifenxiang.ListAdapter
import com.design.appproject.utils.Utils
import com.google.android.flexbox.*
import com.design.appproject.logic.repository.UserRepository
import com.design.appproject.bean.MeishileixingItemBean
import android.view.LayoutInflater
import com.design.appproject.widget.MagicIndexCommonNavigator
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.blankj.utilcode.util.KeyboardUtils
import com.design.appproject.bean.MeishifenxiangItemBean
import com.design.appproject.widget.MyFlexboxLayoutManager
/**
 * 美食分享列表页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_LIST_MEISHIFENXIANG)
class ListFragment : BaseBindingFragment<MeishifenxiangcommonListLayoutBinding>() {

    @JvmField
    @Autowired
    var mSearch: String = "" /*搜索*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    @JvmField
    @Autowired
    var mHasBack: Boolean = true /*是否有返回键*/

    private val mListAdapter by lazy {
        ListAdapter().apply {
            mIsBack = this@ListFragment.mIsBack
        }
    }

    private val mListModel by viewModels<ListModel>()

    private val params by lazy { /*请求参数*/
        mutableMapOf(
            "page" to "1",
            "limit" to "20",
        )
    }
    val queryIndex = Pair<String,String>("Food Name","meishimingcheng")/*查询索引*/

    private val mListFilterdialog by lazy {/*筛选弹窗*/
        ListFilterDialog(requireActivity()).apply {
            callBackListener = {isEnsure,searchParams ->
                isEnsure.yes { /*确定筛选条件*/
                    loadData(1,searchParams)
                }.otherwise {
                    loadData(1,searchParams)
                }
            }
        }
    }
    override fun initEvent() {
        setBarTitle("Food sharing",mHasBack)
        setBarColor("#FFFFFF","black")
        mIsBack.no{
            params.put("sfsh","Yes")
        }
        binding.apply {
            addBtn.isVisible = (mIsBack && Utils.isAuthBack("meishifenxiang","Add"))|| Utils.isAuthFront("meishifenxiang","新增")
            addBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_MEISHIFENXIANG).navigation()
            }
            initSearch()
            initCategory()
            srv.setOnRefreshListener {
                loadData(1)
            }
            filterTv.setOnClickListener {
                XPopup.Builder(requireActivity())
                    .popupPosition(PopupPosition.Right) /*右边*/
                    .hasStatusBarShadow(false) /*启用状态栏阴影*/
                    .autoFocusEditText(false)
                    .asCustom(mListFilterdialog)
                    .show()
            }
            srv.mRecyclerView.layoutManager = MyFlexboxLayoutManager(requireActivity()).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            mListAdapter.addChildClickViewIds(R.id.edit_tv,R.id.delete_tv)
            mListAdapter.setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.edit_tv){
                  ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_MEISHIFENXIANG)
                    .withLong("mId",mListAdapter.data[position].id)
                    .navigation()
                }else if (view.id==R.id.delete_tv){
                  XPopup.Builder(requireActivity()).asConfirm("Prompt","Are you sure you want to delete it?") {
                      HomeRepository.delete<Any>("meishifenxiang",listOf(mListAdapter.data[position].id)).observeKt {
                        it.getOrNull()?.let {
                            "Delete successful".showToast()
                            loadData(1)
                        }
                      }
                  }.show()
                }
            }
            srv.setAdapter(mListAdapter.apply {
                pageLoadMoreListener {
                    loadData(it)
                }
                setOnItemClickListener { adapter, view, position ->
                    ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_MEISHIFENXIANG,map=mapOf("mId" to data[position].id,"mIsBack" to mIsBack))
                }
            })
        }
    }
    private fun MeishifenxiangcommonListLayoutBinding.initSearch() {
        searchValueEt.hint = queryIndex.first
        searchValueEt.setText(mSearch)
        searchBtn.setOnClickListener {
            loadData(1)
        }
        searchValueEt.postDelayed({ KeyboardUtils.hideSoftInput(searchValueEt) }, 200)
    }


    val mCategoryList = mutableListOf("Total")
    var mSelectedIndex = 0

    private fun MeishifenxiangcommonListLayoutBinding.initCategory() {
        mIsBack.yes {
            HomeRepository.page<MeishileixingItemBean>("meishileixing", mapOf("page" to "1", "limit" to "100")).observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let {
                setNavigator(tab,it.data.list)
            }
        } }.otherwise {
            HomeRepository.list<MeishileixingItemBean>("meishileixing", mapOf("page" to "1", "limit" to "100")).observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let {
                setNavigator(tab,it.data.list)
            }
        }
        }
    }

    private fun setNavigator(tab: LinearLayout,list:List<MeishileixingItemBean>){
        mCategoryList.addAll(list.map{it.meishileixing})
        mCategoryList.forEachIndexed { index, s ->
            val itemView = creatTabView(index,tab,index==0,s)
            tab.addView(itemView)
        }
    }

    private fun creatTabView(index:Int,tab: LinearLayout, isSelect:Boolean,title:String):View{
        val itemView = LayoutInflater.from(context).inflate(if (isSelect) R.layout.item_tab_layout_active else R.layout.item_tab_layout_default, tab, false)
        itemView.isSelected = isSelect
        itemView.toConversion<TextView>()?.text = title
        itemView.setOnClickListener {
            if (itemView.isSelected){
                return@setOnClickListener
            }else{
                mSelectedIndex = index
                loadData(1)
                val oldIndex= tab.children.indexOfFirst { it.isSelected }
                if (oldIndex!=-1){
                    tab.removeViewAt(oldIndex)
                    tab.addView(creatTabView(oldIndex,tab,false,mCategoryList[oldIndex]),oldIndex)
                }
                tab.removeViewAt(index)
                tab.addView(creatTabView(index,tab,true,title),index)
            }
        }
        return itemView
    }

    override fun initData() {
        super.initData()
        loadData(1)
        mListModel.pageLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }

        mListModel.listLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }

    }

    private fun loadData(page:Int,searchParams:MutableMap<String,String>?=null){
        if (page==1){
            binding.srv.reload()
        }
        params.put("page",page.toString())
        binding.searchValueEt.text.toString().isNotNullOrEmpty().yes {
            params.put("meishimingcheng","%" + binding.searchValueEt.text.toString() + "%" )
        }.otherwise {
            params.remove("meishimingcheng")
        }
        if(mCategoryList[mSelectedIndex]!="Total"){
            params.put("meishileixing","%" + mCategoryList[mSelectedIndex] + "%" )
        }else{
            params.remove("meishileixing")
        }
        val requestParams = mutableMapOf<String,String>()
        requestParams.putAll(params)
        searchParams?.let { requestParams.putAll(it) }
        mIsBack.yes {
                mListModel.page("meishifenxiang", requestParams)
        }.otherwise {
                mListModel.list("meishifenxiang", requestParams)
        }
    }
}
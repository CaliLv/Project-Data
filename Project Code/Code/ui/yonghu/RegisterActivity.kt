package com.design.appproject.ui.yonghu
import com.union.union_basic.ext.*
import android.text.InputType
import android.annotation.SuppressLint
import android.text.method.PasswordTransformationMethod
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.RegexUtils
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.YonghuactivityRegisterLayoutBinding
import com.design.appproject.ext.load
import androidx.core.view.children
import com.design.appproject.widget.BottomSpinner
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.blankj.utilcode.util.TimeUtils
import com.design.appproject.bean.YonghuItemBean
import com.design.appproject.logic.viewmodel.yonghu.RegisterViewModel
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter
import java.text.SimpleDateFormat
import com.union.union_basic.image.selector.SmartPictureSelector
import java.io.File
import java.util.*

/**
 * Yonghu注册界面
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_REGISTER_YONGHU)
class RegisterActivity : BaseBindingActivity<YonghuactivityRegisterLayoutBinding>() {

    private val mRegisterViewModel by viewModels<RegisterViewModel>()

    /**注册请求参数*/
    var reqisterBean = YonghuItemBean()

    @SuppressLint("SimpleDateFormat")
    override fun initEvent() {
        binding.apply {
            setBarTitle("Register")
            setBarColor("#FFFFFF","black")
            initView()
        }
    }

    /**初始化控件状态*/
    private fun YonghuactivityRegisterLayoutBinding.initView() {
        titleIv.load(this@RegisterActivity,"http://clfile.zggen.cn/20231027/f76234bafa534579beeb9a486c2d2df2.jpg",needPrefix=false)
        registerLl.findViewWithTag<EditText>("yonghuzhanghao")?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(et: Editable?) {
                    reqisterBean.yonghuzhanghao = et?.toString() ?: ""
                }
            })
        }
        registerLl.findViewWithTag<EditText>("yonghumima")?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(et: Editable?) {
                    reqisterBean.yonghumima = et?.toString() ?: ""
                }
            })
        }
        registerLl.findViewWithTag<EditText>("yonghuxingming")?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(et: Editable?) {
                    reqisterBean.yonghuxingming = et?.toString() ?: ""
                }
            })
        }
        registerLl.findViewWithTag<LinearLayout>("touxiang").let {
            it.setOnClickListener {
                SmartPictureSelector.openPicture(this@RegisterActivity) {
                    val path = it[0]
                    showLoading("Uploading...")
                    mRegisterViewModel.upload(File(path), "touxiang")
                }
            }
        }
        registerLl.findViewWithTag<BottomSpinner>("xingbie")
            .let { spinner ->

                spinner.setOptions(
                    "Male,Female".split(","),
                    "Please select gender",
                    listener = object : BottomSpinner.OnItemSelectedListener {
                        override fun onItemSelected(position: Int, content: String) {
                            super.onItemSelected(position, content)
                            spinner.text = content
                            reqisterBean.xingbie = content
                        }
                    })
            }
        registerLl.findViewWithTag<EditText>("shoujihaoma")?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(et: Editable?) {
                    reqisterBean.shoujihaoma = et?.toString() ?: ""
                }
            })
        }
        registerBtn.setOnClickListener {
            verify().yes {
                showLoading("Register...")
                mRegisterViewModel.register("yonghu", reqisterBean)
            }
        }
    }

    /**验证*/
    private fun verify(): Boolean {
        binding.registerLl.findViewWithTag<EditText>("yonghuzhanghao").let {
            if (it.text.toString().isNullOrEmpty()) {
                "User account cannot be empty".showToast()
                return@verify false
            } else {
                reqisterBean.yonghuzhanghao = it.text.toString()
            }
        }
        binding.registerLl.findViewWithTag<EditText>("yonghumima").let {
            if (it.text.toString().isNullOrEmpty()) {
                "User password cannot be empty".showToast()
                return@verify false
            } else {
                reqisterBean.yonghumima = it.text.toString()
            }
        }
        binding.registerLl.findViewWithTag<EditText>("yonghuxingming").let {
            if (it.text.toString().isNullOrEmpty()) {
                "User name cannot be empty".showToast()
                return@verify false
            } else {
                reqisterBean.yonghuxingming = it.text.toString()
            }
        }
        if (!RegexUtils.isMobileExact(reqisterBean.shoujihaoma)) {
            "Phone number should be entered in phone format".showToast()
            return false
        }
        return true
    }

    override fun initData() {
        super.initData()
        mRegisterViewModel.optionLiveData.observeKt {
            it.getOrNull()?.let {
                it.callBackData?.toConversion<Pair<String, Boolean>>()?.let { callData ->
                    val spinnerView =
                        binding.registerLl.findViewWithTag<BottomSpinner>(callData.first)
                    spinnerView.setOptions(it.data, spinnerView.hint.toString(), callData.second)
                    spinnerView.dialogShow()
                }
            }
        }
        mRegisterViewModel.uploadLiveData.observeKt {
            it.getOrNull()?.let {
                it.callBackData?.let { tag ->
                    val imageView =
                        binding.registerLl.findViewWithTag<LinearLayout>(tag).getChildAt(1)
                            .toConversion<ImageView>()
                    imageView?.load(this, "file/"+it.file)
                    if (tag.toString()=="touxiang") {
                        reqisterBean.touxiang = "file/" + it.file
                    }
                }
            }
        }

        mRegisterViewModel.registerLiveData.observeKt {
            it.getOrNull()?.let {
                "Registration successful".showToast()
                finish()
            }
        }
    }
}
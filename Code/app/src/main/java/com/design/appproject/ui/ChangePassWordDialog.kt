package com.design.appproject.ui

import android.content.Context
import com.design.appproject.databinding.DialogChangePasswordLayoutBinding
import com.dylanc.viewbinding.inflate
import com.lxj.xpopup.core.BottomPopupView
import com.union.union_basic.ext.showToast

class ChangePassWordDialog(context: Context,oldPassword:String) : BottomPopupView(context) {

    lateinit var binding: DialogChangePasswordLayoutBinding

    private var mOldPassword = oldPassword


    var mUpdateListener: ((String) -> Unit)? =null

    override fun addInnerContent() {
        binding = bottomPopupContainer.inflate()
    }

    override fun onCreate() {
        super.onCreate()
        binding.apply {
            updateBtn.setOnClickListener {
                if (oldPassowrdEt.text.toString() != mOldPassword){
                    "Old password is incorrect！".showToast()
                    return@setOnClickListener
                }
                if (newPassowrdEt.text.toString().length<8){
                    "The length of the new password should not be less than 8 digits！".showToast()
                    return@setOnClickListener
                }
                if (newPassowrdEt.text.toString() != ensurePassowrdEt.text.toString()){
                    "Two passwords do not match".showToast()
                    return@setOnClickListener
                }
                mUpdateListener?.invoke(newPassowrdEt.text.toString())
            }
        }
    }
}
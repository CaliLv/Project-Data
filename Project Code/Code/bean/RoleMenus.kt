package com.design.appproject.bean

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken

var roleMenusList =
    GsonUtils.fromJson<List<RoleMenusItem>>("[{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-circle\",\"buttons\":[\"Check\",\"Delete\"],\"menu\":\"Shopping List\",\"menuJump\":\"List\",\"tableName\":\"gouwuqingdan\"}],\"fontClass\":\"icon-common43\",\"menu\":\"购物清单管理\",\"unicode\":\"&#xef27;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-full\",\"buttons\":[\"Add\",\"Check\",\"Modify\",\"Delete\"],\"menu\":\"User\",\"menuJump\":\"List\",\"tableName\":\"yonghu\"}],\"fontClass\":\"icon-user8\",\"menu\":\"User manage\",\"unicode\":\"&#xef9e;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-wenzi\",\"buttons\":[\"Check\",\"Delete\"],\"menu\":\"Meal Plan\",\"menuJump\":\"List\",\"tableName\":\"yongcanjihua\"}],\"fontClass\":\"icon-common45\",\"menu\":\"Meal PLan menage\",\"unicode\":\"&#xef3b;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-link\",\"buttons\":[\"Add\",\"Check\",\"Modify\",\"Delete\"],\"menu\":\"Food Type\",\"menuJump\":\"List\",\"tableName\":\"meishileixing\"},{\"appFrontIcon\":\"cuIcon-vip\",\"buttons\":[\"Check\",\"Delete\",\"Auditing\",\"Check comment\"],\"menu\":\"Food sharing\",\"menuJump\":\"List\",\"tableName\":\"meishifenxiang\"}],\"fontClass\":\"icon-common5\",\"menu\":\"Sharing management\",\"unicode\":\"&#xedae;\"}],\"frontMenu\":[],\"hasBackLogin\":\"Yes\",\"hasBackRegister\":\"No\",\"hasFrontLogin\":\"No\",\"hasFrontRegister\":\"No\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-wenzi\",\"buttons\":[\"Add\",\"Check\",\"Modify\",\"Delete\",\"Shopping List\"],\"menu\":\"Meal Plan\",\"menuJump\":\"List\",\"tableName\":\"yongcanjihua\"}],\"fontClass\":\"icon-common45\",\"menu\":\"Meal plan manege\",\"unicode\":\"&#xef3b;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-brand\",\"buttons\":[\"Check\"],\"menu\":\"My favorites\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common13\",\"menu\":\"My Collection Management\",\"unicode\":\"&#xedf7;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-circle\",\"buttons\":[\"Check\",\"Modify\",\"Delete\"],\"menu\":\"Shopping List\",\"menuJump\":\"List\",\"tableName\":\"gouwuqingdan\"}],\"fontClass\":\"icon-common43\",\"menu\":\"购物清单管理\",\"unicode\":\"&#xef27;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vip\",\"buttons\":[\"Add\",\"Check\",\"Modify\",\"Delete\"],\"menu\":\"Sharing\",\"menuJump\":\"List\",\"tableName\":\"meishifenxiang\"}],\"fontClass\":\"icon-common5\",\"menu\":\"Sharing manage\",\"unicode\":\"&#xedae;\"}],\"frontMenu\":[],\"hasBackLogin\":\"No\",\"hasBackRegister\":\"No\",\"hasFrontLogin\":\"Yes\",\"hasFrontRegister\":\"Yes\",\"roleName\":\"User\",\"tableName\":\"yonghu\"}]", object : TypeToken<List<RoleMenusItem>>() {}.type)

data class RoleMenusItem(
    val backMenu: List<MenuBean>,
    val frontMenu: List<MenuBean>,
    val hasBackLogin: String,
    val hasBackRegister: String,
    val hasFrontLogin: String,
    val hasFrontRegister: String,
    val roleName: String,
    val tableName: String
)

data class MenuBean(
    val child: List<Child>,
    val menu: String,
    val fontClass: String,
    val unicode: String=""
)

data class Child(
    val appFrontIcon: String,
    val buttons: List<String>,
    val menu: String,
    val menuJump: String,
    val tableName: String
)


package com.umeox.skin_lib.config

import com.umeox.skin_lib.ext.getSpStringValue
import com.umeox.skin_lib.ext.saveSpStringValue

object SkinConfig {

    const val NAMESPACE = "http://schemas.android.com/android/skin"
    const val ATTR_SKIN_ENABLE = "skinAble"
    const val PREF_CUSTOM_SKIN_PATH = "cn_feng_skin_custom_path"
    const val DEFALT_SKIN = "cn_feng_skin_default"


    const val SKIN_SUFFIX = "skin_suffix"

    /**
     * 配置是否使用皮肤包
     */
    var useSkinPackage = false

    /**
     * get path of last skin package path
     * @param context
     * @return path of skin package
     */
    fun getCustomSkinPath(): String {
        return PREF_CUSTOM_SKIN_PATH.getSpStringValue(DEFALT_SKIN)
    }

    fun saveSkinPath(path: String) {
        PREF_CUSTOM_SKIN_PATH.saveSpStringValue(path)
        SKIN_SUFFIX.saveSpStringValue("")
    }

    fun isDefaultSkin(): Boolean {
        return DEFALT_SKIN == getCustomSkinPath()
    }

    fun getSkinSuffix(): String {
        return SKIN_SUFFIX.getSpStringValue("")
    }


    fun saveSkinSuffix(suffix: String) {
        SKIN_SUFFIX.saveSpStringValue(suffix)
    }

}
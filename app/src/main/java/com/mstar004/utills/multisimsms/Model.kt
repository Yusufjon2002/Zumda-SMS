package com.mstar004.utills.multisimsms

import android.annotation.SuppressLint
import android.content.Context
import com.mstar004.utills.multisimsms.MultiSimSMS.setOnSMSListener

/**
 * Created by Aamir on 25-01-2017.
 */

@SuppressLint("StaticFieldLeak")
object Model {
    @JvmStatic
    var context: Context? = null
    @JvmStatic
    var message: String? = null
    @JvmStatic
    var mobNumber: String? = null
    @JvmStatic
    var listener: setOnSMSListener? = null
}
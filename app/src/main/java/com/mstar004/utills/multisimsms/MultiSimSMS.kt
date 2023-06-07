package com.mstar004.utills.multisimsms

import android.content.Context
import com.mstar004.utills.multisimsms.Model.context
import com.mstar004.utills.multisimsms.Model.mobNumber
import com.mstar004.utills.multisimsms.Model.message
import com.mstar004.utills.multisimsms.ImplSendSms.sendTextMessage
import com.mstar004.utills.multisimsms.Model.listener
import androidx.annotation.RequiresApi
import android.os.Build
import com.mstar004.utills.multisimsms.ImplSendSms
import com.mstar004.utills.multisimsms.MultiSimSMS.setOnSMSListener

/**
 * Created by Aamir on 25-01-2017.
 */
class MultiSimSMS(context: Context?) {
    interface setOnSMSListener {
        fun onSmsSent()
        fun onGenericFailure()
        fun onNoService()
        fun onNullPdu()
        fun onRadioOff()
        fun onInvalidDestinationAddress()
        fun serviceAvailOnSingleSim(Carrier: CharSequence?, id: Int)
        fun singleSimOrLessBuild()
    }

    companion object {
        fun setNumber(number: String?) {
            mobNumber = number
        }

        fun setMessage(message: String?) {
            Model.message = message
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        fun sendMessage() {
            sendTextMessage()
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        fun sendMessage(listener: setOnSMSListener?) {
            Model.listener = listener
            sendTextMessage()
        }

        fun initialize(context: Context?) {
            Model.context = context
        }
    }

    init {
        Model.context = context
    }
}
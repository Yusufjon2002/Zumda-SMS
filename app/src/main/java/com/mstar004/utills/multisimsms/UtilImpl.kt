package com.mstar004.utills.multisimsms

import android.telephony.TelephonyManager
import android.os.Build
import androidx.annotation.RequiresApi
import android.telephony.SubscriptionManager
import android.annotation.SuppressLint
import android.app.PendingIntent
import com.mstar004.utills.multisimsms.MultiSimSMS.setOnSMSListener
import android.content.BroadcastReceiver
import android.content.Intent
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.telephony.SmsManager
import android.util.Log
import android.view.Window
import android.widget.RadioGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import com.mstar004.utills.multisimsms.Resources.dualSim
import com.mstar004.zumda_sms.R
import java.lang.IllegalArgumentException

/**
 * Created by Aamir on 26-01-2017.
 */
internal class UtilImpl : Utils() {
    override fun getNoOfSims(): Int {
        var count = -1
        var manager: TelephonyManager? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager =
                Model.context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            count = manager.phoneCount
        }
        return count
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun getSubscriptionId(index: Int): Int {
        val manager = SubscriptionManager.from(Model.context)
        @SuppressLint("MissingPermission") val subId =
            manager.getActiveSubscriptionInfoForSimSlotIndex(index)
        return subId?.subscriptionId ?: -1
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun getCarrierName(index: Int): CharSequence {
        val value: CharSequence = "false"
        val manager = SubscriptionManager.from(Model.context)
        @SuppressLint("MissingPermission") val carrierInfo =
            manager.getActiveSubscriptionInfoForSimSlotIndex(index)
        return if (carrierInfo != null) {
            carrierInfo.carrierName
        } else value
    }

    override fun sendSMS(sentPI: PendingIntent) {
        run {
            val sms = SmsManager.getDefault()
            sms.sendTextMessage(Model.mobNumber, null, Model.message, sentPI, null)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun sendSMS(sentPI: PendingIntent, id: Int) {
        run {
            try {
                val sms = SmsManager.getSmsManagerForSubscriptionId(id)
                sms.sendTextMessage(Model.mobNumber, null, Model.message, sentPI, null)
            } catch (e: IllegalArgumentException) {
                Model.listener?.onInvalidDestinationAddress()
                Log.e(Resources.tAG, (e.message)!!)
            }
        }
    }

    override fun registerReceiver(SENT: String, listener: setOnSMSListener) {
        run {
            //---when the SMS has been sent---
            Model.context?.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(arg0: Context, arg1: Intent) {
                    when (resultCode) {
                        Activity.RESULT_OK -> listener.onSmsSent()
                        SmsManager.RESULT_ERROR_GENERIC_FAILURE -> listener.onGenericFailure()
                        SmsManager.RESULT_ERROR_NO_SERVICE -> listener.onNoService()
                        SmsManager.RESULT_ERROR_NULL_PDU -> listener.onNullPdu()
                        SmsManager.RESULT_ERROR_RADIO_OFF -> listener.onRadioOff()
                    }
                }
            }, IntentFilter(SENT))
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun showAlertDialog(sentPI: PendingIntent) {
       /* run {
            //Show the custom AlertDialog

            val dialog = Dialog(Model.context!!, com.google.android.material.R.style.AlertDialog_AppCompat)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_sim_asking)

            dialog.findViewById<RadioButton>(R.id.b1).text = UtilImpl().getCarrierName(Resources.sIM0).toString()
            dialog.findViewById<RadioButton>(R.id.b2).text = UtilImpl().getCarrierName(Resources.sIM1).toString()

            dialog.findViewById<AppCompatTextView>(R.id.btnSelect).setOnClickListener {
                if (dialog.findViewById<RadioGroup>(R.id.mRadioGroup).checkedRadioButtonId == R.id.b1) {
                    sendSMS(sentPI, UtilImpl().getSubscriptionId(0))
                    dialog.dismiss()
                } else {
                    sendSMS(sentPI, UtilImpl().getSubscriptionId(1))
                    dialog.dismiss()
                }
            }
                dialog.show()

        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun checkCondition(sentPI: PendingIntent) {
        run {
            if (UtilImpl().noOfSims > 1) {
                //More than 1 sim is present
                if (!UtilImpl().getCarrierName(0).toString()
                        .equals("No Service", ignoreCase = true) &&
                    UtilImpl().getCarrierName(1).toString().equals("No Service", ignoreCase = true)
                ) {
                    //if vodafone sim has service and jio Doesn't have
                    UtilImpl().sendSMS(sentPI, UtilImpl().getSubscriptionId(0))
                    Model.listener
                        ?.serviceAvailOnSingleSim(UtilImpl().getCarrierName(0), Resources.sIM0)
                } else if (!UtilImpl().getCarrierName(1).toString()
                        .equals("No Service", ignoreCase = true) &&
                    UtilImpl().getCarrierName(0).toString().equals("No Service", ignoreCase = true)
                ) {
                    //if jio sim has service and Voda doesn't have
                    UtilImpl().sendSMS(sentPI, UtilImpl().getSubscriptionId(1))
                    Model.listener
                        ?.serviceAvailOnSingleSim(UtilImpl().getCarrierName(1), Resources.sIM1)
                } else if ((!UtilImpl().getCarrierName(1).toString()
                        .equals("No Service", ignoreCase = true) &&
                            !UtilImpl().getCarrierName(0).toString()
                                .equals("No Service", ignoreCase = true))
                ) {
                    //both sims are in service show alert Dialog
                    UtilImpl().showAlertDialog(sentPI)
                    dualSim = true

                } else {

                }
            } else if (UtilImpl().noOfSims == 1 || UtilImpl().noOfSims == -1) {
                UtilImpl().sendSMS(sentPI) //only 1 sim is present need to use Default method for SMS Manager
                Model.listener?.singleSimOrLessBuild()
            } else {

            }
        }
    }
}
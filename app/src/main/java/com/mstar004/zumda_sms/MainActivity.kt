package com.mstar004.zumda_sms

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.SmsManager.getSmsManagerForSubscriptionId
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mstar004.adapter.MyAdapter
import com.mstar004.models.ContactList
import com.mstar004.models.Contacts
import com.mstar004.utills.multisimsms.MultiSimSMS
import com.mstar004.utills.multisimsms.Resources
import com.mstar004.utills.multisimsms.Resources.dualSim
import com.mstar004.utills.multisimsms.UtilImpl
import com.mstar004.zumda_sms.databinding.ActivityMainBinding
import com.mstar004.zumda_sms.databinding.ItemAddDialogBinding
import java.util.concurrent.TimeUnit

@Suppress("IMPLICIT_CAST_TO_ANY", "DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var myAdapter: MyAdapter
    val list2 = ArrayList<Contacts>()
    var smsXabari: String = ""
    private val pickFileResultCode = 1
    private var scAddress: String? = null
    var subID: Int = 0
    var delay: String = "1"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        MultiSimSMS.initialize(this)


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        ) {
            onResume()

        } else {
            requestSendSMSPermission()
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            val path = data?.data
            val jsonSelectedFile = contentResolver.openInputStream(path!!)
            val inputAsString = jsonSelectedFile?.bufferedReader().use { it?.readText() }

            val gson = Gson()
            val lisType = object : TypeToken<ContactList>() {}.type

            val contacts: ContactList = gson.fromJson(inputAsString, lisType)

            for (i in contacts.contacts) {
                list2.add(i)
            }
            myAdapter = MyAdapter(contacts = list2)
            binding.rvContacts.adapter = myAdapter
            binding.title.text = "Qabul qiluvchilar ro'yhati (${list2.size})"
        } catch (e: Exception) {
            Toast.makeText(this, "Noto'g'ri formatdagi fayl tanlandi!", Toast.LENGTH_SHORT).show()
            recreate()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onResume() {
        super.onResume()

        binding.addFile.setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
            chooseFile.type = "*/*"
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            startActivityForResult(chooseFile, pickFileResultCode)

        }

        binding.imageSendSms.setOnClickListener {
            writeSms()
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("MissingPermission")

    private fun writeSms() {
        val alertDialog = android.app.AlertDialog.Builder(this@MainActivity)
        val dialogSms = alertDialog.create()
        val itemAddDialogBinding =
            ItemAddDialogBinding.inflate(LayoutInflater.from(this@MainActivity))
        dialogSms.setView(itemAddDialogBinding.root)

        val spanBuilder = SpannableStringBuilder()
            .color(resources.getColor(R.color.black)) {
                this.bold {
                    append(getString(R.string.note_colon))
                }
            }
            .color(resources.getColor(R.color.white)) {
                append("  " + getString(R.string.txt_note_text))
            }
        itemAddDialogBinding.txtNote.text = spanBuilder

        itemAddDialogBinding.btnDialogSave.setOnClickListener {
            smsXabari = itemAddDialogBinding.edtSMS.text.toString().trim()

            if (smsXabari.isNotEmpty() && list2.isNotEmpty() && itemAddDialogBinding.editDelay.text.isNotEmpty()) {

                if (dualSim) {
                    val dialog = Dialog(this@MainActivity,
                        com.google.android.material.R.style.AlertDialog_AppCompat)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.dialog_sim_asking)

                    dialog.findViewById<RadioButton>(R.id.b1).text =
                        UtilImpl().getCarrierName(Resources.sIM0).toString()
                    dialog.findViewById<RadioButton>(R.id.b2).text =
                        UtilImpl().getCarrierName(Resources.sIM1).toString()

                    dialog.findViewById<Button>(R.id.btnSelect).setOnClickListener {
                        if (dialog.findViewById<RadioGroup>(R.id.mRadioGroup).checkedRadioButtonId == R.id.b1) {
                            scAddress = "${UtilImpl().getSubscriptionId(0)}"
                            subID = 0
                            dialog.dismiss()
                            sendMessage()
                        } else {
                            scAddress = "${UtilImpl().getSubscriptionId(1)}"
                            subID = 1
                            dialog.dismiss()
                            sendMessage()
                        }
                    }
                    dialog.show()
                } else {
                    sendMessage()
                }

                dialogSms.dismiss()
            } else {
                Toast.makeText(this@MainActivity,
                    "Avval ma'lumotlarni to'ldiring!!!",
                    Toast.LENGTH_SHORT)
                    .show()
            }

        }

        dialogSms.show()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun sendMessage() {
        SendSmsAsyncTask().execute(smsXabari)
    }

    private fun requestSendSMSPermission() =
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.SEND_SMS)
            && ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            && ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                Manifest.permission.READ_PHONE_STATE)
        ) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("SMS yuborish uchun ruxsat berish")
            dialog.setMessage("Iltimos, dasturdan foydalanish uchun ruxsat bering!")
            dialog.setPositiveButton(
                "Ha roziman"
            ) { _, _ ->
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE),
                    1)
            }
            dialog.setNegativeButton(
                "Yo'q"
            ) { dialog1, _ ->

                dialog1.dismiss()
            }
            dialog.show()

        } else {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE),
                1)
        }

    @SuppressLint("MissingPermission", "StaticFieldLeak")
    inner class SendSmsAsyncTask : AsyncTask<String, Void, Void>() {

        var listError = ArrayList<String>()

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            binding.progressSend.visibility = View.VISIBLE
            binding.linearRoot.alpha = 0.5f
        }

        @SuppressLint("MissingPermission", "NewApi")
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): Void? {

            if (list2.isNotEmpty()) {
                try {
                    list2.forEach {
                        val formatted = smsXabari.replace(":name", it.name)

                        if (!dualSim) {
                            val smsDual = SmsManager.getDefault()
                            val parts = smsDual.divideMessage(formatted)
                            //smsDual.sendTextMessage(it.phone, null, formatted, null, null)
                            smsDual.sendMultipartTextMessage(it.phone, null, parts, null, null)
                        } else {
                            val sms = getSmsManagerForSubscriptionId(subID)
                            val parts = sms.divideMessage(formatted)
                            // sms.sendTextMessage(it.phone, null, formatted, null, null)
                            sms.sendMultipartTextMessage(it.phone, null, parts, null, null)
                        }

                        TimeUnit.SECONDS.sleep(delay.toLong())
                    }

                } catch (e: Exception) {
                }
            }

            return null
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            binding.linearRoot.alpha = 1.0f
            binding.progressSend.visibility = View.GONE
            if (listError.isNotEmpty()) {
                Toast.makeText(this@MainActivity, "$listError larda xatolik", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this@MainActivity, "Muvaffaqiyatli yuborildi", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}
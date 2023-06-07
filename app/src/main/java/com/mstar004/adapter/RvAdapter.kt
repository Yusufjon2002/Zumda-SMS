package com.mstar004.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.mstar004.models.Contacts
import com.mstar004.zumda_sms.databinding.ItemRvSendSmsKimgaBinding

class MyAdapter(private var contacts: List<Contacts>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var smsTextSet = HashSet<String>()
    inner class ViewHolder(var itemRv: ItemRvSendSmsKimgaBinding) :
        RecyclerView.ViewHolder(itemRv.root) {

        fun onBind(contacts: Contacts, position: Int) {
            itemRv.txtNumber.text = contacts.phone
            itemRv.txtName.text = contacts.name
            itemRv.chexItemRvSendSmsKimga.isChecked = true

            smsTextSet.add(contacts.phone)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRvSendSmsKimgaBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(contacts = contacts[position], position)
        setAnimation(viewToAnimate = holder.itemView, position = position)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        val anim = ScaleAnimation(0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f)
        anim.duration = 200
        viewToAnimate.startAnimation(anim)

    }

    interface onMark {
        fun onMark(contacts: Contacts, position: Int)
    }
}
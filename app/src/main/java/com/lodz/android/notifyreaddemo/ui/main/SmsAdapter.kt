package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.pandora.widget.rv.recycler.BaseRecyclerViewAdapter

class SmsAdapter(context: Context) :BaseRecyclerViewAdapter<SmsBean>(context){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SmsViewHolder(getLayoutView(parent, R.layout.rv_item_sms))

    override fun onBind(holder: RecyclerView.ViewHolder, position: Int) {
        val bean = getItem(position)
        if (bean == null || holder !is SmsViewHolder) {
            return
        }
        showItem(holder, bean)
    }

    private fun showItem(holder: SmsViewHolder, bean: SmsBean) {
        holder.addressTv.setText(bean.address)
        holder.dateTv.setText(bean.date)
        holder.codeTv.setText(bean.getVerificationCode())
        holder.bodyTv.setText(bean.body)
    }

    private inner class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTv by bindView<TextView>(R.id.address_tv)
        val dateTv by bindView<TextView>(R.id.date_tv)
        val codeTv by bindView<TextView>(R.id.code_tv)
        val bodyTv by bindView<TextView>(R.id.body_tv)
    }
}
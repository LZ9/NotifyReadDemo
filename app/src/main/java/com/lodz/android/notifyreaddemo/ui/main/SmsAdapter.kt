package com.lodz.android.notifyreaddemo.ui.main

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lodz.android.corekt.anko.bindView
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.bean.sms.SmsBean
import com.lodz.android.notifyreaddemo.utils.BankUtils
import com.lodz.android.notifyreaddemo.utils.TaobaoUtils
import com.lodz.android.pandora.widget.rv.recycler.BaseRecyclerViewAdapter

class SmsAdapter(context: Context) : BaseRecyclerViewAdapter<SmsBean>(context) {

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
        if (bean.smsType == SmsBean.TAOBAO_VC_TYPE) {
            holder.codeLayout.visibility = View.VISIBLE
            holder.cardNumberLayout.visibility = View.GONE
            holder.amountLayout.visibility = View.GONE
            holder.codeTv.setText(TaobaoUtils.getVerificationCode(bean))
        } else {
            holder.codeLayout.visibility = View.GONE
            holder.cardNumberLayout.visibility = View.VISIBLE
            holder.amountLayout.visibility = View.VISIBLE
            holder.cardNumberTv.setText(BankUtils.getCode(bean))
            holder.amountTv.setText(BankUtils.getAmount(bean))
        }
        holder.bodyTv.setText(bean.body)
    }

    private inner class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTv by bindView<TextView>(R.id.address_tv)
        val dateTv by bindView<TextView>(R.id.date_tv)
        val codeLayout by bindView<ViewGroup>(R.id.code_layout)
        val codeTv by bindView<TextView>(R.id.code_tv)
        val cardNumberLayout by bindView<ViewGroup>(R.id.card_number_layout)
        val cardNumberTv by bindView<TextView>(R.id.card_number_tv)
        val amountLayout by bindView<ViewGroup>(R.id.amount_layout)
        val amountTv by bindView<TextView>(R.id.amount_tv)
        val bodyTv by bindView<TextView>(R.id.body_tv)
    }

}
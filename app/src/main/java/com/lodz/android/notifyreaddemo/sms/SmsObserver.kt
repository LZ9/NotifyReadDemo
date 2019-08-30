package com.lodz.android.notifyreaddemo.sms

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler

class SmsObserver(val context: Context, handler: Handler) : ContentObserver(handler) {
    /*
        content://sms/inbox    收件箱
        content://sms/sent      已发送
        content://sms/draft    草稿
        content://sms/outbox    发件箱  (正在发送的信息)
        content://sms/failed    发送失败
        content://sms/queued    待发送列表  (比如开启飞行模式后，该短信就在待发送列表里)
    */

    private val SMS_URI_INBOX = "content://sms/inbox"

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(
                Uri.parse(SMS_URI_INBOX),
                arrayOf("_id", "address", "body", "read"),
                "body like ? and read=?",
                arrayOf("%快递%", "0"),
                "date desc"
            )
            if (cursor != null) {
                cursor.moveToFirst()
                if (cursor.moveToNext()) {
                    val smsbody = cursor.getString(cursor.getColumnIndex("body"))

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        /*
         *
         *
         *
      Cursor cursor = null;
        // 读取收件箱中含有某关键词的短信
        ContentResolver contentResolver = activity.getContentResolver();
        cursor = contentResolver.query(Uri.parse(SMS_URI_INBOX), new String[] {
        "_id", "address", "body", "read" }, "body like ? and read=?",
        new String[] { "%快递%", "0" }, "date desc");
        if (cursor != null) {
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
        String smsbody = cursor
        .getString(cursor.getColumnIndex("body"));
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(smsbody.toString());
        smsContent = m.replaceAll("").trim().toString();
        if (!TextUtils.isEmpty(smsContent)) {
        listener.onResult(smsContent);
        }

        }
        }
         *
         *
         *
         *
         *
         *

        //读取所有短信
        Uri uri = Uri.parse("content://sms/");
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id", "address", "body", "date", "type"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            int _id;
            String address;
            String body;
            String date;
            int type;
            while (cursor.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                _id = cursor.getInt(0);
                address = cursor.getString(1);
                body = cursor.getString(2);
                date = cursor.getString(3);
                type = cursor.getInt(4);
                map.put("names", body);

                Log.i("test", "_id=" + _id + " address=" + address + " body=" + body + " date=" + date + " type=" + type);
                data.add(map);
                //通知适配器发生改变
                sa.notifyDataSetChanged();
            }
        }
         *
         *
         *
         *
         * */
    }
}
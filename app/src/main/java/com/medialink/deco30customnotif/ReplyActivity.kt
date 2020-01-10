package com.medialink.deco30customnotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_reply.*

class ReplyActivity : AppCompatActivity() {

    companion object {
        private const val KEY_MESSAGE_ID = "key_message_id"
        private const val KEY_NOTIF_ID = "key_notif_id"

        fun getReplyMessageIntent(context: Context, notifId: Int, messageId: Int): Intent {
            val intent = Intent(context, ReplyActivity::class.java)
            intent.action = NotificationService.REPLY_ACTION
            intent.putExtra(KEY_MESSAGE_ID, messageId)
            intent.putExtra(KEY_NOTIF_ID, notifId)
            return intent
        }
    }

    private var mMessageId: Int = 0
    private var mNotifyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        val intent = intent
        if (NotificationService.REPLY_ACTION == intent.action) {
            mMessageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)
            mNotifyId = intent.getIntExtra(KEY_NOTIF_ID, 0)
        }

        btn_send.setOnClickListener { sendMessage(mNotifyId, mMessageId) }
    }

    private fun sendMessage(notifId: Int, messageId: Int) {
        updateNotification(notifId)

        val message = edt_reply.text.toString().trim() {it <= ' '}
        val root = window.decorView.rootView
        Snackbar.make(root, "Message ID: $notifId\nMessage: $message", Snackbar.LENGTH_SHORT).show()
        finish()
    }

    private fun updateNotification(notifyId: Int) {
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(getString(R.string.notif_title_sent))
            .setContentText(getString(R.string.notif_content_sent))

        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
        Materi ini akan dibahas lebih lanjut di modul extended
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NotificationService.CHANNEL_ID,
                NotificationService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            notifManager.createNotificationChannel(channel)

            builder.setChannelId(NotificationService.CHANNEL_ID)
        }

        val notification = builder.build()
        notifManager.notify(notifyId, notification)
    }
}

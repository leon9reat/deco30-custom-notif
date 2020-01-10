package com.medialink.deco30customnotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat

class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val KEY_NOTIFICATION_ID = "key_notification_id"
        private const val KEY_MESSAGE_ID = "key_message_id"

        fun getReplyMessageIntent(context: Context, notificationId: Int, messageId: Int): Intent {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java)
            intent.action = NotificationService.REPLY_ACTION
            intent.putExtra(KEY_NOTIFICATION_ID, notificationId)
            intent.putExtra(KEY_MESSAGE_ID, messageId)
            return intent
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (NotificationService.REPLY_ACTION == intent.action) {
            val message = NotificationService.getReplyMessage(intent)
            val messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)

            Toast.makeText(context, "Message ID: $messageId\nMessage: $message",
                Toast.LENGTH_SHORT).show()

            val notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1)
            updateNotification(context, notifyId)
        }
    }

    private fun updateNotification(context: Context, notifyId: Int) {
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(context.getString(R.string.notif_title_sent))
            .setContentText(context.getString(R.string.notif_content_sent))

        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
        Materi ini akan dibahas lebih lanjut di modul extended
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NotificationService.CHANNEL_ID,
                NotificationService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            notifManager.createNotificationChannel(channel)

            builder.setChannelId(NotificationService.CHANNEL_ID)
        }

        val notification = builder.build()
        notifManager.notify(notifyId, notification)
    }
}

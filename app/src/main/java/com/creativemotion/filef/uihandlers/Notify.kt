package com.creativemotion.filef.uihandlers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.creativemotion.filef.R

object Notify {
    private const val CHANNEL_ID = "com.creativemotion.filef"
    private const val CHANNEL_NAME = "FileF"

    /**
     * shows the notification with the continuous notification
     */
    fun createStartingNotification(context: Context, notificationId: Int){
        updateNotification(
            context,
            notificationId,
            createNotification(
                context,
                context.getString(R.string.search_notification_the_biggest_files),
                null
            )
        )
    }


    /**
     * shows the notification with text about finished search for the biggest files
     */
    fun createFinishingNotification(context: Context, notificationId: Int){
        updateNotification(
            context,
            notificationId,
            createNotification(
                context,
                context.getString(R.string.search_notification_the_biggest_files_finished),
                null
            )
        )
    }


    /**
     * basic notification
     *
     * @param context
     * @param title - title of the notification
     * @param content - subtext of notification
     * @return built notification
     */
    private fun createNotification(context: Context, title: String, content: String?): Notification {

        createChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setContentTitle(title)
        builder.setAutoCancel(false)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(content))

        builder.setSmallIcon(R.drawable.ic_folder_icon)
        builder.color = ContextCompat.getColor(context, R.color.black)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_DEFAULT
            builder.setCategory(Notification.CATEGORY_SERVICE)
        }

        return builder.build()
    }


    /**
     * replaces notification
     *
     * @param context
     * @param id - id of the notification
     * @param notification - notification to to be placed
     */
    private fun updateNotification(context: Context, id: Int, notification: Notification) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }

    /**
     * cancels notification by id
     *
     * @param context
     * @param id - of notification to cancel
     */
    fun cancelNotification(context: Context, id: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }


    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}
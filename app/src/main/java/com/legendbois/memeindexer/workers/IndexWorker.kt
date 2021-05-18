package com.legendbois.memeindexer.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.legendbois.memeindexer.ConstantsHelper
import com.legendbois.memeindexer.MainActivity
import com.legendbois.memeindexer.R
import com.legendbois.memeindexer.database.MemeFile
import com.legendbois.memeindexer.database.MemeFilesDatabase
import kotlinx.coroutines.delay
import java.util.*

class IndexWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    val notificationId = 69420
    val channelId = "Meme Indexer Service"
    var totalFiles: Int = 0
    var progressNumber: Int = 0
    var concurrentImages: Int = 0
    var deletedImages: Int = 0

    private val maxParallelRequests = 10
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private lateinit var notificationBuilder : NotificationCompat.Builder


    override suspend fun doWork(): Result {
        val memeFileDatabase = MemeFilesDatabase.getDatabase(this.applicationContext).memeFileDao
        totalFiles = memeFileDatabase.getUnindexedRowCount()
        var rawTotal: Int
        val intialContent = "Preparing Files..."
        val model = TextRecognition.getClient()
        initializeNotifBuilder()
        val startTime = SystemClock.elapsedRealtime()

        val foregroundInfo = createForegroundInfo(intialContent)
        setForeground(foregroundInfo)

        // Lazy man's paginations?
        while(totalFiles > 0){
            val memeFiles = memeFileDatabase.getUnindexedRows(maxParallelRequests)
            concurrentImages = memeFiles.size
            for(memeFile in memeFiles){
                updateImageText(model, memeFile)
            }
            while(concurrentImages > 0){
                //Log.d("MEMEINDEXERSERVICE", "Concurrent images $concurrentImages")
                delay(1000)
            }
            /* TODO: Decide whether deleting is good or shall we just store them with the failed message
                in ocrtext? Will help unnecessary rescans when user selects the same directory to be
                indexed in the future.*/
            val updateableMemes = memeFiles.mapNotNull {
                    if (it.ocrtext.isNullOrBlank() || it.ocrtext!!.length < 3){
                        deletedImages += 1
                        memeFileDatabase.delete(it)
                        null
                    }
                    else{
                        it
                    }
                }
            memeFileDatabase.update(*updateableMemes.toTypedArray())
            totalFiles = memeFileDatabase.getUnindexedRowCount()
            progressNumber += memeFiles.size
            rawTotal = progressNumber + totalFiles
            setForeground(
                ForegroundInfo(notificationId, notificationBuilder
                    .setContentTitle("Processing Images...")
                    .setContentText("$progressNumber/$rawTotal")
                    .setProgress(rawTotal, progressNumber, false)
                    .build()
                )
            )
            //Log.d("MEMEINDEXERSERVICE", "Remaining Files $totalFiles")
        }
        setForeground(ForegroundInfo(notificationId, notificationBuilder.setContentText("Cleaning up...").build()))
        //memeFileDatabase.deleteEmpty(deleteIds)

        sendSummaryNotif(startTime)

        return Result.success()
    }

    private fun updateImageText(model: TextRecognizer, memeFile: MemeFile) {
        try{
            val imageBitmap = BitmapFactory.decodeFile(memeFile.filepath)
            val image: InputImage = InputImage.fromBitmap(imageBitmap, 0)

            model.process(image)
                .addOnSuccessListener { visionText ->
                    memeFile.apply {
                        ocrtext = visionText.text.toLowerCase(Locale.ROOT)
                    }
                }
                .addOnFailureListener{
                    memeFile.apply {
                        ocrtext = ConstantsHelper.defaultFailedText
                    }
                }
                .addOnCompleteListener {
                    concurrentImages -= 1
                }
        }
        catch (e: java.lang.Exception){
            concurrentImages -= 1
            memeFile.apply {
                ocrtext = ConstantsHelper.defaultFailedText
            }
            //Log.d("MEMEINDEXERSERVICE", "OCRTEXT ${memeFile.ocrtext}")
        }
    }

    private fun sendSummaryNotif(startTime: Long){
        val id = channelId
        val title = applicationContext.getString(R.string.notification_title)
        // This PendingIntent can be used to cancel the worker
        val intent = Intent(applicationContext, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val elapsedTimeSeconds = (SystemClock.elapsedRealtime() - startTime)/1000
        val elapsedTime = String.format("%02d:%02d:%02d", (elapsedTimeSeconds / (60 * 60))%24, (elapsedTimeSeconds / 60)%60, elapsedTimeSeconds % 60)
        val filesSummary = """
            Total: $progressNumber
            Processed: ${progressNumber-deletedImages}
            Invalid: $deletedImages
        """.trimIndent()

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle("Total $progressNumber files processed")
            .setContentText("Elapsed time $elapsedTime")
            .setStyle(NotificationCompat.BigTextStyle().bigText(filesSummary))
            .setTicker(title)
            .setContentIntent(PendingIntent.getActivity(applicationContext, 0, intent, 0))
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground).build()

        notificationManager.notify(notificationId+1, notification)

    }

    private fun initializeNotifBuilder(){
        val id = channelId
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.notification_cancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        notificationBuilder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle("Processing Images")
            .setTicker(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
    }


    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(content: String): ForegroundInfo {


        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = notificationBuilder.setContentTitle(content).build()

        return ForegroundInfo(notificationId, notification)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, R.string.notification_channel_name.toString(), NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}

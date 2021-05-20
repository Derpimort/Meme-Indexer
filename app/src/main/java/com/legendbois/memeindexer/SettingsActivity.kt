package com.legendbois.memeindexer

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.legendbois.memeindexer.MiscHelper.getStartDelay
import com.legendbois.memeindexer.workers.IndexWorker
import kotlinx.android.synthetic.main.settings_activity.*
import java.util.concurrent.TimeUnit

class SettingsActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener{

    private lateinit var  sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(settings_toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // TODO: why so redundant?
        sharedPrefManager = SharedPrefManager.getInstance(this)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == SharedPrefManager.KEY_SCAN_BOOL){
            val scanBool = sharedPrefManager.scanBool
            if(scanBool){
                schedulePeriodicWork()
            }
            else{
                cancelAllPeriodicWork()
            }
        }
        if(key == SharedPrefManager.KEY_SCAN_TIME){
            schedulePeriodicWork()
        }
    }


    fun schedulePeriodicWork(){
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val workManager = WorkManager.getInstance(this)

        val scanTime = sharedPrefManager.scanTime.split(":")

        try{
            val workerRequest = PeriodicWorkRequestBuilder<IndexWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(getStartDelay(scanTime[0].toInt(), scanTime[1].toInt())/60, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(ConstantsHelper.PERIODIC_WORKREQ_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                ConstantsHelper.WORKMANAGER_UID,
                ExistingPeriodicWorkPolicy.REPLACE,
                workerRequest
            )
        }
        catch (e: Exception){
            Toast.makeText(this, "Some error occurred please check if you have entered the right scheduled time", Toast.LENGTH_LONG).show()
        }

    }

    fun cancelAllPeriodicWork(){
        WorkManager.getInstance(this).cancelAllWorkByTag(ConstantsHelper.PERIODIC_WORKREQ_TAG)
    }
}

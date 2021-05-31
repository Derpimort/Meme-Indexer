package com.legendbois.memeindexer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import com.legendbois.memeindexer.ui.main.SearchMemesFragment
import com.legendbois.memeindexer.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var tabs: TabLayout
    private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3,
        R.string.tab_text_4
    )

    companion object{
        val sdkVersion = Build.VERSION.SDK_INT
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Finalize a good font
        TypefaceUtil.overrideFont(applicationContext,"fonts/noto_sans_light.ttf")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(pos: Int) {
                updateToolbarText(TAB_TITLES[pos])
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                tabs.visibility = View.VISIBLE
            }
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        tabs = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        setupTabIcons()

        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.getPermissions(this)

        }
        /*
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }*/

        // uncomment to nuke database
        /*val db = MemeFilesDatabase.getDatabase(applicationContext)
        db.clearAllTables()

        val db = UsageHistoryDatabase.getDatabase(applicationContext)
        db.clearAllTables()*/

        // uncomment to log total number of actions in db
        /*val db = UsageHistoryDatabase.getDatabase(applicationContext)
        Log.d("MainActivity", "Actions in db: ${db.usageHistoryDao.getRowCount()}")*/

        // uncomment to log total number of memes in db
        /*val db = MemeFilesDatabase.getDatabase(applicationContext)
        Log.d("MainActivity", "Memes in db: ${db.memeFileDao.getRowCount()}")*/

        // uncomment to log all usagehistory
        /*lifecycleScope.launch {
            val db = UsageHistoryDatabase.getDatabase(applicationContext).usageHistoryDao
            for (usage in db.getAll()){
                Log.d(TAG, "UsageHistory: ${usage.id}, ${usage.pathOrQuery}," +
                        "${usage.actionId}, ${usage.extraInfo}," +
                        "${usage.createdAt}, ${usage.modifiedAt}")
            }
        }*/

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        checkDisclaimer(SharedPrefManager.getInstance(this))

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionHelper.PERMISSION_CODE) {
            if (!PermissionHelper.hasPermissions(this)) {
                Toast.makeText(this, "App needs storage permissions", Toast.LENGTH_LONG) .show()
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.feedback_menuitem ->{
                sendFeedback()
                true
            }
            R.id.aboutus_menuitem ->{
                val aboutUsIntent = Intent(this@MainActivity, AboutUsActivity::class.java)
                startActivity(aboutUsIntent)
                true
            }
            R.id.settings_menuitem ->{
                val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setupTabIcons(){
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_image_search_24px)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_image_scan_24px)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_history_24px)
    }

    fun updateToolbarText(text: Int){
        // TODO: Change to better animation or use layoutanimation
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) { }
            override fun onAnimationStart(animation: Animation?) { }
            override fun onAnimationRepeat(animation: Animation?) {
                toolbar_fragment_title.text = getString(text)
            }
        })

        toolbar_fragment_title.startAnimation(anim)
    }

    fun sendFeedback(){
        val alertDialog = AlertDialog.Builder(this, R.style.AlertDialogBase)
        val dialogView = layoutInflater.inflate(R.layout.popup_feedback, null)
        alertDialog.setTitle("Send Feedback")
        // Created a layout just in case
        alertDialog.setView(dialogView)

        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which ->
            dialog.dismiss()
        }

        alertDialog.setPositiveButton(
            "Send"
        ){ dialog, i ->
            val feedbackText = dialogView.findViewById<EditText>(R.id.feedback_text)
            if(feedbackText.text.isNotBlank()){

                MiscHelper.sendMail(context = this,
                                    emailSubject = "Feedback for Meme Indexer",
                                    emailText = feedbackText.text.toString())
            }
        }
        alertDialog.create().show()

    }

    fun checkDisclaimer(sharedPrefManager: SharedPrefManager){
        if(sharedPrefManager.disclaimerBool){
            return
        }
        else{
            val dialogView = layoutInflater.inflate(R.layout.popup_disclaimer, null)

            val alertDialog = AlertDialog.Builder(this, R.style.AlertDialogBase)
                .setTitle("Disclaimer Of Warranty")
                .setView(dialogView)
                .setCancelable(false)


            alertDialog.setNegativeButton(
                "Disagree"
            ) { dialog, which ->
                finish()
            }
            alertDialog.setPositiveButton(
                "Agree"
            ){ dialog, i ->
                sharedPrefManager.disclaimerBool = true
            }

            alertDialog.create().show()
        }

    }
}

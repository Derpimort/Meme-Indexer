package com.legendbois.memeindexer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.legendbois.memeindexer.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Finalize a good font
        TypefaceUtil.overrideFont(applicationContext,"fonts/arvo.ttf")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

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
        db.clearAllTables()*/


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
            else -> super.onOptionsItemSelected(item)
        }
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

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data= Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("jatinsaini580@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback for Meme Indexer")
                intent.putExtra(Intent.EXTRA_TEXT, feedbackText.text)
                try{
                    startActivity(Intent.createChooser(intent,"Send Mail..."))
                }
                catch ( ex : android.content.ActivityNotFoundException){
                    Toast.makeText(applicationContext,"There are no email clients installed",Toast.LENGTH_SHORT).show()
                }
            }
        }
        alertDialog.create().show()

    }
}
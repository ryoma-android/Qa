package com.example.qa_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString(NameKEY, "")
        nameText.setText(name)

        mDatabaseReference = FirebaseDatabase.getInstance().reference

        title = getString(R.string.settings_titile)

        changeButton.setOnClickListener { v ->
            val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Snackbar.make(v, getString(R.string.no_login_user), Snackbar.LENGTH_LONG).show()

            } else {
                val name2 = nameText.text.toString()
                val userRef = mDatabaseReference.child(UsersPATH).child(user.uid)
                val data = HashMap<String, String>()
                data["name"] = name2
                userRef.setValue(data)

                val sp2 = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val editor = sp2.edit()
                editor.putString(NameKEY, name)
                editor.commit()

                Snackbar.make(v, getString(R.string.change_disp_name), Snackbar.LENGTH_LONG).show()

            }
        }

        logoutButton.setOnClickListener { v ->
            FirebaseAuth.getInstance().signOut()
            nameText.setText("")
            Snackbar.make(v, getString(R.string.logout_complete_message), Snackbar.LENGTH_LONG)
                .show()
        }
    }
}
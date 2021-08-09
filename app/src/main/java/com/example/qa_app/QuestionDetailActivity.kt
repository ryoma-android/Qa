package com.example.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.activity_question_send.*

class QuestionDetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mFavorites: DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<*, *>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {

                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)

            mAdapter.notifyDataSetChanged()

        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    private val mmEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<*, *>

            val Uid = dataSnapshot.key ?: ""

            val user = FirebaseAuth.getInstance().currentUser

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            val favorites = dataBaseReference.child(FavoritesPATH).child(user!!.uid)
                .child(mQuestion.questionUid)


            val uid = map["uid"] as? String ?: ""
            val questionUid = map["questionUid"] as? String ?: ""

            }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question


        title = mQuestion.title

        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()


        fab.setOnClickListener {

            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {

                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {

                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)

            }
        }

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            favorite.visibility = View.INVISIBLE
            pushedFavorite.visibility = View.INVISIBLE

        } else {
            favorite.visibility = View.VISIBLE
            pushedFavorite.visibility = View.INVISIBLE
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)

        val mDatabasereference = FirebaseDatabase.getInstance().reference
        mFavorites = mDatabasereference.child(FavoritesPATH).child(user!!.uid)
            .child(mQuestion.questionUid)
        mFavorites.addChildEventListener(mmEventListener)


        favorite.setOnClickListener(this)
        pushedFavorite.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        if (v.id == R.id.favorite) {
            pushedFavorite.visibility = View.VISIBLE
            favorite.visibility = View.INVISIBLE

            val user = FirebaseAuth.getInstance().currentUser

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            val favorites = dataBaseReference.child(FavoritesPATH).child(user!!.uid)
                .child(mQuestion.questionUid)

            val data = HashMap<String, String>()

            data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            data["questionUid"] = mQuestion.questionUid

            favorites.push().setValue(data)


        } else if (v.id == R.id.pushedFavorite) {
            favorite.visibility = View.VISIBLE
            pushedFavorite.visibility = View.INVISIBLE

            val user = FirebaseAuth.getInstance().currentUser
            val dataBaseReference = FirebaseDatabase.getInstance().reference
            val favorites = dataBaseReference.child(FavoritesPATH).child(user!!.uid)
                .child(mQuestion.questionUid)

            favorites.removeValue()
        }
    }

}




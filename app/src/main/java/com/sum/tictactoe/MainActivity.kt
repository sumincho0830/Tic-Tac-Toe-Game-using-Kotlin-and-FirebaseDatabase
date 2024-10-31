package com.sum.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.sum.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Bindings
    private lateinit var binding: ActivityMainBinding

    // database instance
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference // reference to the root node of the Database

    var myEmail: String? = null

    private var mFirebaseAnalytics: FirebaseAnalytics ? = null // FirebaseAnalytics 타입의 변수 선언



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        var b: Bundle = intent.extras!! //non-null assertion operator is used to tell the compiler that a particular nullable variable or expression is guaranteed to be non-null at that point, even if its type allows for null values.
        //  If the value actually is null at runtime, it will throw a NullPointerException.
        // intent.extras is originally a nullable value of type Bundle?
        // intent variable is a property of an Activity class

        FirebaseApp.initializeApp(this);
        myEmail = b.getString("email")
        IncommingCalls()

    }

    protected fun buClick(view: View){
        val buSelected = view as Button // cast as type Button?
        var cellID = 0

        when(buSelected.id){ // the casted Button object's id
            R.id.bu1 -> cellID = 1
            R.id.bu2 -> cellID = 2
            R.id.bu3 -> cellID = 3
            R.id.bu4 -> cellID = 4
            R.id.bu5 -> cellID = 5
            R.id.bu6 -> cellID = 6
            R.id.bu7 -> cellID = 7
            R.id.bu8 -> cellID = 8
            R.id.bu9 -> cellID = 9
        }

        Toast.makeText(this, "ID: "+cellID, Toast.LENGTH_SHORT).show()

        myRef.child("PlayerOnline").child(sessionID!!).child(cellID.toString()).setValue(myEmail)
    }

    var player1 = java.util.ArrayList<Int>()
    var player2 = java.util.ArrayList<Int>()
    var ActivePlayer =1

    fun PlayGame(cellID:Int, buSelected:Button){
        if(ActivePlayer == 1){
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellID) //cell of the tictactoe grid
            ActivePlayer = 2
        }else{
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.darkgreen)
            player2.add(cellID)
            ActivePlayer = 1

        }

        buSelected.isEnabled = false
        CheckWinner() //승자가 있는지 여부 확인 (매 수를 둘 때 마다 확인)
    }

    fun CheckWinner(){
        // this function lays out all the possible winning combinations
        var winner = -1

        // row 1
        if(player1.contains(1) && player1.contains(2) && player1.contains(3)){
            winner = 1
        }
        if(player2.contains(1) && player2.contains(2) && player2.contains(3)){
            winner = 2
        }

        // row 2

        if(player1.contains(4) && player1.contains(5) && player1.contains(6)){
            winner = 1
        }
        if(player2.contains(4) && player2.contains(5) && player2.contains(6)){
            winner = 2
        }

        // row 3

        if(player1.contains(7) && player1.contains(8) && player1.contains(9)){
            winner = 1
        }
        if(player2.contains(7) && player2.contains(8) && player2.contains(9)){
            winner = 2
        }


        // col 1
        if(player1.contains(1) && player1.contains(4) && player1.contains(7)){
            winner = 1
        }
        if(player2.contains(1) && player2.contains(4) && player2.contains(7)){
            winner = 2
        }

        // col 2

        if(player1.contains(2) && player1.contains(5) && player1.contains(8)){
            winner = 1
        }
        if(player2.contains(2) && player2.contains(5) && player2.contains(8)){
            winner = 2
        }

        // col 3

        if(player1.contains(3) && player1.contains(6) && player1.contains(9)){
            winner = 1
        }
        if(player2.contains(3) && player2.contains(6) && player2.contains(9)){
            winner = 2
        }

        // diagonal 1
        if(player1.contains(1) && player1.contains(5) && player1.contains(9)){
            winner = 1
        }
        if(player2.contains(1) && player2.contains(5) && player2.contains(9)){
            winner = 2
        }

        // diagonal 2

        if(player1.contains(3) && player1.contains(5) && player1.contains(7)){
            winner = 1
        }
        if(player2.contains(3) && player2.contains(5) && player2.contains(7)){
            winner = 2
        }


        if(winner != -1){ // if a valid value has been assigned
            if(winner == 1){
                Toast.makeText(this, "Player 1 wins", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "Player 2 wins", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun AutoPlay(cellID: Int){

        var buSelected: Button? //type button, nullable
        when(cellID){
            1 -> buSelected = binding.bu1
            2 -> buSelected = binding.bu2
            3 -> buSelected = binding.bu3
            4 -> buSelected = binding.bu4
            5 -> buSelected = binding.bu5
            6 -> buSelected = binding.bu6
            7 -> buSelected = binding.bu7
            8 -> buSelected = binding.bu8
            9 -> buSelected = binding.bu9
            else->{
                buSelected = binding.bu1
            }
        }

        PlayGame(cellID, buSelected)
    }

    protected fun buRequestEvent(view:android.view.View){
        var userDemail = binding.etEmail.text.toString() //retrieve the text from the EditText
        myRef.child("Users").child(SplitString(userDemail)).child("Request").push().setValue(myEmail)

        PlayerOnline(SplitString(myEmail!!)+SplitString(userDemail))
        PlayerSymbol = "X"
    }

    protected fun buAcceptEvent(view:android.view.View){
        var userDemail = binding.etEmail.text.toString()
        myRef.child("Users").child(SplitString(userDemail)).child("Request").push().setValue(myEmail)

        PlayerOnline(SplitString(userDemail)+SplitString(myEmail!!))
        PlayerSymbol = "O"
    }


    var sessionID:String? = null
    var PlayerSymbol:String? = null

    fun PlayerOnline(sessionID:String){
        this.sessionID = sessionID
        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionID)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    try{
                        player1.clear()
                        player2.clear()

                        val td = snapshot!!.value as HashMap<String, Any>

                        if(td != null){

                            var value:String

                            for (key in td.keys){
                                value = td[key] as String

                                if(value != myEmail){
                                    ActivePlayer = if(PlayerSymbol === "X") 1 else 2

                                }else{
                                    ActivePlayer = if(PlayerSymbol === "O") 2 else 1
                                }

                                AutoPlay(key.toInt()) // {cellID : username} 형태로 맵에 저장되어있다.
                            }
                        }
                    }catch (ex:Exception){}
                }
            })
    }


    fun SplitString(str: String): String { // returns a String type object
        var split = str.split("@")
        return split[0] // return the first unique username of the email
    }

}
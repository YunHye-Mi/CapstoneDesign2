package com.example.capstonedesign2.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonedesign2.data.entities.User
import com.example.capstonedesign2.data.remote.AuthService
import com.example.capstonedesign2.data.remote.RegisterRequest
import com.example.capstonedesign2.databinding.ActivityGeneralBinding
import com.example.capstonedesign2.ui.MainActivity
import com.google.gson.Gson

class GeneralActivity : AppCompatActivity(), RegisterView {
    lateinit var binding : ActivityGeneralBinding
    private var access = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면이 시작되었을 때 닉네임 입력창에 포커스를 맞춰 키보드가 올라오도록 함.
        binding.nickEt.clearFocus()
        binding.nickEt.requestFocus()
        var inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.nickEt, InputMethodManager.SHOW_IMPLICIT)


        // 닉네임 입력 후 엔터 키를 누르면 키보드가 내려감.
        binding.nickEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        binding.nickEt.setOnEditorActionListener { view, i, event ->
            if (event != null && (event.action == KeyEvent.KEYCODE_ENTER || i == EditorInfo.IME_ACTION_DONE)) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.nickEt.windowToken, 0)
                true
            } else {
                false
            }
        }


        // 유저에 등록하기 위해 access token과 access token을 갱신하기 위해 refresh token을 가져옴.
        val spf = getSharedPreferences("token", MODE_PRIVATE)
        access = spf.getString("access", "").toString()

        writeView()
    }

    private fun writeView(){
        binding.addInfoActivity.viewTreeObserver.addOnGlobalLayoutListener {
            if (intent.getStringExtra("setName").equals("setName")) {
                binding.nickTv.text = "변경하기"
                if (binding.nickEt.text.toString().isNotEmpty()) {
                    binding.startTv.setTextColor(Color.parseColor("#754C24"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#FDC536"))

                    binding.startTv.setOnClickListener {
                        finish()
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    binding.startTv.setTextColor(Color.parseColor("#666666"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#C8C8C8"))
                    binding.startTv.setOnClickListener {
                        Toast.makeText(this, "닉네임 입력은 필수 입니다", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                if(binding.nickEt.text.toString().isNotEmpty()) {
                    binding.startTv.setTextColor(Color.parseColor("#754C24"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#FDC536"))

                    binding.startTv.setOnClickListener {
                        val name = binding.nickEt.text.toString()
                        val user = User(0, access, name, null, "General")
                        val gson = Gson()
                        val userJson = gson.toJson(user)
                        val userSpf = getSharedPreferences("currentUser", MODE_PRIVATE)
                        val editor = userSpf.edit()
                        editor.apply {
                            putString("User", userJson)
                        }

                        editor.commit()

//                        var authService = AuthService()
//                        var registerRequest = RegisterRequest(name, null)
//                        authService.register("Barer $access", registerRequest)

                        var intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("user", "General")
                        finish()
                        startActivity(intent)
                    }
                }
                else {
                    binding.startTv.setTextColor(Color.parseColor("#666666"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#C8C8C8"))
                    binding.startTv.setOnClickListener {
                        Toast.makeText(this,"닉네임 입력은 필수 입니다", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // edittext 이외 영역 클릭 시 키보드를 숨기도록 재정의
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name.startsWith("android.webkit.")) {
            var scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) {
                hideKeyBoard()
            }
            binding.nickEt.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    // 키보드 숨기기
    private fun hideKeyBoard(){
        var inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onRegisterSuccess(code: Int) {
       Toast.makeText(this, "등록 완료", Toast.LENGTH_LONG)
    }

    override fun onRegisterFailure(code: Int, message: String) {
        Log.d("Register/Failure", message)
    }
}
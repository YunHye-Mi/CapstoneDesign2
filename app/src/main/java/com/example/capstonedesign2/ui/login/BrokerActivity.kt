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
import com.example.capstonedesign2.data.remote.RefreshRequest
import com.example.capstonedesign2.data.remote.RegisterRequest
import com.example.capstonedesign2.databinding.ActivityBrokerBinding
import com.example.capstonedesign2.ui.MainActivity
import com.google.gson.Gson

class BrokerActivity : AppCompatActivity(), RegisterView, RefreshView {
    lateinit var binding : ActivityBrokerBinding
    private var authService = AuthService()
    private var access = ""
    private var refresh = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrokerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authService.setRegisterView(this)
        authService.setRefreshView(this)

        val userSpf = getSharedPreferences("currentUser", MODE_PRIVATE)
        val userJson = userSpf.getString("User", "")
        val gson = Gson()
        val user = gson.fromJson(userJson, User::class.java)

        if (user != null) {
            if (user.accessToken != "" && user.refreshToken != "" && user.nickname != "" && user.registerNumber!!.isNotEmpty()) {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

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

        binding.intermediaryEt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        binding.intermediaryEt.setOnEditorActionListener { view, i, event ->
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
        refresh = spf.getString("refresh", "").toString()

        writeView()
    }

    private fun writeView(){
        binding.addInfoActivity.viewTreeObserver.addOnGlobalLayoutListener {
            if (intent.getStringExtra("IntermediaryRegister").equals("register")) {
                binding.startTv.text = "등록하기"
                if (binding.nickEt.text.toString().isNotEmpty()
                    && binding.intermediaryEt.text.toString().isNotEmpty()) {
                    binding.startTv.setTextColor(Color.parseColor("#754C24"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#FDC536"))

                    binding.startTv.setOnClickListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("user", "Broker")
                        startActivity(intent)
                    }
                } else {
                    binding.startTv.setTextColor(Color.parseColor("#666666"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#C8C8C8"))
                }
            } else {
                if(binding.nickEt.text.toString().isNotEmpty() && binding.intermediaryEt.text.toString().isNotEmpty()) {
                    binding.startTv.setTextColor(Color.parseColor("#754C24"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#FDC536"))

                    binding.startTv.setOnClickListener {
                        finish()

                        Toast.makeText(this, "등록 완료", Toast.LENGTH_LONG).show()
                        finish()
                        val name = binding.nickEt.text.toString()
                        val role  = "Broker"
                        val user = User("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwiaWF0IjoxNjg1Njg5MTg0LCJpc3MiOiJNb3JnYW4iLCJleHAiOjE2ODU3NzU1ODQsInN1YiI6InVzZXJJbmZvIn0.ZqiwGq1ALKWoI_zPa3O1trWWLza5Lob4jjxYsivaumg",
                            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwiaWF0IjoxNjg1Njg5MTg0LCJpc3MiOiJNb3JnYW4iLCJleHAiOjE2ODgyODExODQsInN1YiI6InVzZXJJbmZvIn0.qQHuaeXC1mhmj_KEa_OLgT61s6V2CzOvIal5-joLo8I", name, binding.intermediaryEt.text.toString(), role)
                        val gson = Gson()
                        val userJson = gson.toJson(user)
                        val userSpf = getSharedPreferences("currentUser", MODE_PRIVATE)
                        val editor = userSpf.edit()
                        editor.apply {
                            putString("User", userJson)
                        }

                        editor.apply()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        //정식으로 시장에 앱을 출시하지 않아 kakao에서 sso_idf를 발급해주지 않아 사용자 구분이 안 되므로 브로커는 임의로 만든 정보를 명시적으로 하여 접속 중
//                        authService.register(access, RegisterRequest(binding.nickEt.text.toString(), binding.intermediaryEt.text.toString()))
                    }
                }
                else {
                    binding.startTv.setTextColor(Color.parseColor("#666666"))
                    binding.startTv.setBackgroundColor(Color.parseColor("#C8C8C8"))
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
            binding.intermediaryEt.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    // 키보드 숨기기
    private fun hideKeyBoard(){
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onRegisterSuccess(message: String, data: Boolean) {
        Toast.makeText(this, "등록 완료", Toast.LENGTH_LONG).show()
        finish()
        val name = binding.nickEt.text.toString()
        var role  = ""
        role = if (data) {
            "Broker"
        } else {
            "General"
        }
        val user = User(access, refresh, name, binding.intermediaryEt.text.toString(), role)
        val gson = Gson()
        val userJson = gson.toJson(user)
        val userSpf = getSharedPreferences("currentUser", MODE_PRIVATE)
        val editor = userSpf.edit()
        editor.apply {
            putString("User", userJson)
        }

        editor.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onRegisterFailure(code: Int, message: String) {
        when (code) {
            401 -> {
                Log.d("Register/Failure", "$code/$message")
                authService.refresh(RefreshRequest("Bearer $refresh"))
            }
            403 -> Log.d("Register/Failure", "$code/$message")
        }
    }

    override fun onRefreshSuccess(accessToken: String, refreshToken: String) {
        val updateUser = User(accessToken, refreshToken, binding.nickEt.text.toString(), binding.intermediaryEt.text.toString(), "Broker")
        val gson = Gson()
        val userJson = gson.toJson(updateUser)
        val userSpf = getSharedPreferences("currentUser", MODE_PRIVATE)
        val editor = userSpf.edit()
        editor.apply {
            putString("User", userJson)
        }

        startActivity(intent)

        editor.apply()

        authService.register(accessToken, RegisterRequest(binding.nickEt.text.toString(), binding.intermediaryEt.text.toString()))

        Log.d("ReRegister/Success", "${updateUser.nickname}/${updateUser.role}")
    }

    override fun onRefreshFailure(code: Int, message: String) {
        when (code) {
            401 -> {
                Log.d("Refresh/Failure", "$code/$message")
                authService.refresh(RefreshRequest(refresh))
            }
            403 -> Log.d("Refresh/Failure", "$code/$message")
        }
    }
}
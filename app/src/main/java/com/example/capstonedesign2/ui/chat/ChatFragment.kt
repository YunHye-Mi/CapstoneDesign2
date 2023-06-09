package com.example.capstonedesign2.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstonedesign2.data.entities.User
import com.example.capstonedesign2.data.remote.*
import com.example.capstonedesign2.databinding.FragmentChatBinding
import com.example.capstonedesign2.ui.login.RefreshView
import com.google.gson.Gson

class ChatFragment() : Fragment(), ChatView, RefreshView {
    lateinit var binding: FragmentChatBinding
    lateinit var chatRVAdapter: ChatRVAdapter
    lateinit var user: User
    private var authService = AuthService()
    private var chatView = ChatService()
    private var access = false
    private var roomList = ArrayList<ChatRoomResult>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        chatView.setChatView(this)
        authService.setRefreshView(this)

        var userSpf = this.requireContext().getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE)
        var currentUser = userSpf.getString("User", "")
        var gson = Gson()
        user = gson.fromJson(currentUser, User::class.java)

        chatRVAdapter = ChatRVAdapter(roomList)
        binding.chatRoomRv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.chatRoomRv.adapter = chatRVAdapter
        chatView.getChatRoom(user.accessToken)


        var intent = Intent(this.context, ChatActivity::class.java)

        chatRVAdapter.setMyItemClickListener(object : ChatRVAdapter.MyItemClickListener {
            override fun onItemClick(room: ChatRoomResult) {
                intent.putExtra("chatRoomId", room.id)
                intent.putExtra("brokerName", room.broker)
                startActivity(intent)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onCreateChatSuccess(roomResult: ChatRoomResult) {
        TODO("Not yet implemented")
    }

    override fun onCreateChatFailure(code: Int, message: String) {
        TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChatSuccess(roomResult: ArrayList<ChatRoomResult>?) {
        if (roomResult != null) {
            for (i in roomResult)
                roomList.add(i)
            Log.d("GetChatRoom/Success", "채팅방 목록 가져오기 성공")
            chatRVAdapter.notifyDataSetChanged()
            access = true
        }
    }

    override fun onChatFailure(code: Int, message: String) {
        when (code) {
            401 -> {
                Log.d("GetChatRoom/Failure", "$code/$message")
                authService.refresh(RefreshRequest(user.refreshToken))
            }
            403 -> Log.d("GetChatRoom/Failure", "$code/$message")
        }
    }

    override fun onBeforeChatSuccess(chatList: ArrayList<ChatMessage>?) {
        TODO("Not yet implemented")
    }

    override fun onBeforeChatFailure(code: Int, message: String) {
        TODO("Not yet implemented")
    }

    override fun onRefreshSuccess(accessToken: String, refreshToken: String) {
        val updateUser = User(accessToken, refreshToken, user.nickname, user.registerNumber, user.role)
        this.onAttach(this.requireContext())
            .let {
                val gson = Gson()
                val userJson = gson.toJson(updateUser)
                val userSpf = this.requireActivity().getSharedPreferences("currentUser", AppCompatActivity.MODE_PRIVATE)
                val editor = userSpf.edit()
                editor.apply {
                    putString("User", userJson)
                }

                editor.apply()

                chatView.setChatView(this)
                chatView.getChatRoom(accessToken)
                Log.d("ReGetRoom", "${updateUser.accessToken}/${updateUser.refreshToken}")
            }
    }

    override fun onRefreshFailure(code: Int, message: String) {
        when (code) {
            401 -> {
                Log.d("Refresh/Failure", "$code/$message")
                authService.refresh(RefreshRequest(user.refreshToken))
            }
            403 -> Log.d("Refresh/Failure", "$code/$message")
        }
    }
}

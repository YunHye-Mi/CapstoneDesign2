package com.example.capstonedesign2.data.remote

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class AuthResponse(
    @SerializedName(value = "status")val status: Int,
    @SerializedName(value = "success") val success: Boolean,
    @SerializedName(value = "message") val message: String,
    @SerializedName(value = "accessToken") val accessToken: String,
    @SerializedName(value = "refreshToken") val refreshToken: String
)

data class AuthRequest(
    @SerializedName(value = "provider") var provider: String,
    @SerializedName(value = "ssoToken") var ssoToken: String
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class RegisterRequest(
    @SerializedName(value = "name") var name: String,
    @SerializedName(value = "registerNumber") var registerNumber: String?
)

data class RegisterResponse(
    @SerializedName(value = "status") var status: Int,
    @SerializedName(value = "success") var success: Boolean,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "data") var data: Role
)

data class Role(
    @SerializedName(value = "isBroker") val isBroker: Boolean
)
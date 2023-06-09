package com.example.capstonedesign2.data.remote

import retrofit2.Call
import retrofit2.http.*

interface RoomRetrofitInterface {
    @Headers("Content-Type: application/json")
    @GET("/api/room")
    fun getZoomOUt(
        @Query("lbLat") lbLat: Double, // 좌측 하단 위도
        @Query("lbLng") lbLng: Double, // 좌측 하단 경도
        @Query("rtLat") rtLat: Double, // 우측 상단 위도
        @Query("rtLng") rtLng: Double, // 우측 상단 경도
        @Query("zoomin") zoomIn: Int, // 0이면 줌아웃, 1이면 줌인
        @Query("minRent") minRent: Double,
        @Query("maxRent") maxRent: Double,
        @Query("type") type: Int, // 전월세 0과 1로 구분
        @Query("minSize") minSize: Double,
        @Query("maxSize") maxSize: Double,
        @Query("minDeposit") minDeposit: Int,
        @Query("maxDeposit") maxDeposit: Int,
        @Query("minManage") minManage: Double,
        @Query("maxManage") maxManage: Double,
        @Query("minFloor") minFloor: Int?,
        @Query("maxFloor") maxFloor: Int?,
    ): Call<ArrayList<MapZoomOut>>

    @Headers("Content-Type: application/json")
    @GET("/api/room")
    fun getZoomIn(
        @Query("lbLat") lbLat: Double, // 좌측 하단 위도
        @Query("lbLng") lbLng: Double, // 좌측 하단 경도
        @Query("rtLat") rtLat: Double, // 우측 상단 위도
        @Query("rtLng") rtLng: Double, // 우측 상단 경도
        @Query("zoomin") zoomIn: Int,
        @Query("minRent") minRent: Double,
        @Query("maxRent") maxRent: Double,
        @Query("type") type: Int, // 전월세 0과 1로 구분
        @Query("minSize") minSize: Double,
        @Query("maxSize") maxSize: Double,
        @Query("minDeposit") minDeposit: Int,
        @Query("maxDeposit") maxDeposit: Int,
        @Query("minManage") minManage: Double,
        @Query("maxManage") maxManage: Double,
        @Query("minFloor") minFloor: Int,
        @Query("maxFloor") maxFloor: Int
    ): Call<ArrayList<MapZoomIn>>

    @Headers("Content-Type: application/json")
    @GET("/api/detail")
    fun getRoomDetail(@Query("id") id: String): Call<List<Room>>
}
package com.daarba.flashalert.helper

import android.content.Context
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.*

object FlashLightManager {

    private var camera: Camera? = null
    private var isBlinking = false
    private var blinkJob: Job? = null
    private var mCameraManager: CameraManager? = null
    private var mCameraId: String? = null
    private var isFlashLightOn = false

    fun openFlashLight(context: Context, isChecked: Boolean) {
        handleCamera2(context, isChecked)
    }

    private fun toggleFlashlight(context: Context) {
        isFlashLightOn = !isFlashLightOn
        handleCamera2(context, isFlashLightOn)
    }

    private fun handleCamera2(context: Context, isChecked: Boolean) {
        try {
            if (mCameraManager == null) {
                mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                mCameraId = mCameraManager?.cameraIdList?.get(0)
            }
            mCameraId?.let {
                mCameraManager?.setTorchMode(it, isChecked)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startBlinking(context: Context, speed:Long) {
        if (isBlinking){
            stopBlinking()
            return
        }
        isBlinking = true
        blinkJob = CoroutineScope(Dispatchers.Default).launch {
            while (isBlinking) {
                if (SessionManager.getBool(SessionManager.FLASH_MODE_RHYTHM, true)) {
                    toggleFlashlight(context)
                    delay(speed)
                    toggleFlashlight(context)
                    delay(speed)
                }else{
                    toggleFlashlight(context)
                }
            }
            stopBlinking()
        }
    }

    fun stopBlinking() {
        try {
            isBlinking = false
            blinkJob?.cancel()
            isFlashLightOn = false
            mCameraId?.let {
                mCameraManager?.setTorchMode(it, false)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun flashOnce(context: Context,speed:Long) {
        CoroutineScope(Dispatchers.Default).launch {
            handleCamera2(context, true)
            delay(200)
            handleCamera2(context, false)
        }
    }


}
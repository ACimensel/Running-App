package com.example.artun_cimensel_myruns4.classifier

object Globals {
    // Debugging tag
    const val TAG = "MyRuns"


    const val ACCELEROMETER_BUFFER_CAPACITY = 2048
    const val ACCELEROMETER_BLOCK_CAPACITY = 64

    const val ACTIVITY_ID_STANDING = 0
    const val ACTIVITY_ID_WALKING = 1
    const val ACTIVITY_ID_RUNNING = 2
    const val ACTIVITY_ID_OTHER = 2

    const val SERVICE_TASK_TYPE_COLLECT = 0
    const val SERVICE_TASK_TYPE_CLASSIFY = 1

    const val ACTION_MOTION_UPDATED = "MYRUNS_MOTION_UPDATED"

    const val CLASS_LABEL_STANDING = "standing"
    const val CLASS_LABEL_WALKING = "walking"
    const val CLASS_LABEL_RUNNING = "running"

    const val FEAT_FFT_COEF_LABEL = "fft_coef_"
    const val FEAT_MAX_LABEL = "max"
    const val FEAT_SET_NAME = "accelerometer_features"

    const val NOTIFICATION_ID = 1
}
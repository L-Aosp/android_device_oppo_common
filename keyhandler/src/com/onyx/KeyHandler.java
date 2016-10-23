/*
 * Copyright (C) 2015-2016 The CyanogenMod Project
 * 		 2016 OldDroid - CodeAndroidFiber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.keyhandler;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;
import com.android.internal.util.ArrayUtils;

public class KeyHandler implements DeviceKeyHandler {

    private static final String TAG = KeyHandler.class.getSimpleName();
    private static final int GESTURE_REQUEST = 1;

    // Supported scancodes
    private static final int MODE_TOTAL_SILENCE = 600;
    private static final int MODE_ALARMS_ONLY = 601;
    private static final int MODE_PRIORITY_ONLY = 602;
    private static final int MODE_NONE = 603;

    private static final int GESTURE_WAKELOCK_DURATION = 3000;

    private static final SparseIntArray sSupportedSliderModes = new SparseIntArray();
    static {
        sSupportedSliderModes.put(MODE_TOTAL_SILENCE, Settings.Global.ZEN_MODE_NO_INTERRUPTIONS);
        sSupportedSliderModes.put(MODE_ALARMS_ONLY, Settings.Global.ZEN_MODE_ALARMS);
        sSupportedSliderModes.put(MODE_PRIORITY_ONLY, Settings.Global.ZEN_MODE_IMPORTANT_INTERRUPTIONS);
        sSupportedSliderModes.put(MODE_NONE, Settings.Global.ZEN_MODE_OFF);
    }

    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private Vibrator mVibrator;

    public KeyHandler(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (mVibrator == null || !mVibrator.hasVibrator()) {
            mVibrator = null;
        }
    }

    public boolean handleKeyEvent(KeyEvent event) {
        int scanCode = event.getScanCode();
        boolean isSliderModeSupported = sSupportedSliderModes.indexOfKey(scanCode) >= 0;
        if (!isSliderModeSupported) {
            return false;
        }

        if (isSliderModeSupported) {
            mNotificationManager.setZenMode(sSupportedSliderModes.get(scanCode), null, TAG);
            doHapticFeedback();
        }
        return true;
    }

    private void doHapticFeedback() {
        if (mVibrator == null) {
            return;
        }
        mVibrator.vibrate(50);
    }
}

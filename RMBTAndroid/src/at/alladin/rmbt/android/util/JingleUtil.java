/*******************************************************************************
 * Copyright 2016-2017 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.rmbt.android.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by lb on 28.03.17.
 */

public class JingleUtil {

    public static void playStartJingle(final Context context) {
        try {
            if (ConfigHelper.isPlayJingleEnabled(context)) {
                AssetFileDescriptor afd = context.getAssets().openFd("start_sound.mp3");
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }


    public static void playFinishJingle(final Context context) {
        try {
            if (ConfigHelper.isPlayJingleEnabled(context)) {
                AssetFileDescriptor afd = context.getAssets().openFd("finish_sound.mp3");
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

}

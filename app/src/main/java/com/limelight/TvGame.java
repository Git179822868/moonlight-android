package com.limelight;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

/**
 * TV-specific stream Activity.
 *
 * Unlike the phone stream Activity, this Activity intentionally uses the
 * standard Android launch mode (declared in the manifest). Every selection
 * from the TV app grid therefore receives a new Surface, decoder, and
 * connection lifecycle. The host application is not quit when this Activity
 * exits, so selecting it again resumes the existing Sunshine session.
 */
public class TvGame extends Game {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Some TV compositors expose the Activity window before SurfaceView has
        // produced a frame. Keep that transition black instead of white.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
    }

    @Override
    public void onBackPressed() {
        // A TV remote must always be able to leave a pending or active stream.
        // Game.onStop() performs the actual connection/decoder teardown.
        finish();
    }
}

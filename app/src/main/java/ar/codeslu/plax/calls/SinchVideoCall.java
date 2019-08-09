package ar.codeslu.plax.calls;

import android.view.View;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;

public class SinchVideoCall implements VideoCallListener {

     SinchClient sinchClient;

    public SinchVideoCall(SinchClient sinchClient) {
        this.sinchClient = sinchClient;

    }

    @Override
    public void onVideoTrackAdded(Call call) {
        VideoController vc = sinchClient.getVideoController();
        View myPreview = vc.getLocalView();
        View remoteView = vc.getRemoteView();
    }

    @Override
    public void onVideoTrackPaused(Call call) {

    }

    @Override
    public void onVideoTrackResumed(Call call) {

    }

    @Override
    public void onCallProgressing(Call call) {

    }

    @Override
    public void onCallEstablished(Call call) {

    }

    @Override
    public void onCallEnded(Call call) {

    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }
}

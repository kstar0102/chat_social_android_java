package ar.codeslu.plax.calls;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import ar.codeslu.plax.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class IncAudioActivity extends BaseActivity {
    static final String TAG = IncAudioActivity.class.getSimpleName();
    private String mCallId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inc_audio);
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        CircleImageView answer = (CircleImageView) findViewById(R.id.btn_accept);
        answer.setOnClickListener(mClickListener);
        CircleImageView decline = (CircleImageView) findViewById(R.id.btn_reject);
        decline.setOnClickListener(mClickListener);

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        super.onServiceConnected(componentName, iBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        super.onServiceDisconnected(componentName);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListenere());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    @Override
    protected void onServiceDisconnected() {
        super.onServiceDisconnected();
    }

    @Override
    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return super.getSinchServiceInterface();
    }

    private class SinchCallListenere implements CallListener {
        @Override
        public void onCallProgressing(Call call) {

        }

        @Override
        public void onCallEstablished(Call call) {

        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_accept:
                    answerClicked();
                    break;
                case R.id.btn_reject:
                    declineClicked();
                    break;
            }
        }
    };

    private void answerClicked() {

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallingActivity.class);
            intent.putExtra("Userid", mCallId);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    private void declineClicked() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

}

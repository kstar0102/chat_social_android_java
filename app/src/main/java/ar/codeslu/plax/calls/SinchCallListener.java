package ar.codeslu.plax.calls;

import android.content.Context;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class SinchCallListener implements CallListener
{
    /**
     * STATUS 0 -
     * STATUS 100 - RINGING
     * STATUS 200 - ESTABLISHED
     * STATUS 900 - ENDED
     */

    private int status=0;
    private Context context;

    public  SinchCallListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onCallProgressing(Call call) {
        status = 100;
    }

    @Override
    public void onCallEstablished(Call call) {
        status = 200;
    }

    @Override
    public void onCallEnded(Call endedcall) {
        status = 900;
        Toast.makeText(context,"Ended",Toast.LENGTH_SHORT).show();
        endedcall.hangup();
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

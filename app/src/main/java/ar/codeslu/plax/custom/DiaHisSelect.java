package ar.codeslu.plax.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URISyntaxException;

import ar.codeslu.plax.R;

import static android.content.Intent.getIntent;

public class DiaHisSelect extends Dialog {
    private String friendid;
    private Activity c;
    ImageView userPic;
    TextView username, userphone;

    public DiaHisSelect(Activity a, String friendid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.friendid = friendid;
    }

    public DiaHisSelect(Context context, Activity c) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_dialog_history);

        userPic = findViewById(R.id.userpic);
        username = findViewById(R.id.Uname);
        userphone = findViewById(R.id.Uphonenumber);

        switch(friendid){
            case "1":
                userPic.setImageResource(R.drawable.janate);
                username.setText("Janet Folwer");
                break;
            case "2":
                userPic.setImageResource(R.drawable.jason);
                username.setText("Jason Boyd");
                break;
            case "4":
                userPic.setImageResource(R.drawable.nicolas);
                username.setText("Nicholas Dunn");
                break;
            case "5":
                userPic.setImageResource(R.drawable.carol);
                username.setText("Carol Clark");
                break;
            case "6":
                userPic.setImageResource(R.drawable.ann);
                username.setText("Ann Carroll");
                break;
            default:
                break;
        }

    }
}

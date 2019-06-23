package ar.codeslu.plax;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ar.codeslu.plax.global.Global;


public class Editor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Global.currentactivity = this;
        //slider

    }
}

//
//   if (dataSnapshot.exists()) {
//           int i = 0;
//           if (Global.check_int(Chat.this)) {
//           for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//           MessageIn message = childSnapshot.getValue(MessageIn.class);
//
////check only in global list range
//        if (i < Global.messG.size()) {
//        //check if the message in its place or not
//        if (message.getMessId().equals(Global.messG.get(i).getMessId())) {
//        //update the list
//        Global.messG.set(i, message);
//        } else {
//        boolean found = false;
//        int k = 0;
//        //check if it found in the list in another place or not
//        if (i == 0)
//        k = i + 1;
//        else
//        k = i;
//        for (int j = k-1; j < Global.messG.size(); j++) {
//        //j is the shift value between global list and server list
//        if (message.getMessId().equals(Global.messG.get(j).getMessId())) {
//        found = true;
//        i = j;
//        break;
//        } else {
//        if (Global.messG.get(j).getStatue().equals("..")) {
//        Global.messG.get(j).setStatue("X");
//        Global.messG.set(j, Global.messG.get(j));
//        found = false;
//        }
//        }
//
//        }
//        if (!found) {
//        Global.messG.add(message);
//        i = Global.messG.size() + 1;
//        found = false;
//        }
//        }
//        ++i;
//        } else
//        Global.messG.add(message);
//
//        }
//        //local store
//        tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
//        }
//
//        messagesAdapter.clear();
//        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
//        messagesAdapter.addToEnd(MessageData.getMessages(), true);
//        messagesAdapter.notifyDataSetChanged();
//        }


//
//   if (dataSnapshot.exists()) {
//           if (Global.check_int(Chat.this)) {
//           for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//           MessageIn message = childSnapshot.getValue(MessageIn.class);
//
//        if (messagesAdapter.halbine(Global.messG,message.getMessId()) == -1)
//        Global.messG.add(message);
//        else
//        Global.messG.set(messagesAdapter.halbine(Global.messG,message.getMessId()), message);
//        }
//        //local store
//        tinydb.putListObject(mAuth.getCurrentUser().getUid() + "/" + friendId, Global.messG);
//        }
//        messagesAdapter.clear();
//        messagesList.getLayoutManager().smoothScrollToPosition(messagesList, null, 0);
//        messagesAdapter.addToEnd(MessageData.getMessages(), true);
//        messagesAdapter.notifyDataSetChanged();
//        if(keyOnce[0] == 0)
//        {
//        Log.d("keyyy", keyOnce[0] +"");
//        keyOnce[0]++;
//        }
//        }
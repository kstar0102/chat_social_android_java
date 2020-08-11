package ar.codeslu.plax.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.StoryList;
import dmax.dialog.SpotsDialog;
import xute.storyview.StoryModel;
import ar.codeslu.plax.story.StoryView;


public class EditStoryA extends RecyclerView.Adapter<EditStoryA.Holder> {
    Context context;
    FirebaseAuth mAuth;
    ArrayList<StoryModel> array;
    ArrayList<StoryModel> single;
    DatabaseReference myData;
    DatabaseReference mUserDB, mPhone;
    //compress
    ArrayList<String> localContacts, ContactsId;
    int poss = 0;
    android.app.AlertDialog dialogg;

    public EditStoryA(ArrayList<StoryModel> array) {
        this.array = array;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_raw_e, null, false);
        context = parent.getContext();
        mAuth = FirebaseAuth.getInstance();
        myData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mAuth = FirebaseAuth.getInstance();
        mUserDB = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mPhone = FirebaseDatabase.getInstance().getReference(Global.Phones);
        //arrays init
        localContacts = new ArrayList<>();
        ContactsId = new ArrayList<>();
        //loader
        if (Global.DARKSTATE) {
            dialogg = new SpotsDialog.Builder()
                    .setContext(context)
                    .setMessage(R.string.pleasW)
                    .setTheme(R.style.darkDialog)
                    .setCancelable(true)
                    .build();
        } else {
            dialogg = new SpotsDialog.Builder()
                    .setContext(context)
                    .setMessage(R.string.pleasW)
                    .setCancelable(true)
                    .build();
        }
        return new Holder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        single = new ArrayList<>();
        single.add(array.get(position));
        holder.storyView.setImageUris(single, context);
        holder.storyView.calculateSweepAngle(0, context);
        holder.time.setText(array.get(position).getTime());
        holder.delete.setFocusableInTouchMode(false);
        holder.delete.setFocusable(false);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(context)) {
                    Global.storyFramel = true;
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    builder.setMessage(R.string.delete_story_mess);
                    builder.setTitle(R.string.delet_sto);
                    builder.setIcon(R.drawable.ic_remove);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogg.show();
                            getContactList();
                            poss = position;

                        }
                    });
                    builder.show();
                } else
                    Toast.makeText(context, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        StoryView storyView;
        TextView time;
        ImageView delete;

        Holder(View itemView) {
            super(itemView);
            storyView = itemView.findViewById(R.id.storyView);
            delete = itemView.findViewById(R.id.delete);
            time = itemView.findViewById(R.id.timeS);
        }
    }

    public int halbine(ArrayList<StoryModel> ml, String id) {
        int j = 0, i = 0;
        for (i = 0; i < ml.size(); i++) {
            if (ml.get(i).getId().equals(id)) {
                j = 1;
                break;
            }

        }
        if (j == 1)
            return i;
        else
            return -1;

    }


    private void getContactList() {
        String ISOPrefix = getCountryISO();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if (phone.length() > 0) {

                if (String.valueOf(phone.charAt(0)).equals("0"))
                    phone = String.valueOf(phone).replaceFirst("0", "");

                if (phone.length() > 0) {
                    if (!String.valueOf(phone.charAt(0)).equals("+"))
                        phone = ISOPrefix + phone;
                    if (phone.length() > 0) {
                        if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone) && !phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                            if (!phone.contains(".") && !phone.contains("#") && !phone.contains("$") && !phone.contains("[") && !phone.contains("]"))
                                localContacts.add(phone);
                        }
                    }
                }
            }
        }

        getUserDetails();


    }

    private String getCountryISO() {
        String countryCode = null;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();

            if (countryCode != null && countryCode.length() == 2)
                return CountryToPhonePrefix.getPhone(countryCode.toUpperCase());

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return CountryToPhonePrefix.getPhone(countryCode.toUpperCase());
        }

        try {
            countryCode = CountryToPhonePrefix.getPhone(countryCode.toUpperCase());
        } catch (NullPointerException e) {
            countryCode = CountryToPhonePrefix.getPhone("US");
        }

        return countryCode;
    }

    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330:
                    return "PR";
                case 310:
                    return "US";
                case 311:
                    return "US";
                case 312:
                    return "US";
                case 316:
                    return "US";
                case 283:
                    return "AM";
                case 460:
                    return "CN";
                case 455:
                    return "MO";
                case 414:
                    return "MM";
                case 619:
                    return "SL";
                case 450:
                    return "KR";
                case 634:
                    return "SD";
                case 434:
                    return "UZ";
                case 232:
                    return "AT";
                case 204:
                    return "NL";
                case 262:
                    return "DE";
                case 247:
                    return "LV";
                case 255:
                    return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }


    private void getUserDetails() {

        for (int i = 0; i < localContacts.size(); i++) {

            int finalI = i;
            mPhone.child(localContacts.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        StoryList user = dataSnapshot.getValue(StoryList.class);
                        ContactsId.add(user.getId());

                        if (finalI == localContacts.size() - 1)
                            removeStorytoOthers();


                    } else {
                        if (finalI == localContacts.size() - 1)
                            removeStorytoOthers();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void removeStorytoOthers() {


        if (ContactsId.size() == 0) {
            myData.child(mAuth.getCurrentUser().getUid()).child(Global.myStoryS).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        StoryList storyList = data.getValue(StoryList.class);
                        if (storyList.getId().equals(array.get(poss).id)) {
                            if (halbine(Global.myStoryList, array.get(poss).getId()) != -1 && Global.storyFramel) {
                                Global.storyFramel = false;
                                data.getRef().removeValue();
                                Global.myStoryList.remove(halbine(Global.myStoryList, array.get(poss).getId()));
                                notifyItemRemoved(poss);
                                notifyDataSetChanged();
                                dialogg.dismiss();
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        for (int i = 0; i < ContactsId.size(); i++) {
            int finalI = i;
            mUserDB.child(ContactsId.get(i)).child(Global.StoryS).child(mAuth.getCurrentUser().getUid()).child(array.get(poss).getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (finalI == ContactsId.size() - 1) {
                        myData.child(mAuth.getCurrentUser().getUid()).child(Global.myStoryS).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    StoryList storyList = data.getValue(StoryList.class);
                                    if (storyList.getId().equals(array.get(poss).id)) {
                                        if (halbine(Global.myStoryList, array.get(poss).getId()) != -1 && Global.storyFramel) {
                                            Global.storyFramel = false;
                                            data.getRef().removeValue();
                                            Global.myStoryList.remove(halbine(Global.myStoryList, array.get(poss).getId()));
                                            notifyItemRemoved(poss);
                                            notifyDataSetChanged();
                                            dialogg.dismiss();
                                        }

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });
        }


    }

}

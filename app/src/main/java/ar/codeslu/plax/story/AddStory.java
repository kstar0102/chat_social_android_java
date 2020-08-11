package ar.codeslu.plax.story;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.codeslu.plax.Contacts;
import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.lists.CountryToPhonePrefix;
import ar.codeslu.plax.lists.StoryList;
import ar.codeslu.plax.lists.UserData;
import ar.codeslu.plax.lists.UserDetailsStory;
import ar.codeslu.plax.settingsitems.ChatSettings;
import ar.codeslu.plax.settingsitems.TestWall;
import ar.codeslu.plax.story.base.BaseActivity;
import ar.codeslu.plax.story.filters.FilterListener;
import ar.codeslu.plax.story.filters.FilterViewAdapter;
import ar.codeslu.plax.story.tools.EditingToolsAdapter;
import ar.codeslu.plax.story.tools.ToolType;
import id.zelory.compressor.Compressor;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;


public class AddStory extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = AddStory.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private Button addS;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    //firebase
    FirebaseAuth mAuth;
    DatabaseReference mData, mUserDB, mPhone;

    //encrypt

    //compress
    ArrayList<String> localContacts, ContactsId;
    String idStory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_add_story);
        Global.currentactivity = this;

        initViews();

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mUserDB = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mPhone = FirebaseDatabase.getInstance().getReference(Global.Phones);

        ((AppBack) getApplication()).getBlock();
        ((AppBack) getApplication()).getMute();

        Global.stickerIcon = true;

        //encryption

        //arrays init
        localContacts = new ArrayList<>();
        ContactsId = new ArrayList<>();
        addS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Global.check_int(AddStory.this)) {
                    Dexter.withActivity(AddStory.this)
                            .withPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {

                                    if (report.areAllPermissionsGranted()) {
                                        addToStory();

                                    } else
                                        Toast.makeText(AddStory.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                    token.continuePermissionRequest();

                                }
                            }).check();
                } else
                    Toast.makeText(AddStory.this, R.string.check_int, Toast.LENGTH_SHORT).show();

            }
        });


        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);
        addS = findViewById(R.id.addS);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgCamera.setVisibility(View.GONE);

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;

            case R.id.imgCamera:
                Dexter.withActivity(AddStory.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(AddStory.this);
                                } else
                                    Toast.makeText(AddStory.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
                break;

            case R.id.imgGallery:
                Dexter.withActivity(AddStory.this)
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {

                                if (report.areAllPermissionsGranted()) {
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .start(AddStory.this);
                                } else
                                    Toast.makeText(AddStory.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                                token.continuePermissionRequest();

                            }
                        }).check();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        Dexter.withActivity(AddStory.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            showLoading(getString(R.string.saving));
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + ""
                                    + System.currentTimeMillis() + ".png");
                            try {
                                file.createNewFile();

                                SaveSettings saveSettings = new SaveSettings.Builder()
                                        .setClearViewsEnabled(true)
                                        .setTransparencyEnabled(true)
                                        .build();

                                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                                    @Override
                                    public void onSuccess(@NonNull String imagePath) {
                                        hideLoading();
                                        showSnackbar(getResources().getString(R.string.save_succ));
                                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(imagePath)));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        hideLoading();
                                        showSnackbar(getResources().getString(R.string.save_fail));
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                hideLoading();
                                showSnackbar(e.getMessage());
                            }

                        } else
                            Toast.makeText(AddStory.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    @SuppressLint("MissingPermission")
    private void addToStory() {
        Dexter.withActivity(AddStory.this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            showLoading(getResources().getString(R.string.ups));
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + File.separator + ""
                                    + System.currentTimeMillis() + ".png");
                            try {
                                file.createNewFile();

                                SaveSettings saveSettings = new SaveSettings.Builder()
                                        .setClearViewsEnabled(true)
                                        .setTransparencyEnabled(true)
                                        .build();

                                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                                    @Override
                                    public void onSuccess(@NonNull String imagePath) {
                                        try {
                                            Bitmap imageBitmap = SiliCompressor.with(AddStory.this).getCompressBitmap(imagePath, true);
                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                            byte[] byteArray = stream.toByteArray();
                                            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                                            StorageReference riversRef = mStorageRef.child(Global.StoryS + "/" + mAuth.getCurrentUser().getUid() + "/story_" + mAuth.getCurrentUser().getUid() + "_" + System.currentTimeMillis() + ".png");
                                            UploadTask uploadTask = riversRef.putBytes(byteArray);
                                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                @Override
                                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                    if (!task.isSuccessful()) {
                                                        throw task.getException();
                                                    }

                                                    // Continue with the task to get the download URL
                                                    return riversRef.getDownloadUrl();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        Uri downloadUrl = task.getResult();
                                                        idStory = mAuth.getCurrentUser().getUid() + "_" + System.currentTimeMillis();
                                                        final Map<String, Object> map = new HashMap<>();
                                                        map.put("time", ServerValue.TIMESTAMP);
                                                        map.put("link", encryption.encryptOrNull(String.valueOf(downloadUrl)));
                                                        map.put("id", idStory);
                                                        map.put("name", Global.nameLocal);
                                                        map.put("ava", Global.avaLocal);
                                                        mData.child(mAuth.getCurrentUser().getUid()).child(Global.myStoryS).child(idStory).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                getContactList(map);
                                                                finish();
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        hideLoading();
                                                                        showSnackbar(getResources().getString(R.string.error));
                                                                    }
                                                                });

                                                    } else {
                                                        hideLoading();
                                                        showSnackbar(getResources().getString(R.string.error));
                                                    }
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        hideLoading();
                                        showSnackbar(getResources().getString(R.string.error));
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                                hideLoading();
                                showSnackbar(e.getMessage());
                            }
                        } else
                            Toast.makeText(AddStory.this, getString(R.string.acc_per), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    mPhotoEditor.clearAllViews();
                    Uri uri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    mPhotoEditorView.getSource().setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);

    }

    @Override
    public void onStickerClick(Bitmap bitmap, int p) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }


    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exitq));
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton(getString(R.string.disc), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                if(Global.stickerIcon) {
                    Global.stickerIcon = false;
                    mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                }
                break;
        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = this;
        Global.stickerIcon = true;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;

    }


    private void getContactList(Map<String, Object> map) {
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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

                    if(phone.length()>0) {
                    if (!Global.phoneLocal.equals(phone) && !localContacts.contains(phone) && !phone.equals("t88848992hisuseri9483828snothereri9949ghtnow009933")) {
                        if (!phone.contains(".") && !phone.contains("#") && !phone.contains("$") && !phone.contains("[") && !phone.contains("]"))
                            localContacts.add(phone);
                    }
                }
            }
            }
        }
        getUserDetails(map);

    }

    private String getCountryISO() {
        String countryCode = null;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
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


    private void getUserDetails(Map<String, Object> map) {

        for (int i = 0; i < localContacts.size(); i++) {

            int finalI = i;
            mPhone.child(localContacts.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        StoryList user = dataSnapshot.getValue(StoryList.class);
                        ContactsId.add(user.getId());
                        if (finalI == localContacts.size() - 1)
                            setStorytoOthers(map);


                    } else {
                        if (finalI == localContacts.size() - 1)
                            setStorytoOthers(map);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setStorytoOthers(Map<String, Object> map) {
        for (int i = 0; i < ContactsId.size(); i++) {
            if (!Global.blockList.contains(ContactsId.get(i)) && !Global.mutelist.contains(ContactsId.get(i))) {
                mUserDB.child(ContactsId.get(i)).child(Global.StoryS).child(mAuth.getCurrentUser().getUid()).child(idStory).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
            }
        }
    }


}



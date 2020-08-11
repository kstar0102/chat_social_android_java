package ar.codeslu.plax.story;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;

public class StickerBSFragment extends BottomSheetDialogFragment {


    public StickerBSFragment() {
        // Required empty public constructor
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap,int position);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        RecyclerView rvEmoji = contentView.findViewById(R.id.rvEmoji);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        StickerAdapter stickerAdapter = new StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        int[] stickerList = new int[]{R.drawable.aa, R.drawable.bb,R.drawable.st1,R.drawable.st2,R.drawable.st3,R.drawable.st4,R.drawable.st5,R.drawable.st6,R.drawable.st7,R.drawable.st8,
                R.drawable.st9,R.drawable.st10,R.drawable.st11,R.drawable.st12,R.drawable.st13,R.drawable.st14,R.drawable.st15,R.drawable.st16,R.drawable.st17,R.drawable.st18,R.drawable.st19,
                R.drawable.st20,R.drawable.st21,R.drawable.st22,R.drawable.st23,R.drawable.st24,R.drawable.st25,R.drawable.st26,R.drawable.st27,R.drawable.st28,R.drawable.st29,R.drawable.st30,R.drawable.st31,R.drawable.st32,
                R.drawable.st33,R.drawable.st34,R.drawable.st35,R.drawable.st36,R.drawable.st37,R.drawable.st38,R.drawable.st39,R.drawable.st40,R.drawable.st41,R.drawable.st42,
                R.drawable.st43,R.drawable.st44,R.drawable.st45,R.drawable.st46,R.drawable.st47,R.drawable.st48,R.drawable.st49,R.drawable.st50,R.drawable.st51,
                R.drawable.st52,R.drawable.st53,R.drawable.st54,R.drawable.st55,R.drawable.st56,R.drawable.st57,R.drawable.st58,R.drawable.st59,R.drawable.st60,R.drawable.st61,
                R.drawable.st62,R.drawable.st63,R.drawable.st64,R.drawable.st65,R.drawable.st66,R.drawable.st67,R.drawable.st68,R.drawable.st69
        };

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgSticker.setImageResource(stickerList[position]);

        }

        @Override
        public int getItemCount() {
            return stickerList.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerListener != null) {
                            mStickerListener.onStickerClick(
                                    BitmapFactory.decodeResource(getResources(),
                                            stickerList[getLayoutPosition()]),stickerList[getLayoutPosition()]);
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onPause() {
        super.onPause();
        Global.stickerIcon = true;
    }
}
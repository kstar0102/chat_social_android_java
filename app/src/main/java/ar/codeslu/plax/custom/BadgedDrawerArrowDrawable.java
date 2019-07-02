package ar.codeslu.plax.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;

import ar.codeslu.plax.R;

public class BadgedDrawerArrowDrawable extends DrawerArrowDrawable {

    /**
     * @param context used to get the configuration for the drawable from
     */
    public BadgedDrawerArrowDrawable(Context context) {
        super(context);

        setColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(130);
        canvas.drawText("•", canvas.getWidth() - 125, 55, paint);
    }
}
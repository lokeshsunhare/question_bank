package com.igkvmis.questionbank.activities.BlurImageAndRelatedFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {
    private Context mContext;
    private float mRadius;

    public BlurTransformation(Context context, float radius) {
        mContext = context;
        mRadius = radius;
    }

    @Override
    public String key() {
        return "blur(" + String.valueOf(mRadius) + ")";
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // Initialize RenderScript and the script to be used
        RenderScript renderScript = RenderScript.create(mContext);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation output = Allocation
                .createFromBitmap(renderScript, blurredBitmap);

        script.setInput(input);
        script.setRadius(mRadius);
        script.forEach(output);
        output.copyTo(blurredBitmap);

        renderScript.destroy();
        bitmap.recycle();
        return blurredBitmap;
    }
}

package com.symplyfyweb.gherghina.pokypoker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;


public class MainActivity extends Activity implements View.OnClickListener {
    Button btn_Play, btn_Card_Deck;
    /*Bitmap background;
    Display display;
    Point size = new Point();
    RelativeLayout r;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        setReferences();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setReferences() {
        btn_Play = (Button) findViewById(R.id.btn_Play);
        btn_Card_Deck = (Button) findViewById(R.id.btn_Card_Deck);
        //r = (RelativeLayout) findViewById(R.id.relLay_main);
        btn_Play.setOnClickListener(this);
        btn_Card_Deck.setOnClickListener(this);

        /*display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        background = decodeSampledBitmapFromResource(getResources(), getResourceID("poker_table", "drawable", this), width / 2, height / 2);
        r.setBackground(new BitmapDrawable(this.getResources(), background));*/
    }

    @Override
    public void onClick(View v) {
        Intent newActivity;
        switch (v.getId()) {
            case R.id.btn_Play :
                newActivity = new Intent("com.symplyfyweb.gherghina.pokypoker.PLAYACTIVITY");
                startActivity(newActivity);
                //overridePendingTransition(R.anim.card_flip_in, R.anim.card_flip_out);
                //finish();
                break;
            case R.id.btn_Card_Deck :
                newActivity = new Intent("com.symplyfyweb.gherghina.pokypoker.CHOOSECARDDECK");
                startActivity(newActivity);
                //overridePendingTransition(R.anim.card_flip_in, R.anim.card_flip_out);
                //finish();
                break;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    protected static int getResourceID (final String resName, final String resType, final Context ctx) {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
            return ResourceID;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        //close();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (r.getBackground() != null)
        //    r.setBackground(null);
    }
}

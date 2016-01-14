package com.symplyfyweb.gherghina.pokypoker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


public class DoubleActivity extends Activity implements View.OnClickListener {
    TextView txt_actual_Winning;
    Button btn_Red, btn_Black;
    Button btn_Trefla, btn_Romb, btn_InimaRosie, btn_InimaNeagra;
    Button btn_GetMoney;
    ImageView iv_card_to_Guess;

    Bundle winToReceive;
    Bundle winToPass = new Bundle();
    Intent newActivity;
    Bitmap deck_card, cardImage;

    boolean played = false;
    boolean wrong = false;
    boolean getmoney = false;

    int actualWinning, actualCredit, betCredit;
    int count = 0;
    int level = 1;
    ArrayList<Integer> numbers = new ArrayList<>();
    Random randomGenerator = new Random();
    //
    // Thread
    // It goes all the life of the activity non-stop because of the while(true) loop
    // inside the run method
    // Change every 0.1 seconds image , like a "blinck of an eye"
    //
    Thread timer = new Thread() {
        public void run() {
            while (true) {
                if(wrong) {
                    actualCredit += actualWinning;
                    winToPass.putInt("returnWinning", actualCredit);
                    winToPass.putInt("betCredit", betCredit);
                    newActivity.putExtras(winToPass);
                    newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newActivity);
                    finish();
                }
                if(getmoney) {
                    actualCredit += actualWinning;
                    winToPass.putInt("returnWinning", actualCredit);
                    winToPass.putInt("betCredit", betCredit);
                    newActivity.putExtras(winToPass);
                    newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newActivity);
                    finish();
                }
                if(count == 3) {
                    actualCredit += actualWinning;
                    winToPass.putInt("returnWinning", actualCredit);
                    winToPass.putInt("betCredit", betCredit);
                    newActivity.putExtras(winToPass);
                    newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newActivity);
                    finish();
                }
                if(count < 3) {
                    try {
                        if(wrong || getmoney) {
                            sleep(10);
                            break;
                        }
                        else if(!played && !wrong && !getmoney) {
                            sleep(100);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!played) {
                                        if (iv_card_to_Guess.getDrawable() == null)
                                            iv_card_to_Guess.setImageBitmap(deck_card);
                                        else
                                            iv_card_to_Guess.setImageDrawable(null);
                                    }
                                }
                            });
                        }
                        if(played) {
                            sleep(2200);
                            played = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_double);

        setReferences();

        timer.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setReferences() {
        //
        // TextView
        //
        txt_actual_Winning = (TextView) findViewById(R.id.txt_actual_Winning);
        //
        // Receiving the info passed from PlayActivity
        //
        winToReceive = getIntent().getExtras();
        actualWinning = winToReceive.getInt("actualWinning");
        actualCredit = winToReceive.getInt("actualCredit");
        betCredit = winToReceive.getInt("betCredit");
        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
        //
        // ImageView
        //
        iv_card_to_Guess = (ImageView) findViewById(R.id.iv_card_to_Guess);
        iv_card_to_Guess.setBackgroundColor(Color.TRANSPARENT);
        deck_card = decodeSampledBitmapFromResource(getResources(), getResourceID("img_card_deck_" + level, "drawable", this), iv_card_to_Guess.getMaxWidth(), iv_card_to_Guess.getMaxHeight());
        deck_card = getRoundedCornerBitmap(deck_card);
        iv_card_to_Guess.setImageBitmap(deck_card);
        //
        // Buttons
        //
        btn_Red = (Button) findViewById(R.id.btn_Red);
        btn_Black = (Button) findViewById(R.id.btn_Black);
        btn_Trefla = (Button) findViewById(R.id.btn_Trefla);
        btn_Romb = (Button) findViewById(R.id.btn_Romb);
        btn_InimaRosie = (Button) findViewById(R.id.btn_InimaRosie);
        btn_InimaNeagra = (Button) findViewById(R.id.btn_InimaNeagra);
        btn_GetMoney = (Button) findViewById(R.id.btn_getMoney_Double);
        //
        // Set Click Event
        //
        btn_Red.setOnClickListener(this);
        btn_Black.setOnClickListener(this);
        btn_Trefla.setOnClickListener(this);
        btn_Romb.setOnClickListener(this);
        btn_InimaRosie.setOnClickListener(this);
        btn_InimaNeagra.setOnClickListener(this);
        btn_GetMoney.setOnClickListener(this);
        //
        // Intent
        //
        newActivity = new Intent("com.symplyfyweb.gherghina.pokypoker.PLAYACTIVITY");

        count = 0;
        wrong = false;
        played = false;
        getmoney = false;
    }

    private void getRandomNumbers() {
        while(numbers.size() < 3) {
            int random = randomGenerator.nextInt(51);
            if (!numbers.contains(random)) {
                numbers.add(random);
            }
        }
    }

    private void clearArrayList() {
        for(int i = numbers.size()-1 ; i >= 0; i--)
            numbers.remove(numbers.get(i));
        numbers.clear();
    }

    private boolean verifyRed(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 1 || nr == 2);
    }

    private boolean verifyBlack(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 0 || nr == 3);
    }

    // TREFLA
    private boolean verifyClubs(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 3);
    }

    // ROMB
    private boolean verifyDiamonds(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 2);
    }

    // INIMA ROSIE
    private boolean verifyHearts(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 1);
    }

    // INIMA NEAGRA
    private boolean verifySpades(int n) {
        int nr = numbers.get(n) % 4;
        return (nr == 0);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_getMoney_Double) {
            // Clear Random Numbers List
            clearArrayList();
            getmoney = true;
        }
        else {
            // Clear Random Numbers List
            clearArrayList();
            // Get Random Numbers
            getRandomNumbers();

            /*
             * Show Card to guess
             */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(count).toString(),
                            "drawable", getApplicationContext()), iv_card_to_Guess.getWidth(), iv_card_to_Guess.getHeight());
                    cardImage = getRoundedCornerBitmap(cardImage);
                    iv_card_to_Guess.setImageBitmap(cardImage);
                    cardImage = null;
                }
            });

            played = true;

            switch (v.getId()) {
                case R.id.btn_Red :
                    if(verifyRed(count)) {
                        actualWinning *= 2;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
                case R.id.btn_Black :
                    if(verifyBlack(count)) {
                        actualWinning *= 2;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
                case R.id.btn_Trefla :
                    if(verifyClubs(count)) {
                        actualWinning *= 4;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
                case R.id.btn_Romb :
                    if(verifyDiamonds(count)) {
                        actualWinning *= 4;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    } else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
                case R.id.btn_InimaRosie :
                    if(verifyHearts(count)) {
                        actualWinning *= 4;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    } else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
                case R.id.btn_InimaNeagra :
                    if(verifySpades(count)) {
                        actualWinning *= 4;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    else {
                        wrong = true;
                        actualWinning = 0;
                        txt_actual_Winning.setText("Actual Winning : " + actualWinning);
                    }
                    break;
            }

            count++;
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

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 55;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deck_card = null;
        cardImage = null;
        iv_card_to_Guess.setImageDrawable(null);
        clearArrayList();
        played = false;
        wrong = false;
        getmoney = false;
        count = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        deck_card = null;
        cardImage = null;
        clearArrayList();
        played = false;
        wrong = false;
        getmoney = false;
        count = 0;
    }
}

package com.symplyfyweb.gherghina.pokypoker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ChooseCardDeck extends Activity implements View.OnClickListener{
    GridView gv;
    ImageView expandedImageView;
    Button btn_Buy_Deck_Card;
    ImageButton img_btn_Back;
    LinearLayout linLay_Zoom_Image;
    TextView txtView_Credit_ChooseDeckCard;

    Bitmap image, roundImage;
    Animator mCurrentAnimator;
    GestureDetector detector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    int mShortAnimationDuration;
    // Shared Prefs
    public static final String PREFS_NAME = "MyPrefsFile";

    int j = 0, credit;
    int count = 0;
    int level;
    int position;

    String thumb[] = { "img_card_deck_1", "img_card_deck_2",
            "img_card_deck_3", "img_card_deck_4",
            "img_card_deck_5", "img_card_deck_6",
            "img_card_deck_7", "img_card_deck_8", "img_card_deck_9",
            "img_card_deck_10", "img_card_deck_11",
            "img_card_deck_12" };

    boolean thumbBought[] = {true, false, false, false, false, false,
            false, false, false, false, false, false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_choose_card_deck);

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);

        level = sharedPref.getInt("returnLevel", 1);
        for(int i = 1; i < thumb.length; i++)
            thumbBought[i] = sharedPref.getBoolean("card_deck_" + i, false);

        //
        // Get Saved Credit
        //
        txtView_Credit_ChooseDeckCard = (TextView) findViewById(R.id.txtView_Credit_ChooseDeckCard);
        credit = sharedPref.getInt(getString(R.string.saved_credit), 1000);
        String creditstring = Integer.toString(credit);
        txtView_Credit_ChooseDeckCard.setText(creditstring);

        btn_Buy_Deck_Card = (Button) findViewById(R.id.btn_Buy_Deck_Card);
        img_btn_Back = (ImageButton) findViewById(R.id.img_btn_Back);

        btn_Buy_Deck_Card.setOnClickListener(this);
        img_btn_Back.setOnClickListener(this);

        detector = new GestureDetector(this, new SwipeGestureDetector());

        gv = (GridView) findViewById(R.id.gridView_Card_Deck);
        gv.setAdapter(new ImageAdapter(this));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int pos,
                                    long id) {
                j = pos;
                zoomImageFromThumb(v, thumb[pos]);
                if(thumbBought[pos] == true)
                    btn_Buy_Deck_Card.setText("Change to this");
                position = pos;
            }
        });

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Buy_Deck_Card :
                if(!thumbBought[position]) {
                    thumbBought[position] = true;
                    level = position + 1;
                    credit -= 100;
                    String creditstring = Integer.toString(credit);
                    txtView_Credit_ChooseDeckCard.setText(creditstring);
                    btn_Buy_Deck_Card.setText("Bought !");
                }
                else
                    level = position + 1;
                break;
            case R.id.img_btn_Back :
                Intent newActivity;
                //
                // Used to pass info between activities
                // The bundle is like a basket and you can put inside whatever you need for later use
                //
                Bundle levelToPass = new Bundle();
                levelToPass.putInt("returnLevel", level);
                newActivity = new Intent("com.symplyfyweb.gherghina.pokypoker.MAINACTIVITY");
                newActivity.putExtras(levelToPass);
                startActivity(newActivity);
                finish();
                break;
        }
    }

    class ImageAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public ImageAdapter(ChooseCardDeck activity) {
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return thumb.length;
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {

            View listItem = convertView;

            if(listItem == null)
                listItem = layoutInflater.inflate(R.layout.single_grid_item, null);

            ImageView iv = (ImageView) listItem.findViewById(R.id.thumb);

            image = decodeSampledBitmapFromResource(getResources(), getResourceID(thumb[pos], "drawable", getApplicationContext()), 130, 130);
            roundImage = getRoundedCornerBitmap(image);
            iv.setImageBitmap(roundImage);
            image = null;
            roundImage = null;

            return listItem;
        }
    }

    private void zoomImageFromThumb(final View thumbView, String imageResString) {

        if(mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        expandedImageView = (ImageView) findViewById(R.id.expanded_mage);

        linLay_Zoom_Image = (LinearLayout) findViewById(R.id.linLay_Zoom_Image);

        expandedImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

        image = decodeSampledBitmapFromResource(getResources(), getResourceID(imageResString, "drawable", this), expandedImageView.getWidth(), expandedImageView.getHeight());
        roundImage = getRoundedCornerBitmap(image);
        expandedImageView.setImageBitmap(roundImage);
        image = null;
        roundImage = null;

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.frmLay_Container).getGlobalVisibleRect(finalBounds, globalOffset);

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;

        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {

            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;

        } else {

            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight = startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        btn_Buy_Deck_Card.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();

        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));

        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();

                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));

                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        btn_Buy_Deck_Card.setVisibility(View.GONE);
                        btn_Buy_Deck_Card.setText("Buy it !");
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        btn_Buy_Deck_Card.setVisibility(View.GONE);
                        btn_Buy_Deck_Card.setText("Buy it !");
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if (diffX > 0) {
                            image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + (j+1) + "_" + count,
                                    "drawable", getApplicationContext()), expandedImageView.getWidth(), expandedImageView.getHeight());
                            roundImage = getRoundedCornerBitmap(image);
                            expandedImageView.setImageBitmap(roundImage);
                            image = null;
                            roundImage = null;
                            if(count > 0)
                                count--;
                            else
                                count = 51;
                            //onSwipeRight();
                        } else {
                            image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + (j+1) + "_" + count,
                                    "drawable", getApplicationContext()), expandedImageView.getWidth(), expandedImageView.getHeight());
                            roundImage = getRoundedCornerBitmap(image);
                            expandedImageView.setImageBitmap(roundImage);
                            image = null;
                            roundImage = null;
                            if(count < 51)
                                count++;
                            else
                                count = 0;
                            //onSwipeLeft();
                        }
                    }
                    return true;
                }
                /*if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    /*image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                    roundImage = getRoundedCornerBitmap(image);
                    expandedImageView.setImageBitmap(roundImage);
                    image = null;
                    roundImage = null;

                    if(count < 51)
                        count++;
                    else
                        count = 0;
                    return true;*/

                    /*if(count == 51) {
                        count = 0;
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        return true;
                    }
                    else {
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        count++;
                        return true;
                    }

                    if(thumb.length > j) {
                        j++;

                        if(j < thumb.length) {
                            image = decodeSampledBitmapFromResource(getResources(), getResourceID(thumb[j], "drawable", getApplicationContext()), 100, 100);
                            roundImage = getRoundedCornerBitmap(image);
                            expandedImageView.setImageBitmap(roundImage);
                            image = null;
                            roundImage = null;
                            return true;
                        } else {
                            j = 0;
                            image = decodeSampledBitmapFromResource(getResources(), getResourceID(thumb[j], "drawable", getApplicationContext()), 100, 100);
                            roundImage = getRoundedCornerBitmap(image);
                            expandedImageView.setImageBitmap(roundImage);
                            image = null;
                            roundImage = null;
                            return true;
                        }
                    }
                } else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                    roundImage = getRoundedCornerBitmap(image);
                    expandedImageView.setImageBitmap(roundImage);
                    image = null;
                    roundImage = null;

                    if(count > 0)
                        count--;
                    else
                        count = 51;
                    return true;

                    if(count > 0) {
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        count--;
                        return true;
                    }
                    else {
                        count = 51;
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + j + "_" + count, "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        return true;
                    }

                    if(j > 0) {
                        j--;
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID(thumb[j], "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        return true;
                    } else {
                        j = thumb.length - 1;
                        image = decodeSampledBitmapFromResource(getResources(), getResourceID(thumb[j], "drawable", getApplicationContext()), 100, 100);
                        roundImage = getRoundedCornerBitmap(image);
                        expandedImageView.setImageBitmap(roundImage);
                        image = null;
                        roundImage = null;
                        return true;
                    }
                }*/
            } catch(Exception e) {
                e.printStackTrace();
            }
            return false;
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
        image = null;
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("returnLevel", level);
        editor.putInt(getString(R.string.saved_credit), credit);
        for(int i = 0; i < thumb.length; i++)
            editor.putBoolean("card_deck_" + i, thumbBought[i]);

        // Commit the edits!
        //editor.commit();
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        image = null;
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("returnLevel", level);
        editor.putInt(getString(R.string.saved_credit), credit);

        // Commit the edits!
        //editor.commit();
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("returnLevel", level);
        editor.putInt(getString(R.string.saved_credit), credit);

        // Commit the edits!
        //editor.commit();
        editor.apply();
    }
}

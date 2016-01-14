package com.symplyfyweb.gherghina.pokypoker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlayActivity extends Activity implements View.OnClickListener {
    Bitmap deck_card, cardImage;
    Bundle returnWin;
    ImageButton btn_Raise_Up, btn_Raise_Down;
    Button btn_Hold_1, btn_Hold_2, btn_Hold_3, btn_Hold_4, btn_Hold_5;
    Button btn_Play_GetMoney, btn_Double;
    ImageView iv_card1, iv_card2, iv_card3, iv_card4, iv_card5, iv_credit;
    TextView txt_Bet, txt_fill_1, txt_fill_2;
    TextView txt_Info, txt_Credit;
    TextView txtView_Hold_1, txtView_Hold_2, txtView_Hold_3, txtView_Hold_4, txtView_Hold_5;
    LinearLayout linLay_Hold, linLay_Hold_Text;
    Bitmap background;
    Display display;
    Point size = new Point();
    RelativeLayout r;

    boolean hold1, hold2, hold3, hold4, hold5;
    int betCredit = 0, credit = 1000;
    int multiplierWin = 0;
    int win;
    int returnCredit, returnBet;
    int level;
    ArrayList<Integer> numbers = new ArrayList<>();
    Random randomGenerator = new Random();
    // Shared Prefs
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_play);

        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);

        level = sharedPref.getInt("returnLevel", 1);
        
        // Default Level
        if(level == 0)
            level = 1;

        setReferences();

        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("returnWinning")) {
            returnWin = getIntent().getExtras();
            returnCredit = returnWin.getInt("returnWinning");
            returnBet = returnWin.getInt("betCredit");
            returnCredit -= returnBet;
            String creditstring = Integer.toString(returnCredit);
            txt_Credit.setText(creditstring);
            txt_Bet.setText("Your bet is " + returnBet);
            credit = returnCredit;
            betCredit = returnBet;
        }
        else {
            credit = sharedPref.getInt(getString(R.string.saved_credit), 1000);
            String creditstring = Integer.toString(credit);
            txt_Credit.setText(creditstring);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setReferences() {
        r = (RelativeLayout) findViewById(R.id.rootLayout);
        display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        background = decodeSampledBitmapFromResource(getResources(), getResourceID("poker_table", "drawable", this), width / 2, height / 2);
        r.setBackground(new BitmapDrawable(this.getResources(), background));
        //
        // ImageViews
        //
        iv_card1 = (ImageView) findViewById(R.id.iv_card1);
        iv_card2 = (ImageView) findViewById(R.id.iv_card2);
        iv_card3 = (ImageView) findViewById(R.id.iv_card3);
        iv_card4 = (ImageView) findViewById(R.id.iv_card4);
        iv_card5 = (ImageView) findViewById(R.id.iv_card5);
        iv_credit = (ImageView) findViewById(R.id.imageView_Credit);
        iv_card1.setBackgroundColor(Color.TRANSPARENT);
        iv_card2.setBackgroundColor(Color.TRANSPARENT);
        iv_card3.setBackgroundColor(Color.TRANSPARENT);
        iv_card4.setBackgroundColor(Color.TRANSPARENT);
        iv_card5.setBackgroundColor(Color.TRANSPARENT);
        //
        // Load background deck ImageViews
        //
        deck_card = decodeSampledBitmapFromResource(getResources(), getResourceID("img_card_deck_" + level, "drawable", this), iv_card1.getMaxWidth(), iv_card1.getMaxHeight());
        //deck_card = getRoundedCornerBitmap(deck_card);
        iv_card1.setImageBitmap(deck_card);
        iv_card2.setImageBitmap(deck_card);
        iv_card3.setImageBitmap(deck_card);
        iv_card4.setImageBitmap(deck_card);
        iv_card5.setImageBitmap(deck_card);
        //
        // Buttons
        //
        btn_Raise_Up = (ImageButton) findViewById(R.id.btn_Raise_Up);
        btn_Raise_Down = (ImageButton) findViewById(R.id.btn_Raise_Down);

        btn_Hold_1 = (Button) findViewById(R.id.btn_Hold_1);
        btn_Hold_2 = (Button) findViewById(R.id.btn_Hold_2);
        btn_Hold_3 = (Button) findViewById(R.id.btn_Hold_3);
        btn_Hold_4 = (Button) findViewById(R.id.btn_Hold_4);
        btn_Hold_5 = (Button) findViewById(R.id.btn_Hold_5);
        btn_Play_GetMoney = (Button) findViewById(R.id.btn_Play_GetMoney);
        btn_Double = (Button) findViewById(R.id.btn_Double);
        //
        // Set Click Event
        //
        btn_Raise_Up.setOnClickListener(this);
        btn_Raise_Down.setOnClickListener(this);
        btn_Hold_1.setOnClickListener(this);
        btn_Hold_2.setOnClickListener(this);
        btn_Hold_3.setOnClickListener(this);
        btn_Hold_4.setOnClickListener(this);
        btn_Hold_5.setOnClickListener(this);
        btn_Play_GetMoney.setOnClickListener(this);
        btn_Double.setOnClickListener(this);
        btn_Double.setEnabled(false);
        //
        // TextViews
        txt_Bet = (TextView) findViewById(R.id.txt_Bet);
        txt_fill_1 = (TextView) findViewById(R.id.txtView_fill_1);
        txt_fill_2 = (TextView) findViewById(R.id.txtView_fill_2);
        txt_Info = (TextView) findViewById(R.id.txtView_Info);
        txt_Credit = (TextView) findViewById(R.id.txtView_Credit);
        txtView_Hold_1 = (TextView) findViewById(R.id.txtView_Hold1);
        txtView_Hold_2 = (TextView) findViewById(R.id.txtView_Hold2);
        txtView_Hold_3 = (TextView) findViewById(R.id.txtView_Hold3);
        txtView_Hold_4 = (TextView) findViewById(R.id.txtView_Hold4);
        txtView_Hold_5 = (TextView) findViewById(R.id.txtView_Hold5);
        txtView_Hold_1.setVisibility(View.INVISIBLE);
        txtView_Hold_2.setVisibility(View.INVISIBLE);
        txtView_Hold_3.setVisibility(View.INVISIBLE);
        txtView_Hold_4.setVisibility(View.INVISIBLE);
        txtView_Hold_5.setVisibility(View.INVISIBLE);
        //
        // LinearLayout
        //
        linLay_Hold = (LinearLayout) findViewById(R.id.linLay_Hold);
        linLay_Hold.setVisibility(View.GONE);
        linLay_Hold_Text = (LinearLayout) findViewById(R.id.linLay_Hold_Text);
        //
        // Variables of Hold Buttons
        //
        hold1 = false; hold2 = false; hold3 = false; hold4 = false; hold5 = false;
    }

    private void getRandomNumbers() {
        while(numbers.size() < 5) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
                case R.id.btn_Raise_Up :
                if(credit == 0) {
                    txt_Info.setText("Insufficient founds !");
                }
                else {
                    betCredit += 100;
                    credit -= 100;
                    txt_Bet.setText("Your bet is " + betCredit);
                    txt_Info.setText("");
                    String creditstring = Integer.toString(credit);
                    txt_Credit.setText(creditstring);
                }
                break;
            case R.id.btn_Raise_Down :
                if(betCredit != 0) {
                    credit += 100;
                    betCredit -= 100;
                    txt_Bet.setText("Your bet is " + betCredit);
                    txt_Info.setText("");
                    String creditstring = Integer.toString(credit);
                    txt_Credit.setText(creditstring);
                }
                else
                    txt_Info.setText("You can't get lower than 0 bet !");
                break;
            case R.id.btn_Hold_1 :
                if(hold1) {
                    hold1 = false;
                    txtView_Hold_1.setVisibility(View.INVISIBLE);
                }
                else {
                    hold1 = true;
                    txtView_Hold_1.setVisibility(View.VISIBLE);
                    if (numbers.get(0) % 4 == 0 || numbers.get(0) % 4 == 3)
                        txtView_Hold_1.setTextColor(Color.RED);
                    else
                        txtView_Hold_1.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_Hold_2 :
                if(hold2) {
                    hold2 = false;
                    txtView_Hold_2.setVisibility(View.INVISIBLE);
                }
                else {
                    hold2 = true;
                    txtView_Hold_2.setVisibility(View.VISIBLE);
                    if (numbers.get(1) % 4 == 0 || numbers.get(1) % 4 == 3)
                        txtView_Hold_2.setTextColor(Color.RED);
                    else
                        txtView_Hold_2.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_Hold_3 :
                if(hold3) {
                    hold3 = false;
                    txtView_Hold_3.setVisibility(View.INVISIBLE);
                }
                else {
                    hold3 = true;
                    txtView_Hold_3.setVisibility(View.VISIBLE);
                    if (numbers.get(2) % 4 == 0 || numbers.get(2) % 4 == 3)
                        txtView_Hold_3.setTextColor(Color.RED);
                    else
                        txtView_Hold_3.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_Hold_4 :
                if(hold4) {
                    hold4 = false;
                    txtView_Hold_4.setVisibility(View.INVISIBLE);
                }
                else {
                    hold4 = true;
                    txtView_Hold_4.setVisibility(View.VISIBLE);
                    if (numbers.get(3) % 4 == 0 || numbers.get(3) % 4 == 3)
                        txtView_Hold_4.setTextColor(Color.RED);
                    else
                        txtView_Hold_4.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_Hold_5 :
                if(hold5) {
                    hold5 = false;
                    txtView_Hold_5.setVisibility(View.INVISIBLE);
                }
                else {
                    hold5 = true;
                    txtView_Hold_5.setVisibility(View.VISIBLE);
                    if (numbers.get(4) % 4 == 0 || numbers.get(4) % 4 == 3)
                        txtView_Hold_5.setTextColor(Color.RED);
                    else
                        txtView_Hold_5.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btn_Play_GetMoney :
                if(betCredit == 0)
                    txt_Info.setText("Please place your bet !");
                else {
                    if(btn_Play_GetMoney.getText().equals("Play")) {
                        btn_Play_GetMoney.setText("Change");
                        txt_Info.setVisibility(View.GONE);
                        linLay_Hold.setVisibility(View.VISIBLE);
                        btn_Raise_Up.setVisibility(View.INVISIBLE);
                        btn_Raise_Down.setVisibility(View.INVISIBLE);
                        clearArrayList();
                        getRandomNumbers();

                        cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(0), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                        int pix1 = iv_card1.getWidth();
                        int pix2 = iv_card1.getHeight();
                        //cardImage = getRoundedCornerBitmap(cardImage);
                        iv_card1.setImageBitmap(cardImage);
                        cardImage = null;
                        cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(1), "drawable", this), iv_card2.getWidth(), iv_card2.getHeight());
                        //cardImage = getRoundedCornerBitmap(cardImage);
                        iv_card2.setImageBitmap(cardImage);
                        cardImage = null;
                        cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(2), "drawable", this), iv_card3.getWidth(), iv_card3.getHeight());
                        //cardImage = getRoundedCornerBitmap(cardImage);
                        iv_card3.setImageBitmap(cardImage);
                        cardImage = null;
                        cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(3), "drawable", this), iv_card4.getWidth(), iv_card4.getHeight());
                        //cardImage = getRoundedCornerBitmap(cardImage);
                        iv_card4.setImageBitmap(cardImage);
                        cardImage = null;
                        cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(4), "drawable", this), iv_card5.getWidth(), iv_card5.getHeight());
                        //cardImage = getRoundedCornerBitmap(cardImage);
                        iv_card5.setImageBitmap(cardImage);
                        cardImage = null;
                    }
                    else if(btn_Play_GetMoney.getText().equals("Change")) {
                        txtView_Hold_1.setVisibility(View.INVISIBLE);
                        txtView_Hold_2.setVisibility(View.INVISIBLE);
                        txtView_Hold_3.setVisibility(View.INVISIBLE);
                        txtView_Hold_4.setVisibility(View.INVISIBLE);
                        txtView_Hold_5.setVisibility(View.INVISIBLE);

                        if(!hold1) {
                            int random;
                            do {
                                random = randomGenerator .nextInt(51);
                            } while(numbers.contains(random));

                            numbers.set(0, random);

                            cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(0), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                            //cardImage = getRoundedCornerBitmap(cardImage);
                            iv_card1.setImageBitmap(cardImage);
                            cardImage = null;
                        }

                        if(!hold2) {
                            int random;
                            do {
                                random = randomGenerator .nextInt(51);
                            } while(numbers.contains(random));

                            numbers.set(1, random);

                            cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(1), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                            //cardImage = getRoundedCornerBitmap(cardImage);
                            iv_card2.setImageBitmap(cardImage);
                            cardImage = null;
                        }

                        if(!hold3) {
                            int random;
                            do {
                                random = randomGenerator .nextInt(51);
                            } while(numbers.contains(random));

                            numbers.set(2, random);

                            cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(2), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                            //cardImage = getRoundedCornerBitmap(cardImage);
                            iv_card3.setImageBitmap(cardImage);
                            cardImage = null;
                        }

                        if(!hold4) {
                            int random;
                            do {
                                random = randomGenerator .nextInt(51);
                            } while(numbers.contains(random));

                            numbers.set(3, random);

                            cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(3), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                            //cardImage = getRoundedCornerBitmap(cardImage);
                            iv_card4.setImageBitmap(cardImage);
                            cardImage = null;
                        }

                        if(!hold5) {
                            int random;
                            do {
                                random = randomGenerator .nextInt(51);
                            } while(numbers.contains(random));

                            numbers.set(4, random);

                            cardImage = decodeSampledBitmapFromResource(getResources(), getResourceID("img_level_" + level + "_" + numbers.get(4), "drawable", this), iv_card1.getWidth(), iv_card1.getHeight());
                            //cardImage = getRoundedCornerBitmap(cardImage);
                            iv_card5.setImageBitmap(cardImage);
                            cardImage = null;
                        }

                        linLay_Hold.setVisibility(View.GONE);
                        txt_Info.setVisibility(View.VISIBLE);
                        verifyWinning();

                        if(win != 0)
                            btn_Play_GetMoney.setText("Get Money");
                        else
                            btn_Play_GetMoney.setText("Try again");
                    }
                    else if(btn_Play_GetMoney.getText().equals("Get Money") || btn_Play_GetMoney.getText().equals("Try again")) {
                        btn_Play_GetMoney.setText("Play");

                        txt_Info.setVisibility(View.VISIBLE);
                        btn_Raise_Up.setVisibility(View.VISIBLE);
                        btn_Raise_Down.setVisibility(View.VISIBLE);
                        txt_Info.setText("Please place your bet !");
                        credit += win;
                        String creditstring = Integer.toString(credit);
                        txt_Credit.setText(creditstring);

                        resetGame();
                    }
                }
                break;

            case R.id.btn_Double :
                Intent newActivity;
                //
                // Used to pass info between activities
                // The bundle is like a basket and you can put inside whatever you need for later use
                //
                Bundle winToPass = new Bundle();
                winToPass.putInt("actualWinning", win);
                winToPass.putInt("actualCredit", credit);
                winToPass.putInt("betCredit", betCredit);
                newActivity = new Intent("com.symplyfyweb.gherghina.pokypoker.DOUBLEACTIVITY");
                newActivity.putExtras(winToPass);
                startActivity(newActivity);
                finish();
                break;
        }
    }

    private void resetGame() {
        hold1 = false;
        hold2 = false;
        hold3 = false;
        hold4 = false;
        hold5 = false;
        if(credit > betCredit)
            credit -= betCredit;
        else {
            betCredit = credit;
            credit = 0;
        }
        txt_Bet.setText("Your bet is " + betCredit);
        String creditstring = Integer.toString(credit);
        txt_Credit.setText(creditstring);
        if(deck_card == null)
            deck_card = decodeSampledBitmapFromResource(getResources(), getResourceID("img_card_deck_" + level, "drawable", this), 100, 110);
        deck_card = getRoundedCornerBitmap(deck_card);
        iv_card1.setImageBitmap(deck_card);
        iv_card2.setImageBitmap(deck_card);
        iv_card3.setImageBitmap(deck_card);
        iv_card4.setImageBitmap(deck_card);
        iv_card5.setImageBitmap(deck_card);
        btn_Double.setEnabled(false);
        multiplierWin = 0;
        cardImage = null;
    }

    private void verifyWinning() {
        btn_Double.setEnabled(true);

        if(verifyRoyalStraight()) {
            txt_Bet.setText("Royal");
            multiplierWin = 16;
        }
        else
            if(verifyFourOfAKind()) {
                txt_Bet.setText("Four of a kind");
                multiplierWin = 14;
            }
            else
                if(verifyFull()) {
                    txt_Bet.setText("Full House");
                    multiplierWin = 12;
                }
                else
                    if(verifyFlush()) {
                        txt_Bet.setText("Flush");
                        multiplierWin = 10;
                    }
                    else
                        if(verifyStraight()) {
                            txt_Bet.setText("Straight");
                            multiplierWin = 8;
                        }
                        else
                            if(verifyThreeOfAKind()) {
                                txt_Bet.setText("Three of a kind");
                                multiplierWin = 6;
                            }
                            else
                                if(verifyTwoPairs()) {
                                    txt_Bet.setText("Two Pairs");
                                    multiplierWin = 4;
                                }
                                else
                                    if(verifyOnePair()) {
                                        txt_Bet.setText("One Pair");
                                        multiplierWin = 2;
                                    }
                                    else {
                                        txt_Bet.setText("Nothing");
                                        btn_Double.setEnabled(false);
                                    }
        win = betCredit * multiplierWin;
        txt_Info.setText("You won : " + win);
    }

    private boolean verifyFlush() {
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) % 4;
        n2 = numbers.get(1) % 4;
        n3 = numbers.get(2) % 4;
        n4 = numbers.get(3) % 4;
        n5 = numbers.get(4) % 4;
        return (n1 == n2 && n1 == n3 && n1 == n4 && n1 == n5);
    }

    private boolean verifyFourOfAKind() {
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;
        return ((n1 == n2 && n1 == n3 && n1 == n4) ||
        (n1 == n2 && n1 == n3 && n1 == n5) ||
                (n1 == n2 && n1 == n4 && n1 == n5) ||
                (n1 == n3 && n1 == n4 && n1 == n5) ||
                (n2 == n3 && n2 == n4 && n2 == n5));
    }

    private boolean verifyFull() {
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;

        return ((n1 == n2 && n1 == n3 && n4 == n5) ||
                (n1 == n2 && n1 == n4 && n3 == n5) ||
                (n1 == n3 && n1 == n4 && n2 == n5) ||
                (n2 == n3 && n2 == n4 && n1 == n5) ||
                (n1 == n2 && n1 == n5 && n3 == n4) ||
                (n1 == n4 && n1 == n5 && n2 == n3) ||
                (n1 == n2 && n3 == n4 && n3 == n5) ||
                (n1 == n4 && n2 == n3 && n2 == n5) ||
                (n1 == n3 && n2 == n4 && n2 == n5) ||
                (n1 == n3 && n1 == n5 && n2 == n4));
    }

    private boolean verifyStraight() {
        Collections.sort(numbers);
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;
        return ((n2 - n1 == 1 && n3 - n1 == 2 && n4 - n1 == 3 && n5 - n1 == 4) ||
                (n1 == 0 && n2 == 1 && n3 == 2 && n4 == 3 && n5 == 12));
    }

    private boolean verifyRoyalStraight() {
        return (verifyFlush() && verifyStraight());
    }

    private boolean verifyThreeOfAKind() {
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;

        return ((n1 == n2 && n1 == n3) ||
                (n1 == n2 && n1 == n4) ||
                (n1 == n2 && n1 == n5) ||
                (n2 == n3 && n2 == n4) ||
                (n2 == n3 && n2 == n5) ||
                (n3 == n4 && n3 == n5));
    }

    private boolean verifyTwoPairs() {
        Collections.sort(numbers);
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;
        return ((n2 == n3 && n4 == n5) ||
                (n1 == n2 && n4 == n5) ||
                (n1 == n2 && n3 == n4));
    }

    private boolean verifyOnePair() {
        int n1, n2, n3, n4, n5;
        n1 = numbers.get(0) / 4;
        n2 = numbers.get(1) / 4;
        n3 = numbers.get(2) / 4;
        n4 = numbers.get(3) / 4;
        n5 = numbers.get(4) / 4;
        return (n1 == n2 || n1 == n3 || n1 == n4 || n1 == n5 ||
                n2 == n3 || n2 == n4 || n2 == n5 ||
                n3 == n4 || n3 == n5 ||
                n4 == n5);
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
        final float roundPx = 35;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        iv_card1.setBackground(null);
        iv_card2.setBackground(null);
        iv_card3.setBackground(null);
        iv_card4.setBackground(null);
        iv_card5.setBackground(null);
        deck_card = null;
        cardImage = null;
        clearArrayList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deck_card = null;
        cardImage = null;
        clearArrayList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deck_card = null;
        cardImage = null;
        clearArrayList();
        if(betCredit != 0)
            credit += betCredit;
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_credit), credit);

        // Commit the edits!
        //editor.commit();
        editor.apply();
    }
}

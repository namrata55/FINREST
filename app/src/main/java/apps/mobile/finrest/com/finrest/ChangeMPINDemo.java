package apps.mobile.finrest.com.finrest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

public class ChangeMPINDemo extends Activity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {

    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;

    EditText newMpinEditText1, newMpinEditText2, newMpinEditText3, newMpinEditText4, newMpinEditText;
    EditText cnfrmMpinEditText1, cnfrmMpinEditText2, cnfrmMpinEditText3, cnfrmMpinEditText4;

    ConnectionClass connectionClass;
    String customerid;

    String MPIN;
    String OLDMPIN;
    String NEWMPIN;
    String CNFRMMPIN;
    Button changeMpinBtn;


    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Initialize EditText fields.
     */
    private void init() {
        mPinFirstDigitEditText = (EditText) findViewById(R.id.pin_first_edittext);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pin_second_edittext);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pin_third_edittext);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pin_forth_edittext);
        mPinHiddenEditText = (EditText) findViewById(R.id.pin_hidden_edittext);

        newMpinEditText1 = (EditText) findViewById(R.id.new_mpin1);
        newMpinEditText2 = (EditText) findViewById(R.id.new_mpin2);
        newMpinEditText3 = (EditText) findViewById(R.id.new_mpin3);
        newMpinEditText4 = (EditText) findViewById(R.id.new_mpin4);

        cnfrmMpinEditText1 = (EditText) findViewById(R.id.cnfrm_mpin1);
        cnfrmMpinEditText2 = (EditText) findViewById(R.id.cnfrm_mpin2);
        cnfrmMpinEditText3 = (EditText) findViewById(R.id.cnfrm_mpin3);
        cnfrmMpinEditText4 = (EditText) findViewById(R.id.cnfrm_mpin4);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MainLayout(ChangeMPINDemo.this, null));


        Window window = ChangeMPINDemo.this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ChangeMPINDemo.this.getResources().getColor(R.color.light_blue));
        }

        changeMpinBtn = (Button) findViewById(R.id.change_mpin);
        changeMpinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        changeMpinBtn.setEnabled(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Change MPIN");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        connectionClass = new ConnectionClass();
        SharedPreferences Customer_Details = getApplicationContext().getSharedPreferences("customer_details", MODE_PRIVATE);
        customerid = Customer_Details.getString("customer_id", "");

        init();
        setPINListeners();

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        showSoftKeyboard(mPinHiddenEditText);

        switch (id) {
            case R.id.pin_first_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_second_edittext:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_third_edittext:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.new_mpin1:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.new_mpin2:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.new_mpin3:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.new_mpin4:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.cnfrm_mpin1:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.cnfrm_mpin2:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.cnfrm_mpin3:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.cnfrm_mpin4:
                if (hasFocus) {
                    System.out.println(mPinHiddenEditText.getText());
                    System.out.println(mPinHiddenEditText.getText().length());
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {

                        if (mPinHiddenEditText.getText().length() == 12){
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
                            cnfrmMpinEditText4.setText("");

                        }
                        else if (mPinHiddenEditText.getText().length() == 11){
                            cnfrmMpinEditText3.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 10){
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
                            cnfrmMpinEditText2.setText("");
                        }
                        else if (mPinHiddenEditText.getText().length() == 9){
                            cnfrmMpinEditText1.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
                        }
                        else if (mPinHiddenEditText.getText().length() == 8){
                            newMpinEditText4.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
                        }
                        else if (mPinHiddenEditText.getText().length() == 7){
                            newMpinEditText3.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 6){
                            newMpinEditText2.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 5){
                            newMpinEditText1.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 4){
                            mPinForthDigitEditText.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 3){
                            mPinThirdDigitEditText.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 2){
                            mPinSecondDigitEditText.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

                        }
                        else if (mPinHiddenEditText.getText().length() == 1){
                            mPinFirstDigitEditText.setText("");
                            changeMpinBtn.setEnabled(false);
                            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
                        }

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);

        setDefaultPinBackground(newMpinEditText1);
        setDefaultPinBackground(newMpinEditText2);
        setDefaultPinBackground(newMpinEditText3);
        setDefaultPinBackground(newMpinEditText4);

        setDefaultPinBackground(cnfrmMpinEditText1);
        setDefaultPinBackground(cnfrmMpinEditText2);
        setDefaultPinBackground(cnfrmMpinEditText3);
        setDefaultPinBackground(cnfrmMpinEditText4);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
            changeMpinBtn.setEnabled(false);

        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 4) {
            setFocusedPinBackground(newMpinEditText1);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 5) {
            setFocusedPinBackground(newMpinEditText2);
            newMpinEditText1.setText(s.charAt(4) + "");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 6) {
            setFocusedPinBackground(newMpinEditText3);
            newMpinEditText2.setText(s.charAt(5) + "");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 7) {
            setFocusedPinBackground(newMpinEditText4);
            newMpinEditText3.setText(s.charAt(6) + "");
            newMpinEditText4.setText("");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 8) {
            setFocusedPinBackground(cnfrmMpinEditText1);
            newMpinEditText4.setText(s.charAt(7) + "");
            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 9) {
            setFocusedPinBackground(cnfrmMpinEditText2);
            cnfrmMpinEditText1.setText(s.charAt(8) + "");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);
        } else if (s.length() == 10) {
            setFocusedPinBackground(cnfrmMpinEditText3);
            cnfrmMpinEditText2.setText(s.charAt(9) + "");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 11) {
            setFocusedPinBackground(cnfrmMpinEditText4);
            cnfrmMpinEditText3.setText(s.charAt(10) + "");
            cnfrmMpinEditText4.setText("");
            changeMpinBtn.setEnabled(false);
            changeMpinBtn.setBackgroundResource(R.drawable.light_blue_button_rounded);

        } else if (s.length() == 12) {
            setDefaultPinBackground(cnfrmMpinEditText4);
            cnfrmMpinEditText4.setText(s.charAt(11) + "");
            MPIN = s.toString();

            OLDMPIN = String.valueOf(MPIN.charAt(0)) + "" + String.valueOf(MPIN.charAt(1)) + "" + String.valueOf(MPIN.charAt(2)) + "" + String.valueOf(MPIN.charAt(3)) + "";
            NEWMPIN = String.valueOf(MPIN.charAt(4)) + "" + String.valueOf(MPIN.charAt(5)) + "" + String.valueOf(MPIN.charAt(6)) + "" + String.valueOf(MPIN.charAt(7)) + "";
            CNFRMMPIN = String.valueOf(MPIN.charAt(8)) + "" + String.valueOf(MPIN.charAt(9)) + "" + String.valueOf(MPIN.charAt(10)) + "" + String.valueOf(MPIN.charAt(11)) + "";

            System.out.println("final mpin====" + MPIN);
            System.out.println("final mpin====" + OLDMPIN);
            System.out.println("final mpin====" + NEWMPIN);
            System.out.println("final mpin====" + CNFRMMPIN);
            changeMpinBtn.setBackgroundResource(R.drawable.blue_button_rounded);
            changeMpinBtn.setEnabled(true);
            hideSoftKeyboard(mPinForthDigitEditText);
        }

    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.round_edittext));
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.blue_border_rounded_rectangle));
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);

        newMpinEditText1.setOnFocusChangeListener(this);
        newMpinEditText2.setOnFocusChangeListener(this);
        newMpinEditText3.setOnFocusChangeListener(this);
        newMpinEditText4.setOnFocusChangeListener(this);

        newMpinEditText1.setOnKeyListener(this);
        newMpinEditText2.setOnKeyListener(this);
        newMpinEditText3.setOnKeyListener(this);
        newMpinEditText4.setOnKeyListener(this);

        cnfrmMpinEditText1.setOnFocusChangeListener(this);
        cnfrmMpinEditText2.setOnFocusChangeListener(this);
        cnfrmMpinEditText3.setOnFocusChangeListener(this);
        cnfrmMpinEditText4.setOnFocusChangeListener(this);

        cnfrmMpinEditText1.setOnKeyListener(this);
        cnfrmMpinEditText2.setOnKeyListener(this);
        cnfrmMpinEditText3.setOnKeyListener(this);
        cnfrmMpinEditText4.setOnKeyListener(this);
    }

    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }


    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * Custom LinearLayout with overridden onMeasure() method
     * for handling software keyboard show and hide events.
     */
    public class MainLayout extends LinearLayout {

        public MainLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.change_mpindemo, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int proposedHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            Log.d("TAG", "proposed: " + proposedHeight + ", actual: " + actualHeight);

            if (actualHeight >= proposedHeight) {
                // Keyboard is shown
                if (mPinHiddenEditText.length() == 0)
                    setFocusedPinBackground(mPinFirstDigitEditText);
                else
                    setDefaultPinBackground(mPinFirstDigitEditText);
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_mpin_action_bar, menu);
        return true;
    }

    void validation() {


        if (NEWMPIN.equals(OLDMPIN)) {
            Toast toast = Toast.makeText(getApplicationContext(), "New MPIN and old MPIN are same ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");

            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");

            mPinHiddenEditText.setText("");
            mPinHiddenEditText.requestFocus();

        } else if (!NEWMPIN.equals(CNFRMMPIN)) {
            Toast toast = Toast.makeText(getApplicationContext(), "New MPIN and cnfirm MPIN are not same ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            cnfrmMpinEditText1.setText("");
            cnfrmMpinEditText2.setText("");
            cnfrmMpinEditText3.setText("");
            cnfrmMpinEditText4.setText("");

            newMpinEditText1.setText("");
            newMpinEditText2.setText("");
            newMpinEditText3.setText("");
            newMpinEditText4.setText("");

            mPinHiddenEditText.requestFocus();
            showSoftKeyboard(mPinHiddenEditText);

            mPinFirstDigitEditText.setText(String.valueOf(MPIN.charAt(0)));
            mPinHiddenEditText.setText(mPinFirstDigitEditText.getText());
            mPinSecondDigitEditText.setText(String.valueOf(MPIN.charAt(1)));
            mPinHiddenEditText.setText(mPinHiddenEditText.getText()+""+mPinSecondDigitEditText.getText());
            mPinThirdDigitEditText.setText(String.valueOf(MPIN.charAt(2)));
            mPinHiddenEditText.setText(mPinHiddenEditText.getText()+""+mPinThirdDigitEditText.getText());
            mPinForthDigitEditText.setText(String.valueOf(MPIN.charAt(3)));
            mPinHiddenEditText.setText(mPinHiddenEditText.getText()+""+mPinForthDigitEditText.getText());

            System.out.println(MPIN.charAt(0));
            System.out.println(MPIN.charAt(1));
            System.out.println(MPIN.charAt(2));
            System.out.println(MPIN.charAt(3));

            System.out.println("Oldmpin:"+OLDMPIN);
            System.out.println("mPinHiddenEditText size:"+mPinHiddenEditText.getText().length());
            System.out.println("mPinHiddenEditText size:"+mPinHiddenEditText.getText());

        } else if (NEWMPIN.equals(CNFRMMPIN)) {
            ChangeMPIN1 changeMPIN = new ChangeMPIN1();
            changeMPIN.execute();
        }
    }

    public class ChangeMPIN1 extends AsyncTask<String, String, String> {

        String z = "";
        Boolean isSuccess = false;
        private Dialog loadingDialog;

        @Override
        protected void onPreExecute() {
            loadingDialog = createProgressDialog(ChangeMPINDemo.this);
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SharedPreferences Database_Details = ChangeMPINDemo.this.getSharedPreferences("database_details", MODE_PRIVATE);
                String _DB=Database_Details.getString("database_name","");
                String _server=Database_Details.getString("database_ip_address","");
                String _user=Database_Details.getString("database_username","");
                String _pass=Database_Details.getString("database_password","");
//                String _server = "10.0.2.2";
//                String _DB = "CIS_FINREST_KRCMigrate";
//                String _user = "sa";
//                String _pass = "Pa$$w0rd";
                Connection con = connectionClass.CONN(_server,_DB,_user,_pass,"SQLEXPRESS");
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {
                    String query = "update CIS_FINREST_CUSTOMER set mpin='" + NEWMPIN + "'where customer_id='" + customerid + "' and mpin='" + OLDMPIN + "'";
                    System.out.println("query" + query);
                    Statement stmt = con.createStatement();
                    int i = stmt.executeUpdate(query);
                    if (i > 0) {

                        z = "Login successfull";
                        isSuccess = true;
                    } else {
                        z = "Invalid Credentials";
                        isSuccess = false;
                    }
                }
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions";
            }

            return z;
        }

        @Override
        protected void onPostExecute(String r) {
            loadingDialog.dismiss();

            if (isSuccess) {
                Toast toast = Toast.makeText(getApplicationContext(), "MPIN has been changed successfully", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

                System.out.println("Size::::::"+mPinHiddenEditText.length());
                Intent i = new Intent(ChangeMPINDemo.this, LoginDemo.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(i);

            } else {

                Toast toast = Toast.makeText(getApplicationContext(), "Invalid old MPIN", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                cnfrmMpinEditText1.setText("");
                cnfrmMpinEditText2.setText("");
                cnfrmMpinEditText3.setText("");
                cnfrmMpinEditText4.setText("");

                newMpinEditText1.setText("");
                newMpinEditText2.setText("");
                newMpinEditText3.setText("");
                newMpinEditText4.setText("");


                mPinFirstDigitEditText.setText("");
                mPinSecondDigitEditText.setText("");
                mPinThirdDigitEditText.setText("");
                mPinForthDigitEditText.setText("");

                mPinHiddenEditText.setText("");
                mPinHiddenEditText.requestFocus();


                showSoftKeyboard(mPinHiddenEditText);

            }

        }
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        // dialog.setMessage(Message);
        return dialog;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


    //-----------------------------BROADCAST RECEIVER----------------------------------
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (networkInfo != null && networkInfo.isConnected()) {

            } else {
                final Dialog dialog = new Dialog(ChangeMPINDemo.this);

                //setting custom layout to dialog
                dialog.setContentView(R.layout.no_internet_alert);
                dialog.setTitle("Custom Dialog");

                Button dismissButton = (Button) dialog.findViewById(R.id.button);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        registerReceiver(mConnReceiver,
                                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    }
                });
                dialog.show();
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mConnReceiver);
        if(isApplicationSentToBackground(this))
        {
            finishAffinity();
        }
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }


}

package com.jonny.passwordgenerator;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MainFragment()).commit();
        }
    }

    public static class MainFragment extends Fragment {
        private CheckBox lowercase, uppercase, numbers, underscore, symbols;
        private TextView hintTextView, passwordTextView, copyTextView;
        private Button generate;
        private Spinner spinner;

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);
            setupActionBar();
            setupCheckBoxes(view);
            setupGenerateButton(view);
            spinner = ((Spinner)view.findViewById(R.id.spinner));
            ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.length_options, R.layout.spinner_item);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(arrayAdapter);
            return view;
        }

        private void setupActionBar() {
            ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setTitle(R.string.app_name);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintColor(Color.parseColor("#FFAF44"));
            }
        }

        @TargetApi(19)
        private void setTranslucentStatus(boolean on) {
            Window win = getActivity().getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) winParams.flags |= bits;
            else winParams.flags &= ~bits;
            win.setAttributes(winParams);
        }

        private void setupCheckBoxes(View view) {
            lowercase = (CheckBox)view.findViewById(R.id.lowercaseCheckBox);
            lowercase.setChecked(getFromSP("lowercase"));
            lowercase.setOnCheckedChangeListener(checkboxHandler);
            uppercase = (CheckBox)view.findViewById(R.id.uppercaseCheckBox);
            uppercase.setChecked(getFromSP("uppercase"));
            uppercase.setOnCheckedChangeListener(checkboxHandler);
            numbers = (CheckBox)view.findViewById(R.id.numbersCheckBox);
            numbers.setChecked(getFromSP("numbers"));
            numbers.setOnCheckedChangeListener(checkboxHandler);
            underscore = (CheckBox)view.findViewById(R.id.underscoreCheckBox);
            underscore.setChecked(getFromSP("underscore"));
            underscore.setOnCheckedChangeListener(checkboxHandler);
            symbols = (CheckBox)view.findViewById(R.id.symbolsCheckBox);
            symbols.setChecked(getFromSP("symbols"));
            symbols.setOnCheckedChangeListener(checkboxHandler);
            if (!lowercase.isChecked() && !uppercase.isChecked() && !numbers.isChecked() && !underscore.isChecked() && !symbols.isChecked()) {
                lowercase.setChecked(true);
                saveInSP("lowercase", true);
            }
        }

        private boolean getFromSP(String key){
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            return preferences.getBoolean(key, false);
        }

        private void saveInSP(String key, boolean value){
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.edit().putBoolean(key, value).apply();
        }

        private void checkChecked(String key, Boolean isChecked) {
            if (!lowercase.isChecked() && !uppercase.isChecked() && !numbers.isChecked() && !underscore.isChecked() && !symbols.isChecked()) {
                if (key.equals("lowercase")) lowercase.setChecked(true);
                else if (key.equals("uppercase")) uppercase.setChecked(true);
                else if (key.equals("numbers")) numbers.setChecked(true);
                else if (key.equals("underscore")) underscore.setChecked(true);
                else if (key.equals("symbols")) symbols.setChecked(true);
                saveInSP(key, true);
                Toast.makeText(getActivity(), R.string.checkbox_error, Toast.LENGTH_SHORT).show();
            } else {
                saveInSP(key, isChecked);
            }
        }

        @SuppressWarnings("deprecation")
        private void setupGenerateButton(View view) {
            generate = (Button)view.findViewById(R.id.generateButton);
            generate.setOnClickListener(clickHandler);
            hintTextView = (TextView)view.findViewById(R.id.hintTextView);
            passwordTextView = (TextView)view.findViewById(R.id.passwordTextView);
            passwordTextView.setOnClickListener(clickHandler);
            copyTextView = (TextView)view.findViewById(R.id.copyTextView);
        }

        private String csRandomAlphaNumericString(int numChars) {
            String chars = "";
            if (lowercase.isChecked()) chars = chars + "abcdefghijklmnopqrstuvwxyz";
            if (uppercase.isChecked()) chars = chars + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            if (numbers.isChecked()) chars = chars + "0123456879";
            if (underscore.isChecked()) chars = chars + "_";
            if (symbols.isChecked()) chars = chars + "!@#$%^&*(){}[],.;:><'?/|\\\\-=+";
            final char[] VALID_CHARACTERS = chars.toCharArray();
            SecureRandom secureRandom = new SecureRandom();
            Random random = new Random();
            final char[] buff = new char[numChars];
            for (int i = 0; i < numChars; ++i) {
                if ((i % 10) == 0) random.setSeed(secureRandom.nextLong());
                buff[i] = VALID_CHARACTERS[random.nextInt(VALID_CHARACTERS.length)];
            }
            return new String(buff);
        }

        CompoundButton.OnCheckedChangeListener checkboxHandler = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch(buttonView.getId()) {
                    case R.id.lowercaseCheckBox:
                        checkChecked("lowercase", isChecked);
                        break;
                    case R.id.uppercaseCheckBox:
                        checkChecked("uppercase", isChecked);
                        break;
                    case R.id.numbersCheckBox:
                        checkChecked("numbers", isChecked);
                        break;
                    case R.id.underscoreCheckBox:
                        checkChecked("underscore", isChecked);
                        break;
                    case R.id.symbolsCheckBox:
                        checkChecked("symbols", isChecked);
                        break;
                }
            }
        };

        @SuppressWarnings("deprecation")
        View.OnClickListener clickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.passwordTextView:
                        if (passwordTextView.getText() == null) return;
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(passwordTextView.getText().toString());
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText(getActivity().getResources().getString(R.string.password), passwordTextView.getText().toString());
                            clipboard.setPrimaryClip(clip);
                        }
                        Toast.makeText(getActivity(), R.string.password_copied, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.generateButton:
                        int length = Integer.parseInt(getResources().getStringArray(R.array.length_options)[spinner.getSelectedItemPosition()]);
                        String generatedPassword = csRandomAlphaNumericString(length);
                        if (hintTextView.getVisibility() == View.VISIBLE) {
                            hintTextView.setVisibility(View.INVISIBLE);
                            passwordTextView.setText(generatedPassword);
                            copyTextView.setVisibility(View.VISIBLE);
                        } else {
                            passwordTextView.setText(generatedPassword);
                        }
                        break;
                }
            }
        };
    }
}
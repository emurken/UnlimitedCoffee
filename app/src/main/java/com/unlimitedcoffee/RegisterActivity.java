package com.unlimitedcoffee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import static com.google.i18n.phonenumbers.PhoneNumberUtil.*;

public class RegisterActivity extends AppCompatActivity {

    SessionPreferences session;
    EditText mTextPhoneNumber;
    EditText mTextPassword;
    EditText mTextCfPassword;
    Button mRegisterButton;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Instance of session to establish log in status
        session = new SessionPreferences(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instance of db helper
        db = new DatabaseHelper(this);

        mTextPhoneNumber = (EditText) findViewById(R.id.edittext_username);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mTextCfPassword = (EditText) findViewById(R.id.edittext_cf_password);
        mRegisterButton = (Button) findViewById(R.id.register_button);

        //OnClickLister for Register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = mTextPhoneNumber.getText().toString().trim();
                String password = mTextPassword.getText().toString().trim();
                String cf_password = mTextCfPassword.getText().toString().trim();

                //Validating phone number
                if(isValidMobile(phoneNumber)) {
                    //Checks if phone number is already registered
                    if(!db.checkPhone(phoneNumber)) {
                        //Validating password confirmation
                        if (password.equals(cf_password)) {
                            session.createLoginSession(phoneNumber);
                            db.addUser(phoneNumber, password);
                            Intent smsApp = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(smsApp);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "Phone number already registered.", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(RegisterActivity.this, "Invalid phone number. Please include country code. E.g. \"1 + phone number for US\"", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*
    Method to validate phone numbers. Returns true if phone number is valid.
     */
    private boolean isValidMobile(String phone) {

        boolean isValid = false;

        PhoneNumberUtil phoneUtil = getInstance();

        try {

            Phonenumber.PhoneNumber parsedNumber = phoneUtil.parse( "+"+phone,"");
            isValid = getInstance().isValidNumber(parsedNumber);

        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return isValid;
    }
}

package com.example.picobank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class OTP extends AppCompatActivity {
    /* access modifiers changed from: private */
    public EditText otpDigit1;
    /* access modifiers changed from: private */
    public EditText otpDigit2;
    /* access modifiers changed from: private */
    public EditText otpDigit3;
    /* access modifiers changed from: private */
    public EditText otpDigit4;
    private RequestQueue requestQueue;
    private Button submitOtpButton;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OTP$$ExternalSyntheticLambda0());
        this.otpDigit1 = (EditText) findViewById(R.id.otpDigit1);
        this.otpDigit2 = (EditText) findViewById(R.id.otpDigit2);
        this.otpDigit3 = (EditText) findViewById(R.id.otpDigit3);
        this.otpDigit4 = (EditText) findViewById(R.id.otpDigit4);
        this.submitOtpButton = (Button) findViewById(R.id.submitOtpButton);
        this.requestQueue = Volley.newRequestQueue(this);
        this.submitOtpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                OTP.this.verifyOtp(OTP.this.otpDigit1.getText().toString() + OTP.this.otpDigit2.getText().toString() + OTP.this.otpDigit3.getText().toString() + OTP.this.otpDigit4.getText().toString());
            }
        });
    }

    static /* synthetic */ WindowInsetsCompat lambda$onCreate$0(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    /* access modifiers changed from: private */
    public void verifyOtp(String otp) {
        String endpoint = "your server url" + "/verify-otp";
        if (getResources().getString(R.string.otp_value).equals(otp)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid OTP", 0).show();
        }
        JSONObject postData = new JSONObject();
        try {
            postData.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.requestQueue.add(new JsonObjectRequest(1, endpoint, postData, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        String flag = response.getString("flag");
                        String hint = response.getString("hint");
                        Intent intent = new Intent(OTP.this, MainActivity.class);
                        intent.putExtra("flag", flag);
                        intent.putExtra("hint", hint);
                        OTP.this.startActivity(intent);
                        OTP.this.finish();
                        return;
                    }
                    Toast.makeText(OTP.this, "Invalid OTP", 0).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }));
    }
}

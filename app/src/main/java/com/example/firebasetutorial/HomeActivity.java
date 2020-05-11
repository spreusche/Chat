package com.example.firebasetutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

enum ProviderType {
    BASIC
}

public class HomeActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView providerTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logoutButton = findViewById(R.id.logoutButton);
        emailTextView = findViewById(R.id.emailTextView);
        providerTextView = findViewById(R.id.providerTextView);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String email = bundle.getString("email");
        String provider = bundle.getString("provider");
        setup(email, provider);
    }

    private void setup(String email, String provider) {
        setTitle("Home");

        emailTextView.setText(email);
        providerTextView.setText(provider);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });
    }
}

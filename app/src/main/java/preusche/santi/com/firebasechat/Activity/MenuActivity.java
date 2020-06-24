package preusche.santi.com.firebasechat.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import preusche.santi.com.firebasechat.Persistencia.UserDAO;
import preusche.santi.com.firebasechat.R;

public class MenuActivity extends AppCompatActivity {

    private Button btnSeeUsers;
    private Button btnLogOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnSeeUsers = findViewById(R.id.btnSeeUsers);
        btnLogOut = findViewById(R.id.btnCloseSession);

        setTitle("Menu");

        btnSeeUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SeeUsersActivity.class);
                startActivity(intent);
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                returnLogin();
            }
        });

    }

    private void returnLogin(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UserDAO.getInstance().isUserLogged()){
            //el usuario esta logeado y hacemos algo
        }else{
            returnLogin();
        }
    }

}

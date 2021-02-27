package sk.shopking.shopkingapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.model.User;
import sk.shopking.shopkingapp.model.UserAdministrator;
import sk.shopking.shopkingapp.model.UserPokladnik;
import sk.shopking.shopkingapp.tools.Database;

public class LoginActivity extends AppCompatActivity {

    private static User prihlaseny = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = findViewById(R.id.username);
        final EditText etPassword = findViewById(R.id.password);
        final Button bLogin = findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prihlaseny = Database.loginUser(etUsername.getText().toString(), etPassword.getText().toString());
                if (prihlaseny == null){
                    Toast.makeText(getApplicationContext(),R.string.login_failed,Toast.LENGTH_LONG).show();
                }
                else{
                    if (prihlaseny instanceof UserPokladnik){
                        String pokladnica = Database.getIPPokladnikaOnline(prihlaseny.getId());
                        if (pokladnica != null){
                            Intent intent = new Intent(getApplicationContext(),PokladnikScannerActivity.class);
                            intent.putExtra("IP",pokladnica);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),R.string.no_logged_treasury,Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (prihlaseny instanceof UserAdministrator){
                        Toast.makeText(getApplicationContext(),R.string.for_admin_unavailable,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public static User getUser(){
        return prihlaseny;
    }
}

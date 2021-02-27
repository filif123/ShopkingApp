package sk.shopking.shopkingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.tools.CacheFile;
import sk.shopking.shopkingapp.tools.Database;
import sk.shopking.shopkingapp.tools.VyhladavanieServera;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_CAMERA = 1;

    private TextView tvStatus;
    private TextView tvLogin;
    private Button bCustomer,bReload;

    private CacheFile cf;

    private static int idZakaznik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tvStatus = findViewById(R.id.tvStatus);
        tvLogin = findViewById(R.id.tvLogin);
        bCustomer = findViewById(R.id.bCustomer);
        bReload = findViewById(R.id.bReload);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
        }

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idZakaznik = Database.createNewCustomer();
                Intent intent = new Intent(getApplicationContext(), ZakaznikHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadServer();
            }
        });

        try {
            cf = new CacheFile(getApplicationContext());
            cf.readFile();
            Database.addressToServer = cf.getIpAddress();

            if (Database.isConnectionAvailable()){
                tvStatus.setText("");
                tvLogin.setVisibility(View.VISIBLE);
                bCustomer.setVisibility(View.VISIBLE);
                bReload.setVisibility(View.INVISIBLE);
            }
            else{
                loadServer();
            }
        }
        catch (FileNotFoundException e){
            loadServer();
        }
    }

    private void loadServer(){
        try {
            VyhladavanieServera vyhladavanieServera = new VyhladavanieServera();
            String ip = vyhladavanieServera.discoverServer("S");
            if (ip != null){
                Database.addressToServer = ip;
                cf.setIpAddress(ip);
                cf.saveFile();
                vyhladavanieServera.close();
            }
            else {
                Toast.makeText(getApplicationContext(),R.string.server_not_found,Toast.LENGTH_LONG).show();
                tvStatus.setText(R.string.server_not_found);
                tvLogin.setVisibility(View.INVISIBLE);
                bCustomer.setVisibility(View.INVISIBLE);
                bReload.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),getString(R.string.unuexpected_error,e.toString()),Toast.LENGTH_LONG).show();
            tvStatus.setText(getString(R.string.unuexpected_error,e.toString()));
            tvLogin.setVisibility(View.INVISIBLE);
            bCustomer.setVisibility(View.INVISIBLE);
            bReload.setVisibility(View.VISIBLE);
        }
    }

    public static int getIdZakaznik(){
        return idZakaznik;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),R.string.permission_explanation_camera,Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA);
            }
        }
    }
}

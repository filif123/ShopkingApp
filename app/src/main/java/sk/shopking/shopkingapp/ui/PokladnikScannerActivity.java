package sk.shopking.shopkingapp.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.model.Tovar;
import sk.shopking.shopkingapp.tools.Database;
import sk.shopking.shopkingapp.tools.SendDataToPC;
import sk.shopking.shopkingapp.tools.ShopKingTools;

import static sk.shopking.shopkingapp.tools.Database.getSpecificTovar;

public class PokladnikScannerActivity extends AppCompatActivity {

    TextView barcodeInfo, tvTovarName;
    SurfaceView cameraView;
    CameraSource cameraSource;
    TableRow tbOznacenie;

    String ziskanyKod;
    boolean scanning = true;

    String ipPokladnice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokladnik_scanner);

        cameraView = findViewById(R.id.camera_view);
        barcodeInfo = findViewById(R.id.txtContent);
        tvTovarName = findViewById(R.id.tvTovarName);
        tbOznacenie = findViewById(R.id.trOznacenie);

        Intent intent = getIntent();
        this.ipPokladnice = intent.getStringExtra("IP");

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)//Barcode.QR_CODE
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

    }

    class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new MyBarcodeTracker();
        }
    }

    class MyBarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionResults, final Barcode barcode) {
            barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                public void run() {
                    if (scanning){
                        ziskanyKod = barcode.displayValue;
                        barcodeInfo.setText(barcode.displayValue);

                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP);

                        if (ShopKingTools.isNumberLong(ziskanyKod)){
                            long bLong = Long.parseLong(ziskanyKod);
                            Tovar tovar = Database.getSpecificTovar(bLong);
                            if (tovar == null){
                                tvTovarName.setText(R.string.tovar_nenajdeny);
                            }
                            else {
                                tvTovarName.setText(tovar.getTovarName());
                            }
                            SendDataToPC sendDataToPC = new SendDataToPC(ipPokladnice);
                            sendDataToPC.sendData("" + bLong);
                        }
                        else {
                            tvTovarName.setText(R.string.neplatny_ean);
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                tbOznacenie.setBackgroundColor(getResources().getColor(R.color.ic_launcher_background));
                                barcodeInfo.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                                scanning = true;

                            }
                        },1000);
                        scanning = false;
                        tbOznacenie.setBackgroundColor(getResources().getColor(R.color.redScan));
                        barcodeInfo.setTextColor(getResources().getColor(R.color.white));
                    }
                }

            });
        }
    }
}

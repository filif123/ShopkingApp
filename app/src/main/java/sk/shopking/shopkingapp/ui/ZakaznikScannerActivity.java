package sk.shopking.shopkingapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.model.JednotkaType;
import sk.shopking.shopkingapp.model.NakupenyTovar;
import sk.shopking.shopkingapp.model.Tovar;
import sk.shopking.shopkingapp.model.TovarZlavaCena;
import sk.shopking.shopkingapp.model.TovarZlavaMnozstvo;
import sk.shopking.shopkingapp.tools.Database;
import sk.shopking.shopkingapp.tools.ShopKingTools;

public class ZakaznikScannerActivity extends AppCompatActivity {

    TextView tvTovarName;
    SurfaceView cameraView;
    CameraSource cameraSource;
    TableRow tbOznacenie;

    String ziskanyKod;
    long bLong;
    boolean scanning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaznik_scanner);

        cameraView = findViewById(R.id.camera_view);
        tvTovarName = findViewById(R.id.tvTovarName);
        tbOznacenie = findViewById(R.id.trOznacenie);

        /*Intent intent = getIntent();
        this.ipPokladnice = intent.getStringExtra("IP");*/

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

        ZakaznikScannerActivity.BarcodeTrackerFactory barcodeFactory = new ZakaznikScannerActivity.BarcodeTrackerFactory();
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

    }

    class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            return new ZakaznikScannerActivity.MyBarcodeTracker();
        }
    }

    class MyBarcodeTracker extends Tracker<Barcode> {
        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionResults, final Barcode barcode) {
            tvTovarName.post(new Runnable() {    // Use the post method of the TextView
                public void run() {
                    if (scanning){
                        ziskanyKod = barcode.displayValue;

                        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                        toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP);

                        if (ShopKingTools.isNumberLong(ziskanyKod)){
                            bLong = Long.parseLong(ziskanyKod);
                            Tovar tovar = Database.getSpecificTovar(bLong);
                            if (tovar == null){
                                tvTovarName.setText(R.string.tovar_nenajdeny);
                            }
                            else {
                                tvTovarName.setText(tovar.getTovarName());

                                if (tovar.getTovarJednotka().equals(JednotkaType.KS)){
                                    showNumberPickerDialog(tovar);
                                }
                                else{
                                    showEditTextDialog(tovar);
                                }
                            }


                        }
                        else {
                            tvTovarName.setText(R.string.neplatny_ean);
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                tbOznacenie.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                                tvTovarName.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                                scanning = true;

                            }
                        },1000);
                        scanning = false;
                        tbOznacenie.setBackgroundColor(getResources().getColor(R.color.redScan));
                        tvTovarName.setTextColor(getResources().getColor(R.color.white));
                    }
                }

            });
        }
    }

    private void showNumberPickerDialog(final Tovar t){
        final int[] mnozstvo = {0};
        RelativeLayout linearLayout = new RelativeLayout(this);
        final NumberPicker aNumberPicker = new NumberPicker(this);
        aNumberPicker.setMaxValue(20);
        aNumberPicker.setMinValue(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.choose_amount);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                mnozstvo[0] = aNumberPicker.getValue();

                                Intent output = new Intent();
                                output.putExtra("EAN",bLong);
                                output.putExtra("AMOUNT", (float)mnozstvo[0]);
                                output.putExtra("OBJECT",new Gson().toJson(t));
                                if (t instanceof TovarZlavaCena){
                                    output.putExtra("TYPE", 1);
                                }
                                else if (t instanceof TovarZlavaMnozstvo){
                                    output.putExtra("TYPE", 2);
                                }
                                else{
                                    output.putExtra("TYPE", 0);
                                }
                                setResult(RESULT_OK,output);
                                finish();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                mnozstvo[0] = 0;
                                dialog.cancel();

                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showEditTextDialog(final Tovar t){
        final float[] mnozstvo = {0};

        RelativeLayout linearLayout = new RelativeLayout(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(editText,numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.enter_amount);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                mnozstvo[0] = Float.parseFloat(editText.getText().toString());

                                Intent output = new Intent();
                                output.putExtra("EAN",bLong);
                                output.putExtra("AMOUNT", mnozstvo[0]);
                                output.putExtra("OBJECT",new Gson().toJson(t));
                                if (t instanceof TovarZlavaCena){
                                    output.putExtra("TYPE", 1);
                                }
                                else if (t instanceof TovarZlavaMnozstvo){
                                    output.putExtra("TYPE", 2);
                                }
                                else{
                                    output.putExtra("TYPE", 0);
                                }
                                setResult(RESULT_OK,output);
                                finish();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                mnozstvo[0] = 0;
                                dialog.cancel();

                                setResult(RESULT_CANCELED);
                                finish();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

package sk.shopking.shopkingapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import org.w3c.dom.Text;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.model.NakupAdapter;
import sk.shopking.shopkingapp.tools.BarcodeGenerator;
import sk.shopking.shopkingapp.tools.Database;

public class ZakaznikPayingActivity extends AppCompatActivity {

    private ImageView imageBarcode;
    private TextView tvCode;
    private Button bExitShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaznik_paying);

        imageBarcode = findViewById(R.id.imageBarcode);
        tvCode = findViewById(R.id.tvCode);
        bExitShop = findViewById(R.id.bExitShop);

        try {
            Bitmap obr = BarcodeGenerator.createBarcodeBitmap(MainActivity.getIdZakaznik(),400,100);
            imageBarcode.setImageBitmap(obr);
            tvCode.setText("" + MainActivity.getIdZakaznik());
        } catch (WriterException e) {
            e.printStackTrace();
        }

        bExitShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Database.isNakupZakaznikaUkonceny(MainActivity.getIdZakaznik())){
                    finish();
                }
                else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    finish();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ZakaznikPayingActivity.this);
                    builder.setMessage(R.string.question_exit_shopping).setPositiveButton(R.string.yes, dialogClickListener)
                            .setNegativeButton(R.string.no, dialogClickListener).show();
                }
            }
        });
    }
}

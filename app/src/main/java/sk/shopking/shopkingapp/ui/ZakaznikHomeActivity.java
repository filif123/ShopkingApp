package sk.shopking.shopkingapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import sk.shopking.shopkingapp.R;
import sk.shopking.shopkingapp.model.JednotkaType;
import sk.shopking.shopkingapp.model.NakupAdapter;
import sk.shopking.shopkingapp.model.NakupenyTovar;
import sk.shopking.shopkingapp.model.Tovar;
import sk.shopking.shopkingapp.model.TovarZlavaCena;
import sk.shopking.shopkingapp.model.TovarZlavaMnozstvo;
import sk.shopking.shopkingapp.tools.Database;

public class ZakaznikHomeActivity extends AppCompatActivity {

    private ListView lvItems;
    private Button bScan,bPay;
    private NakupAdapter nakupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakaznik_home);

        lvItems = findViewById(R.id.lvItems);
        bScan = findViewById(R.id.bScan);
        bPay = findViewById(R.id.bPay);

        //aaNakup  = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        nakupAdapter = new NakupAdapter(new ArrayList<NakupenyTovar>(),getApplicationContext());
        lvItems.setAdapter(nakupAdapter);

        bScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(getApplicationContext(),ZakaznikScannerActivity.class);
                startActivityForResult(result,0);
            }
        });

        bPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dialog.dismiss();
                                Database.setNakupZakaznika(MainActivity.getIdZakaznik(),nakupAdapter.getItems());
                                Intent intent = new Intent(getApplicationContext(),ZakaznikPayingActivity.class);
                                startActivity(intent);
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ZakaznikHomeActivity.this);
                builder.setMessage("Ste si istý?").setPositiveButton("Áno", dialogClickListener)
                        .setNegativeButton("Nie", dialogClickListener).show();

            }
        });

        registerForContextMenu(lvItems);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null){
            long kodEAN = data.getLongExtra("EAN",0);
            float mnozstvo = data.getFloatExtra("AMOUNT",0f);
            int type = data.getIntExtra("TYPE",0);
            Tovar tovar;
            if (type == 1){
                tovar = new Gson().fromJson(data.getStringExtra("OBJECT"), TovarZlavaCena.class);
            }
            else if(type ==2){
                tovar = new Gson().fromJson(data.getStringExtra("OBJECT"), TovarZlavaMnozstvo.class);
            }
            else {
                tovar = new Gson().fromJson(data.getStringExtra("OBJECT"), Tovar.class);
            }

            if (tovar != null){
                NakupenyTovar nakup;
                for (int i = 0 ; i < nakupAdapter.getCount() ; i++){
                    if (tovar.getTovarPLU() == nakupAdapter.get(i).getTovarPLU()){
                        if (tovar.getTovarJednotka().equals(JednotkaType.KS)){
                            if (tovar instanceof TovarZlavaCena){
                                nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo + nakupAdapter.get(i).getNakupeneMnozstvo(),((TovarZlavaCena) tovar).getNovaCena());
                            }
                            else if(tovar instanceof TovarZlavaMnozstvo){
                                nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo + nakupAdapter.get(i).getNakupeneMnozstvo(),((TovarZlavaMnozstvo) tovar).getPovodneMnozstvo(),((TovarZlavaMnozstvo) tovar).getNoveMnozstvo(),((TovarZlavaMnozstvo) tovar).getMinimalneMnozstvo());
                            }
                            else{
                                nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo + nakupAdapter.get(i).getNakupeneMnozstvo());
                            }
                            nakupAdapter.remove(nakupAdapter.getItem(i));
                            nakupAdapter.add(nakup);
                            return;
                        }
                        else{
                            if (tovar instanceof TovarZlavaCena){
                                nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo,((TovarZlavaCena) tovar).getNovaCena());
                            }
                            else{
                                nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo);
                            }
                            nakupAdapter.remove(nakupAdapter.getItem(i));
                            nakupAdapter.add(nakup);
                            return;
                        }
                    }
                }
                if (tovar instanceof TovarZlavaCena){
                    nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo,((TovarZlavaCena) tovar).getNovaCena());
                }
                else if(tovar instanceof TovarZlavaMnozstvo){
                    nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo,((TovarZlavaMnozstvo) tovar).getPovodneMnozstvo(),((TovarZlavaMnozstvo) tovar).getNoveMnozstvo(),((TovarZlavaMnozstvo) tovar).getMinimalneMnozstvo());
                }
                else{
                    nakup = new NakupenyTovar(tovar.getTovarPLU(),tovar.getTovarName(),tovar.getTovarCategory(),tovar.getTovarJednotka(),tovar.getTovarEAN(),tovar.getTovarJednotkovaCena(),tovar.getTovarDPH(),mnozstvo);
                }
                nakupAdapter.add(nakup);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_nakup, menu);
        menu.setHeaderTitle(R.string.choose_item_in_nakup_menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId()==R.id.mEdit){
            NakupenyTovar nakupenyTovar = nakupAdapter.getItem(info.position);
            if (nakupenyTovar != null){
                if (nakupenyTovar.getTovarJednotka().equals(JednotkaType.KS)){
                    showNumberPickerDialog(nakupenyTovar);
                }
                else {
                    showEditTextDialog(nakupenyTovar);
                }
            }

        }
        else if(item.getItemId()==R.id.mDelete){
            nakupAdapter.remove(nakupAdapter.getItem(info.position));
        }else{
            return false;
        }
        return true;
    }

    private void showNumberPickerDialog(final NakupenyTovar n){
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
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int mnozstvo = aNumberPicker.getValue();
                        NakupenyTovar nakupenyTovar;
                        if ((Tovar)n instanceof TovarZlavaCena){
                            nakupenyTovar = new NakupenyTovar(n.getTovarPLU(),n.getTovarName(),n.getTovarCategory(),n.getTovarJednotka(),n.getTovarEAN(),n.getTovarJednotkovaCena(),n.getTovarDPH(),mnozstvo,((TovarZlavaCena)((Tovar)n)).getNovaCena());
                        }
                        else if((Tovar)n instanceof TovarZlavaMnozstvo){
                            nakupenyTovar = new NakupenyTovar(n.getTovarPLU(),n.getTovarName(),n.getTovarCategory(),n.getTovarJednotka(),n.getTovarEAN(),n.getTovarJednotkovaCena(),n.getTovarDPH(),mnozstvo,((TovarZlavaMnozstvo)((Tovar)n)).getPovodneMnozstvo(),((TovarZlavaMnozstvo)((Tovar)n)).getNoveMnozstvo(),((TovarZlavaMnozstvo)((Tovar)n)).getMinimalneMnozstvo());
                        }
                        else{
                            nakupenyTovar = new NakupenyTovar(n.getTovarPLU(),n.getTovarName(),n.getTovarCategory(),n.getTovarJednotka(),n.getTovarEAN(),n.getTovarJednotkovaCena(),n.getTovarDPH(),mnozstvo);
                        }
                        nakupAdapter.remove(n);
                        nakupAdapter.add(nakupenyTovar);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showEditTextDialog(final NakupenyTovar n){

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
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        float mnozstvo = Float.parseFloat(editText.getText().toString());
                        NakupenyTovar nakupenyTovar;
                        if ((Tovar)n instanceof TovarZlavaCena){
                            nakupenyTovar = new NakupenyTovar(n.getTovarPLU(),n.getTovarName(),n.getTovarCategory(),n.getTovarJednotka(),n.getTovarEAN(),n.getTovarJednotkovaCena(),n.getTovarDPH(),mnozstvo,((TovarZlavaCena)((Tovar)n)).getNovaCena());
                        }
                        else{
                            nakupenyTovar = new NakupenyTovar(n.getTovarPLU(),n.getTovarName(),n.getTovarCategory(),n.getTovarJednotka(),n.getTovarEAN(),n.getTovarJednotkovaCena(),n.getTovarDPH(),mnozstvo);
                        }
                        nakupAdapter.remove(n);
                        nakupAdapter.add(nakupenyTovar);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

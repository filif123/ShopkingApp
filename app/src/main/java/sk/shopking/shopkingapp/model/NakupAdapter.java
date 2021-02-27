package sk.shopking.shopkingapp.model;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sk.shopking.shopkingapp.R;

public class NakupAdapter extends ArrayAdapter<NakupenyTovar>{

    private ArrayList<NakupenyTovar> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvMnozstvo;
        TextView tvCena;
    }

    public NakupAdapter(ArrayList<NakupenyTovar> data, Context context) {
        super(context, R.layout.nakup_types, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NakupenyTovar nakupenyTovar = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.nakup_types, parent, false);
            viewHolder.tvName = convertView.findViewById(R.id.name);
            viewHolder.tvMnozstvo = convertView.findViewById(R.id.mnozstvo);
            viewHolder.tvCena = convertView.findViewById(R.id.cena);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (nakupenyTovar != null){
            viewHolder.tvName.setText(nakupenyTovar.getTovarName());
            if (nakupenyTovar.getTovarJednotka().equals(JednotkaType.KS)){
                viewHolder.tvMnozstvo.setText(new DecimalFormat("#.##").format(nakupenyTovar.getNakupeneMnozstvo()) + " ks");
            }
            else if(nakupenyTovar.getTovarJednotka().equals(JednotkaType.G)){
                viewHolder.tvMnozstvo.setText(nakupenyTovar.getNakupeneMnozstvo() + " g");
            }
            else{
                viewHolder.tvMnozstvo.setText(nakupenyTovar.getNakupeneMnozstvo() + " kg");
            }

            viewHolder.tvCena.setText(nakupenyTovar.getNakupenaCena() + " €");
        }


        // Return the completed view to render on screen
        return convertView;
    }

    public List<NakupenyTovar> getItems() {
        return dataSet;
    }

    //public void add(NakupenyTovar nakupenyTovar){dataSet.add(nakupenyTovar);}

    //public void remove(NakupenyTovar nakupenyTovar){dataSet.remove(nakupenyTovar);}

    public NakupenyTovar get(int i){return dataSet.get(i);}


}

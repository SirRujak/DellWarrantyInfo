package ironhammerindustries.dellwarrantyfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rujak on 10/1/2014.
 */
public class DellWarrantyAdapter extends ArrayAdapter<WarrantyInfoContainer> {

    private final Context context;
    private final ArrayList<WarrantyInfoContainer> dellWarrantyList;

    public DellWarrantyAdapter(Context context, ArrayList<WarrantyInfoContainer> dellWarrantyList) {

        super(context, R.layout.data_container, dellWarrantyList);

        this.context = context;
        this.dellWarrantyList = dellWarrantyList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View dataContainer = inflater.inflate(R.layout.data_container, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) dataContainer.findViewById(R.id.first_line);
        TextView valueView = (TextView) dataContainer.findViewById(R.id.second_line);

        ImageView iconView = (ImageView) dataContainer.findViewById(R.id.icon);

        // 4. Set the text for textView
        labelView.setText(dellWarrantyList.get(position).getEntitlementType());
        valueView.setText(dellWarrantyList.get(position).getEndDate());

        if (dellWarrantyList.get(position).getIsActive()) {
            iconView.setImageResource(R.drawable.green_check_mark4848);
        } else {
            iconView.setImageResource(R.drawable.red_x4848);
        }

        // 5. return rowView
        return dataContainer;
    }
}

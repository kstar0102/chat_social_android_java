package ar.codeslu.plax.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ar.codeslu.plax.R;
import ar.codeslu.plax.models.DocmentModel;

public class DocmentAdapter extends BaseAdapter {
    private ArrayList<Object> personArray;
    private LayoutInflater inflater;
    private static final int TYPE_PERSON = 0;
    private static final int TYPE_DIVIDER = 1;

    public DocmentAdapter(Context context, ArrayList<Object> personArray) {
        this.personArray = personArray;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return personArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return personArray.get(position);
    }

    @Override
    public int getViewTypeCount() {
        // TYPE_PERSON and TYPE_DIVIDER
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof DocmentModel) {
            return TYPE_PERSON;
        }

        return TYPE_DIVIDER;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PERSON);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_PERSON:
                    convertView = inflater.inflate(R.layout.document_body, parent, false);
                    break;
                case TYPE_DIVIDER:
                    convertView = inflater.inflate(R.layout.gallery_header, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_PERSON:
                DocmentModel docment = (DocmentModel)getItem(position);
                ImageView img1 = (ImageView)convertView.findViewById(R.id.doc_image);
                TextView text = convertView.findViewById(R.id.doc_text);
                img1.setImageResource(docment.getImage());
                text.setText(docment.getTitle()  );
                break;
            case TYPE_DIVIDER:
                TextView title = (TextView)convertView.findViewById(R.id.tvSectionTitle);
                String titleString = (String)getItem(position);
                title.setText(titleString);
                break;
        }

        return convertView;
    }
}

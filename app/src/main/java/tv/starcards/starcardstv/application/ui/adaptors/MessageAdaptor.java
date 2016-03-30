package tv.starcards.starcardstv.application.ui.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.ui.models.MessagesListModel;

public class MessageAdaptor extends BaseAdapter {

    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources resources;
    MessagesListModel model = null;

    public MessageAdaptor(Activity activity, ArrayList data, Resources resources){
        this.activity = activity;
        this.data = data;
        this.resources = resources;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (data.size() <= 0) return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView theme;
        public TextView date;
        public TextView message;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(convertView == null) {
            view = inflater.inflate(R.layout.list_messages_layout, null);
            holder = new ViewHolder();
            holder.theme = (TextView) view.findViewById(R.id.message_title);
            holder.date = (TextView) view.findViewById(R.id.message_date);
            holder.message = (TextView) view.findViewById(R.id.message);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (data.size() <= 0) {
            holder.message.setText("Новых сообщений нет");
        } else  {
            model = null;
            model = (MessagesListModel) data.get(position);
            holder.theme.setText(model.getTheme());
            holder.date.setText(model.getDate());
            holder.message.setText(model.getMessage());
            view.setOnClickListener(new OnItemClickListener(position));
        }
        return view;
    }

    public void onClick(View view) {

    }

    private class OnItemClickListener implements View.OnClickListener {
        private int position;

        OnItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
//            MainScreenActivity scr = (MainScreenActivity) activity;
//            scr.onMessageItemClick(position);
        }
    }
}

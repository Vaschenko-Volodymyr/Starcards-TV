package tv.starcards.starcardstv.application.ui.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import tv.starcards.starcardstv.MainScreenActivity;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.ui.fragments.CabinetFragment;
import tv.starcards.starcardstv.application.ui.models.PacketListModel;

public class PacketAdaptor extends BaseAdapter implements View.OnClickListener{

    private Fragment fragment;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources resources;
    PacketListModel model = null;

    public PacketAdaptor(Fragment fragment, ArrayList data, Resources resources){
        this.fragment = fragment;
        this.data = data;
        this.resources = resources;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if(data.size() <= 0) return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView id;
        public TextView name;
        public TextView date;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        ViewHolder holder;

        if(convertView==null) {
            view = inflater.inflate(R.layout.list_packets_layout, null);
            holder = new ViewHolder();
            holder.id = (TextView) view.findViewById(R.id.packet_id);
            holder.name=(TextView) view.findViewById(R.id.packet_name);
            holder.date=(TextView) view.findViewById(R.id.packet_date);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (data.size()<=0){
            holder.name.setText("У вас нет пакетов");
        } else {
            model = null;
            model = (PacketListModel) data.get(position);
            holder.id.setText(model.getId());
            holder.name.setText(model.getName());
            holder.date.setText(model.getDate());

            view.setOnClickListener(new OnItemClickListener(position));
            view.setOnLongClickListener(new OnLongItemClickListener(position));
        }
        return view;
    }

    public void onClick(View view) {

    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int position;

        OnItemClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            CabinetFragment container = (CabinetFragment) fragment;
            container.onPacketsItemClick(position);
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int position;

        OnLongItemClickListener(int position) {this.position = position; }

        @Override
        public boolean onLongClick(View v) {
            CabinetFragment container = (CabinetFragment) fragment;
            container.onPacketItemLongClick(position);
            return false;
        }
    }
}

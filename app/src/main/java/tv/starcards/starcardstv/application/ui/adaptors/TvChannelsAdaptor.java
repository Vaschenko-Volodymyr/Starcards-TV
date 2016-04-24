package tv.starcards.starcardstv.application.ui.adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import tv.starcards.starcardstv.R;
import tv.starcards.starcardstv.application.ui.fragments.ChannelsFragment;
import tv.starcards.starcardstv.application.ui.models.TvChannelListModel;

public class TvChannelsAdaptor extends BaseAdapter implements View.OnClickListener {

    private ChannelsFragment              fragment;
    private ArrayList             data;
    private static LayoutInflater inflater = null;
    public Resources              resources;
    TvChannelListModel            model = null;

    private String url;
    private String packetId;
    private String channelId;

    ViewHolder holder;

    public TvChannelsAdaptor(ChannelsFragment fragment, ArrayList data, Resources resources){
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
        public TextView title;
        public TextView genre;
        public TextView number;
        public ImageView img;
        public CircleImageView available;
        public TextView info;
        public ImageView favorite;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if(convertView==null) {
            view = inflater.inflate(R.layout.list_channels_layout, null);
            holder = new ViewHolder();

            holder.title = (TextView) view.findViewById(R.id.channel_title);
            holder.genre = (TextView) view.findViewById(R.id.channel_genre);
            holder.number = (TextView) view.findViewById(R.id.channel_number);
            holder.img = (ImageView) view.findViewById(R.id.channel_logo);
            holder.available = (CircleImageView) view.findViewById(R.id.channel_is_available);
            holder.info = (TextView) view.findViewById(R.id.channel_info);
            holder.favorite = (ImageView) view.findViewById(R.id.channel_favorite);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (data.size()<=0){
            holder.title.setText("У вас нет пакетов");
        } else {
            model = null;
            model = (TvChannelListModel) data.get(position);
            holder.title.setText(model.getTitle());
            holder.genre.setText(model.getGenre());
            holder.number.setText(model.getNumber());

            if(!model.isAvailable()) {
                holder.available.setVisibility(View.VISIBLE);
            }

            if (model.isFavorite()) {
                holder.favorite.setImageDrawable(resources.getDrawable(R.drawable.channel_is_favorite));
            } else {
                holder.favorite.setImageDrawable(resources.getDrawable(R.drawable.channel_is_not_favorite));
            }

            String channelInfo = "";
            if (model.isArchivable()) {
                channelInfo = resources.getString(R.string.isArchivable);
            }

            if (model.isCensored()) {
                if (channelInfo.equals("")) {
                    channelInfo = resources.getString(R.string.isCensoredFirst);
                } else {
                    channelInfo = channelInfo + resources.getString(R.string.isCensoredContinuous);
                }
            }

            if (channelInfo.equals("")) {
                channelInfo = "Обычный канал";
            }
            holder.info.setText(channelInfo);

            holder.favorite.setOnClickListener(new OnFavoriteIconClickListener(position));
            Picasso.with(fragment.getActivity().getApplicationContext()).load(model.getLogo()).into(holder.img);

            view.setOnLongClickListener(new OnLongItemClickListener(position));
            view.setOnClickListener(new OnShortItemClick(position));
            this.packetId = model.getPacketId();
            this.channelId = model.getId();

        }
        return view;
    }

    public void updateResults(ArrayList results) {
        data = results;
        //Triggers the list update
        notifyDataSetChanged();
    }

    public void onClick(View view) {

    }

    private class OnShortItemClick implements View.OnClickListener {
        private int position;

        OnShortItemClick(int position){
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            ChannelsFragment container = fragment;
            container.onChannelsItemClick(position);
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int position;

        OnLongItemClickListener(int position){
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v) {
            ChannelsFragment container = fragment;
            container.onChannelsItemLongClick(position);
            return false;
        }
    }

    private class OnFavoriteIconClickListener implements View.OnClickListener {
        private int position;

        OnFavoriteIconClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            ChannelsFragment container = fragment;
            container.onChannelsFavoriteIconClick(position);
        }
    }

}

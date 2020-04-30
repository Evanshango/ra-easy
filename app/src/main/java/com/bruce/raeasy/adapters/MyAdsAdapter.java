package com.bruce.raeasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Item;
import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.MyAdHolder> {

    private Context mContext;
    private List<Item> mItems;
    private AdInteraction mAdInteraction;

    public MyAdsAdapter(Context context, AdInteraction adInteraction) {
        mContext = context;
        mAdInteraction = adInteraction;
    }

    @NonNull
    @Override
    public MyAdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_ads_item, parent, false);
        return new MyAdHolder(view, mAdInteraction);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(List<Item> items){
        mItems = items;
        notifyDataSetChanged();
    }

    class MyAdHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdInteraction mAdInteraction;
        LinearLayout itemCard;
        TextView deleteAd, name, tradeIn, datePosted;
        ImageView adImg;

        MyAdHolder(@NonNull View itemView, AdInteraction adInteraction) {
            super(itemView);
            mAdInteraction = adInteraction;
            itemCard = itemView.findViewById(R.id.itemCard);
            deleteAd = itemView.findViewById(R.id.deleteAd);
            adImg = itemView.findViewById(R.id.adImg);
            name = itemView.findViewById(R.id.item_ad_name);
            tradeIn = itemView.findViewById(R.id.item_ad_trade_in);
            datePosted = itemView.findViewById(R.id.item_ad_date_posted);

            deleteAd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mAdInteraction.adClicked(mItems.get(getAdapterPosition()));
        }

        void bind(Item item) {
            name.setText(item.getName());
            tradeIn.setText(item.getTradeIn());
            datePosted.setText(item.getDate());

            Glide.with(mContext)
                    .load(item.getImageUrls().get(0).getImgUrl())
                    .into(adImg);
        }
    }

    public interface AdInteraction{

        void adClicked(Item item);
    }
}

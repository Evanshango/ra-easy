package com.bruce.raeasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Favorite;
import com.bruce.raeasy.models.Item;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

    private Context mContext;
    private ItemInteraction mItemInteraction;
    private List<Item> mItems;
    private List<Favorite> mFavorites;

    public ItemAdapter(Context context, ItemInteraction itemInteraction) {
        mContext = context;
        mItemInteraction = itemInteraction;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ItemHolder(view, mItemInteraction);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(List<Item> items, List<Favorite> favorites){
        mItems = items;
        mFavorites = favorites;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemInteraction mItemInteraction;
        ImageView itemImg, favImg;
        TextView name, price;
        CardView itemCard;

        ItemHolder(@NonNull View itemView, ItemInteraction itemInteraction) {
            super(itemView);
            mItemInteraction = itemInteraction;
            itemImg = itemView.findViewById(R.id.itemImg);
            favImg = itemView.findViewById(R.id.imgFavorite);
            name = itemView.findViewById(R.id.txtItemName);
            price = itemView.findViewById(R.id.txtItemAmount);
            itemCard = itemView.findViewById(R.id.itemCard);

            itemCard.setOnClickListener(this);
            favImg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemInteraction.itemClicked(v, mItems.get(getAdapterPosition()), favImg);
        }

        void bind(Item item) {
            name.setText(item.getName());
            price.setText(String.format("Ksh. %s", item.getPrice()));

            Glide.with(mContext)
                    .load(item.getImageUrls().get(0).getImgUrl())
                    .into(itemImg);

            toggleFavIcon(item);
        }

        private void toggleFavIcon(Item item) {
            for (Favorite favorite : mFavorites){
                if (favorite.getItemId().equals(item.getItemId())){
                    favImg.setImageResource(R.drawable.ic_favorite_filled);
                }
            }
        }
    }

    public interface ItemInteraction {

        void itemClicked(View view, Item item, ImageView favImg);
    }
}

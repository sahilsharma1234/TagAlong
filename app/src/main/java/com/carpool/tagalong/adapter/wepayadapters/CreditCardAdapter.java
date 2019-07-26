package com.carpool.tagalong.adapter.wepayadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.carpool.tagalong.R;
import com.carpool.tagalong.models.ContactList;
import com.carpool.tagalong.models.wepay.CreditCards;
import com.carpool.tagalong.views.RegularTextView;
import java.util.List;

/**
 * Created by sahilsharma on 31/1/18.
 */

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.ViewHolder> {

    public static ContactList selectedContactList;
    private List<CreditCards> cardList;
    private static int selectedPosition = -1;// no selection by default
    private Context context;
    private RemoveCardListner removeCardListner;

    public CreditCardAdapter(List<CreditCards> dataList, Context context, RemoveCardListner removeCardListner) {
        this.cardList = dataList;
        this.selectedContactList = new ContactList();
        this.context = context;
        this.removeCardListner = removeCardListner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.defaultCardCheck.setOnCheckedChangeListener(null);

//        if(position == selectedPosition){
//
//            holder.defaultCardCheck.setChecked(true);
//
//        }else{
//            holder.defaultCardCheck.setChecked(false);
//        }

        if(cardList.get(position).isDefault()){
            selectedPosition = position;
        }
        holder.defaultCardCheck.setChecked(cardList.get(position).isDefault());

        holder.defaultCardCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b && !cardList.get(position).isDefault()) {
                    cardList.get(position).setDefault(true);

                    if(selectedPosition >=0){
                        cardList.get(selectedPosition).setDefault(false);
                    }
                    selectedPosition = position;

                    removeCardListner.setDefault(cardList.get(position));
                } else if (!b) {
                    cardList.get(position).setDefault(false);
                }
            }
        });

//        holder.defaultCardCheck.setChecked(cardList.get(position).isDefault());

        holder.cardNumber.setText(cardList.get(position).getNumber());

        holder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCardListner.removeCard(cardList.get(position));
            }
        });

        holder.cardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCardListner.showCard(cardList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public interface RemoveCardListner {

        void removeCard(CreditCards creditCards);
        void showCard(CreditCards creditCards);
        void setDefault(CreditCards card);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RegularTextView cardNumber;
        private CheckBox defaultCardCheck;
        private ImageView removeCard;

        public ViewHolder(View view) {
            super(view);
            removeCard = view.findViewById(R.id.deleteCard);
            cardNumber = view.findViewById(R.id.cardNumber);
            defaultCardCheck = view.findViewById(R.id.default_check);
            this.setIsRecyclable(false);
        }
    }
}
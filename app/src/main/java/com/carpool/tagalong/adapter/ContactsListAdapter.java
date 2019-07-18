package com.carpool.tagalong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.carpool.tagalong.R;
import com.carpool.tagalong.models.Contact;
import com.carpool.tagalong.models.ContactList;
import com.carpool.tagalong.utils.UIUtils;

import java.util.List;


/**
 * Created by sahilsharma on 31/1/18.
 */

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {

    private List<Contact> dataList;
    public  static ContactList selectedContactList;
    private  int  selectedCount = 0;
    private Context context;

    public ContactsListAdapter(List<Contact> dataList, RecyclerView recyclerView, Context context) {

        this.dataList = dataList;
        this.selectedContactList = new ContactList();
        this.context = context;
    }

    public ContactsListAdapter(){
    }

    @Override
    public ContactsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        return new ContactsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsListAdapter.ViewHolder holder, int position) {

        holder.contactName.setText(dataList.get(position).getName());
        holder.contactNumber.setText(dataList.get(position).getPhone());
        holder.contactEmail.setText(dataList.get(position).getEmail());
        holder.contactCheck.setChecked(dataList.get(position).isSelected);
        if(dataList.get(position).isSelected){
            holder.contactCheck.setButtonDrawable(R.drawable.ic_check_box_black);
        }else{
            holder.contactCheck.setButtonDrawable(R.drawable.ic_check_box_outline_blank_black);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private com.carpool.tagalong.views.RegularTextView contactName, contactNumber,contactEmail;
        private CheckBox contactCheck;

        public ViewHolder(View view) {
            super(view);

            contactName   = view.findViewById(R.id.contactName);
            contactNumber = view.findViewById(R.id.contactNumber);
            contactCheck  = view.findViewById(R.id.contactSelect);
            contactEmail  = view.findViewById(R.id.contactEmail);

            contactCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(b){

                        if(selectedCount >= 3){

                            UIUtils.alertBox(context,"You can select max 3 contacts!! Rest will be ignored!");
//                            compoundButton.setSelected(false);
//                            Toast.makeText(context,"You can select max 3 contacts!!", Toast.LENGTH_LONG).show();
                        }
                    }

                    if(b && !dataList.get(getAdapterPosition()).isSelected()){

                        selectedCount++;

                        dataList.get(getAdapterPosition()).setSelected(true);
                        contactCheck.setButtonDrawable(R.drawable.ic_check_box_black);
                        selectedContactList.addContact(dataList.get(getAdapterPosition()));

//                        if(!isFromCreateEvent) {
//                            for (int i = 0; i < dataList.size(); i++) {
//
//                                if (i != getAdapterPosition()) {
//                                    Contact contact = dataList.get(i);
//                                    contact.setSelected(false);
//                                }
//                            }
//                            notifyDataSetChanged();
//                        }
                    }else if(!b){
                        selectedCount--;
                        dataList.get(getAdapterPosition()).setSelected(false);
                        contactCheck.setButtonDrawable(R.drawable.ic_check_box_outline_blank_black);
                        selectedContactList.removeContact(dataList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
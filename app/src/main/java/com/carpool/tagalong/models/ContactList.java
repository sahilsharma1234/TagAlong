package com.carpool.tagalong.models;

import java.util.ArrayList;

/**
 * Created by sahilsharma on 1/2/18.
 */

 public class ContactList{

    public ArrayList<Contact> contactArrayList;

    public ContactList(){

        contactArrayList = new ArrayList<Contact>();
    }

    public int getCount(){

        return contactArrayList.size();
    }

    public void addContact(Contact contact){
        contactArrayList.add(contact);
    }

    public  void removeContact(Contact contact){
        contactArrayList.remove(contact);
    }

//    public Contact getContact(int id){
//
//        for(int i=0;i<this.getCount();i++){
//            if(Integer.parseInt(contactArrayList.get(i).id)==id)
//                return contactArrayList.get(i);
//        }
//        return null;
//    }
}

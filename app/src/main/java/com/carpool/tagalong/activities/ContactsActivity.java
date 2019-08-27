package com.carpool.tagalong.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.carpool.tagalong.R;
import com.carpool.tagalong.adapter.ContactsListAdapter;
import com.carpool.tagalong.models.Contact;
import com.carpool.tagalong.models.ContactList;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView contactsRecyclerView;
    private ImageView backContacts, inviteContacts;
    private Context context;
    private ArrayList<Contact> tempContactHolder = new ArrayList<>();
    private ContactList selectedList = null;
    private com.carpool.tagalong.views.RegularTextView txtProgress     = null;
    private int totalContactsCount   = 0,loadedContactsCount = 0;
    private ContactsListAdapter contactsListAdapter;
    private boolean isFromCreateEvent = false;
    private LinearLayout toolbarLayout;
    private Toolbar toolbar;
    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);

        context = this;
        initializeViews();
        if(checkContactsPermission()) loadContacts();
    }

    private boolean checkContactsPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},123);
            return false ;
        }
        return true;
    }

    private void initializeViews() {

        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        txtProgress          = findViewById(R.id.progressContacts);
        toolbarLayout = findViewById(R.id.toolbar_contacts);
        done          = findViewById(R.id.done);
        done.setOnClickListener(this);
        com.carpool.tagalong.views.RegularTextView title = toolbarLayout.findViewById(R.id.toolbar_title);
        ImageView titleImage = toolbarLayout.findViewById(R.id.title);
        toolbar = toolbarLayout.findViewById(R.id.toolbar);
        title.setText("Tag Contacts");
        title.setVisibility(View.VISIBLE);
        titleImage.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi, null));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_backxhdpi));
        }
    }

    private void loadContacts(){
        ContactsLoader contactsLoader = new ContactsLoader();
        contactsLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        switch (id) {

            case R.id.done:

                ContactsListAdapter contactsListAdapter = new ContactsListAdapter();
                selectedList = contactsListAdapter.selectedContactList;

                if(selectedList.contactArrayList.size() == 0){
                    setResult(RESULT_CANCELED);
                }
                else{
                    Intent resultIntent = new Intent();
                    resultIntent.putParcelableArrayListExtra("SelectedContacts", selectedList.contactArrayList);
                    setResult(RESULT_OK,resultIntent);
                }
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 123) {
            if (checkContactsPermission()) {
                loadContacts();
            } else {
                Toast.makeText(this, "Contacts permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class ContactsLoader extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void...voids) {

            ContentResolver contentResolver = context.getContentResolver();

            Uri uri = ContactsContract.Contacts.CONTENT_URI;

            String[] projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };

            Cursor   cursor;
            cursor = contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            );

            totalContactsCount = cursor.getCount();
            if(cursor!=null   && cursor.getCount() > 0){

                while(cursor.moveToNext()) {
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        String id   = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                                new String[]{id},
                                null
                        );

                        String email = null;
                        Cursor ce    =  contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[]{id}, null);

                        ce.getCount();

                        if (ce != null && ce.moveToFirst()) {
                            email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            ce.close();
                        }

                        if (phoneCursor != null && phoneCursor.getCount() > 0) {

                            while (phoneCursor.moveToNext()) {

                                String phId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                                String customLabel = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));

                                String label = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(),
                                        phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),
                                        customLabel
                                );

                                String phNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                tempContactHolder.add(new Contact(phId, name, phNo, label,email));
                            }
                            phoneCursor.close();
                        }
                    }
                    loadedContactsCount++;
                    publishProgress();
                }
                cursor.close();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void[] v){

            if(txtProgress != null){
                txtProgress.setVisibility(View.VISIBLE);
                String progressMessage = "Loading...("+loadedContactsCount+"/"+totalContactsCount+")";
                txtProgress.setText(progressMessage);
            }
        }

        @Override
        protected void onPostExecute(Void result){

            if(txtProgress != null) {
                txtProgress.setText("");
                txtProgress.setVisibility(View.GONE);
            }
            contactsRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            contactsRecyclerView.setLayoutManager(layoutManager);
            contactsListAdapter = new ContactsListAdapter(tempContactHolder,contactsRecyclerView, context);
            contactsRecyclerView.setAdapter(contactsListAdapter);
        }
    }
}
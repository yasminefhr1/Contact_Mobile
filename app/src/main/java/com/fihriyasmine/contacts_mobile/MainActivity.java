package com.fihriyasmine.contacts_mobile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;import android.Manifest;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int PERMISSIONS_REQUEST_CONTACTS = 100;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<Contact> contactList;
    private List<Contact> filteredContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        filteredContactList = new ArrayList<>();

        // Check for READ_CONTACTS and WRITE_CONTACTS permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                    PERMISSIONS_REQUEST_CONTACTS);
        } else {
            loadContacts();
        }

        // ---------------------- Swipe --------------------------------
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Contact contactToDelete = contactList.get(position);
                //------------------dialg--------------------------------
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Supprimer le contact")
                        .setMessage("Êtes-vous sûr de vouloir supprimer ce contact ?")
                        .setPositiveButton("Oui", (dialog, which) -> {
                            contactList.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Annuler", (dialog, which) -> {
                            adapter.notifyItemChanged(position);
                        })
                        .setOnCancelListener(dialog -> {
                            adapter.notifyItemChanged(position);
                        })
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    Paint paint = new Paint();
                    paint.setColor(Color.TRANSPARENT);
                    if (dX < 0) {
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
                        if (drawable != null) {
                            int iconMargin = (itemView.getHeight() - drawable.getIntrinsicHeight()) / 2;
                            drawable.setBounds(itemView.getRight() - iconMargin - drawable.getIntrinsicWidth(), itemView.getTop() + iconMargin, itemView.getRight() - iconMargin, itemView.getBottom() - iconMargin);
                            drawable.draw(c);
                        }
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // ----------------------ajt----------------------------------------
        ImageButton addContactButton = findViewById(R.id.add_contact_button);
        addContactButton.setOnClickListener(v -> showAddContactDialog());
    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phoneNumber);
                contactList.add(contact);
                filteredContactList.add(contact); // Ajoutez également à la liste filtrée
            }
            cursor.close();
        }

        adapter = new ContactAdapter(filteredContactList, this);
        recyclerView.setAdapter(adapter);
    }

    private void showAddContactDialog() {
        // Création d'une boîte de dialogue pour ajouter un contact
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter un contact");

        // Layout pour le dialogue
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.edit_text_name);
        EditText editTextPhone = dialogView.findViewById(R.id.edit_text_phone);

        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            if (!name.isEmpty() && !phone.isEmpty()) {
                addContactToPhone(name, phone);
                loadContacts(); // Rechargez les contacts après ajout
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addContactToPhone(String name, String phone) {
        // Insérer un nouveau contact
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, (String) null);
        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, (String) null);

        // Insérez le contact et récupérez l'URI
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);

        if (rawContactUri != null) {
            long contactId = ContentUris.parseId(rawContactUri);

            // Ajouter le nom du contact
            ContentValues nameValues = new ContentValues();
            nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
            nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nameValues);

            // Ajouter le numéro de téléphone
            ContentValues phoneValues = new ContentValues();
            phoneValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
            phoneValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            phoneValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            getContentResolver().insert(ContactsContract.Data.CONTENT_URI, phoneValues);

            Toast.makeText(this, "Contact ajouté", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de l'ajout du contact", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        return true;
    }

    private void filterContacts(String query) {
        filteredContactList.clear();
        if (query.isEmpty()) {
            filteredContactList.addAll(contactList);
        } else {
            for (Contact contact : contactList) {
                if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredContactList.add(contact);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}


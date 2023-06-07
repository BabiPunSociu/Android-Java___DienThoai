package com.example.adapterview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.Manifest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private ContentProvider cp;
    private static int SelectedID;
    private static ArrayList<Contact> ContactList;
    private Adapter ListAdapter;
    private EditText etSearch;
    private ListView lstContact;
    private FloatingActionButton btnAdd;
    private static int checkXoa = 0;
    private MyDB db;

    ConnectionReceiver receiver;
    IntentFilter intentFilter;

    private ArrayList<Contact> copyContactList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.actionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static String splitName(String s) {
        String[] result = s.split(" ");
        return result[result.length - 1];
    }

    public ArrayList<Contact> getContactList() {
        return ContactList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bundle b = data.getExtras();
        int id = b.getInt("Id");
        String name = b.getString("Name");
        String phone = b.getString("Phone");
        Contact newContact = new Contact(id, "Images", name, phone);
        if (requestCode == 100 && resultCode == 150) {
            ContactList.add(newContact);
            ListAdapter = new Adapter(ContactList, this);
            lstContact.setAdapter(ListAdapter);
        }
        // Sua
        else if (requestCode == 200 && resultCode == 150) {
            for (Contact contact : ContactList) {
                if (contact.getId() == id) {
                    contact.setName(name);
                    contact.setPhone(phone);
                    break;
                }
            }
            ListAdapter = new Adapter(ContactList, this);
            lstContact.setAdapter(ListAdapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Contact contact = new Contact();
        switch (item.getItemId()) {
            case R.id.mnuSortName:
                Collections.sort(ContactList, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact contact1, Contact contact2) {
                        return splitName(contact1.getName().toUpperCase()).compareTo(splitName(contact2.getName().toUpperCase()));
                    }
                });
                ListAdapter = new Adapter(ContactList, this);
                lstContact.setAdapter(ListAdapter);
                break;
            case R.id.mnuSortPhone:
                Collections.sort(ContactList, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact contact1, Contact contact2) {
                        return contact1.getPhone().compareTo(contact2.getPhone());
                    }
                });
                ListAdapter = new Adapter(ContactList, this);
                lstContact.setAdapter(ListAdapter);
                //Toast.makeText(MainActivity.this,"sortPhone",Toast.LENGTH_SHORT).show();
                break;
            case R.id.mnuEdit:
                //1. Tạo intent để mở subactivity
                Intent intent = new Intent(MainActivity.this, AddActivity.class);

                //2. Truyền dữ liệu sang subactivity bằng bunlde nếu cần
                Contact c = ContactList.get(SelectedID);
                Bundle b = new Bundle();
                b.putInt("Id", c.getId());
                b.putString("Images", c.getImages());
                b.putString("Name", c.getName());
                b.putString("Phone", c.getPhone());
                intent.putExtras(b);
                //3. Mở subactivity bằng cách gọi hàm staractivity hoặc staractivityforresult
                startActivityForResult(intent, 200);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Main
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContactList = new ArrayList<>();
        //Thiết lập dữ liệu mẫu
        db = new MyDB(this, "ContactDB", null, 1);
        db.addContact(new Contact(1, "img1", "Nguyen Van An", "011111"));
        db.addContact(new Contact(2, "img2", "Nguyen Thanh Chien", "022222"));
        db.addContact(new Contact(3, "img3", "Tran Nhu Binh", "033333"));
        ContactList = db.getAllContact();
        ShowContact();

//        ListAdapter = new Adapter(ContactList, this);
        etSearch = findViewById(R.id.etSearch);
        lstContact = findViewById(R.id.lstContact);
        btnAdd = findViewById(R.id.btnAdd);
//        lstContact.setAdapter(ListAdapter);
        copyContactList = new ArrayList<>();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent add = new Intent(this,AddActivity)
                //1. Tạo intent để mở subactivity
                Intent intent = new Intent(MainActivity.this, AddActivity.class);

                //2. Truyền dữ liệu sang subactivity bằng bunlde nếu cần

                //3. Mở subactivity bằng cách gọi hàm staractivity hoặc staractivityforresult
                startActivityForResult(intent, 100);

            }
        });
        lstContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SelectedID = i;
                return false;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ListAdapter.getFilter().filter(charSequence.toString());
                ListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerForContextMenu(lstContact);

        // đăng kí sự kiện cho BroadReceiver
        //==============================================================================
        receiver = new ConnectionReceiver();
        intentFilter = new IntentFilter("com.exemple.listview2023.SOME_ACTION");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);
        //==============================================================================
    }
    //BroadReceiver
    //==============================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }
    //==============================================================================

    // Alert Dialog Xóa: hiện hộp hội thoại hỏi trc khi xóa
    public void thongBao(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(s);
        builder.setNegativeButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(MainActivity.this,String.valueOf(i),Toast.LENGTH_SHORT).show();
                ContactList.remove(SelectedID);
                ListAdapter = new Adapter(ContactList, MainActivity.this);
                lstContact.setAdapter(ListAdapter);
            }
        });
        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    // Đăng kí context menu trên main activity
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menupopup, menu);
        menu.setHeaderTitle("Select Option");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Contact c1 = ContactList.get(SelectedID);
        switch (item.getItemId()) {
            case R.id.idSua:
                //1. Tạo intent để mở subactivity
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                //2. Truyền dữ liệu sang subactivity bằng bunlde nếu cần
                Contact c = ContactList.get(SelectedID);
                Bundle b = new Bundle();
                b.putInt("Id", c.getId());
                b.putString("Images", c.getImages());
                b.putString("Name", c.getName());
                b.putString("Phone", c.getPhone());
                intent.putExtras(b);
                //3. Mở subactivity bằng cách gọi hàm staractivity hoặc staractivityforresult
                startActivityForResult(intent, 200);
                break;
            case R.id.idXoa:
                thongBao("Bạn có chắc muốn xóa không ?");
                break;
            case R.id.idCall:
                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + c1.getPhone()));
                startActivity(in);
                break;
            case R.id.idSendSMS:
                Intent intentSendSMS = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", String.valueOf(c1.getPhone()),null));
                startActivity(intentSendSMS);
                break;

        }

        return super.onContextItemSelected(item);
    }
    private void ShowContact()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        else {
            cp = new ContentProvider(this);
            ContactList = cp.getAllContact();
            ListAdapter = new Adapter(ContactList, this);
            lstContact.setAdapter(ListAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                this.ShowContact();
            }
            else {
                Toast.makeText(this, "Until you grant the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
package com.example.adapterview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private EditText etId;
    private EditText etFullName;
    private EditText etPhone;
    private ImageView ivImage;
    private Button btnOk;
    private Button btnCancel;

    // Alert Dialog
    public void thongBao(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(s);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        etId = findViewById(R.id.txtId);
        etFullName = findViewById(R.id.txtFullname);
        etPhone = findViewById(R.id.txtPhone);
        ivImage = findViewById(R.id.imageView2);
        btnOk = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);

        //Lay arraylist tu main activity
        MainActivity mainActivity = new MainActivity();
        //Lấy intent từ MainActivity chuyển sang
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
            int id = bundle.getInt("Id");
            String images = bundle.getString("Images");
            String name = bundle.getString("Name");
            String phone = bundle.getString("Phone");
            etId.setText(String.valueOf(id));
            etFullName.setText(name);
            etPhone.setText(phone);
            etId.setEnabled(false);
            btnOk.setText("Edit");
            //thongBao("Hello");
            //Toast.makeText(this,String.valueOf(mainActivity.getContactList().size()),Toast.LENGTH_SHORT).show();
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkEdit=0; // Neu check=0 thì sửa thành công còn nếu check=1 thì ko sửa đc
                // Lay du lieu va gui ve cho main activity
                //Trường hợp sửa
                if(btnOk.getText().toString().equals("Edit")){
                    int id = Integer.parseInt(etId.getText().toString());
                    String name = etFullName.getText().toString();
                    String phone = etPhone.getText().toString();
                    for(Contact contact: mainActivity.getContactList()){
                        if(contact.getId()==id)
                            continue;
                            //Truong hop trung so dien thoai
                        else {
                            if(contact.getPhone().equals(phone)){
                                checkEdit=1;
                                thongBao("Số điện thoại này đã tồn tại");
                                etPhone.setFocusable(true);
                                break;
                            }
                        }
                    }
                    if(checkEdit==0){
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putInt("Id",id);
                        b.putString("Name",name);
                        b.putString("Phone",phone);
                        intent.putExtras(b);
                        setResult(150,intent);
                        finish();
                    }
                }
                //Trường hợp thêm
                int checkAdd=0;// Nếu checkAdd =0 thì thêm thành công còn lại thì thêm thất bại
                if(btnOk.getText().toString().equals("OK")){
                    int id = Integer.parseInt(etId.getText().toString());
                    String name = etFullName.getText().toString();
                    String phone = etPhone.getText().toString();
                    for(Contact contact: mainActivity.getContactList()){
                        if(contact.getId()==id||contact.getPhone().equals(phone)){
                            checkAdd=1;
                            thongBao("Id hoặc số tài khoản này đã tồn tại");
                            etId.setFocusable(true);
                            break;
                        }
                    }
                    if(checkAdd==0){
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putInt("Id",id);
                        b.putString("Name",name);
                        b.putString("Phone",phone);
                        intent.putExtras(b);
                        setResult(150,intent);
                        finish();
                    }
                }
            }
        });
    }
}
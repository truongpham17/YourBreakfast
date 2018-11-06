package com.example.user.your_breakfast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.your_breakfast.common.ShareData;
import com.example.user.your_breakfast.helper.RecycerItemAddressTouchHelper;
import com.example.user.your_breakfast.helper.RecyclerItemTouchHelper;
import com.example.user.your_breakfast.helper.RecyclerItemTouchHelperListener;
import com.example.user.your_breakfast.model.Address;
import com.example.user.your_breakfast.model.User;
import com.example.user.your_breakfast.viewholder.AddressViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.logging.SocketHandler;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {
    TextView txtUserName, txtPhone, txtEditPassword;
    CircleImageView imgAddAddress, imgAddPayment, imgAvatar;
    Button btnSave;
    RecyclerView recyclerView;
    CropImageView cropImageView;
    User user = ShareData.getUser();
    boolean isAvatar = false;
    RelativeLayout background;
    DatabaseReference addressDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseRecyclerAdapter<Address, AddressViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_user_profile);
        Slidr.attach(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Info");
        TextView textView = (TextView) toolbar.getChildAt(0);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/nabila.ttf");
        textView.setTypeface(tp);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addControls();
    }

    private void addControls() {
        txtUserName = findViewById(R.id.txtUserName);
        background = findViewById(R.id.backGround);
        txtPhone = findViewById(R.id.txtPhone);
        imgAddAddress = findViewById(R.id.imgAddAddress);
        imgAddPayment = findViewById(R.id.imgAddPayment);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtEditPassword = findViewById(R.id.txtEditPassword);
        txtEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPasswordDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = ShareData.getUser().getName();
                String phone = ShareData.getUser().getPhone();
                String userNameText = txtUserName.getText().toString();
                if (!userName.equals(userNameText)) {
                    FirebaseDatabase.getInstance().getReference("USER").child(phone).child("name").setValue(userNameText);
                    ShareData.getUser().setName(userNameText);
                }
                onBackPressed();
            }
        });


        User user = ShareData.getUser();
        txtUserName.setText(user.getName());

        txtPhone.setText(user.getPhone());
        if (user.getImage() != null) {
            Picasso.get().load(user.getImage()).into(imgAvatar);
        }

        imgAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPaymentMethod();
            }
        });

        imgAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddressMethod();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        addressDatabase = FirebaseDatabase.getInstance().getReference("USER").child(user.getPhone()).child("address");
        adapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(
                Address.class, R.layout.address_item, AddressViewHolder.class, addressDatabase
        ) {
            @Override
            protected void populateViewHolder(AddressViewHolder viewHolder, final Address model, final int position) {
                viewHolder.txtTitle.setText(model.getType());
                viewHolder.txtAddress.setText(model.getAddress());

                viewHolder.imgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editUserAddress(model, adapter.getRef(position).getKey());
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback simpleCallback = new RecycerItemAddressTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
                isAvatar = true;
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
                isAvatar = false;
            }
        });
        cropImageView = findViewById(R.id.cropImageView);
    }

    private void changeAvatar() {
        isAvatar = true;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                    if (isAvatar) {
                        imgAvatar.setImageBitmap(bitmap);
                        pushImageIntoWebServer(bitmap, true);
                    } else {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        background.setBackground(drawable);
                        pushImageIntoWebServer(bitmap, true);
                    }
                } catch (Exception e) {

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("error", "onActivityResult:  error at crop image");
            }
        }
    }

    private void pushImageIntoWebServer(Bitmap bitmap, boolean isAvatar) {
        String path;
        if (isAvatar) {
            path = user.getPhone() + "/" + "avatar";
        } else {
            path = user.getPhone() + "/" + "background";
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final StorageReference firememeRef = storage.getReference(path);

        StorageMetadata metadata  = new StorageMetadata.Builder().setCustomMetadata("text", "user avatar image").build();
        UploadTask uploadTask = firememeRef.putBytes(byteArray, metadata)
                ;
       Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
           @Override
           public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
               if(!task.isSuccessful()){
                   throw task.getException();
               }
               return firememeRef.getDownloadUrl();
           }
       }).addOnCompleteListener(new OnCompleteListener<Uri>() {
           @Override
           public void onComplete(@NonNull Task<Uri> task) {
               if(task.isSuccessful()){
                   Uri downloadUri = task.getResult();
                   if(downloadUri != null){
                       user.setImage(downloadUri.toString());
                       DatabaseReference db = FirebaseDatabase.getInstance().getReference("USER").child(user.getPhone()).child("image");
                       db.setValue(downloadUri.toString());
                   }

               }
           }
       });


    }

    private void showEditPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.delete_dialog, null);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        final TextView txtPassword, txtNewPassword, txtRePassword;
        Button btnSubmit;
        txtPassword = v.findViewById(R.id.txtPassword);
        txtNewPassword = v.findViewById(R.id.txtNewPassword);
        txtRePassword = v.findViewById(R.id.txtReNewPassword);
        btnSubmit = v.findViewById(R.id.btnSubmit);
        User user = ShareData.getUser();
        final String password = user.getPassword();
        final SweetAlertDialog dialog1 = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        dialog1.setTitleText("SUCCESS");
        final DatabaseReference userPassword = FirebaseDatabase.getInstance().getReference("USER").child(user.getPhone()).child("password");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals(txtPassword.getText().toString())
                        && ((txtNewPassword.getText().toString()).equals(txtRePassword.getText().toString()))
                        && txtNewPassword.getText().toString().length() > 5) {
                    userPassword.setValue(txtNewPassword.getText().toString());
                    ShareData.getUser().setPassword(txtNewPassword.getText().toString());
                    dialog.dismiss();
                    dialog1.show();
                }
            }
        });

    }


    private void editUserAddress(final Address model, final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.address_dialog, null);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView txtTitle = v.findViewById(R.id.txtTitle);
        txtTitle.setText("Edit Address");
        final TextView txtAddress = v.findViewById(R.id.txtAddress);
        txtAddress.setText(model.getAddress());
        Button btnSubmit = v.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addressDatabase.child(key).setValue(new Address(model.getType(), txtAddress.getText().toString()));
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void addPaymentMethod() {

        Snackbar.make(recyclerView, "Sorry, add payment method is not support yet", Snackbar.LENGTH_LONG).show();
    }


    private void addAddressMethod() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.address_dialog, null);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        Button btnSubmit = v.findViewById(R.id.btnSubmit);
        final TextView txtAddress = v.findViewById(R.id.txtAddress);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDatabase.push().setValue(new Address("Company", txtAddress.getText().toString()));
                alertDialog.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        final AddressViewHolder addressViewHolder = (AddressViewHolder) viewHolder;
        if (addressViewHolder.txtTitle.getText().toString().equals("Home")) {
            adapter.notifyDataSetChanged();
            Snackbar.make(recyclerView, "Can not remove home address", Snackbar.LENGTH_SHORT).show();

        } else {
            addressDatabase.child(adapter.getRef(position).getKey()).removeValue();
            adapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(recyclerView, "Remove address successfully!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressDatabase.push().setValue(new Address("Company", addressViewHolder.txtAddress.getText().toString()));
                    adapter.notifyDataSetChanged();
                }
            });
            snackbar.show();

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onBackPressed();
    }


}

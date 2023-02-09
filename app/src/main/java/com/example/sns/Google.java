package com.example.sns;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Google extends AppCompatActivity {
    private EditText edtSignEmail,edtSignName;
    private FirebaseAuth mAuth;
    private ImageView profileimage;
    private Uri imageUri;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_signup);


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Thread mThread= new Thread(){
            @Override
            public void run() {
                try{
                    //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        profileimage=(ImageView) findViewById(R.id.account_iv_profile);
        try{

            mThread.join();
            //변환한 bitmap적용
            profileimage.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        edtSignEmail= (EditText) findViewById(R.id.edtSignEmail);
        edtSignEmail.setText(user.getDisplayName());
        edtSignName = (EditText) findViewById(R.id.edtSignName);
        Button btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strName = edtSignName.getText().toString();
                if(strName.length()==0){
                    Toast.makeText(Google.this,"이름을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                createUser();
            }
        });

    }
    private void createUser() {
        final String uid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userProfileImages").child(uid);
        profileImageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return profileImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("userProfileImages").child(uid);

                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            User userModel = new User();
                            userModel.setUsername(edtSignName.getText().toString());
                            userModel.setEmail(edtSignEmail.getText().toString());
                            userModel.setUid(uid);
                            String img_uri = uri.toString();
                            userModel.setProfileUri(img_uri);
                            db.collection("profileImage").document(uid)
                                    .set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Google.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplication(), Login.class);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    });
                } else {
                    Toast.makeText(Google.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        Thread mThread= new Thread(){
            @Override
            public void run() {
                try{
                    //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
            imageUri =getImageUri(Google.this,bitmap);
            Log.e("비트맵", String.valueOf(bitmap));
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}


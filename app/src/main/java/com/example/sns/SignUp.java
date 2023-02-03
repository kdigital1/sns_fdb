package com.example.sns;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.example.sns.navigation.Frag5;
import com.example.sns.navigation.model.ProfileImage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

;import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private EditText edtSignEmail,edtSignPwd,edtSignPwdCheck,edtSignName;
    private Button btnSignup;
    private FirebaseAuth mFirebaseAuth;
    private ImageView account_iv_profile;
    private Uri imageUri;
    public static int PICK_PROFILE_FROM_ALBUM=10;
    private FirebaseFirestore firestore;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sns_sginup);

        edtSignPwdCheck =findViewById(R.id.edtSignPwdCheck);
        edtSignEmail = findViewById(R.id.edtSignEmail);
        edtSignPwd = findViewById(R.id.edtSignPwd);
        btnSignup = findViewById(R.id.btnSignup);
        edtSignName=findViewById(R.id.edtSignName);
        account_iv_profile = findViewById(R.id.account_iv_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();

        account_iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                startActivityForResult(photoPickIntent, PICK_PROFILE_FROM_ALBUM);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edtSignEmail.getText().toString();
                String strPwd = edtSignPwd.getText().toString();
                int strLength = strPwd.length();

                if(account_iv_profile==null){
                    Toast.makeText(SignUp.this,"프로필사진을 등록하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtSignEmail.getText().toString().length()==0){
                    Toast.makeText(SignUp.this,"이메일을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches())
                {
                    Toast.makeText(SignUp.this,"이메일 형식이 아닙니다",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtSignEmail.getText().toString().length()==0){
                    Toast.makeText(SignUp.this,"이름을 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(edtSignPwd.getText().toString().length()==0){
                    Toast.makeText(SignUp.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strLength<6){
                    Toast.makeText(SignUp.this, "비밀번호를 6자리이상 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                if(!edtSignPwd.getText().toString().equals(edtSignPwdCheck.getText().toString())){
                    Log.e("비밀번호 : ",edtSignPwd.getText().toString());
                    Log.e("비밀번호확인 : ",edtSignPwdCheck.getText().toString());
                    Toast.makeText(SignUp.this, "비밀번호가 다릅니다 ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strLength>=6){
                    createUser(strEmail,strPwd);
                }
            }
        });
    }

    private void createUser(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
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
                                                            Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplication(), Login.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUp.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == RESULT_OK) {
            account_iv_profile.setImageURI(data.getData());    //가운데 뷰를 바꿈
            imageUri = data.getData();    //이미지 경로 원본
        }
    }
}
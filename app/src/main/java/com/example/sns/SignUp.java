package com.example.sns;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

;

public class SignUp extends AppCompatActivity {
    private EditText edtSignEmail,edtSignPwd,edtSignName;
    private Button btnSignUpFinish;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtSignEmail = findViewById(R.id.edtSignEmail);
        edtSignPwd = findViewById(R.id.edtSignPwd);
        edtSignName = findViewById(R.id.edtSignName);
        btnSignUpFinish = findViewById(R.id.btnSignUpFinish);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnSignUpFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = edtSignEmail.getText().toString();
                String strPwd = edtSignPwd.getText().toString();
                int strLength = strPwd.length();
                if(strLength>=6){
                    createUser(strEmail,strPwd);
                }else{
                    Toast.makeText(SignUp.this, "비밀번호는 6자리 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplication(), Login.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignUp.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
package com.example.sns.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sns.MainActivity;
import com.example.sns.R;
import com.example.sns.User;
import com.example.sns.navigation.model.ContentDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.Any;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdatePhoto extends AppCompatActivity {
    private ImageView updatephoto_img;
    private EditText updatephoto_edit;
    private Button updatephoto_btn;
    private FirebaseFirestore firestore;
    private FirebaseAuth mFirebaseAuth;
    public static final int PICK_IMAGE_FROM_ALBUM = 1;
    private FirebaseStorage storage;
    private String uid;
    private Uri photoUri;
    private SimpleDateFormat timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss");
    public ArrayList<String> contentUidList = new ArrayList<>();
    private String imageFileName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_photo);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        updatephoto_btn =findViewById(R.id.updatephoto_btn);
        updatephoto_edit=findViewById(R.id.updatephoto_edit);
        updatephoto_img=findViewById(R.id.updatephoto_img);
/* 수정시 기존값 가져오는코드
        try {
            Intent secondIntent = getIntent();
            int position =secondIntent.getIntExtra("position",0);
            firestore.collection("images").document(contentUidList.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot value) {
                    String updateedit = String.valueOf(value.getData().get("explain"));
                    String str = String.valueOf(value.getData().get("imageUri"));
                    Log.e("수정",updateedit);
                    Log.e("수정",str);
                    updatephoto_edit.setText(updateedit);
                    Uri uri = Uri.parse(str);
                    Glide.with(UpdatePhoto.this)
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(updatephoto_img);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
 */

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("images").orderBy("timestamp").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots==null)
                            return;
                        contentUidList.clear();
                        try{
                            for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                contentUidList.add(snapshot.getId());
                            }
                        }catch (Exception e){
                        }
                    }
                });
        updatephoto_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        updatephoto_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePhoto();
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data.getData();
                updatephoto_img.setImageURI(photoUri);

            }else{
                finish();
            }
        }
    }

    private void updatePhoto(){
        Date date = new Date();
        String time = timestamp.format(date);
        imageFileName = "IMAGE_"+time+"_.png";

        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference pathReference = storageReference.child("images").child(imageFileName);

        UploadTask uploadTask = pathReference.putFile(photoUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Long now =  System.currentTimeMillis();
                        Date mDate = new Date(now);
                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String timestamp = simpleDate.format(mDate);
                        String explain= updatephoto_edit.getText().toString();
                        Map<String, Object> update = new HashMap<>();
                        update.put("imageUri",uri);
                        update.put("timestamp",timestamp);
                        update.put("explain",explain);
                        Intent secondIntent = getIntent();
                        int position =secondIntent.getIntExtra("position",1);
                        firestore.collection("images").document(contentUidList.get(position)).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpdatePhoto.this, "수정완료",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdatePhoto.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
            }

        });
    }
}

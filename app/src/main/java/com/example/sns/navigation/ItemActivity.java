package com.example.sns.navigation;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sns.MainActivity;
import com.example.sns.R;
import com.example.sns.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.sns.navigation.model.AlarmDTO;
import com.example.sns.navigation.model.ContentDTO.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemActivity extends AppCompatActivity {
    private String contentUid = null;
    private Button comment_btn_send;
    private EditText comment_edit_message;
    private TextView detailviewitem_profile_textview;
    private TextView detailviewitem_timestamp_textview;
    private RecyclerView comment_recyclerview;
    private ImageView detailviewitem_profile_image;
    private ImageView detailviewitem_profile_imageview_content;
    private ImageView detailviewitem_favrite_imageview;
    private ImageView detailviewitem_comment_imageview;
    private LinearLayoutManager layoutManager;
    private TextView detailviewitem_favoritecounter_textview;
    private TextView detailviewitem_explain_textview;
    ArrayList<Comment> comments = new ArrayList<>();

    private String destinationUid;
    private String destinationImageUri;
    private String destinationUsername;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);



        contentUid = getIntent().getStringExtra("contentUid");
        destinationUid = getIntent().getStringExtra("destinationUid");
        destinationImageUri = getIntent().getStringExtra("destinationImageUri");
        destinationUsername = getIntent().getStringExtra("destinationUsername");
        detailviewitem_profile_image = findViewById(R.id.detailviewitem_profile_image);
        detailviewitem_profile_textview = findViewById(R.id.detailviewitem_profile_textview);

    //   detailviewitem_timestamp_textview = findViewById(R.id.detailviewitem_timestamp_textview)
        detailviewitem_profile_image = findViewById(R.id.detailviewitem_profile_image);
        detailviewitem_profile_imageview_content = findViewById(R.id.detailviewitem_profile_imageview_content);
        detailviewitem_favrite_imageview = findViewById(R.id.detailviewitem_favrite_imageview);
        detailviewitem_explain_textview = findViewById(R.id.detailviewitem_explain_textview);







        }
    }
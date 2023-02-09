package com.example.sns.navigation;

import android.app.Activity;
import android.content.Context;
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
import com.example.sns.navigation.model.ContentDTO;
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

import org.w3c.dom.Text;

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
    private String destinationUid;
    private String destinationImageUri;
    private String destinationUsername;
    private String destinationtimestamp;
    private String destinationProfileUri;
    private String destinationExplain;
    ArrayList<ContentDTO> contentDTOs;
    private FirebaseFirestore firestore;
    private Context context;
    private Context itemview;
    private Activity activity;
    private FirebaseAuth mFirebaseAuth;
    private String currentUserUid;
    private TextView detailviewitem_time_textview;
    String uid;
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseFirestore.getInstance().collection("contents").document().set(contentDTO);

        String message = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        contentUid = getIntent().getStringExtra("contentUid");
        Log.d("sss", contentUid+"");
        destinationUid = getIntent().getStringExtra("destinationUid");
        Log.d("sss", destinationUid+"");
        destinationImageUri = getIntent().getStringExtra("destinationUri");
        Log.d("sss", destinationImageUri+"");
        destinationUsername = getIntent().getStringExtra("destinationUsername");
        destinationtimestamp = getIntent().getStringExtra("destinationtimestamp");
        destinationProfileUri = getIntent().getStringExtra("destinationProfileUri");
        Log.d("sss", destinationProfileUri+"");
        destinationExplain = getIntent().getStringExtra("destinationExplain");
        detailviewitem_profile_image = findViewById(R.id.detailviewitem_profile_image);
        detailviewitem_profile_textview = findViewById(R.id.detailviewitem_profile_textview);
        detailviewitem_profile_textview.setText(destinationUsername);

          detailviewitem_timestamp_textview = findViewById(R.id.detailviewitem_time_textview);
        detailviewitem_timestamp_textview.setText(destinationtimestamp);
        detailviewitem_profile_image = findViewById(R.id.detailviewitem_profile_image);


        Glide.with(this)
                .load(destinationProfileUri)
                .into(detailviewitem_profile_image);
        detailviewitem_profile_imageview_content = findViewById(R.id.detailviewitem_profile_imageview_content);
    //    detailviewitem_profile_imageview_content.setText(destinationImageUri);

        Glide.with(this)
                .load(destinationImageUri)
                .into(detailviewitem_profile_imageview_content);
        detailviewitem_favrite_imageview = findViewById(R.id.detailviewitem_favrite_imageview);
        detailviewitem_explain_textview = findViewById(R.id.detailviewitem_explain_textview);
        detailviewitem_explain_textview.setText(destinationExplain);
      //  detailviewitem_explain_textview.setText(ContentDTO);
//        detailviewitem_profile_image.setImageURI(contentDTOs.get(position).getComment());
//        detailviewitem_profile_imageview_content.setText(contentDTOs.get(position).getUserId());
//        detailviewitem_explain_textview.setText(contentDTOs.get(position).getTimestamp());
//        detailviewitem_profile_image.setImageURI(contentDTOs.getUid());
        ItemView(destinationUid,detailviewitem_explain_textview.getText().toString());

    }

    public void ItemView (String destinationUid, String explain){
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setDestinationUid(destinationUid);
        contentDTO.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        contentDTO.setImageUri(FirebaseAuth.getInstance().getCurrentUser().getUid());
        contentDTO.setExplain(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Long now =  System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);
        contentDTO.setTimestamp(getTime);
        contentDTO.setExplain(explain);

        FirebaseFirestore.getInstance().collection("contents").document().set(contentDTO);





    }

    }




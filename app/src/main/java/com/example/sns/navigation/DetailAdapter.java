package com.example.sns.navigation;



import static com.example.sns.navigation.Frag5.str;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sns.R;
import com.example.sns.User;
import com.example.sns.navigation.model.ContentDTO;
import com.example.sns.navigation.model.ProfileImage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.example.sns.MainActivity;
import com.example.sns.navigation.model.AlarmDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{
    private FirebaseFirestore firestore;
    private Context context;
    private Context itemview;
    private Activity activity;
    private ImageView detailviewitem_favrite_imageview;
    private FirebaseAuth mFirebaseAuth;
    private String currentUserUid;
    private TextView detailviewitem_time_textview;
    ArrayList<ProfileImage> ProfileImages;
    ArrayList<ContentDTO> contentDTOs;
    ArrayList<User> user;
    public ArrayList<String> contentUidList = new ArrayList<>();

    String uid;
String username;

    Map<String,Boolean> favori  = new HashMap<>();


    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";


    public DetailAdapter(Context context,ArrayList<ContentDTO> contentDTOs) {
        this.context = context;
        this.itemview = context;
        this.contentDTOs = contentDTOs;

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("images").orderBy("timestamp").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots==null)
                            return;
                        contentDTOs.clear();
                        contentUidList.clear();
                        try{
                            for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                ContentDTO item = snapshot.toObject(ContentDTO.class);
                                contentDTOs.add(item);
                                contentUidList.add(snapshot.getId());
                            }
                        }catch (Exception e){
                        }
                        notifyDataSetChanged();
                    }
                });

    }

    //3번
    //일반 onCreate랑 비슷한 친구
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
        ViewHolder holder = new ViewHolder(view);


        return holder;
    }
    //4번
    //실제 추가 될때 작성하는 부분
    //이곳에서 변경되는 부분을 작성하면 다음 프래그1이 실행 될때 추가된다.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String uid2 = contentDTOs.get(position).getUid();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = mFirebaseAuth.getInstance().getCurrentUser().getUid();
        if(Objects.equals(uid2, currentUserUid)){
            holder.item_update.setVisibility(View.VISIBLE);
            holder.item_delete.setVisibility(View.VISIBLE);
        }else {
            holder.item_update.setVisibility(View.INVISIBLE);
            holder.item_delete.setVisibility(View.INVISIBLE);
        }

        holder.item_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UpdatePhoto.class);
                intent.putExtra("position",position);
                v.getContext().startActivity(intent);
            }
        });
        holder.item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("images").document(contentUidList.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(v.getContext(), "삭제완료",Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.detailviewitem_profile_textview.setTextColor(Color.parseColor("#000000"));

        holder.detailviewitem_profile_textview.setText(contentDTOs.get(position).getUsername());
        holder.detailviewitem_time_textview.setText(contentDTOs.get(position).getTimestamp());
        Glide.with(holder.itemView)
                .load(contentDTOs.get(position).getImageUri())
                .into(holder.detailviewitem_profile_imageview_content);
        holder.detailviewitem_explain_textview.setText(contentDTOs.get(position).getExplain());
        holder.detailviewitem_favoritecounter_textview.setText("Likes "+contentDTOs.get(position).getFavoriteCount());
        FirebaseFirestore.getInstance().collection("profileImage").document(contentDTOs.get(position).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot value) {
                        String str = String.valueOf(value.getData().get("profileUri"));
                        //str=str.substring(10,str.length()-1);
                        Uri uri = Uri.parse(str);
                        Glide.with(holder.itemView)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.detailviewitem_profile_image);
                    }
                });

        if(contentDTOs.get(position).getFavorites().containsKey(uid)){
            holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24);
        }else{
            holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
        holder.detailviewitem_profile_image.setTag(position);
        holder.detailviewitem_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Frag5 fragment = new Frag5();
                Bundle bundle = new Bundle();
                bundle.putString("destinationUid",contentDTOs.get(position).getUid());
                bundle.putString("destinationEmail",contentDTOs.get(position).getEmail());
                bundle.putString("userId",contentDTOs.get(position).getUserId());
                fragment.setArguments(bundle);
                FragmentManager iv = ((MainActivity)itemview).getSupportFragmentManager();
                FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
                FragmentTransaction ft;
                FragmentTransaction vi;
                ft = fm.beginTransaction();
                vi = iv.beginTransaction();
                vi.replace(R.id.main_content,fragment);
                vi.commit();
                ft.replace(R.id.main_content,fragment);
                ft.commit();
            }
        });

        holder.detailviewitem_favrite_imageview.setTag(position);
        holder.detailviewitem_favrite_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favori.clear();
                favori.put(uid,true);

                DocumentReference tsDoc = firestore.collection("images").document(contentUidList.get(position));
                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        ContentDTO contentDTO =  transaction.get(tsDoc).toObject(ContentDTO.class);
                        if(contentDTO.getFavorites().containsKey(uid)){
                            contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() -1);
                            contentDTO.getFavorites().remove(uid);

                            firestore.collection("images").whereEqualTo("favorites",favori)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (value != null) {
                                                holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                                holder.detailviewitem_favoritecounter_textview.setText("Likes "+contentDTO.getFavoriteCount());
                                                return;
                                            }

                                            for (DocumentSnapshot doc : value) {

                                            }
                                                notifyDataSetChanged();
                                        }
                                    });


                        }else{
                            favoriteAlarm(contentDTOs.get(position).getUid());
                            contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() +1);
                            contentDTO.getFavorites().put(uid,true);
                            firestore.collection("images").whereEqualTo("favorites",favori)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (value != null) {
                                                holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24);
                                                holder.detailviewitem_favoritecounter_textview.setText("Likes "+contentDTO.getFavoriteCount());
                                                return;
                                            }
                                            for (DocumentSnapshot doc : value) {

                                            }
                                                notifyDataSetChanged();
                                        }
                                    });

                        }
                        transaction.set(tsDoc,contentDTO);

                        return null;
                    }
                });

            }
        });



        holder.detailviewitem_profile_imageview_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("profileImage").document(contentDTOs.get(position).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    //        itemIntent.putExtra("destinationProfileUri",ProfileImage.get(position).getImageUri());

                    @Override
                    public void onSuccess(DocumentSnapshot value) {
                        Intent itemIntent = new Intent(view.getContext(),ItemActivity.class);
                        String str = String.valueOf(value.getData().get("profileUri"));
                        //str=str.substring(10,str.length()-1);
                        Uri uri = Uri.parse(str);


                    }
                });
                Intent itemIntent = new Intent(view.getContext(),ItemActivity.class);
                itemIntent.putExtra("destinationProfileUri",contentDTOs.get(position).getProfileUri());
                itemIntent.putExtra("contentUid", contentUidList.get(position));
                itemIntent.putExtra("destinationUid",contentDTOs.get(position).getUid());
                itemIntent.putExtra("destinationEmail",contentDTOs.get(position).getEmail());
                itemIntent.putExtra("destinationUri",contentDTOs.get(position).getImageUri());
                itemIntent.putExtra("destinationUsername",contentDTOs.get(position).getUsername());
                itemIntent.putExtra("destinationtimestamp",contentDTOs.get(position).getTimestamp());
                itemIntent.putExtra("destinationExplain",contentDTOs.get(position).getExplain());
                itemIntent.putExtra("destinationProfileUri",contentDTOs.get(position).getProfileUri());


                itemview.startActivity(itemIntent);
            }
        });


        holder.detailviewitem_comment_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("contentUid", contentUidList.get(position));
                intent.putExtra("destinationUid",contentDTOs.get(position).getUid());
                intent.putExtra("destinationEmail",contentDTOs.get(position).getEmail());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contentDTOs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView detailviewitem_profile_image;
        TextView detailviewitem_profile_textview;
        ImageView detailviewitem_profile_imageview_content;
        TextView detailviewitem_favoritecounter_textview;
        TextView detailviewitem_explain_textview;
        ImageView detailviewitem_favrite_imageview;
        ImageView detailviewitem_comment_imageview;

TextView detailviewitem_time_textview;
        TextView item_update;
        TextView item_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.detailviewitem_profile_image = itemView.findViewById(R.id.detailviewitem_profile_image);
            this.detailviewitem_profile_textview = itemView.findViewById(R.id.detailviewitem_profile_textview);
            this.detailviewitem_profile_imageview_content = itemView.findViewById(R.id.detailviewitem_profile_imageview_content);
            this.detailviewitem_favoritecounter_textview = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
            this.detailviewitem_explain_textview = itemView.findViewById(R.id.detailviewitem_explain_textview);
            this.detailviewitem_favrite_imageview = itemView.findViewById(R.id.detailviewitem_favrite_imageview);
            this.detailviewitem_comment_imageview = itemView.findViewById(R.id.detailviewitem_comment_imageview);
            this.detailviewitem_time_textview = itemView.findViewById(R.id.detailviewitem_time_textview);
            this.item_update=itemView.findViewById(R.id.item_update);
            this.item_delete=itemView.findViewById(R.id.item_delete);



        }
    }
    private void favoriteAlarm(String destinationUid){
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setDestinationUid(destinationUid);
        alarmDTO.setUserId(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        alarmDTO.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        alarmDTO.setKind(0);
        Long now =  System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);
        alarmDTO.setTimestamp(getTime);

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO);

        String message = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }
}

package com.example.sns.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sns.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.example.sns.Login;
import com.example.sns.MainActivity;
import com.example.sns.navigation.model.ContentDTO;
import com.example.sns.navigation.model.FollowDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//user Fragment
public class Frag5 extends Fragment {
    private View view;
    private FirebaseFirestore firestore;
    private FirebaseAuth mFirebaseAuth;
    private String uid;
    private String currentUserUid = null;
    private View fragmentView;
    private Button account_btn_follow_signout;
    private ImageView account_iv_profile;
    private TextView account_tv_following_count;
    private TextView account_tv_follower_count;
    private ImageView detailviewitem_profile_imageview_content;
    private Context itemview;
    private RecyclerView account_recyclerview;
    private LinearLayout followerList;
    private UserFragmentRecyclerviewAdapter adapter;

    private GridLayoutManager gridLayoutManager;
    public ArrayList<String> contentUidList = new ArrayList<>();
    ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
    TextView account_tv_post_count;

    public static String str;
    public static int PICK_PROFILE_FROM_ALBUM=10;
    MainActivity mainActivity;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://sns-fbs2.appspot.com/");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.frag5,container,false);
        firestore = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        account_btn_follow_signout = fragmentView.findViewById(R.id.account_btn_follow_signout);
        account_iv_profile = fragmentView.findViewById(R.id.account_iv_profile);
        account_tv_following_count = fragmentView.findViewById(R.id.account_tv_following_count);
        account_tv_follower_count = fragmentView.findViewById(R.id.account_tv_follower_count);
        followerList = fragmentView.findViewById(R.id.followerlist);
        //DetailAdapter ?????? ?????? ?????? ?????????
        //????????? ??????
        uid = getArguments().getString("destinationUid");

        //?????? ????????? ?????? ?????? ?????????
        currentUserUid = mFirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.e("TAG", "onCreateView: uid = "+ uid);
        Log.e("TAG", "onCreateView: currentUserUid = "+ currentUserUid);
        //????????? ????????? ?????? == ??? ???????????? ?????????.
        //????????? ?????? ????????? ?????? ????????? equals()??? ???????????? ??????.


        account_recyclerview = fragmentView.findViewById(R.id.account_recyclerview);
        account_recyclerview.setHasFixedSize(true);
        //3????????? ????????? ?????????
        gridLayoutManager = new GridLayoutManager(getContext(),3);
        account_recyclerview.setLayoutManager(gridLayoutManager);
        adapter = new UserFragmentRecyclerviewAdapter(contentDTOs);
        account_recyclerview.setAdapter(adapter);
        account_tv_post_count = fragmentView.findViewById(R.id.account_tv_post_count);


        getProfileImage();
        getFollowerAndFollowing();

        if(uid.equals(currentUserUid)){
            //?????? ?????????
            account_btn_follow_signout.setText("????????????");
            account_iv_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                    photoPickIntent.setType("image/*");
                    getActivity().startActivityForResult(photoPickIntent, PICK_PROFILE_FROM_ALBUM);

                }
            });
/*
            followerList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> contentUidList = new ArrayList<>();
                    Intent followerList = new Intent(getActivity(),Frag6.class);
                    int position = getArguments().getInt("position",0);
                    followerList.putExtra("uid",uid);
                    //followerList.putExtra("destinationEmail",contentDTOs.get(position).getEmail());
                    startActivity(followerList);
                }
            });

 */

            account_btn_follow_signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(), Login.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }else {

            //???????????? ?????????
            account_btn_follow_signout.setText("?????????");

            mainActivity = (MainActivity) getContext();
            if (mainActivity != null) {
                String userId = getArguments().getString("userId");

                account_btn_follow_signout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestFollow();
                    }
                });
            }
        }

        return fragmentView;

    }

    private void getFollowerAndFollowing(){
        firestore.collection("users").document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value == null){
                            return;
                        }
                        FollowDTO followDTO = value.toObject(FollowDTO.class);
                        if(followDTO == null){
                            account_tv_following_count.setText("0");
                            account_tv_follower_count.setText("0");
                            account_btn_follow_signout.setText("????????????");


                            return;
                        }
                        if(followDTO.getFollowingCount() == 0){
                            account_tv_following_count.setText("0");
                        }else{
                            account_tv_following_count.setText(String.valueOf(followDTO.getFollowingCount()));
                        }

                        if(followDTO.getFollowerCount() == 0){
                            account_tv_follower_count.setText("0");
                        }else{
                            account_tv_follower_count.setText(String.valueOf(followDTO.getFollowerCount()));
                            if(followDTO.getFollowers().containsKey(currentUserUid)){
                                account_btn_follow_signout.setText("????????????");
                            }else{

                                    account_btn_follow_signout.setText("?????????");

                            }
                        }
                    }
                });
    }
    private void requestFollow() {
        //????????? ?????? ?????? ?????????
        //uid??? ??????????????? ??????
        //currentUserUid??? ?????? ???????????? ?????? ??????
        DocumentReference tsDocFollowing = firestore.collection("users").document(currentUserUid);
        firestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                FollowDTO followDTO = transaction.get(tsDocFollowing).toObject(FollowDTO.class);
                if (followDTO == null) {
                    followDTO = new FollowDTO();
                    followDTO.setFollowingCount(1);
                    Map<String, Boolean> set_follow = new HashMap<>();
                    set_follow.clear();
                    set_follow.put(uid, true);
                    followDTO.setFollowings(set_follow);
                    transaction.set(tsDocFollowing, followDTO);
                    set_follow.clear();
                    Log.e("?????? ???????????? ????????? ???", "????????? ??????1");
                    return null;
                }


                if (followDTO.getFollowings().containsKey(uid)) {
                    followDTO.setFollowingCount(followDTO.getFollowingCount() - 1);
                    followDTO.getFollowings().remove(uid);
                    transaction.set(tsDocFollowing, followDTO);
                    Log.e("???????????? ????????? ???", "????????? ??????1");

                    return null;
                } else {
                    Map<String, Boolean> set_follow = new HashMap<>();
                    set_follow.clear();
                    String follow_str;
                    Iterator<String> keys = followDTO.getFollowings().keySet().iterator();
                    while (keys.hasNext()) {
                        follow_str = keys.next();
                        set_follow.put(follow_str, true);
                    }

                    set_follow.put(uid, true);


                    followDTO.setFollowingCount(followDTO.getFollowingCount() + 1);
                    followDTO.setFollowings(set_follow);

                    transaction.update(tsDocFollowing, "followings", set_follow);
                    transaction.update(tsDocFollowing, "followingCount", followDTO.getFollowingCount());
                    Log.e("????????? ???????????? ????????? ???", "????????? ??????1");

                    return null;
                }

            }
        });
        //?????? ????????? ?????? ?????????
        //uid??? ??????????????? ??????+
        //currentUserUid??? ?????? ???????????? ?????? ??????
        DocumentReference tsDocFollower = firestore.collection("users").document(uid);
        firestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                FollowDTO followDTO = transaction.get(tsDocFollower).toObject(FollowDTO.class);
                if (followDTO == null) {
                    followDTO = new FollowDTO();
                    followDTO.setFollowerCount(1);
                    Map<String, Boolean> set_follow = new HashMap<>();
                    set_follow.clear();
                    set_follow.put(currentUserUid,true);
                    followDTO.setFollowers(set_follow);

                    transaction.set(tsDocFollower, followDTO);
                    set_follow.clear();
                    Log.e("?????? ???????????? ????????? ???", "????????? ??????2");

                    return null;
                }
                if (followDTO.getFollowers().containsKey(currentUserUid)) {
                    followDTO.setFollowerCount(followDTO.getFollowerCount() - 1);
                    followDTO.getFollowers().remove(currentUserUid);
                    transaction.set(tsDocFollower, followDTO);
                    Log.e("???????????? ????????? ???", "????????? ??????2");

                    return null;
                } else {
                    Map<String, Boolean> set_follow = new HashMap<>();
                    set_follow.clear();
                    String follow_str;
                    Iterator<String> keys = followDTO.getFollowers().keySet().iterator();
                    while (keys.hasNext()) {
                        follow_str = keys.next();
                        set_follow.put(follow_str, true);
                        Log.e("????????? ?????? ?????????", follow_str);
                    }
                    set_follow.put(currentUserUid, true);
                    followDTO.setFollowerCount(followDTO.getFollowerCount() + 1);
                    followDTO.setFollowers(set_follow);
                    transaction.update(tsDocFollower, "followers", set_follow);
                    transaction.update(tsDocFollower, "followerCount", followDTO.getFollowerCount());
                    Log.e("????????? ???????????? ????????? ???", "????????? ??????2");

                    return null;
                }
            }
        });
    }

    //????????? ????????????
    private void getProfileImage(){
        firestore.collection("profileImage").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot value) {
                        if(value.getData()==null){
                            Toast.makeText(getActivity(), "????????? ???????????? ????????? ????????? ??????", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(value !=null ){
                            String str = String.valueOf(value.getData().get("profileUri"));
                            Uri uri = Uri.parse(str);
                            Glide.with(getActivity())
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(account_iv_profile);

                        }
                    }
                });
    }
    public class UserFragmentRecyclerviewAdapter extends RecyclerView.Adapter<UserFragmentRecyclerviewAdapter.ViewHolder>{
        public UserFragmentRecyclerviewAdapter(ArrayList<ContentDTO> contentDTOs){
            contentDTOs.clear();
            String user_test = FirebaseAuth.getInstance().getCurrentUser().getEmail();


            firestore.collection("images").whereEqualTo("uid",uid).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            contentDTOs.clear();

                            try{
                                for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                                    ContentDTO item = snapshot.toObject(ContentDTO.class);
                                    contentDTOs.add(item);
                                }
                            }catch (Exception e){
                            }
                            finally {
                                Collections.sort(contentDTOs,new SortByDate());
                                Collections.reverse(contentDTOs);
                            }
                            str = Integer.toString(contentDTOs.size());
                            notifyDataSetChanged();

                        }
                    });
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view,parent,false);
            ViewHolder holder = new ViewHolder(view);

            //????????? ????????? ??????????????? ?????? ????????? ?????? ????????????.
            /*
            Display wdisplay=getActivity().getWindowManager().getDefaultDisplay();  // in Fragment
            Point wsize = new Point();
            wdisplay.getSize(wsize);
            int width = wsize.x/3;

            Display hdisplay=getActivity().getWindowManager().getDefaultDisplay();  // in Fragment
            Point hsize = new Point();
            hdisplay.getSize(hsize);
            int height = hsize.y;

            String wstr = Integer.toString(width);
            Toast.makeText(getContext(), wstr, Toast.LENGTH_SHORT).show();

            String hstr = Integer.toString(height);
            Toast.makeText(getContext(), hstr, Toast.LENGTH_SHORT).show();*/


//            int width = getResources().getDisplayMetrics().widthPixels / 3;
//            ImageView imageview = new ImageView(parent.getContext());
//            imageview.setLayoutParams(new LinearLayoutCompat.LayoutParams(width,width));
//
//            ViewHolder holder = new ViewHolder(imageview);

            return holder;
        }








        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            account_tv_post_count.setText(str);

            Glide.with(holder.itemView)
                    .load(contentDTOs.get(position).getImageUri())
                    .into(holder.profile_image);

            //???????????? ?????????????????? ????????? @SuppressLint("RecyclerView") ????????? ???????????????.
            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //?????? ?????? ????????????
//                    String pstr = Integer.toString(position);
//                    Toast.makeText(getContext(), pstr, Toast.LENGTH_SHORT).show();
                }
            });
            if(uid.equals(currentUserUid)) {
                account_iv_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                        photoPickIntent.setType("image/*");
                        getActivity().startActivityForResult(photoPickIntent, PICK_PROFILE_FROM_ALBUM);

                    }
                });


            }
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView account_tv_post_count;
            ImageView profile_image;
            TextView toolbar_username;
            ImageView account_iv_profile;
            TextView account_tv_following_count;
            ImageView detailviewitem_favoritecounter_textview;
            ImageView detailviewitem_profile_imageview_content;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.account_tv_post_count = itemView.findViewById(R.id.account_tv_post_count);
                this.profile_image = itemView.findViewById(R.id.profile_image);
                this.account_iv_profile = itemView.findViewById(R.id.account_iv_profile);
                this.account_tv_following_count =itemView.findViewById(R.id.account_tv_following_count);
                this.detailviewitem_favoritecounter_textview = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
                this.detailviewitem_profile_imageview_content = itemView.findViewById(R.id.detailviewitem_profile_imageview_content);
            }
        }
    }

    //???????????? ??????
    class SortByDate implements Comparator<ContentDTO>{
        @Override
        public int compare(ContentDTO contentDTO, ContentDTO t1) {
            return contentDTO.getTimestamp().compareTo(t1.getTimestamp());
        }
    }

}

package com.example.sns.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns.R;
import com.example.sns.navigation.model.ContentDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

//detail Fragment
public class Frag1 extends Fragment {
    private View view;
    private FirebaseFirestore firestore;
    //리싸이클러뷰 사용을 위한 선언↓
    private RecyclerView detailviewfragment_recyclerview;
    private DetailAdapter adapter;
    private LinearLayoutManager layoutManager;
    ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
    ArrayList<String> contentUidList = new ArrayList<>();
    String uid;
String username;
    private ImageView detailviewitem_favrite_imageview;




    //2번
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1,container,false);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //리싸이클러뷰를 정의한다.↓
        detailviewfragment_recyclerview = view.findViewById(R.id.detailviewfragment_recyclerview);
        detailviewfragment_recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,true);
        layoutManager.setStackFromEnd(true);
        detailviewfragment_recyclerview.setLayoutManager(layoutManager);
        //adapter에 contentDTOs를 담아서
        //리싸이클러뷰에 적용 시킨다.
        adapter = new DetailAdapter(getActivity(),contentDTOs);
        detailviewfragment_recyclerview.setAdapter(adapter);
        detailviewitem_favrite_imageview = view.findViewById(R.id.detailviewitem_favrite_imageview);


        return view;
    }





/*

리사이클러뷰 어댑터를 한곳에 만드는 것을 좋지 않다고 들었다.
DetailAdapter 라는 자바 파일을 따로 만들어 주었다.
그냥 참고용 으로 남겨두는 것이다.
    public class DetailViewRecyclerViewAdapter extends RecyclerView.Adapter<DetailViewRecyclerViewAdapter.ViewHolder>{
//        ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
//        ArrayList<String> contentUidList = new ArrayList<>();
//홍        Context context;


        //리싸이클러뷰 어댑터 생성자↓
        //파이어베이스에서 데이터를 가져와서 contentDTOs에 담고
        //contentDTOs는 ContentDTO와 연결되어있어서 데이터를 받아들인다.
        public DetailViewRecyclerViewAdapter(ArrayList<ContentDTO> contentDTOs) {
//홍            this.contentDTOs = contentDTOs;
//홍            this.contentUidList = contentUidList;
//홍            this.context = context;

            firestore.collection("images").orderBy("timestamp").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
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
        @NonNull
        @Override
        public DetailViewRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail,parent,false);
            ViewHolder holder= new  ViewHolder(view);



            return holder;
        }

        //가져온 이미지와 정보를 저장하는 곳↓ //담는 곳
        @Override
        public void onBindViewHolder(@NonNull DetailViewRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.detailviewitem_profile_textview.setText(contentDTOs.get(position).getUserId());
            Glide.with(holder.itemView)
                    .load(contentDTOs.get(position).getImageUri())
                    .into(holder.detailviewitem_profile_imageview_content);
            holder.detailviewitem_explain_textview.setText(contentDTOs.get(position).getExplain());
            holder.detailviewitem_favoritecounter_textview.setText("Likes "+contentDTOs.get(position).getFavoriteCount());
            Glide.with(holder.itemView)
                    .load(contentDTOs.get(position).getImageUri())
                    .into(holder.detailviewitem_profile_image);

            if(contentDTOs.get(position).getFavorites().containsKey(uid)){
                holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_24);
            }else{
                holder.detailviewitem_favrite_imageview.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
            holder.detailviewitem_favrite_imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteEvent(position);



                }
            });


        }
        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView detailviewitem_profile_image;
            TextView detailviewitem_profile_textview;
            ImageView detailviewitem_profile_imageview_content;
            TextView detailviewitem_favoritecounter_textview;
            TextView detailviewitem_explain_textview;
            ImageView detailviewitem_favrite_imageview;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.detailviewitem_profile_image = itemView.findViewById(R.id.detailviewitem_profile_image);
                this.detailviewitem_profile_textview = itemView.findViewById(R.id.detailviewitem_profile_textview);
                this.detailviewitem_profile_imageview_content = itemView.findViewById(R.id.detailviewitem_profile_imageview_content);
                this.detailviewitem_favoritecounter_textview = itemView.findViewById(R.id.detailviewitem_favoritecounter_textview);
                this.detailviewitem_explain_textview = itemView.findViewById(R.id.detailviewitem_explain_textview);
                this.detailviewitem_favrite_imageview = itemView.findViewById(R.id.detailviewitem_favrite_imageview);
            }
        }

        private void favoriteEvent(int position){
            DocumentReference tsDoc = firestore.collection("images").document(contentUidList.get(position));

            firestore.runTransaction(new Transaction.Function<Void>() {

                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                    ContentDTO contentDTO =  transaction.get(tsDoc).toObject(ContentDTO.class);

                    if(contentDTO.getFavorites().containsKey(uid)){
                        contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() -1);
                        contentDTO.getFavorites().remove(uid);
                    }else{
                        contentDTO.setFavoriteCount(contentDTO.getFavoriteCount() +1);
                        contentDTO.getFavorites().put(uid,true);
                    }
                    transaction.set(tsDoc,contentDTO);
                    return null;
                }
            });
        }
    }*/
}

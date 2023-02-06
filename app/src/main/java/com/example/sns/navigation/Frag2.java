package com.example.sns.navigation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sns.R;
import com.example.sns.navigation.model.ContentDTO;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//grid fragment
public class Frag2 extends Fragment {
    private View view;
    public FirebaseFirestore firestore;
    private View fragmentView;

    private RecyclerView gridfragment_recy;
    private GridFragmentAdapter adapter;
    private LinearLayoutManager layoutManager;

    private GridLayoutManager gridLayoutManager;
    ArrayList<ContentDTO> contentDTOs = new ArrayList<>();
    ArrayList<String> contentUidList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.frag2,container,false);
        firestore = FirebaseFirestore.getInstance();

        gridfragment_recy = fragmentView.findViewById(R.id.gridfragment_recy);
        gridfragment_recy.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getContext(),3);
        gridfragment_recy.setLayoutManager(gridLayoutManager);
        adapter = new GridFragmentAdapter(contentDTOs);
        gridfragment_recy.setAdapter(adapter);

        return fragmentView;
    }

    public class GridFragmentAdapter extends RecyclerView.Adapter<GridFragmentAdapter.ViewHolder>{
        private FirebaseFirestore firestore;
        private Context context;
        private Activity activity;
        private ImageView detailviewitem_favrite_imageview;
        String uid;
        String username;
        Map<String,Boolean> favori  = new HashMap<>();




        //1번
        public GridFragmentAdapter(ArrayList<ContentDTO> contentDTOs) {
//            this.contentDTOs = contentDTOs;
            firestore = FirebaseFirestore.getInstance();
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            username = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                            finally {
                                Collections.sort(contentDTOs,new SortByDate());
                                //정렬 이후 정렬된 것을 반대로 돌린다.
                                Collections.reverse(contentDTOs);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view,parent,false);
            ViewHolder holder = new ViewHolder(view);



            return holder;
        }
        //4번
        //실제 추가 될때 작성하는 부분
        //이곳에서 변경되는 부분을 작성하면 다음 프래그1이 실행 될때 추가된다.
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Glide.with(holder.itemView)
                    .load(contentDTOs.get(position).getImageUri())
                    .into(holder.profile_image);

        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView profile_image;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.profile_image = itemView.findViewById(R.id.profile_image);

            }
        }
    }
    class SortByDate implements Comparator<ContentDTO> {
        @Override
        public int compare(ContentDTO contentDTO, ContentDTO t1) {
            return contentDTO.getTimestamp().compareTo(t1.getTimestamp());
        }
    }

}

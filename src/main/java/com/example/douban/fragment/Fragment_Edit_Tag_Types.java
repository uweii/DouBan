package com.example.douban.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.douban.R;
import com.example.douban.SharePre.SharePreUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by uwei on 2018/3/8.
 */

public class Fragment_Edit_Tag_Types extends Fragment {
    private String TAG = "Fragment_Edit_Tag_Types";
    private Button mButton;
    private  int mType;  // 0: movie  1: book
    private String[] mMovieTags = {"爱情", "喜剧", "动画", "科幻", "动作", "经典",
            "剧情","悬疑","犯罪","恐怖","青春",
            "励志","战争","文艺","黑色","幽默",
            "传记","情色","暴力","音乐","家庭"};
    private String[] mBookTags = {
            "小说","名著","科幻","历史","爱情","编程",
            "旅行",	"生活","成长",
            "励志","心理","摄影",	"女性",
            "职场","教育",	"美食","游记"
            ,"灵修","健康",	"情感",	"手工",
            "两性","养生","人际","关系","家居",
            "自助游"};
    private List<String> mUnchoosed;
    private List<String> mChoosed;
    private boolean mShowIcon = false;  //是否显示编辑icon
    private TagAdapter mTagAdapter; //其他标签adapter
    private TagChoosedAdapter mChoosedTagAdapter; //我的标签adapter
    private RecyclerView mTagChoosed;  //已选择的ev
    private RecyclerView mTagUnchoosed;  //未选择的ev
    private  Fragment_Edit_Tag_Types mEditTagTypes;
    public  Fragment_Edit_Tag_Types newInstance(int type)
    {
        mEditTagTypes = new Fragment_Edit_Tag_Types();
        mEditTagTypes.mType = type;
        return mEditTagTypes;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_tab_movie_or_book,container,false);
        mButton = v.findViewById(R.id.edit_button);
        mTagChoosed = v.findViewById(R.id.tag_choosed);
        mTagUnchoosed = v.findViewById(R.id.tag_unchoose);
       init();
        return v;
    }

    private void init()
    {
        mChoosed = new ArrayList<>();
        mUnchoosed = new ArrayList<>();
        if(mType==0){
            String SeletedTag = SharePreUtil.getSelectedMovieTag(getActivity()).trim();
            String movieSeletedTag = SeletedTag.substring(1,SeletedTag.length()-1);
            String[] selectedMovieArr = movieSeletedTag.split(",");
            for(int i =0; i <selectedMovieArr.length;i++)
            {
                mChoosed.add(selectedMovieArr[i]);
            }
            for(int i = 0;i<mMovieTags.length;i++){
                if(mChoosed.contains(mMovieTags[i])){
                    continue;
                }
                mUnchoosed.add(mMovieTags[i]);
            }
        }else {
            String SeletedTag = SharePreUtil.getSelectedBookTag(getActivity()).trim();
            String  bookSeletedTag = SeletedTag.substring(1,SeletedTag.length() - 1);
            String[] selectedBookArr = bookSeletedTag.split(",");
            for(int i =0; i <selectedBookArr.length;i++)
            {
                mChoosed.add(selectedBookArr[i]);
            }
            for(int i = 0;i<mBookTags.length;i++){
                if(mChoosed.contains(mBookTags[i])){
                    continue;
                }
                mUnchoosed.add(mBookTags[i]);
            }
        }
        mTagAdapter = new TagAdapter();
        mChoosedTagAdapter = new TagChoosedAdapter();
        GridLayoutManager choosedManager = new GridLayoutManager(getActivity(),3);
        GridLayoutManager unChosedManager = new GridLayoutManager(getActivity(),3);
        mTagChoosed.setLayoutManager(choosedManager);
        mTagUnchoosed.setLayoutManager(unChosedManager);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemDrag());
        touchHelper.attachToRecyclerView(mTagChoosed);
        mTagAdapter.setHasStableIds(true);
        mChoosedTagAdapter.setHasStableIds(true);
        mTagUnchoosed.setAdapter(mTagAdapter);
        mTagChoosed.setAdapter(mChoosedTagAdapter);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mShowIcon){
                    mButton.setText("编辑");
                    SharePreUtil.saveTags(mType,mChoosed,getActivity());
                }else{
                    mButton.setText("完成");
                }
                mShowIcon = !mShowIcon;
                mTagAdapter.notifyDataSetChanged();
                mChoosedTagAdapter.notifyDataSetChanged();
            }
        });
    }

    private class TagAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.tag_item,parent,false);
            return new TagHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TagHolder tagHolder = (TagHolder) holder;
            tagHolder.tagName.setText(mUnchoosed.get(position));
            tagHolder.myIcon.setVisibility(mShowIcon?View.VISIBLE:View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mShowIcon){
                        String cur = mUnchoosed.get(position);
                        mUnchoosed.remove(position);
                        mTagAdapter.notifyItemRemoved(position);
                        mTagAdapter.notifyDataSetChanged();
                        mChoosed.add(cur);
                        mChoosedTagAdapter.notifyItemInserted(mChoosed.size());
                        mChoosedTagAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUnchoosed.size();
        }

        class TagHolder extends RecyclerView.ViewHolder{
            public TextView tagName;
            public ImageView myIcon;
            public TagHolder(View itemView) {
                super(itemView);
                tagName = itemView.findViewById(R.id.tag_name);
                myIcon = itemView.findViewById(R.id.special_tag);
            }
        }
    }

    private class TagChoosedAdapter extends RecyclerView.Adapter{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.tag_item,parent,false);
            return new TagHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TagHolder tagHolder = (TagHolder) holder;
                tagHolder.tagName.setText(mChoosed.get(position));
                tagHolder.myIcon.setVisibility(mShowIcon?View.VISIBLE:View.INVISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mShowIcon){
                            String cur = mChoosed.get(position);
                            mChoosed.remove(position);
                            mChoosedTagAdapter.notifyItemRemoved(position);
                            mChoosedTagAdapter.notifyDataSetChanged();
                            mUnchoosed.add(0,cur);
                            mTagAdapter.notifyItemInserted(0);
                            mTagAdapter.notifyDataSetChanged();
                        }
                    }
                });

        }

        @Override
        public int getItemCount() {
                return mChoosed.size();
        }

        class TagHolder extends RecyclerView.ViewHolder{
            public TextView tagName;
            public ImageView myIcon;
            public TagHolder(View itemView) {
                super(itemView);
                tagName = itemView.findViewById(R.id.tag_name);
                myIcon = itemView.findViewById(R.id.special_tag);
            }
        }
    }

    class ItemDrag extends ItemTouchHelper.Callback{
        //设置可移动的标志
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN
                    |ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
            int swipeFlags = 0;
            return makeMovementFlags(dragFlags,swipeFlags);
        }
        //移动时回调
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(mChoosed,fromPosition,toPosition);
            mChoosedTagAdapter.notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            mShowIcon = true;
            mTagAdapter.notifyDataSetChanged();
            mChoosedTagAdapter.notifyDataSetChanged();
            mButton.setText("完成");
            return true;
        }

        @Override //移动完成后
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override  //设置移动时背景色
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override//移动完成后恢复背景色
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundColor(0);
        }
    }
}

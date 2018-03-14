package com.example.douban.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douban.Cache.DeleteData;
import com.example.douban.R;
import com.example.douban.SharePre.SharePreUtil;
import com.example.douban.activity.MainActivity;

/**
 * Created by uwei on 2018/3/12.
 */

public class Fragment_Settings extends Fragment {
    private String TAG = "Fragment_Settings";
    private LinearLayout ll_change_theme;
    private TextView tv_show_theme_status;
    private RelativeLayout rl_layout_change_paper;
    private TextView show_paper_status;
    private CheckBox checkbox;
    private LinearLayout ll_clear;
    private TextView show_space_status;
    private LinearLayout ll_source_code;
    private LinearLayout ll_contact;
    private int mThemeTag ;
    private boolean isChangePaperOpen;  //是否打开了切换壁纸
    private String mCacheSize;  //缓存大小
    public static Fragment_Settings newInstance()
    {
        return new Fragment_Settings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting,container,false);
        init(v);
        return v;
    }

    private void init(View v)
    {
        mCacheSize = DeleteData.getDataSize(getActivity());
        mThemeTag = MainActivity.intThemeTag;
        ll_change_theme = v.findViewById(R.id.ll_change_theme);
        tv_show_theme_status = v.findViewById(R.id.tv_show_theme_status);
        rl_layout_change_paper = v.findViewById(R.id.rl_layout_change_paper);
        show_paper_status = v.findViewById(R.id.show_paper_status);
        checkbox = v.findViewById(R.id.change_paper_checkbox);
        ll_clear = v.findViewById(R.id.ll_clear);
        show_space_status = v.findViewById(R.id.show_space_status);
        ll_source_code = v.findViewById(R.id.source_code);
        ll_contact = v.findViewById(R.id.ll_contact);
        show_space_status.setText(mCacheSize);
        ll_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteData.del(getActivity());
                show_space_status.setText("OK");
                new MainActivity().showToast(getActivity(),"清理缓存成功");
            }
        });
        if(mThemeTag==0){
            tv_show_theme_status.setText("白天");
        }else {
            tv_show_theme_status.setText("黑夜");
        }
        //切换主题侦听
        ll_change_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String[] choice = {"白天","黑夜"};
                builder.setTitle("更换主题模式").setSingleChoiceItems(choice, mThemeTag, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(mThemeTag == 0){
                                dialog.dismiss();
                                return;
                            }
                            SharePreUtil.setTheme(getActivity(),"0");
                        }else {
                            if(mThemeTag==1){
                                dialog.dismiss();
                                return;
                            }
                            SharePreUtil.setTheme(getActivity(),"1");
                        }
                        dialog.dismiss();
                        getActivity().finish();
                        Intent intent = new Intent(getContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消",null).show();
            }
        });
        if(MainActivity.intPaperTag==1){
            isChangePaperOpen = true;
            show_paper_status.setText("打开");
            checkbox.setChecked(true);
        }else {
            isChangePaperOpen = false;
            checkbox.setChecked(false);
            show_paper_status.setText("关闭");
        }
        //切换壁纸的侦听
        rl_layout_change_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChangePaperOpen = !isChangePaperOpen;
                show_paper_status.setText(isChangePaperOpen?"打开":"关闭");
                checkbox.setChecked(isChangePaperOpen);
                SharePreUtil.setWhetherChangeWallPaper(getActivity(),isChangePaperOpen?1:2);
                Toast.makeText(getActivity(),"更改将会在下次打开应用生效",Toast.LENGTH_SHORT).show();
            }
        });

        ll_source_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uweii/DouBan.git"));
                startActivity(intent);
            }
        });

        ll_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=893702494";//uin是发送过去的qq号码
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }catch (Exception e){
                    new MainActivity().showToast(getActivity(),"请检查是否安装QQ");
                }
            }
        });

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            mCacheSize = DeleteData.getDataSize(getActivity());
            show_space_status.setText(mCacheSize);
        }
    }
}

package com.plantnurse.plantnurse.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.kot32.ksimplelibrary.activity.i.IBaseAction;
import com.kot32.ksimplelibrary.fragment.t.base.KSimpleBaseFragmentImpl;
import com.kot32.ksimplelibrary.manager.task.base.NetworkTask;
import com.kot32.ksimplelibrary.manager.task.base.SimpleTaskManager;
import com.kot32.ksimplelibrary.network.NetworkExecutor;
import com.plantnurse.plantnurse.Network.GetIndexResponse;
import com.plantnurse.plantnurse.utils.CharacterParser;
import com.plantnurse.plantnurse.utils.Constants;
import com.plantnurse.plantnurse.utils.PinyinComparator;
import com.plantnurse.plantnurse.utils.PlantIndexManager;
import com.plantnurse.plantnurse.utils.SideBar;
import com.plantnurse.plantnurse.utils.SortAdapter;
import com.plantnurse.plantnurse.utils.SortModel;
import com.plantnurse.plantnurse.R;
import com.plantnurse.plantnurse.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.name;
import static android.R.id.list;
import static com.plantnurse.plantnurse.R.array.date;

/**
 * Created by Heloise on 2016/8/31.
 */

public class BookFragment extends KSimpleBaseFragmentImpl implements IBaseAction {

    private ListView sortListView;
    private SideBar sideBar; // 右边的引导
    private TextView dialog;

    private SortAdapter adapter; // 排序的适配器

    private CharacterParser characterParser;

    private PinyinComparator pinyinComparator;
    private LinearLayout xuanfuLayout; // 顶部悬浮的layout
    private TextView xuanfaText; // 悬浮的文字， 和左上角的群发
    private int lastFirstVisibleItem = -1;
    private ArrayList<String> plantname;
    private ArrayList<Integer> planticon;
    boolean isUpdate=false;
    GetIndexResponse indexdata;
    private List<SortModel> sourceDateList;

    @Override
    public int initLocalData() {
        indexdata= PlantIndexManager.getPlantIndex();
        plantname=new ArrayList<>();
        planticon=new ArrayList<>();
        for(int i=0;i<indexdata.response.size();i++) {
            plantname.add(indexdata.response.get(i).name);
            planticon.add(indexdata.response.get(i).id);
        }
        isUpdate=false;
        return 0;

    }

    @Override
    public void initView(ViewGroup view) {
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        xuanfuLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        xuanfaText = (TextView) view.findViewById(R.id.top_char);
        sideBar = (SideBar) view.findViewById(R.id.sidrbar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        sortListView = (ListView) view.findViewById(R.id.country_lvcountry);
    }



    //核心 成功生成了List<SortModel>,去生成那一排排列表状的东西
    private List<SortModel> filledData(List<String> name) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < name.size(); i++) {

            SortModel sortModel = new SortModel();
            sortModel.setName(name.get(i));
            sortModel.setId(planticon.get(i));
            Bitmap bitmap=Util.getHttpBitmap(Constants.PLANTICON_URL+planticon.get(i));
            sortModel.seticonBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.avatar));
           // sortModel.seticonBitmap(bitmap);
            String pinyin = characterParser.getSelling(name.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();

            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }
    //核心 成功生成了List<SortModel>,去生成那一排排列表状的东西


    @Override
    public void initController() {


    }

    @Override
    public void onLoadingNetworkData() {

        sourceDateList = filledData(plantname);
    }
    public void updateindex(){
        // 填充数据
        Collections.sort(sourceDateList, pinyinComparator);
        //adapter = new SortAdapter(getActivity(), SourceDateList);
        adapter=new SortAdapter(getActivity(), sourceDateList);
        adapter.updateListView(sourceDateList);
        sortListView.setAdapter(adapter);
        sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
//                List<SortModel> list=adapter.getlist();
//                list.get(firstVisibleItem).seticonBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pwd2));
//                adapter.updateListView(list);
                int section = adapter.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = adapter
                        .getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) xuanfuLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    xuanfuLayout.setLayoutParams(params);
                    xuanfaText.setText(String.valueOf((char) section));
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = xuanfuLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) xuanfuLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            xuanfuLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                xuanfuLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //点击事件，以后会实现跳转
                Toast.makeText(getContext(),
                        ((SortModel) adapter.getItem(position)).getName(),
                        Toast.LENGTH_SHORT).show();
            }

        });
        /**
         * 为右边添加触摸事件
         */
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });
    }
    @Override
    public void onLoadedNetworkData(View view) {

        updateindex();
    }

    @Override
    public int getContentLayoutID() {
        return R.layout.fragment_book;
    }
}

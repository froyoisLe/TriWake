package site.isleti.triwake.ui.Intros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.adapter.IntrosListAdapter;
import site.isleti.triwake.db.IntrosDBHelper;
import site.isleti.triwake.event.StartUpdateIntrosEvent;
import site.isleti.triwake.model.IntrosBean;
import site.isleti.triwake.ui.diary.DiaryActivity;
import site.isleti.triwake.util.DateUtil;

public class IntrosFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    private static final String TAG = "IntrosFragment";

    private ImageButton mDiaryBtn;
    private ListView mIntrospectListView;
    private FloatingActionButton mAddIntsButton;
    private IntrosListAdapter mIntrosListAdapter;
    private IntrosDBHelper mIntrosDBHelper;
    private LinkedList<IntrosBean> introspects = new LinkedList<>();
    private Context mContext;
    private String date = "";
    private TextView dayText;
    public View mIntsView;
    private MaterialSearchBar searchBar;

    public IntrosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //*** 初始化View和Context,注册EventBus ***@author：IsLeti***/
        mIntsView = inflater.inflate(R.layout.intro_fg, container, false);
        mContext = this.getActivity();

        searchBar = mIntsView.findViewById(R.id.intro_searchBar);
        searchBar.hideSuggestionsList();
        searchBar.setMaxSuggestionCount(0);
        searchBar.setOnSearchActionListener(this);


        //init ListView
        mIntrospectListView = mIntsView.findViewById(R.id.ints_list_view);
        initListView();
        //click -> skip to DiaryPage
        mDiaryBtn = mIntsView.findViewById(R.id.diaryBtn);
        skipToDiary();
        //click -> skip to AddIntrosPage
        mAddIntsButton = mIntsView.findViewById(R.id.add_ints);
        skipToAddIntros();
        //Date Show : May 10
        dayText = mIntsView.findViewById(R.id.intros_day_text);
        showDate();
        //Update Data
        update();
        return mIntsView;
    }

    /*** 初始化列表 ***@author：IsLeti***/
    private void initListView() {
        mIntrosListAdapter = new IntrosListAdapter(mContext,introspects);
        mIntrosDBHelper = new IntrosDBHelper(mContext, "Introspect.db", null, 1);
        introspects = mIntrosDBHelper.getIntrosList();
        mIntrosListAdapter.setInts(introspects);
        mIntrospectListView.setAdapter(mIntrosListAdapter);
        mIntrosListAdapter.notifyDataSetChanged();
    }
    /*** 设置日期显示 ***@author：IsLeti***/
    private void showDate() {
        date = DateUtil.getFormattedDate();
        String day = DateUtil.getDateTitle(date);
        String weekDay = DateUtil.getWeekDay(date);
        dayText.setText( ""+ day + "      " + weekDay );
    }
    /*** 点击按钮跳转日记界面 ***@author：IsLeti***/
    private void skipToDiary(){
        mDiaryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DiaryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
    /*** 新增反思记录 ***@author：IsLeti***/
    private void skipToAddIntros(){
        mAddIntsButton.setOnClickListener(v -> AddIntrosActivity.startActivity(mContext));
        update();
    }
    /***新增条目返回后更新ListView ***@author：IsLeti***/
    public void update(){
        introspects = mIntrosDBHelper.getIntrosList();//通过数据库列表将显示内容
        if (mIntrosListAdapter == null){ //对Adapter先进行判空
            mIntrosListAdapter = new IntrosListAdapter(mContext,introspects);
        }
        mIntrosListAdapter.setInts(introspects);//将数据库中记录传给Adapter
        mIntrosListAdapter.notifyDataSetChanged();//通知Adapter更新内容
        mIntrospectListView.setAdapter(mIntrosListAdapter);//为列表视图设置适配器
    }

    /*** 利用EventBus进行数据更新传递 ***@author：IsLeti***/
    @Subscribe
    public void startUpdateIntroActivity(StartUpdateIntrosEvent event) {
        //获得选中预编辑条目的uuid，文本内容，创建日期，创建时间及用以排序的tag
        String uuid = introspects.get(event.getPosition()).getUuid();
        String content = introspects.get(event.getPosition()).getContent();
        String date = introspects.get(event.getPosition()).getDate();
        String time = introspects.get(event.getPosition()).getTime();
        long tag = introspects.get(event.getPosition()).getTag();
        //将参数传递给编辑活动的启动方法并启动编辑活动
        UpdateIntrosActivity.startActivity(getActivity(), content, uuid, date, time, tag);
    }

    //注册EventBus ***@author：IsLeti***/
    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }

    /*** 注销EventBus ***@author：IsLeti***/
    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
            mIntrosListAdapter.setInts(introspects);
            mIntrosListAdapter.notifyDataSetChanged();
            Log.d(TAG, "onSearchStateChanged: 搜索结束");
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String keyword = searchBar.getText();
        Log.d(TAG, "onSearchConfirmed: 关键词" + keyword);
        LinkedList<IntrosBean> searchResults = new LinkedList<>();
        introspects = mIntrosDBHelper.getIntrosList();
        int length = introspects.size();
        for (int i = 0; i< length; i++){
            if (introspects.get(i).getContent().contains(keyword) ||
                    introspects.get(i).getDate().contains(keyword)){
                searchResults.add(introspects.get(i));
                Log.d(TAG, "onSearchConfirmed: 搜索结果" + introspects.get(i).getContent());
            }
            }
        if (searchResults.isEmpty())
        {
            Toast.makeText(getActivity(),"不存在的呢",Toast.LENGTH_SHORT).show();
        }
            mIntrosListAdapter.setInts(searchResults);
            mIntrosListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onButtonClicked(int buttonCode) {
    }
}



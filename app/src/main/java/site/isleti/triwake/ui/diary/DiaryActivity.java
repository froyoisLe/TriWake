package site.isleti.triwake.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import butterknife.ButterKnife;
import site.isleti.triwake.R;
import site.isleti.triwake.adapter.DiaryAdapter;
import site.isleti.triwake.db.DiaryDBHelper;
import site.isleti.triwake.event.StartUpdateDiaryEvent;
import site.isleti.triwake.model.DiaryBean;
import site.isleti.triwake.ui.activity.MainActivity;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.util.GetDate;
import site.isleti.triwake.util.SpHelper;

public class DiaryActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private List<DiaryBean> mDiaryBeanList;
    private DiaryDBHelper mHelper;

    private static final String TAG = "DiaryActivity";

    /*** 标识今天是否已经写了日记 ***@author：IsLeti***/
    private boolean isWrite = false;
    private static TextView mTvTest;

    private TextView mDiaryDate;
    private TextView mDiaryContent;
    private RecyclerView mMainRvShowDiary;
    private FloatingActionButton mDiaryFabEnterEdit;
    private LinearLayout mItemFirst;
    private LinearLayout mMainLlMain;
    private String today = "今天";
    private ImageView deleteDiary;
    private static String IS_WRITE = "true";
    private int mEditPosition = -1;
    private MaterialSearchBar searchBar;
    private DiaryAdapter mDiaryAdapter;

    /*** 启动界面 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DiaryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_activity);



        mDiaryDate = findViewById(R.id.diary_tv_date);
        mDiaryContent = findViewById(R.id.diary_tv_content);
        mMainRvShowDiary = findViewById(R.id.main_rv_show_diary);
        mDiaryFabEnterEdit = findViewById(R.id.diary_fab_enter_edit);

        mItemFirst = findViewById(R.id.item_first);
        mMainLlMain = findViewById(R.id.main_ll_main);

        searchBar = findViewById(R.id.diary_searchBar);
        searchBar.hideSuggestionsList();
        searchBar.setMaxSuggestionCount(0);
        searchBar.setOnSearchActionListener(this);
        
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        mHelper = new DiaryDBHelper(this, "Diary.db", null, 1);
        SpHelper spHelper = SpHelper.getInstance(this);
        getDiaryBeanList();
        initTitle();
        mMainRvShowDiary.setLayoutManager(new LinearLayoutManager(this));
        mDiaryAdapter = new DiaryAdapter(this, mDiaryBeanList);
        mMainRvShowDiary.setAdapter(mDiaryAdapter);
        //跳转到写日记界面
        mDiaryFabEnterEdit.setOnClickListener(v -> AddDiaryActivity.startActivity(DiaryActivity.this));
}
    /*** 初始化日期 ***@author：IsLeti***/
    private void initTitle() {
        mDiaryDate.setText(GetDate.getDate());
    }
    /*** 获取数据库中的日记记录 ***@author：IsLeti***/
    private List<DiaryBean> getDiaryBeanList() {

        mDiaryBeanList = new ArrayList<>();
        List<DiaryBean> diaryList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = mHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("Diary", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String dateSystem = GetDate.getDate().toString();
                if (date.equals(dateSystem)) {
                    mMainLlMain.removeView(mItemFirst);//判断日期
                    break;
                }
            } while (cursor.moveToNext());
        }
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String tag = cursor.getString(cursor.getColumnIndex("tag"));
                mDiaryBeanList.add(new DiaryBean(date, title, content, tag));
            } while (cursor.moveToNext());
        }
        cursor.close();
        for (int i = mDiaryBeanList.size() - 1; i >= 0; i--) {
            diaryList.add(mDiaryBeanList.get(i));
        }
        mDiaryBeanList = diaryList;
        return mDiaryBeanList;
    }
    /*** 利用EventBus实现编辑日记 ***@author：IsLeti***/
    @Subscribe
    public void startUpdateDiaryActivity(StartUpdateDiaryEvent event) {
        String title = mDiaryBeanList.get(event.getPosition()).getTitle();
        String content = mDiaryBeanList.get(event.getPosition()).getContent();
        String tag = mDiaryBeanList.get(event.getPosition()).getTag();
        UpdateDiaryActivity.startActivity(this, title, content, tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//注销EventBus
    }
    /*** Home 回退 Main ***@author：IsLeti***/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.startActivity(DiaryActivity.this);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
           mDiaryAdapter.setDiary(mDiaryBeanList);
           mDiaryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String keyword = searchBar.getText();
        Log.d(TAG, "onSearchConfirmed: 关键词" + keyword);
       List<DiaryBean> searchResults = new LinkedList<>();
        getDiaryBeanList();
        int length = mDiaryBeanList.size();
        for (int i = 0; i< length; i++){
            if (mDiaryBeanList.get(i).getContent().contains(keyword) ||
                    mDiaryBeanList.get(i).getDate().contains(keyword) ||
                    mDiaryBeanList.get(i).getTitle().contains(keyword)
            ){
                searchResults.add(mDiaryBeanList.get(i));
                Log.d(TAG, "onSearchConfirmed: 搜索结果" + mDiaryBeanList.get(i).getContent());
            }
        }
        if (searchResults.isEmpty())
        {
            Toast.makeText(this,"不存在的呢",Toast.LENGTH_SHORT).show();
        }
        mDiaryAdapter.setDiary(searchResults);
        mDiaryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}



package site.isleti.triwake.ui.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import site.isleti.triwake.adapter.NoteListAdapter;
import site.isleti.triwake.db.NoteDBHelper;
import site.isleti.triwake.event.StartUpdateNoteEvent;
import site.isleti.triwake.model.NoteBean;
import site.isleti.triwake.ui.painting.DrawActivity;
import site.isleti.triwake.util.DateUtil;

public class NotesFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    private static final String TAG = "NotesFragment";

    private ListView mNoteListView;
    private FloatingActionButton mAddNoteButton;
    private NoteListAdapter mNoteListAdapter;
    private NoteDBHelper mNoteDBHelper;
    private LinkedList<NoteBean> notes = new LinkedList<>();
    private Context mContext;
    private String date = "";
    private TextView dayText;
    public View mNoteView;
    private MaterialSearchBar searchBar;
    private FloatingActionButton drawBtn;

    public NotesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //*** 初始化View和Context,注册EventBus ***@author：IsLeti***/
        mNoteView = inflater.inflate(R.layout.notes_fg, container, false);
        mContext = this.getActivity();

        searchBar = mNoteView.findViewById(R.id.note_searchBar);
        searchBar.hideSuggestionsList();
        searchBar.setMaxSuggestionCount(0);
        searchBar.setOnSearchActionListener(this);

        drawBtn = mNoteView.findViewById(R.id.drawBtn);
        drawBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DrawActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        //init ListView
        mNoteListView = mNoteView.findViewById(R.id.notes_list_view);
        initListView();
        //click -> skip to AddNotesPage
        mAddNoteButton = mNoteView.findViewById(R.id.add_notes);
        skipToAddNotes();
        //Date Show : May 10
        dayText = mNoteView.findViewById(R.id.notes_date);
        showDate();
        //Update Data
        update();
        return mNoteView;
    }

    /*** 初始化列表 ***@author：IsLeti***/
    private void initListView() {
        mNoteListAdapter = new NoteListAdapter(mContext, notes);
        mNoteDBHelper = new NoteDBHelper(getActivity(), "Notes.db", null, 1);
        notes = mNoteDBHelper.getNotesList();
        mNoteListAdapter.setNotes(notes);
        mNoteListView.setAdapter(mNoteListAdapter);
        mNoteListAdapter.notifyDataSetChanged();
    }

    /*** 设置日期显示 ***@author：IsLeti***/
    private void showDate() {
        date = DateUtil.getFormattedDate();
        String day = DateUtil.getDateTitle(date);
        String weekDay = DateUtil.getWeekDay(date);
        dayText.setText( ""+ day + "      " + weekDay );
    }

    /*** 新增Note ***@author：IsLeti***/
    private void skipToAddNotes() {
        mAddNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddNotesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 3);
            update();
        });
        update();
    }

    /***新增条目返回后更新ListView ***@author：IsLeti***/
    public void update() {
        notes = mNoteDBHelper.getNotesList();
        if (mNoteListAdapter == null) {
            mNoteListAdapter = new NoteListAdapter(mContext, notes);
        }
        mNoteListAdapter.setNotes(notes);
        mNoteListAdapter.notifyDataSetChanged();
        mNoteListView.setAdapter(mNoteListAdapter);
    }

    /*** 利用EventBus进行数据更新传递 ***@author：IsLeti***/
    @Subscribe
    public void startUpdateNoteActivity(StartUpdateNoteEvent event) {
        String uuid = notes.get(event.getPosition()).getUuid();
        String content = notes.get(event.getPosition()).getContent();
        String date = notes.get(event.getPosition()).getDate();
        String time = notes.get(event.getPosition()).getTime();
        long tag = notes.get(event.getPosition()).getTag();
        Intent intent = new Intent(getActivity(), UpdateNoteActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("uuid", uuid);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("tag", tag);
        startActivityForResult(intent, 4);
    }

    //注册EventBus ***@author：IsLeti***/
    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {//加上判断
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
        if (requestCode == 3 | requestCode == 4) {
            update();
        }
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
            mNoteListAdapter.setNotes(notes);
            mNoteListAdapter.notifyDataSetChanged();
            Log.d(TAG, "onSearchStateChanged: 搜索结束");
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        String keyword = searchBar.getText();
        Log.d(TAG, "onSearchConfirmed: 关键词" + keyword);
        LinkedList<NoteBean> searchResults = new LinkedList<>();
        notes = mNoteDBHelper.getNotesList();
        int length = notes.size();
        for (int i = 0; i< length; i++){
            if (notes.get(i).getContent().contains(keyword) ||
                    notes.get(i).getDate().contains(keyword)){
                searchResults.add(notes.get(i));
                Log.d(TAG, "onSearchConfirmed: 搜索结果" + notes.get(i).getContent());
            }
        }
        if (searchResults.isEmpty())
        {
            Toast.makeText(getActivity(),"不存在的呢",Toast.LENGTH_SHORT).show();
        }
        mNoteListAdapter.setNotes(searchResults);
        mNoteListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}

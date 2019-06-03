package site.isleti.triwake.ui.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import site.isleti.triwake.adapter.TodoListAdapter;
import site.isleti.triwake.db.TodoDBHelper;
import site.isleti.triwake.event.StartUpdateTodoEvent;
import site.isleti.triwake.model.TodoBean;
import site.isleti.triwake.util.DateUtil;

public class ToDoFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    private static final String TAG = "ToDoFragment";

    private ListView mTodoListView;
    private FloatingActionButton mAddTodoButton;
    private TodoListAdapter mTodoListAdapter;
    private TodoDBHelper mTodoDBHelper;
    private LinkedList<TodoBean> todos = new LinkedList<>();
    private Context mContext;
    private String date = "";
    private TextView dayText;
    public View mTodoView;
    private MaterialSearchBar searchBar;
    private ImageView done;
    private ImageView undone;
    private ImageView all;


    public ToDoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //*** 初始化View和Context,注册EventBus ***@author：IsLeti***/
        mTodoView = inflater.inflate(R.layout.todo_fg, container, false);
        mContext = this.getActivity();

        searchBar = mTodoView.findViewById(R.id.todo_searchBar);
        searchBar.hideSuggestionsList();
        searchBar.setMaxSuggestionCount(0);
        searchBar.setOnSearchActionListener(this);

        done = mTodoView.findViewById(R.id.done_btn);
        undone = mTodoView.findViewById(R.id.undone_btn);
        all = mTodoView.findViewById(R.id.all_btn);
        done.setOnClickListener(v -> getDone());
        undone.setOnClickListener(v -> getUndone());
        all.setOnClickListener(v -> getAll());

        //init ListView
        mTodoListView = mTodoView.findViewById(R.id.todo_list_view);
        initListView();

        //click -> skip to AddToDosPage
        mAddTodoButton = mTodoView.findViewById(R.id.add_todo);
        skipToAddTodos();
        //Date Show : May 10
        dayText = mTodoView.findViewById(R.id.todo_date);
        showDate();
        //Update Data
        update();
        return mTodoView;
    }

    /*** 初始化列表 ***@author：IsLeti***/
    private void initListView() {
        mTodoListAdapter = new TodoListAdapter(mContext,todos);
        mTodoDBHelper = new TodoDBHelper(getActivity(), "Todo.db", null, 1);
        todos = mTodoDBHelper.getTodoList();
        mTodoListAdapter.setTodos(todos);
        mTodoListView.setAdapter(mTodoListAdapter);
        mTodoListAdapter.notifyDataSetChanged();
    }
    /*** 设置日期显示 ***@author：IsLeti***/
    private void showDate() {
        date = DateUtil.getFormattedDate();
        String day = DateUtil.getDateTitle(date);
        String weekDay = DateUtil.getWeekDay(date);
        dayText.setText( ""+ day + "      " + weekDay );
    }

    /*** 新增Todo ***@author：IsLeti***/
    private void skipToAddTodos(){
       mAddTodoButton.setOnClickListener(v -> {
           Intent intent = new Intent(getActivity(),AddTodosActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           startActivityForResult(intent,6);//必须用forResult执行刷新
           update();
       });
        update();
    }
    /***新增条目返回后更新ListView ***@author：IsLeti***/
    public void update(){
        todos = mTodoDBHelper.getTodoList();
        if (mTodoListAdapter == null){
            mTodoListAdapter = new TodoListAdapter(mContext,todos);
        }
        mTodoListAdapter.setTodos(todos);
        mTodoListAdapter.notifyDataSetChanged();
        mTodoListView.setAdapter(mTodoListAdapter);
    }

    /*** 利用EventBus进行数据更新传递 ***@author：IsLeti***/
    @Subscribe
    public void startUpdateTodoActivity(StartUpdateTodoEvent event) {
        String uuid = todos.get(event.getPosition()).getUuid();
        String content = todos.get(event.getPosition()).getContent();
        String date = todos.get(event.getPosition()).getDate();
        String time = todos.get(event.getPosition()).getTime();
        long tag = todos.get(event.getPosition()).getTag();
        int state = todos.get(event.getPosition()).getState();
        Intent intent = new Intent(getActivity(),UpdateTodosActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("uuid", uuid);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("tag",tag);
        intent.putExtra("state",state);
        startActivityForResult(intent,7);
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
        super.onActivityResult(7, resultCode, data);
        update();
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled){
            mTodoListAdapter.setTodos(todos);
            mTodoListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

        String keyword = searchBar.getText();
        Log.d(TAG, "onSearchConfirmed: 关键词" + keyword);
        LinkedList<TodoBean> searchResults = new LinkedList<>();
        todos = mTodoDBHelper.getTodoList();
        int length = todos.size();
        for (int i = 0; i< length; i++){
            if (todos.get(i).getContent().contains(keyword) ||
                    todos.get(i).getDate().contains(keyword)){
                searchResults.add(todos.get(i));
                Log.d(TAG, "onSearchConfirmed: 搜索结果" + todos.get(i).getContent());
            }
        }

        if (searchResults.isEmpty())
        {
            Toast.makeText(getActivity(),"不存在的呢",Toast.LENGTH_SHORT).show();
        }
        mTodoListAdapter.setTodos(searchResults);
        mTodoListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    private void getDone(){
        LinkedList<TodoBean> doneResults = new LinkedList<>();
        todos = mTodoDBHelper.getTodoList();
        int length = todos.size();
        for (int i = 0; i< length; i++){
            if (todos.get(i).getState() % 2 == 1){
                doneResults.add(todos.get(i));
                Log.d(TAG, "onSearchConfirmed: 完成了的" + todos.get(i).getContent());
            }
        }
        mTodoListAdapter.setTodos(doneResults);
        mTodoListAdapter.notifyDataSetChanged();
    }

    private void getUndone(){
        LinkedList<TodoBean> undoneResults = new LinkedList<>();
        todos = mTodoDBHelper.getTodoList();
        int length = todos.size();
        for (int i = 0; i< length; i++){
            if (todos.get(i).getState() % 2 == 0){
                undoneResults.add(todos.get(i));
                Log.d(TAG, "onSearchConfirmed: 没完成的" + todos.get(i).getContent());
            }
        }
        mTodoListAdapter.setTodos(undoneResults);
        mTodoListAdapter.notifyDataSetChanged();
    }

    private void getAll(){
        todos = mTodoDBHelper.getTodoList();
        mTodoListAdapter.setTodos(todos);
        mTodoListAdapter.notifyDataSetChanged();
    }

}



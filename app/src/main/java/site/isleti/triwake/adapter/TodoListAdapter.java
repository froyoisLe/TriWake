package site.isleti.triwake.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import site.isleti.triwake.R;
import site.isleti.triwake.db.TodoDBHelper;
import site.isleti.triwake.event.StartUpdateTodoEvent;
import site.isleti.triwake.model.TodoBean;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slideright;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class TodoListAdapter extends BaseAdapter {

    private static final String TAG = "TodoListAdapter";

    private LinkedList<TodoBean> mTodoBeans;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int mEditPosition = -1;
    private TodoDBHelper mHelper;

    /*** 初始化构造器：传入上下文和DataList，获得数据库信息 ***@author：IsLeti***/
    public TodoListAdapter(Context context, LinkedList<TodoBean> todos) {
        this.mContext = context;
        this.mTodoBeans = todos;
        this.mLayoutInflater = LayoutInflater.from(context);
        mHelper = new TodoDBHelper(mContext, "Todo.db", null, 1);
    }

    public void setTodos(LinkedList<TodoBean> todos) {
        this.mTodoBeans = todos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTodoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mTodoBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TodoViewHolder todoViewHolder;

        //*** HashMap<> 获取View 防止布局复用产生混乱***@author：IsLeti***/
        HashMap<Integer, View> tdmp = new HashMap<>();
        Map<Integer, Boolean> map = new HashMap<>();
        if (tdmp.get(position) == null) {
            convertView = mLayoutInflater.inflate(R.layout.todo_item_view, null);
            todoViewHolder = new TodoViewHolder(convertView, (TodoBean) getItem(position));
            convertView.setTag(todoViewHolder);
            tdmp.put(position, convertView);
        } else {
            convertView = tdmp.get(position);
            todoViewHolder = (TodoViewHolder) convertView.getTag();
        }

        //*** 按钮预先隐藏 ***@author：IsLeti***/
        todoViewHolder.mIvEdit.setVisibility(View.INVISIBLE);
        todoViewHolder.mIvDelete.setVisibility(View.INVISIBLE);
        //*** 移至指定区域可见 ***@author：IsLeti***/
        if (mEditPosition == position) {
            todoViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            todoViewHolder.mIvDelete.setVisibility(View.VISIBLE);
        } else {
            todoViewHolder.mIvEdit.setVisibility(View.GONE);
            todoViewHolder.mIvDelete.setVisibility(View.GONE);
        }
        ;

        //*** 根据State初始化显示 ***@author：IsLeti***/
        //state奇数 表示Done
        if (mTodoBeans.get(position).getState() % 2 == 1) {
            todoViewHolder.mCheckDone.setVisibility(View.VISIBLE);
            todoViewHolder.mCheckUndone.setVisibility(View.GONE);
            todoViewHolder.mTodoDate.setTextColor(mContext.getResources().getColor(R.color.todo_darker));//date red
            todoViewHolder.mTodoContent.setTextColor(mContext.getResources().getColor(R.color.todo_state_grey));//content grey
            todoViewHolder.mTodoContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
            todoViewHolder.mTodoLine.setBackground(mContext.getResources().getDrawable(R.drawable.todo_done_style));//右栏变灰
        }
        //state偶数 表示Undone
        else if (mTodoBeans.get(position).getState() % 2 == 0) {
            todoViewHolder.mCheckDone.setVisibility(View.GONE);
            todoViewHolder.mCheckUndone.setVisibility(View.VISIBLE);
            todoViewHolder.mTodoDate.setTextColor(mContext.getResources().getColor(R.color.todo_state_grey));//date grey
            todoViewHolder.mTodoContent.setTextColor(mContext.getResources().getColor(R.color.todo_darker));//content red
            todoViewHolder.mTodoContent.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG); // 取消中划线并加清晰
            todoViewHolder.mTodoLine.setBackground(mContext.getResources().getDrawable(R.drawable.todo_linear_style));//右栏变红
        }

        //触摸区域时图标变换 若可见则点击后消失，若不可见，则点击后可见
        todoViewHolder.mLayout.setOnClickListener(v -> {
            if (todoViewHolder.mIvEdit.getVisibility() == View.VISIBLE) {
                todoViewHolder.mIvEdit.setVisibility(View.GONE);
            } else {
                todoViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            }
            if (todoViewHolder.mIvDelete.getVisibility() == View.VISIBLE) {
                todoViewHolder.mIvDelete.setVisibility(View.GONE);
            } else {
                todoViewHolder.mIvDelete.setVisibility(View.VISIBLE);
            }
            //移动EditPosition
            if (mEditPosition != position) {
                mEditPosition = position;
            }
            mEditPosition = position;
        });

        //跳转进行编辑,利用EventBus实现数据传递
        todoViewHolder.mIvEdit.setOnClickListener(v -> {
            EventBus.getDefault().post(new StartUpdateTodoEvent(position));
            Log.d(TAG, "onClick: 进入编辑待办界面");
        });

        //点击删除按钮显示对话框  左取消 右确认 执行相应操作
        todoViewHolder.mIvDelete.setOnClickListener(v -> {
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
            dialogBuilder
                    .withTitle("删除待办")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor(mContext.getResources().getColor(R.color.todo_dark))
                    .withMessage("   您确认要删除吗?")
                    .withMessageColor("#FFFFFF")
                    .withDialogColor(mContext.getResources().getColor(R.color.todo_darker))
                    .withIcon(mContext.getResources().getDrawable(R.drawable.ic_todo_36dp))
                    .isCancelableOnTouchOutside(true)
                    .withDuration(500)
                    .withEffect(Slidetop)
                    .withButton1Text("手滑") //左边按钮
                    .withButton2Text("是的") //右边按钮 Yes
                    .setButton2Click(v1 -> {
                        String uuid = mTodoBeans.get(position).getUuid();
                        mHelper.removeTodo(uuid); //执行数据库删除操作
                        mTodoBeans.remove(position); //执行Adapter适配Beans删除
                        notifyDataSetChanged(); //同步更新界面
                        Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })
                    .setButton1Click(v12 -> {
                        Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })
                    .show();
        });

        //Check点击后 state+1 待办内容显示中划线，右栏边框及内容变灰，日期与Check图标变红

        todoViewHolder.mCheckUndone.setOnClickListener(v -> {
            //Undone点击变Done
            todoViewHolder.mTodoDate.setTextColor(mContext.getResources().getColor(R.color.todo_darker));//date red
            todoViewHolder.mCheckUndone.setVisibility(View.GONE); //undone gone
            todoViewHolder.mCheckDone.setVisibility(View.VISIBLE);//done red show
            todoViewHolder.mTodoContent.setTextColor(mContext.getResources().getColor(R.color.todo_state_grey));//content grey
            todoViewHolder.mTodoContent.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
            todoViewHolder.mTodoLine.setBackground(mContext.getResources().getDrawable(R.drawable.todo_done_style));//右栏变灰

            showDoneDialog();
            int state = mTodoBeans.get(position).getState();
            state ++;
            mTodoBeans.get(position).setState(state);
            notifyDataSetChanged();//刷新Adapter Data
            //date content time state tag
            String uuid = mTodoBeans.get(position).getUuid();
            String date = mTodoBeans.get(position).getDate();
            String content = mTodoBeans.get(position).getContent();
            String time = mTodoBeans.get(position).getTime();
            long tag = mTodoBeans.get(position).getTag();
            TodoBean todoBean = new TodoBean(date, content, time, state, tag);
            mHelper.updateTodo(uuid, todoBean);
        });

        todoViewHolder.mCheckDone.setOnClickListener(v -> {

            //CheckDone点击变Undone
            todoViewHolder.mTodoDate.setTextColor(mContext.getResources().getColor(R.color.todo_state_grey));//date grey
            todoViewHolder.mCheckDone.setVisibility(View.GONE); //done gone
            todoViewHolder.mCheckUndone.setVisibility(View.VISIBLE);  //undone show
            todoViewHolder.mTodoContent.setTextColor(mContext.getResources().getColor(R.color.todo_darker));//content red
            todoViewHolder.mTodoContent.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG); // 取消中划线并加清晰
            todoViewHolder.mTodoLine.setBackground(mContext.getResources().getDrawable(R.drawable.todo_linear_style));//右栏变红

            int state = mTodoBeans.get(position).getState();
            state ++;
            mTodoBeans.get(position).setState(state);
            notifyDataSetChanged();//刷新Adapter Data
            //date content time state tag
            String uuid = mTodoBeans.get(position).getUuid();
            String date = mTodoBeans.get(position).getDate();
            String content = mTodoBeans.get(position).getContent();
            String time = mTodoBeans.get(position).getTime();
            long tag = mTodoBeans.get(position).getTag();
            TodoBean todoBean = new TodoBean(date, content, time, state, tag);
            mHelper.updateTodo(uuid, todoBean);
        });
        return convertView;
    }

    private void showDoneDialog() {
            NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
            dialogBuilder
                    .withTitle("你真棒！")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor(mContext.getResources().getColor(R.color.todo_primary))
                    .withMessage("           恭喜你完成!")
                    .withMessageColor("#FFFFFF")
                    .withDialogColor(mContext.getResources().getColor(R.color.todo_dark))                                                        //def
                    .withIcon(mContext.getResources().getDrawable(R.drawable.ic_thumb_up_red_50_36dp))
                    .isCancelableOnTouchOutside(true)
                    .withDuration(500)
                    .withEffect(Slideright)
                    .show();

        Timer timer = new Timer();//声明一个Timer()类实例timer
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                dialogBuilder.cancel();
            }
        };
        timer.schedule(timerTask, 1500);//任务延迟3s执行
    }

    public static class TodoViewHolder {
        private TextView mTodoDate;
        private TextView mTodoContent;
        private LinearLayout mLayout;
        private ImageView mIvEdit;
        private ImageView mIvDelete;
        private ImageView mCheckDone;
        private ImageView mCheckUndone;
        private LinearLayout mTodoLine;
        private TextView clicn;

        TodoViewHolder(View itemView, TodoBean todoBean) {
            mTodoDate = itemView.findViewById(R.id.todo_date_item);
            mTodoContent = itemView.findViewById(R.id.todo_title);
            mTodoDate.setText(todoBean.getDate());
            mTodoContent.setText("  " + todoBean.getContent());
            mLayout = itemView.findViewById(R.id.todo_item_control);
            mIvEdit = itemView.findViewById(R.id.todo_iv_edit);
            mIvDelete = itemView.findViewById(R.id.todo_iv_delete);
            mCheckDone = itemView.findViewById(R.id.todo_state_done);
            mCheckUndone = itemView.findViewById(R.id.todo_state_undone);
            mTodoLine = itemView.findViewById(R.id.todo_line);
            clicn = itemView.findViewById(R.id.clicn);
            clicn.setText(" " + todoBean.getState());
        }
    }
}


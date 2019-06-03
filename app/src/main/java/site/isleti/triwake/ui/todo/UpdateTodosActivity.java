package site.isleti.triwake.ui.todo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Objects;
import butterknife.ButterKnife;
import site.isleti.triwake.R;
import site.isleti.triwake.db.TodoDBHelper;
import site.isleti.triwake.model.TodoBean;
import site.isleti.triwake.util.AppManager;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class UpdateTodosActivity extends AppCompatActivity {

    private static final String TAG = "更新反思";

    private TodoDBHelper mHelper;
    private MaterialEditText mUpdateTodoEtContent;
    private ImageButton mUpdateTodoFabBack;
    private ImageButton mUpdateTodoFabAdd;
    private ImageButton mUpdateTodoFabDelete;
    private ImageView mUTIvBack;
    private TextView mTvUuid;
    private String date = " ";
    private String time = " ";
    private long tag = 0;
    private int state = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_update_activity);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mHelper = new TodoDBHelper(this, "Todo.db", null, 1);

        //从TodoFragment接收数据并显示内容
        mUpdateTodoEtContent = findViewById(R.id.todo_update_content);
        mTvUuid = findViewById(R.id.todo_uuid_up);
        Intent intent = getIntent();
        mUpdateTodoEtContent.setText(intent.getStringExtra("content"));
        mTvUuid.setText(intent.getStringExtra("uuid"));
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        tag = intent.getLongExtra("tag",tag);
        state = intent.getIntExtra("state",state);

        //底部返回退出
        mUpdateTodoFabBack = findViewById(R.id.update_todo_fab_back);
        mUpdateTodoFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackDialog();
            }
        });

        //修改后保存
        mUpdateTodoFabAdd = findViewById(R.id.update_todo_fab_add);
        mUpdateTodoFabAdd.setOnClickListener(v -> saveUpdateTodo());

        //删除显示对话框
        mUpdateTodoFabDelete = findViewById(R.id.update_todo_fab_delete);
        mUpdateTodoFabDelete.setOnClickListener(this::showDeleteDialog);

        //顶部直接退出
        mUTIvBack = findViewById(R.id.update_todo_iv_back);
        mUTIvBack.setOnClickListener(v -> finish());

    }

     //删除时显示对话框
    public void showDeleteDialog(View v) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("删除待办")
                .withTitleColor("#FFFFFF")
                .withDividerColor(getResources().getColor(R.color.todo_primary))
                .withMessage("         您确认要删除吗?")
                .withMessageColor("#FFFFFF")
                .withDialogColor(getResources().getColor(R.color.todo_dark))
                .withIcon(getResources().getDrawable(R.drawable.ic_todo_36dp))
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .withEffect(RotateBottom)
                .withButton1Text("是的")
                .withButton2Text("手滑")
                .setButton1Click(v1 -> {
                    String uuid = mTvUuid.getText().toString();
                    mHelper.removeTodo(uuid);
                    Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setButton2Click(v12 -> Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show())
                .show();
    }

     //返回时显示对话框
    public void showBackDialog( ) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("返回主页")
                .withTitleColor("#FFFFFF")
                .withDividerColor(getResources().getColor(R.color.todo_primary))
                .withMessage("需要保存再返回吗?")
                .withMessageColor("#FFFFFF")
                .withDialogColor(getResources().getColor(R.color.todo_dark))                                                        //def
                .withIcon(getResources().getDrawable(R.drawable.ic_todo_36dp))
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .withEffect(RotateBottom)
                .withButton2Text("是的")
                .withButton1Text("不用")
                .setButton2Click(v -> saveUpdateTodo())   //保存再返回
                .setButton1Click(v -> finish())   //不用直接返回
                .show();
    }

    //Home 返回
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //保存更新后的内容，只更新内容，date,time,tag,state不变,利用uuid进行删除
    public void saveUpdateTodo() {
        //获取之前记录的uuid与更新后的内容，其余不变，修改后不改变位置排序
        String updateContent = Objects.requireNonNull(mUpdateTodoEtContent.getText()).toString();
        String uuid = mTvUuid.getText().toString();
        if (!updateContent.isEmpty()) {
            TodoBean todoBean = new TodoBean(date,updateContent,time,state,tag);
            mHelper.updateTodo(uuid,todoBean);
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
            Log.d(TAG, updateContent + "onClick: 修改成功");
            finish(); //结束Activity
        }
    }

}
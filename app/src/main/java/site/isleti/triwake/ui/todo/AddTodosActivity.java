package site.isleti.triwake.ui.todo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.UUID;
import site.isleti.triwake.R;
import site.isleti.triwake.db.TodoDBHelper;
import site.isleti.triwake.model.TodoBean;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GetDate;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class AddTodosActivity extends AppCompatActivity {

    private static final String TAG = "添加待办";

    private MaterialEditText mEditText;
    private TodoDBHelper mDbHelper;
    private Context mContext;
    private String content;


    /*** 启动该页面 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddTodosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
    public static void startActivity(Context context,String content) {
        Intent intent = new Intent(context, AddTodosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.todos_activity_add);
        mEditText = findViewById(R.id.todo_content);
        ImageButton saveButton = findViewById(R.id.todo_save);
        ImageButton cancelButton = findViewById(R.id.todo_cancel);
        ImageView todoBack = findViewById(R.id.todo_iv_back);
        AppManager.getAppManager().addActivity(this);
        mDbHelper = new TodoDBHelper(this,"Todo.db",null,1);
        saveButton.setOnClickListener(v -> saveContent());
        cancelButton.setOnClickListener(v -> {
            String content = mEditText.getText().toString();//获得当前编辑框的内容
            if (!content.isEmpty()){
                showBackDialog();
            }else
                finish();
        });
        todoBack.setOnClickListener(v -> backHome());
    }

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
                .setButton2Click(v -> saveContent())   //保存再返回
                .setButton1Click(v -> finish())   //不用直接返回
                .show();
    }


    //***返回主页 ***@author：IsLeti***/
    private void backHome() {
        finish();
    }
    //*** 保存新建条目 ***@author：IsLeti***/
    private void saveContent() {
        String content = mEditText.getText().toString() + "";
        if (!content.isEmpty()) {
            String date = GetDate.getDate().toString();
            String time = DateUtil.getFormattedTime(System.currentTimeMillis());
            TodoBean todoBean = new TodoBean(null,null,null,0,0);
            todoBean.setContent(content);
            todoBean.setDate(date);
            todoBean.setTime(time);
            todoBean.setState(0);
            todoBean.setTag(System.currentTimeMillis());
            todoBean.setUuid(UUID.randomUUID().toString());
            mDbHelper.addTodo(todoBean);
            Log.d(TAG, "onClick: 待办添加成功");
            Log.d(TAG, "onClick: new 待办添加成功");
            finish();
        }
        else {
            backHome();
        }
    }
    //Home 返回
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}


package site.isleti.triwake.ui.Intros;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.UUID;
import site.isleti.triwake.R;
import site.isleti.triwake.db.IntrosDBHelper;
import site.isleti.triwake.model.IntrosBean;
import site.isleti.triwake.ui.activity.MainActivity;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GetDate;
import site.isleti.triwake.widget.LinedEditText;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class AddIntrosActivity extends AppCompatActivity {

    private static final String TAG = "AddIntrosActivity";

    private LinedEditText mEditText;
    private IntrosDBHelper mDbHelper;
    private Context mContext;
    private String content;


    /*** 启动该页面 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddIntrosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
    public static void startActivity(Context context,String content) {
        Intent intent = new Intent(context, AddIntrosActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_add_activity);
        mEditText = findViewById(R.id.intrsp_add_content);
        ImageButton saveButton = findViewById(R.id.ints_save);
        ImageButton cancelButton = findViewById(R.id.ints_cancel);
        ImageView introBack = findViewById(R.id.intro_iv_back);
        AppManager.getAppManager().addActivity(this);
        mDbHelper = new IntrosDBHelper(this,"Introspect.db",null,1);
        saveButton.setOnClickListener(v -> saveContent());
        cancelButton.setOnClickListener(v -> {
            String content = mEditText.getText().toString();//获得当前编辑框的内容
            if (!content.isEmpty()){
                showBackDialog();
            }else
                finish();
        });
        introBack.setOnClickListener(v -> backHome());
    }

    public void showBackDialog( ) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("返回主页")
                .withTitleColor("#FFFFFF")
                .withDividerColor(getResources().getColor(R.color.intros_primary))
                .withMessage("需要保存再返回吗?")
                .withMessageColor("#FFFFFF")
                .withDialogColor(getResources().getColor(R.color.intros_dark))                                                        //def
                .withIcon(getResources().getDrawable(R.drawable.ic_intros))
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .withEffect(RotateBottom)
                .withButton2Text("是的")
                .withButton1Text("不用")
                .setButton2Click(v -> saveContent())   //保存再返回
                .setButton1Click(v -> {
                    MainActivity.startActivity(AddIntrosActivity.this);
                    finish();
                })   //不用直接返回
                .show();
    }

    //*** 取消返回主页 ***@author：IsLeti***/
    private void backHome() {
        MainActivity.startActivity(AddIntrosActivity.this);
        Log.d(TAG, "onClick: 取消成功");
        finish();
    }
    //*** 保存新建条目 ***@author：IsLeti***/
    private void saveContent() {
        String content = mEditText.getText().toString();//获得当前编辑框的内容
        if (!content.isEmpty()) {//对内容先进行判空
            String date = GetDate.getDate().toString();//获得当前日期
            String time = DateUtil.getFormattedTime(System.currentTimeMillis());//获得当前时间
            IntrosBean introsBean = new IntrosBean(date,content,time);
            introsBean.setTag(System.currentTimeMillis());//设置记录tag用以排序
            introsBean.setUuid(UUID.randomUUID().toString());//设置记录UUID
            mDbHelper.addIntro(introsBean);//将此记录添加至数据库
            MainActivity.startActivity(AddIntrosActivity.this);
            finish();//结束该添加记录页面的活动，返回模块主界面
        }
        else {
            backHome();
            finish();//若内容为空，直接退出该界面
        }
    }
    //Home 返回
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.startActivity(this);
        finish();
    }

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);

        //here we can use getIntent() to get the extra data.

    }
}


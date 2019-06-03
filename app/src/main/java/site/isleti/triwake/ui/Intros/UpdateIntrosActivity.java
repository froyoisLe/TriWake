package site.isleti.triwake.ui.Intros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import butterknife.ButterKnife;
import site.isleti.triwake.R;
import site.isleti.triwake.db.IntrosDBHelper;
import site.isleti.triwake.model.IntrosBean;
import site.isleti.triwake.ui.activity.MainActivity;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.widget.LinedEditText;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class UpdateIntrosActivity extends AppCompatActivity {

    private static final String TAG = "更新反思";

    private IntrosDBHelper mHelper;
    private LinedEditText mUpdateIntrosEtContent;
    private ImageButton mUpdateIntrosFabBack;
    private ImageButton mUpdateIntrosFabAdd;
    private ImageButton mUpdateIntrosFabDelete;
    private ImageView mUIIvBack;
    private TextView mTvUuid;
    private String date = " ";
    private String time = " ";
    private long tag = 0;

    public static void startActivity(Context context, String content, String uuid, String date, String time, long tag) {
        Intent intent = new Intent(context, UpdateIntrosActivity.class);
        intent.putExtra("content", content);
        intent.putExtra("uuid", uuid);
        intent.putExtra("date", date);
        intent.putExtra("time", time);
        intent.putExtra("tag",tag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intros_update_activity);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mHelper = new IntrosDBHelper(this, "Introspect.db", null, 1);
        mTvUuid = findViewById(R.id.intro_uuid);

        //从IntrosFragment接收数据并设置显示
        mUpdateIntrosEtContent = findViewById(R.id.intro_update_content);
        Intent intent = getIntent();
        mUpdateIntrosEtContent.setText(intent.getStringExtra("content"));
        mTvUuid.setText(intent.getStringExtra("uuid"));
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        tag = intent.getLongExtra("tag",tag);

        //底部返回退出
        mUpdateIntrosFabBack = findViewById(R.id.update_intro_fab_back);
        mUpdateIntrosFabBack.setOnClickListener(v -> showBackDialog());

        //修改后保存
        mUpdateIntrosFabAdd = findViewById(R.id.update_intro_fab_add);
        mUpdateIntrosFabAdd.setOnClickListener(v -> saveUpdateIntro());

        //删除显示对话框
        mUpdateIntrosFabDelete = findViewById(R.id.update_intro_fab_delete);
        mUpdateIntrosFabDelete.setOnClickListener(v -> showDeleteDialog(v));

        //顶部直接退出
        mUIIvBack = findViewById(R.id.update_intro_iv_back);
        mUIIvBack.setOnClickListener(v -> finish());

    }

     //删除时显示对话框
    public void showDeleteDialog(View v) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("删除反思")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor(getResources().getColor(R.color.intros_primary))                              //def
                .withMessage("   您确认要删除吗?")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(getResources().getColor(R.color.intros_dark))                               //def  | withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.ic_intros))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(500)                                          //def
                .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                .withButton1Text("是的")                                      //def gone
                .withButton2Text("手滑")                                  //def gone
                .setButton1Click(v1 -> {
                    String uuid = mTvUuid.getText().toString();
                    mHelper.removeIntro(uuid);
                    MainActivity.startActivity(UpdateIntrosActivity.this);
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
                .setButton2Click(v -> saveUpdateIntro())   //保存再返回
                .setButton1Click(v -> {
                    MainActivity.startActivity(UpdateIntrosActivity.this);
                    finish();
                })   //不用直接返回
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.startActivity(this);
        finish();
    }

    /***保存更新后的内容 只更新内容 ***@author：IsLeti***/
    public void saveUpdateIntro() {
        String updateContent = mUpdateIntrosEtContent.getText().toString(); //获取编辑后的内容
        String uuid = mTvUuid.getText().toString();//获取编辑条目的uuid
        //获取之前记录的日期和时间，修改后不改变位置排序
        String updateDate = date;
        String updateTime = time;
        if (!updateContent.isEmpty()) {//对内容预先判空
            //将原始记录的日期时间及编辑后的内容传给一个新记录
            IntrosBean introsBean = new IntrosBean(updateDate,updateContent,updateTime);
            introsBean.setTag(tag);//设置新记录tag为原纪录tag保持排序
            mHelper.updateIntro(uuid,introsBean);//调用数据库的更新方法
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();//提示已保存
            MainActivity.startActivity(UpdateIntrosActivity.this);
            finish();//结束编辑页面的活动
        }
    }
}
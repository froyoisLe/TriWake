package site.isleti.triwake.ui.notes;

import android.content.Context;
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
import butterknife.ButterKnife;
import site.isleti.triwake.R;
import site.isleti.triwake.db.NoteDBHelper;
import site.isleti.triwake.model.NoteBean;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.widget.LinedEditText;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class UpdateNoteActivity extends AppCompatActivity {

    private static final String TAG = "更新笔记";

    private NoteDBHelper mHelper;
    private LinedEditText mUpdateNoteEtContent;
    private ImageButton mUpdateNoteFabBack;
    private ImageButton mUpdateNoteFabAdd;
    private ImageButton mUpdateNoteFabDelete;
    private ImageView mUNIvBack;
    private TextView mTvUuid;
    private String date = " ";
    private String time = " ";
    private long tag;

    public static void startActivity(Context context, String content, String uuid, String date, String time, long tag) {
        Intent intent = new Intent(context, UpdateNoteActivity.class);
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
        setContentView(R.layout.notes_update_activity);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mHelper = new NoteDBHelper(this, "Notes.db", null, 1);

        //从NoteFragment接收数据并设置显示
        mUpdateNoteEtContent = findViewById(R.id.note_update_content);
        mTvUuid = findViewById(R.id.note_uuid);
        Intent intent = getIntent();
        mUpdateNoteEtContent.setText(intent.getStringExtra("content"));
        mTvUuid.setText(intent.getStringExtra("uuid"));
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        tag = intent.getLongExtra("tag",tag);

        //底部返回退出
        mUpdateNoteFabBack = findViewById(R.id.update_note_fab_back);
        mUpdateNoteFabBack.setOnClickListener(v -> showBackDialog());

        //修改后保存
        mUpdateNoteFabAdd = findViewById(R.id.update_note_fab_add);
        mUpdateNoteFabAdd.setOnClickListener(v -> saveUpdateNote());

        //删除显示对话框
        mUpdateNoteFabDelete = findViewById(R.id.update_note_fab_delete);
        mUpdateNoteFabDelete.setOnClickListener(v -> showDeleteDialog(v));

        //顶部直接退出
        mUNIvBack = findViewById(R.id.update_note_iv_back);
        mUNIvBack.setOnClickListener(v -> finish());

    }

     //删除时显示对话框
    public void showDeleteDialog(View v) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("删除笔记")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor(getResources().getColor(R.color.note_primary))                              //def
                .withMessage("   您确认要删除吗?")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(getResources().getColor(R.color.note_dark))                               //def  | withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.ic_filter_vintage_teal_50_36dp))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(500)                                          //def
                .withEffect(RotateBottom)                                         //def Effectstype.Slidetop
                .withButton1Text("是的")                                      //def gone
                .withButton2Text("手滑")                                  //def gone
                .setButton1Click(v1 -> {
                    String uuid = mTvUuid.getText().toString();
                    mHelper.removeNotes(uuid);
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
                .withTitle("返回笔记页")
                .withTitleColor("#FFFFFF")
                .withDividerColor(getResources().getColor(R.color.note_primary))
                .withMessage("需要保存再返回吗?")
                .withMessageColor("#FFFFFF")
                .withDialogColor(getResources().getColor(R.color.note_dark))                                                        //def
                .withIcon(getResources().getDrawable(R.drawable.ic_filter_vintage_teal_50_36dp))
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .withEffect(RotateBottom)
                .withButton2Text("是的")
                .withButton1Text("不用")
                .setButton2Click(v -> saveUpdateNote())   //保存再返回
                .setButton1Click(v -> finish())   //不用直接返回
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //保存更新后的内容 只更新内容
    public void saveUpdateNote() {
        String updateContent = mUpdateNoteEtContent.getText().toString();
        String uuid = mTvUuid.getText().toString();
        //获取之前记录的日期和时间，修改后不改变位置排序
        String updateDate = date;
        String updateTime = time;
        if (!updateContent.isEmpty()) {
            NoteBean noteBean = new NoteBean(updateDate,updateContent,updateTime);
            noteBean.setTag(tag);
            mHelper.updateNote(uuid,noteBean);
            Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
            Log.d(TAG, updateContent + "onClick: 修改成功");
            finish();
        }
    }
}
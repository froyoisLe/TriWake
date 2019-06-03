package site.isleti.triwake.ui.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.UUID;
import site.isleti.triwake.R;
import site.isleti.triwake.db.NoteDBHelper;
import site.isleti.triwake.model.NoteBean;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GetDate;
import site.isleti.triwake.widget.LinedEditText;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.RotateBottom;

public class AddNotesActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";

    private LinedEditText mEditText;
    private NoteDBHelper mDbHelper;
    private Context mContext;
    private String content;


    /*** 启动该页面 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddNotesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
    public static void startActivity(Context context,String content) {
        Intent intent = new Intent(context, AddNotesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_add_activity);
        mEditText = findViewById(R.id.note_add_content);
        ImageButton saveButton = findViewById(R.id.note_save);
        ImageButton cancelButton = findViewById(R.id.note_cancel);
        ImageView noteBack = findViewById(R.id.note_iv_back);
        AppManager.getAppManager().addActivity(this);
        mDbHelper = new NoteDBHelper(this,"Notes.db",null,1);
        saveButton.setOnClickListener(v -> saveContent());
        cancelButton.setOnClickListener(v -> {
            String content = mEditText.getText().toString();//获得当前编辑框的内容
            if (!content.isEmpty()){
                showBackDialog();
            }else
                finish();
        });
        noteBack.setOnClickListener(v -> backHome());
    }

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
                .setButton2Click(v -> saveContent())   //保存再返回
                .setButton1Click(v -> finish())   //不用直接返回
                .show();
    }

    //*** 取消返回主页 ***@author：IsLeti***/
    private void backHome() {
        Log.d(TAG, "onClick: 取消成功");
        finish();
    }
    //*** 保存新建条目 ***@author：IsLeti***/
    private void saveContent() {
        String content = mEditText.getText().toString();
        if (!content.isEmpty()) {
            String date = GetDate.getDate().toString();
            String time = DateUtil.getFormattedTime(System.currentTimeMillis());
            NoteBean noteBean = new NoteBean(null,null,null);
            noteBean.setContent(content);
            noteBean.setDate(date);
            noteBean.setTime(time);
            noteBean.setTag(System.currentTimeMillis());
            noteBean.setUuid(UUID.randomUUID().toString());
            mDbHelper.addNotes(noteBean);
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


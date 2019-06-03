package site.isleti.triwake.ui.diary;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import butterknife.ButterKnife;
import butterknife.OnClick;
import site.isleti.triwake.R;
import site.isleti.triwake.db.DiaryDBHelper;
import site.isleti.triwake.util.AppManager;
import site.isleti.triwake.util.GetDate;
import site.isleti.triwake.widget.LinedEditText;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Flipv;

public class UpdateDiaryActivity extends AppCompatActivity {

    private DiaryDBHelper mHelper;
    private TextView mUpdateDiaryTvDate;
    private EditText mUpdateDiaryEtTitle;
    private LinedEditText mUpdateDiaryEtContent;
    private ImageButton mUpdateDiaryFabBack;
    private ImageButton mUpdateDiaryFabAdd;
    private ImageButton mUpdateDiaryFabDelete;
    private ImageView mUDIvBack;
    private TextView mTvTag;

    public static void startActivity(Context context, String title, String content, String tag) {
        Intent intent = new Intent(context, UpdateDiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("tag", tag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_update_activity);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);

        mUpdateDiaryTvDate = findViewById(R.id.update_diary_tv_date);
        mUpdateDiaryEtTitle = findViewById(R.id.update_diary_et_title);
        mUpdateDiaryEtContent = findViewById(R.id.update_diary_et_content);
        mUpdateDiaryFabBack = findViewById(R.id.update_diary_fab_back);
        mUpdateDiaryFabAdd = findViewById(R.id.update_diary_fab_add);
        mUpdateDiaryFabDelete = findViewById(R.id.update_diary_fab_delete);
        mUDIvBack = findViewById(R.id.update_diary_iv_back);
        mTvTag = findViewById(R.id.update_diary_tv_tag);
        mHelper = new DiaryDBHelper(this, "Diary.db", null, 1);
        Intent intent = getIntent();
        mUpdateDiaryTvDate.setText("今天，" + GetDate.getDate());
        mUpdateDiaryEtTitle.setText(intent.getStringExtra("title"));
        mUpdateDiaryEtContent.setText(intent.getStringExtra("content"));
        mTvTag.setText(intent.getStringExtra("tag"));
    }

    @OnClick({R.id.update_diary_iv_back, R.id.update_diary_fab_back, R.id.update_diary_fab_add, R.id.update_diary_fab_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_diary_iv_back:
                DiaryActivity.startActivity(this);
            case R.id.update_diary_fab_delete:
               showDeleteDialog(mUpdateDiaryFabDelete);
                break;
            case R.id.update_diary_fab_add:
                SQLiteDatabase dbUpdate = mHelper.getWritableDatabase();
                ContentValues valuesUpdate = new ContentValues();
                String title = mUpdateDiaryEtTitle.getText().toString();
                String content = mUpdateDiaryEtContent.getText().toString();
                valuesUpdate.put("title", title);
                valuesUpdate.put("content", content);
                dbUpdate.update("Diary", valuesUpdate, "title = ?", new String[]{title});
                dbUpdate.update("Diary", valuesUpdate, "content = ?", new String[]{content});
                DiaryActivity.startActivity(this);
                break;
            case R.id.update_diary_fab_back:
                dbUpdate = mHelper.getWritableDatabase();
                valuesUpdate = new ContentValues();
                title = mUpdateDiaryEtTitle.getText().toString();
                content = mUpdateDiaryEtContent.getText().toString();
                if(!title.isEmpty() || !content.isEmpty()) {
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                    dialogBuilder
                            .withTitle("返回日记")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor(getResources().getColor(R.color.diary_primary))                              //def
                            .withMessage("   是否保存编辑内容？")                     //.withMessage(null)  no Msg
                            .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                            .withDialogColor(getResources().getColor(R.color.diary_dark))                               //def  | withDialogColor(int resid)                               //def
                            .withIcon(getResources().getDrawable(R.drawable.ic_book_white_36dp))
                            .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                            .withDuration(700)                                          //def
                            .withEffect(Flipv)                                         //def Effectstype.Slidetop
                            .withButton1Text("确定")                                      //def gone
                            .withButton2Text("不用")                                  //def gone
                            .setButton1Click(v1 -> {
                                valuesUpdate.put("title", title);
                                valuesUpdate.put("content", content);
                                dbUpdate.update("Diary", valuesUpdate, "title = ?", new String[]{title});
                                dbUpdate.update("Diary", valuesUpdate, "content = ?", new String[]{content});
                                DiaryActivity.startActivity(this);
                            })
                            .setButton2Click(v12 -> {
                                Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                                DiaryActivity.startActivity(UpdateDiaryActivity.this);
                            })
                            .show();
                }
                break;
        }
    }

    public void showDeleteDialog(View v){
        NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("删除日记")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor(getResources().getColor(R.color.diary_primary))                              //def
                .withMessage("   您确认要删除吗?")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(getResources().getColor(R.color.diary_dark))                               //def  | withDialogColor(int resid)                               //def
                .withIcon(getResources().getDrawable(R.drawable.ic_book_white_36dp))
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
                .withEffect(Flipv)                                         //def Effectstype.Slidetop
                .withButton1Text("是的")                                      //def gone
                .withButton2Text("手滑")                                  //def gone
                .setButton1Click(v1 -> {
                    String tag = mTvTag.getText().toString();
                    SQLiteDatabase dbDelete = mHelper.getWritableDatabase();
                    dbDelete.delete("Diary", "tag = ?", new String[]{tag});
                    DiaryActivity.startActivity(UpdateDiaryActivity.this);
                    Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                    dialogBuilder.cancel();
                })
                .setButton2Click(v12 -> {
                    Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                    dialogBuilder.cancel();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DiaryActivity.startActivity(this);
    }
}
package site.isleti.triwake.ui.diary;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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

public class AddDiaryActivity extends AppCompatActivity {

    private DiaryDBHelper mHelper;
    private TextView mAddDiaryTvDate;
    private EditText mAddDiaryEtTitle;
    private LinedEditText mAddDiaryEtContent;
    private ImageButton mAddDiaryFabBack;
    private ImageButton mAddDiaryFabAdd;
    private ImageView mDiaryIvBack;

    /*** 启动活动 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        context.startActivity(intent);
    }
    /*** 启动活动 ***@author：IsLeti***/
    public static void startActivity(Context context, String title, String content ) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary__add_activity);
        AppManager.getAppManager().addActivity(this);
        ButterKnife.bind(this);
        mAddDiaryTvDate = findViewById(R.id.add_diary_tv_date);
        mAddDiaryEtTitle = findViewById(R.id.add_diary_et_title);
        mAddDiaryEtContent = findViewById(R.id.add_diary_et_content);
        mAddDiaryFabBack = findViewById(R.id.add_diary_fab_back);
        mAddDiaryFabAdd = findViewById(R.id.add_diary_fab_add);
        mDiaryIvBack = findViewById(R.id.diary_iv_back);
        Intent intent = getIntent();
        mAddDiaryEtTitle.setText(intent.getStringExtra("title"));
        mAddDiaryTvDate.setText("今天，" + GetDate.getDate());
        mAddDiaryEtContent.setText(intent.getStringExtra("content"));
        mHelper = new DiaryDBHelper(this, "Diary.db", null, 1);
    }


    @OnClick({R.id.diary_iv_back, R.id.add_diary_fab_back, R.id.add_diary_fab_add})
    public void onClick(View view) {
        switch (view.getId()) {
            //顶部返回
            case R.id.diary_iv_back:
               DiaryActivity.startActivity(this);
               break;
             //添加日记
            case R.id.add_diary_fab_add:
                String date = GetDate.getDate().toString();
                String tag = String.valueOf(System.currentTimeMillis());
                String title = mAddDiaryEtTitle.getText().toString() + "";
                String content = mAddDiaryEtContent.getText().toString() + "";
                if (!title.equals("") || !content.equals("")) {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("date", date);
                    values.put("title", title);
                    values.put("content", content);
                    values.put("tag", tag);
                    db.insert("Diary", null, values);
                    values.clear();
                }
                DiaryActivity.startActivity(this);
                break;
             //底部退出
            case R.id.add_diary_fab_back:
                String diaryTag = String.valueOf(System.currentTimeMillis());
                final String dateBack = GetDate.getDate().toString();
                final String titleBack = mAddDiaryEtTitle.getText().toString();
                final String contentBack = mAddDiaryEtContent.getText().toString();
                if(!titleBack.isEmpty() || !contentBack.isEmpty()) {
                    NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                    dialogBuilder
                            .withTitle("返回日记")                                  //.withTitle(null)  no title
                            .withTitleColor("#FFFFFF")                                  //def
                            .withDividerColor(getResources().getColor(R.color.diary_primary))                              //def
                            .withMessage("   是否保存新增内容？")                     //.withMessage(null)  no Msg
                            .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                            .withDialogColor(getResources().getColor(R.color.diary_dark))                               //def  | withDialogColor(int resid)                               //def
                            .withIcon(getResources().getDrawable(R.drawable.ic_book_white_36dp))
                            .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                            .withDuration(700)                                          //def
                            .withEffect(Flipv)                                         //def Effectstype.Slidetop
                            .withButton1Text("确定")                                      //def gone
                            .withButton2Text("不用")                                  //def gone
                            .setButton1Click(v1 -> {
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("date", dateBack);
                                values.put("title", titleBack);
                                values.put("content", contentBack);
                                values.put("tag", diaryTag);
                                db.insert("Diary", null, values);
                                values.clear();
                                DiaryActivity.startActivity(AddDiaryActivity.this);
                                dialogBuilder.cancel();
                            })
                            .setButton2Click(v12 -> {
                                Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                                DiaryActivity.startActivity(AddDiaryActivity.this);
                                dialogBuilder.cancel();
                            })
                            .show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DiaryActivity.startActivity(this);
    }
}












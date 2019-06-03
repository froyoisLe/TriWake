package site.isleti.triwake.ui.painting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import java.io.ByteArrayOutputStream;
import site.isleti.triwake.R;
import static android.view.View.GONE;

public class DrawActivity extends Activity {

    private ImageView pen;
    private ImageView penInactive;
    private ImageView pencil;
    private ImageView pencilInactive;
    private ImageView erase;
    private ImageView eraseInactive;
    private ImageView mark;
    private ImageView markInactive;
    private ImageView chooseColor;
    private LinearLayout showColor;
    private ImageView choosePattern;
    private LinearLayout showPattern;
    private ImageButton currPaint;
    private DrawingView drawView;
    private ImageView deleteBtn;
    private ImageButton refreshTip;
    private TextView tip;
    private ImageButton warnWhite;
    private ImageButton warnBlack;
    private LinearLayout mTipLin;
    private int count = 0;
    private static final String TAG = "画板";
    private String currentPen = "";
    private ImageButton done;
    private Bitmap mBitmap;
    private TextView easyDraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        drawView = findViewById(R.id.drawing);
        penChoose();
        showTip();
        clearDrawing();
        tipRefresh();
        LinearLayout paintLayout = findViewById(R.id.pick_color);
        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setBackground(getResources().getDrawable(R.drawable.circle));
        drawView.setBrushSize(8);
        currentPen = "pencil";
        drawView.setPaintAlpha(95);
        save();
    }
    private void save() {
        done = findViewById(R.id.done);
        done.setOnClickListener(v -> {
            drawView.setDrawingCacheEnabled(true);
            mBitmap = drawView.getDrawingCache();
            //Drawable mDrawable = new BitmapDrawable(mBitmap);
            byte[] bytes = bitmap2Bytes(mBitmap);
            Intent intent = new Intent(DrawActivity.this,SaveActvity.class);
            intent.putExtra("image",bytes);
            startActivity(intent);
            Log.d(TAG, "onClick: 携带图片跳转");
            drawView.setDrawingCacheEnabled(false);//禁用避免性能受影响
        });
    }
    private byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    private void showTip() {
        mTipLin = findViewById(R.id.tip);
        warnBlack = findViewById(R.id.warn_black);
        warnWhite = findViewById(R.id.warn_white);
        easyDraw  = findViewById(R.id.easy_draw);
        warnWhite.setOnClickListener(v -> {
            warnWhite.setVisibility(GONE);
            easyDraw.setVisibility(View.VISIBLE);
            warnBlack.setVisibility(View.VISIBLE);
            mTipLin.setVisibility(View.INVISIBLE);
        });
        warnBlack.setOnClickListener(v -> {
            warnWhite.setVisibility(View.VISIBLE);
            warnBlack.setVisibility(GONE);
            mTipLin.setVisibility(View.VISIBLE);
            easyDraw.setVisibility(GONE);
        });
    }
    private void tipRefresh() {
        refreshTip = findViewById(R.id.refresh);
        tip = findViewById(R.id.tip_text);
        refreshTip.setOnClickListener(v -> {
            count ++;
            int tag = count % 10;
            String [] tips = {
                    "随便画点什么吧...",
                    "如果不知道画什么",
                    "那就背背单词吧 :)",
                    "homogeneous-均匀的",
                    "prototype-原型",
                    "democracy-民主主义",
                    "assimilate-吸收",
                    "susceptible-易感动的",
                    "obscure-晦涩的",
                    "点击左方图标关闭提示",
            };
            tip.setText(tips[tag]);
        });

    }
    private void clearDrawing() {
        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(v -> {
            final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(DrawActivity.this);
            dialogBuilder
                    .withTitle("      确定要清空当前绘画？")                                  //.withTitle(null)  no title
                    .withTitleColor("#FFFFFF")                                  //def
                    .withDividerColor(getResources().getColor(R.color.note_primary))                              //def
                    .withMessage("温馨提示：此操作不可逆")                     //.withMessage(null)  no Msg
                    .withMessageColor("#FFFFFF")                              //def  | withMessageColor(int resid)
                    .withDialogColor(getResources().getColor(R.color.note_dark))                           //def  | withDialogColor(int resid)
                    .withIcon(getResources().getDrawable(R.drawable.ic_warning_black_36dp))
                    .withDuration(500)                                          //def
                    .withEffect(Effectstype.SlideBottom)                                         //def Effectstype.Slidetop
                    .withButton1Text("取消")                                      //def gone
                    .withButton2Text("确定")                                  //def gone
                    .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.cancel();
                            Toast.makeText(v.getContext(), "已取消", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setButton2Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawView.startNew();
                            Toast.makeText(v.getContext(),"已清除",Toast.LENGTH_SHORT).show();
                            dialogBuilder.cancel();
                        }
                    })
                    .show();
        });
    }
    private void penChoose() {
        penCase();
        pencilCase();
        markCase();
        eraseCase();
        chooseColor = findViewById(R.id.choose_color);
        showColor = findViewById(R.id.show_color);
        chooseColor.setOnClickListener(v -> {
            if (showColor.getVisibility() == GONE) {
                showColor.setVisibility(View.VISIBLE);
                showPattern.setVisibility(GONE);
            } else
                showColor.setVisibility(GONE);
        });
        choosePattern = findViewById(R.id.choose_pattern);
        showPattern = findViewById(R.id.show_pattern);
        choosePattern.setOnClickListener(v -> {
            if (showPattern.getVisibility() == GONE) {
                showPattern.setVisibility(View.VISIBLE);
                showColor.setVisibility(GONE);
            } else showPattern.setVisibility(GONE);
        });
    }
    private void eraseCase() {

        erase = findViewById(R.id.eraser_active);
        eraseInactive = findViewById(R.id.eraser_inactive);
        erase.setOnClickListener(v -> {
            //erase.setVisibility(GONE);
            Log.d(TAG, "colorChoose:选擦透明度 " + drawView.getPaintAlpha());
            eraseAct();
            //eraseInactive.setVisibility(View.VISIBLE);
        });
        eraseInactive.setOnClickListener(v -> {
            Log.d(TAG, "colorChoose:选擦透明度 " + drawView.getPaintAlpha());
            eraseInactive.setVisibility(GONE);
            erase.setVisibility(View.VISIBLE);
            markInactive.setVisibility(View.VISIBLE);
            mark.setVisibility(GONE);
            pencil.setVisibility(GONE);
            pencilInactive.setVisibility(View.VISIBLE);
            penInactive.setVisibility(View.VISIBLE);
            pen.setVisibility(GONE);
            eraseAct();
        });
    }
    private void eraseAct() {
                 Log.d(TAG, "colorChoose:选擦透明度 " + drawView.getPaintAlpha());
                drawView.setErase(true);
                drawView.setBrushSize(28);
    }
    private void markCase() {
        mark = findViewById(R.id.mark_active);
        markInactive = findViewById(R.id.mark_inactive);
        mark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPen = "mark";
                drawView.setPaintAlpha(80);
                Log.d(TAG, "colorChoose:mark透明度 " + drawView.getPaintAlpha());
                //mark.setVisibility(GONE);
                markAct();
                //markInactive.setVisibility(View.VISIBLE);
            }
        });
        markInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPen = "mark";
                drawView.setPaintAlpha(80);
                Log.d(TAG, "colorChoose:mark透明度 " + drawView.getPaintAlpha());
                markAct();
                markInactive.setVisibility(GONE);
                mark.setVisibility(View.VISIBLE);
                pencil.setVisibility(GONE);
                pencilInactive.setVisibility(View.VISIBLE);
                erase.setVisibility(GONE);
                eraseInactive.setVisibility(View.VISIBLE);
                penInactive.setVisibility(View.VISIBLE);
                pen.setVisibility(GONE);
            }
        });
    }
    private void markAct() {
        currentPen = "mark";
        drawView.setPaintAlpha(80);
        drawView.setErase(false);
        drawView.setBrushSize(24);
        drawView.setLastBrushSize(24);
    }
    private void pencilCase() {
        pencil = findViewById(R.id.pencil_active);
        pencilInactive = findViewById(R.id.pencil_inactive);
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPen = "pencil";
                drawView.setPaintAlpha(100);
                Log.d(TAG, "colorChoose:pencil透明度 " + drawView.getPaintAlpha());
                pencilAct();
               // pencil.setVisibility(GONE);
                //pencilInactive.setVisibility(View.VISIBLE);
            }
        });

        pencilInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPen = "pencil";
                drawView.setPaintAlpha(100);
                Log.d(TAG, "colorChoose:pencil透明度 " + drawView.getPaintAlpha());
                pencilInactive.setVisibility(GONE);
                pencil.setVisibility(View.VISIBLE);
                mark.setVisibility(GONE);
                markInactive.setVisibility(View.VISIBLE);
                erase.setVisibility(GONE);
                eraseInactive.setVisibility(View.VISIBLE);
                penInactive.setVisibility(View.VISIBLE);
                pen.setVisibility(GONE);
                pencilAct();
            }
        });
    }
    private void pencilAct() {
        currentPen = "pencil";
        drawView.setPaintAlpha(95);
        drawView.setErase(false);
        drawView.setBrushSize(8);
        drawView.setLastBrushSize(8);
    }
    private void penCase() {
        pen = findViewById(R.id.pen_active);
        pen.setOnClickListener(v -> {
            currentPen = "pen";
            drawView.setPaintAlpha(100);
            penAct();
            //penInactive.setVisibility(View.VISIBLE);
            //pen.setVisibility(GONE);
        });

        penInactive = findViewById(R.id.pen_inactive);
        penInactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPen = "pen";
                drawView.setPaintAlpha(100);
                penAct();
                penInactive.setVisibility(GONE);
                pen.setVisibility(View.VISIBLE);
                pencil.setVisibility(GONE);
                pencilInactive.setVisibility(View.VISIBLE);
                mark.setVisibility(GONE);
                markInactive.setVisibility(View.VISIBLE);
                erase.setVisibility(GONE);
                eraseInactive.setVisibility(View.VISIBLE);

            }
        });
    }
    private void penAct() {
        currentPen = "pen";
        drawView.setErase(false);
        drawView.setBrushSize(3);
        drawView.setLastBrushSize(3);
        drawView.setPaintAlpha(98);
    }
    private void setBrushAlpha() {
        String currentPen = this.currentPen;
        Log.d(TAG, "colorChoose:现在的笔 " + currentPen);
        switch (currentPen) {
            case "pen":
                drawView.setPaintAlpha(90);
                break;
            case "mark":
                drawView.setPaintAlpha(80);
                break;
            case "pencil":
                drawView.setPaintAlpha(98);
                break;
        }
        Log.d(TAG, "colorChoose:笔的透明度 " + drawView.getPaintAlpha());
    }
    public void colorChoose(View view) {

        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        String currentPen = this.currentPen;
        Log.d(TAG, "colorChoose:现在的笔 " + currentPen);
        switch (currentPen) {
            case "pen":
                drawView.setPaintAlpha(98);
                drawView.setBrushSize(3);
                break;
            case "mark":
                drawView.setPaintAlpha(80);
                drawView.setBrushSize(24);
                break;
            case "pencil":
                drawView.setPaintAlpha(95);
                drawView.setBrushSize(8);
                break;
        }
        Log.d(TAG, "colorChoose:笔的透明度 " + drawView.getPaintAlpha());
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            //update ui
            imgView.setBackground(getResources().getDrawable(R.drawable.circle));
            currPaint.setBackground(getResources().getDrawable(R.drawable.circle_inactive));
            currPaint = (ImageButton) view;
            Log.d(TAG, "colorChoose: 选中颜色" + color);
            setBrushAlpha();
            chooseColor.setImageDrawable(currPaint.getDrawable());
            showColor.setVisibility(GONE);
            showPattern.setVisibility(GONE);
        }
    }
    public void patternChoose(View view) {
        drawView.setErase(false);
        drawView.setPaintAlpha(100);
        drawView.setBrushSize(drawView.getLastBrushSize());
        String currentPen = this.currentPen;
        Log.d(TAG, "colorChoose:现在的笔 " + currentPen);
        switch (currentPen) {
            case "pen":
                drawView.setPaintAlpha(98);
                drawView.setBrushSize(3);
                break;
            case "mark":
                drawView.setPaintAlpha(80);
                drawView.setBrushSize(24);
                break;
            case "pencil":
                drawView.setPaintAlpha(95);
                drawView.setBrushSize(8);
                break;
        }
        Log.d(TAG, "colorChoose:笔的透明度 " + drawView.getPaintAlpha());
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            //update ui
            imgView.setBackground(getResources().getDrawable(R.drawable.circle));
            currPaint.setBackground(getResources().getDrawable(R.drawable.circle_inactive));
            currPaint = (ImageButton) view;
            Log.d(TAG, "colorChoose: 选中颜色" + color);
            setBrushAlpha();
            choosePattern.setImageDrawable(currPaint.getDrawable());
            showColor.setVisibility(GONE);
            showPattern.setVisibility(GONE);
        }
    }
}

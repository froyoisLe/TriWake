package site.isleti.triwake.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import java.util.Timer;
import java.util.TimerTask;
import site.isleti.triwake.R;
import site.isleti.triwake.util.Typefaces;

public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";
    private  ImageView bling; //发光小球
    private Animation animation =null; //Bling()
    private ShimmerTextView wrsx;
    private ShimmerTextView tw;
    private TextView laodingTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //动画效果
        wrsx = findViewById(R.id.wrsxws);
        tw = findViewById(R.id.appname);
        new Shimmer().start(wrsx);
        new Shimmer().start(tw);

        //字体设置
        laodingTV = findViewById(R.id.laodingText);
       // wrsx.setTypeface(Typefaces.get(this,"FZZJ_CZYKSJW.TTF"));
        laodingTV.setTypeface(Typefaces.get(this,"LOKICOLA.TTF"));
        Bling();

        //bling 点击事件
        bling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bling();
            }
        });

        //计时自动跳转 时长 3s
        Timer timer = new Timer();//声明一个Timer()类实例timer
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //使用Intent传入context进行页面跳转
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);//利用intent启动新的活动
                finish();//结束当前活动
            }
        };
        timer.schedule(timerTask, 3000);//任务延迟3s执行
    }

    void Bling(){
        bling = findViewById(R.id.bling);//绑定小球的ImageView视图
        //实例化Animation后为其绑定动画资源文件bling.xml
        animation = AnimationUtils.loadAnimation(this,R.anim.bling);
        bling.startAnimation(animation);//启动动画效果
    }
}




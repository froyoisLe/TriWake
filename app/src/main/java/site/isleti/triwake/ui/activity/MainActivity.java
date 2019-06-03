package site.isleti.triwake.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import site.isleti.triwake.R;
import site.isleti.triwake.adapter.MyFragmentPagerAdapter;
import site.isleti.triwake.adapter.RecordViewPagerAdapter;
import site.isleti.triwake.ui.account.AccountFragment;
import site.isleti.triwake.util.CrashHandler;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";

    //UI Objects Define
    private static BubbleNavigationConstraintView mBubbleNavigationConstraintView;
    private static ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    private RecordViewPagerAdapter pagerAdapter;

    //代表页面position的常量
    public static final int PAGE_INTROSPECT = 0;
    public static final int PAGE_TODO = 1;
    public static final int PAGE_NOTES = 2;
    public static final int PAGE_ACCOUNT = 3;
    //退出判断
    private boolean isQuit = false;


    /*** Context 启动页面 ***@author：IsLeti***/
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBubbleNavigationConstraintView = findViewById(R.id.floating_top_bar_navigation);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        bindViews();
        initBubbleMenu();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    /*** 页面控件绑定 ***@author：IsLeti***/
    private void bindViews() {
        if (mAdapter == null){//先对Adapter判空，避免空引用
            mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        }
        vpager = findViewById(R.id.vpager);//找到vpger的容器视图
        vpager.setAdapter(mAdapter);//将Fragment的适配器传给vpger
        //vpager.setCurrentItem(PAGE_INTROSPECT);//初始设置页面为反思页
        vpager.addOnPageChangeListener(this);//为ViewPager设置页面变化的监听事件
    }

    /*** 初始化FloatingBubbleMenu ***@author：IsLeti***/
    private void initBubbleMenu() {
        mBubbleNavigationConstraintView.setTypeface(
                Typeface.createFromAsset(getAssets(), "rubik.ttf"));//设置菜单字体
        mBubbleNavigationConstraintView.setNavigationChangeListener((view, position) -> {
            switch (view.getId()) {
                case R.id.l_item_home:  //当图标显示为反思Icon时
                    vpager.setCurrentItem(PAGE_INTROSPECT); //页面为反思界面主页
                    break;
                case R.id.l_item_todo:  //当图标显示为待办Icon时
                    vpager.setCurrentItem(PAGE_TODO);
                    break;
                case R.id.l_item_notes: //当图标显示为笔记Icon时
                    vpager.setCurrentItem(PAGE_NOTES); //页面为笔记界面主页
                    break;
                case R.id.l_item_account: //当图标显示为记账Icon时
                    vpager.setCurrentItem(PAGE_ACCOUNT); //页面为记账界面主页
                    break;
            }
        });
    }

    /*** setCurrentActiveItem()->页面变化时顶部菜单选项跟随变动  ***@author：IsLeti***/
    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_INTROSPECT://当为反思页面时，顶部对应选中菜单项为第一个 位置0
                    mBubbleNavigationConstraintView.setCurrentActiveItem(0);
                    break;
                case PAGE_TODO://当为待办页面时，顶部对应选中菜单项为第二个 位置1
                    mBubbleNavigationConstraintView.setCurrentActiveItem(1);
                    break;
                case PAGE_NOTES://当为笔记页面时，顶部对应选中菜单项为第三个 位置2
                    mBubbleNavigationConstraintView.setCurrentActiveItem(2);
                    break;
                case PAGE_ACCOUNT://当为记账页面时，顶部对应选中菜单项为第四个 位置3
                    mBubbleNavigationConstraintView.setCurrentActiveItem(3);
                    break;
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //here we can use getIntent() to get the extra data.
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1){
                int id = getIntent().getIntExtra("id", 4);
                if (id == 4) {

                    Fragment fragment = new AccountFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.ac_fg,fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        }
    }



    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            isQuit = true;
            //两秒钟之后isQuit会变成false
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    isQuit = false;
                }
            }).start();
        } else {
            this.finish();
        }
    }

}





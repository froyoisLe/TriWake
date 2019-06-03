package site.isleti.triwake.adapter;

import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import site.isleti.triwake.ui.Intros.IntrosFragment;
import site.isleti.triwake.ui.activity.MainActivity;
import site.isleti.triwake.ui.account.AccountFragment;
import site.isleti.triwake.ui.notes.NotesFragment;
import site.isleti.triwake.ui.todo.ToDoFragment;

/***
*@author islet
*@time 2019/5/5 0005 19:29
***/
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4; //页面总数
    private IntrosFragment mIntrosFragment = null; //反思
    private ToDoFragment mToDoFragment = null;  //待办
    private NotesFragment mNotesFragment = null;  //笔记
    private AccountFragment mAccountFragment = null;  //记账
   //在Fragment的适配器中初始化各模块主页面的Fragment
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mIntrosFragment = new IntrosFragment();//初始化反思主界面碎片
        mToDoFragment = new ToDoFragment();//初始化待办主界面碎片
        mNotesFragment = new NotesFragment();//初始化笔记主界面碎片
        mAccountFragment = new AccountFragment();//初始化记账主界面碎片
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
   /*** 各模块主界面碎片与页面位置关系的绑定 ***@author：IsLeti***/
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_INTROSPECT: //0-第一页-反思
                fragment = mIntrosFragment;
                break;
            case MainActivity.PAGE_TODO:  //1-第二页-待办
                fragment = mToDoFragment;
                break;
            case MainActivity.PAGE_NOTES:  //2-第三页-笔记
                fragment = mNotesFragment;
                break;
            case MainActivity.PAGE_ACCOUNT:  //3-第四页-记账
                fragment = mAccountFragment;
                break;
        }
        return fragment;//返回获得相应position的碎片
    }
}


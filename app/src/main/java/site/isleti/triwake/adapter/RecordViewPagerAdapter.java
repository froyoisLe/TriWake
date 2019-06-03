package site.isleti.triwake.adapter;

import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.LinkedList;
import site.isleti.triwake.ui.account.RecordMainFragment;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GlobalUtil;

public class RecordViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "RecordViewPagerAdapter";

    /***
    *初始化每天的日期及Fragment显示
    *@author islet
    *@time 2019/5/6 0006 18:14
    ***/
    LinkedList<RecordMainFragment> fragments = new LinkedList<>();

    public LinkedList<String> getDates() {
        return dates;
    }

    public void setDates(LinkedList<String> dates) {
        this.dates = dates;
    }

    LinkedList<String> dates = new LinkedList<>();

    public RecordViewPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }



    private void initFragments(){
        dates = GlobalUtil.getInstance().mRecordDBHelper.getAvailableDate();
        if (!dates.contains(DateUtil.getFormattedDate())){
            dates.addLast(DateUtil.getFormattedDate());
        }
        for (String date:dates){
            RecordMainFragment fragment = new RecordMainFragment(date);
            fragments.add(fragment);
        }
    }


    public void reload(){
        for (RecordMainFragment fragment :
                fragments) {
            fragment.reload();
        }
    }

    public int getLatsIndex(){
        return fragments.size()-1;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getDateStr(int index){
        return dates.get(index);
    }

    public int getTotalIncome(int index){
        return fragments.get(index).getTotalIncome();
    }

    public int getTotalAccount(int index){
        return fragments.get(index).getTotalAccount();
    }

    public int getTotalCost(int index){
        return fragments.get(index).getTotalCost();
    }


    public int getAllCost(){
        int allCost = 0;
        for (int i = 0; i < fragments.size();i++)
        {
           allCost = allCost + fragments.get(i).getTotalCost();
        }
         return  allCost;
    }

    public int getAllIncome(){
        int allIncome = 0;
            for (int i = 0; i < fragments.size();i++)
            {
                allIncome = allIncome + fragments.get(i).getTotalIncome();
            }
        return  allIncome;
    }

    public int getAllAccount(){
        int allAccount = 0;
        for (int i = 0; i < fragments.size();i++)
        {
            allAccount = allAccount + fragments.get(i).getTotalAccount();
        }
        return  allAccount;
    }

    public int getPickDate(int index,String pickDate){
        for (int i = 0; i < fragments.size(); i++){
            if (fragments.get(i).getDate().contains(pickDate)){
                index = i;
                break;
            }
        }
        Log.d(TAG, "getPickDate: 日期 " + index);
        return index;
    }

}



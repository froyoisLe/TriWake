package site.isleti.triwake.ui.account;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import java.util.Calendar;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.adapter.RecordViewPagerAdapter;
import site.isleti.triwake.util.CrashHandler;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GlobalUtil;
import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String TAG = "AccountFragment";

    private RecordViewPagerAdapter pagerAdapter;
    private TickerView amountText;
    private TextView dateText;
    private int currentPagerPosition = 0;
    private ViewPager mViewPager;
    private TextView mShou;
    private TextView mZhi;
    private TextView mZong;
    private TextView mZShou;
    private TextView mZZhi;
    private ImageView mZZong;
    protected Activity mActivity;
    private FloatingActionButton datePicker;
    private FloatingActionButton mFloatingActionButton;
    private DatePicker mDatePicker;
    private int        year;
    private int        month;//月份是从0开始算的.
    private int        day;
    LinkedList<String> dates = new LinkedList<>();


    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View accountView = inflater.inflate(R.layout.account_fg, container, false);
        mViewPager = accountView.findViewById(R.id.view_pager);
        //*** 设置头部金额显示 Ticker ***@author：IsLeti***/
        amountText =  accountView.findViewById(R.id.amount_text);
        dateText = accountView.findViewById(R.id.day_text);
        amountText.setCharacterLists(TickerUtils.provideNumberList());
        GlobalUtil.getInstance().setContext(getActivity());
        GlobalUtil.getInstance().mAccountFragment = this;
        pagerAdapter = new RecordViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(pagerAdapter.getLatsIndex());
        mShou = accountView.findViewById(R.id.income_text);
        mZhi = accountView.findViewById(R.id.expense_text);
        mZong = accountView.findViewById(R.id.account_text);

        mZShou = accountView.findViewById(R.id.income_all);
        mZZhi = accountView.findViewById(R.id.expense_all);
        mZZong = accountView.findViewById(R.id.account_all);
        mShou.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getTotalIncome(currentPagerPosition))));
        mZhi.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getTotalCost(currentPagerPosition))));
        mZong.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getTotalAccount(currentPagerPosition))));

        mZShou.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getAllIncome())));
        mZZhi.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getAllCost())));
        mZZong.setOnClickListener(v -> amountText.setText(String.valueOf(pagerAdapter.getAllAccount())));

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getActivity());


        Calendar ca = Calendar.getInstance();
        year = ca.get(Calendar.YEAR);
        month = ca.get(Calendar.MONTH);
        day = ca.get(Calendar.DAY_OF_MONTH);
        datePicker  = accountView.findViewById(R.id.date_pick);
        datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year, month, dayOfMonth) -> {
                //选择时间之后的回掉方法，可以获取年月日
                String pickDate;
                if (month + 1 < 10) {
                    if (dayOfMonth < 10) {
                        pickDate = new StringBuffer().append(year).append("-").append("0").
                                append(month + 1).append("-").append("0").append(dayOfMonth).toString();
                    } else {
                        pickDate = new StringBuffer().append(year).append("-").append("0").
                                append(month + 1).append("-").append(dayOfMonth).toString();
                    }

                } else {
                    if (dayOfMonth < 10) {
                        pickDate = new StringBuffer().append(year).append("-").
                                append(month + 1).append("-").append("0").append(dayOfMonth).toString();
                    } else {
                        pickDate = new StringBuffer().append(year).append("-").
                                append(month + 1).append("-").append(dayOfMonth).toString();
                    }
                }

                currentPagerPosition =  pagerAdapter.getPickDate(0,pickDate);
                mViewPager.setCurrentItem(currentPagerPosition);
                updateHeader();

            }, year, month, day);
            //设置日期的范围
            datePickerDialog.show();
        });
       mFloatingActionButton = accountView.findViewById(R.id.add_account);
       mFloatingActionButton.setOnClickListener(v -> {
        Intent intent = new Intent(getActivity(),AddRecordActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           intent.putExtra("id",4);
             getActivity().startActivityForResult(intent,1);
        });
        updateHeader();
        return accountView;
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1){
                pagerAdapter.reload();
                updateHeader();
                Log.d(TAG, "onActivityResult:接收回退->requestCode ==    " + requestCode);
                Log.d(TAG, "onActivityResult: 接收回退-> resultCode    " +  resultCode);
            }
        }
        }




    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPagerPosition = position;
        updateHeader();
    }

    /*** 头部刷新实现 ***@author：IsLeti***/
    public void updateHeader(){
        String amount = String.valueOf(   //获得当前页面对应日期的收支总额
                pagerAdapter.getTotalAccount(currentPagerPosition));
        amountText.setText(amount);//在金额显示处显示获得的金额
        //获得当前页面对应子Fragments的日期
        String date = pagerAdapter.getDateStr(currentPagerPosition);
        //刷新头部的星期显示
        dateText.setText(DateUtil.getDateTitle(date) + "      " +  DateUtil.getWeekDay(date));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}




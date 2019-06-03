package site.isleti.triwake.ui.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.adapter.RecordListViewAdapter;
import site.isleti.triwake.model.RecordBean;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GlobalUtil;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

@SuppressLint("ValidFragment")
public class RecordMainFragment extends Fragment implements AdapterView.OnItemLongClickListener{

    private View rootView;
    private TextView textView;
    private ListView listView;
    private RecordListViewAdapter mRecordListViewAdapter;
    private LinkedList<RecordBean> records = new LinkedList<>();


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date = "";
    private Context mContext;
    protected Activity mActivity;

    private static final String TAG = "记账Fragment";



    @SuppressLint("ValidFragment")
    public RecordMainFragment(String date){
        this.date = date;
        records = GlobalUtil.getInstance().mRecordDBHelper.getRecords(date);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.account_fragment_main,container, false);
        initView();
        textView =  rootView.findViewById(R.id.day_text);
        listView =  rootView.findViewById(R.id.listView);
        textView.setText(DateUtil.getWeekDay(date));
        mRecordListViewAdapter = new RecordListViewAdapter(getContext(),records);
        mRecordListViewAdapter.setData(records);
        listView.setAdapter(mRecordListViewAdapter);
        if (mRecordListViewAdapter.getCount()>0){
            rootView.findViewById(R.id.no_record_layout).setVisibility(View.INVISIBLE);
        }
        textView.setText(DateUtil.getDateTitle(date) + "      " +  DateUtil.getWeekDay(date));
        listView.setOnItemLongClickListener(this);
        mContext = this.getActivity().getApplicationContext();
        return rootView;
    }

    private void initView(){
    }

    public void reload(){
        records = GlobalUtil.getInstance().mRecordDBHelper.getRecords(date);
        if (mRecordListViewAdapter ==null){
            Log.d(TAG, "reload:刷新方法 mRecordListViewAdapter 是空的");
            //只能通过getContext()获取上下文
            mRecordListViewAdapter = new RecordListViewAdapter(getContext(),records);
        }
        mRecordListViewAdapter.setData(records);
        mRecordListViewAdapter.notifyDataSetChanged();
        listView.setAdapter(mRecordListViewAdapter);
        if (mRecordListViewAdapter.getCount()>0){
            rootView.findViewById(R.id.no_record_layout).setVisibility(View.INVISIBLE);
        }
    }

    public int getTotalCost(){
        double totalCost = 0;
        for (RecordBean record: records){
            if (record.getType()==1){
                totalCost+= record.getAmount();
            }
        }
        return (int)totalCost;
    }

    public int getTotalIncome(){
        double totalIncome = 0;
        for (RecordBean record: records){
            if (record.getType()==2){
                totalIncome+= record.getAmount();
            }
        }
        return (int)totalIncome;
    }

    public int getTotalAccount(){
        double totalAccount = 0;
        totalAccount = getTotalIncome() -getTotalCost();
        return (int)totalAccount;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final RecordBean selectedRecord = records.get(position);
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("记账")
                .withTitleColor("#FFFFFF")
                .withDividerColor(getActivity().getResources().getColor(R.color.account_dark))
                .withMessage("   选择您想进行的操作")
                .withMessageColor("#FFFFFF")
                .withDialogColor(getActivity().getResources().getColor(R.color.account_darker))
                .withIcon(getActivity().getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_36dp))
                .isCancelableOnTouchOutside(true)
                .withDuration(500)
                .withEffect(Slidetop)
                .withButton2Text("删除")
                .withButton1Text("编辑")
                .setButton2Click(v -> {
                    String uuid = selectedRecord.getUuid();
                    GlobalUtil.getInstance().mRecordDBHelper.removeRecord(uuid);
                    Toast.makeText(getActivity(),"已删除",Toast.LENGTH_SHORT).show();
                    reload();
                    GlobalUtil.getInstance().mAccountFragment.updateHeader();
                    dialogBuilder.cancel();
                }).setButton1Click(v -> {
                    Intent intent = new Intent(getActivity(), AddRecordActivity.class);
                    Bundle extra = new Bundle();
                    extra.putSerializable("record", selectedRecord);
                    intent.putExtras(extra);
                    intent.putExtra("id",4);
                    getActivity().startActivityForResult(intent, 1);
                    dialogBuilder.cancel();
                }).show();
        return false;
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

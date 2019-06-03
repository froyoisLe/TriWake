package site.isleti.triwake.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.model.RecordBean;
import site.isleti.triwake.util.DateUtil;
import site.isleti.triwake.util.GlobalUtil;

public class RecordListViewAdapter extends BaseAdapter {

    private LinkedList<RecordBean> records = new LinkedList<>();

    private LayoutInflater mInflater;
    private Context mContext;

    public RecordListViewAdapter(Context context,LinkedList<RecordBean> mRecords){
        this.mContext = context;
        this.records = mRecords;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(LinkedList<RecordBean> records){
        this.records = records;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<Integer,View> rcmp = new HashMap<>();
        RecordViewHolder holder;
        if (rcmp.get(position) == null){
            convertView = mInflater.inflate(R.layout.account_cell_list_view,null);
            RecordBean recordBean = (RecordBean) getItem(position);
            holder = new RecordViewHolder(convertView, recordBean);
            convertView.setTag(holder);
            rcmp.put(position,convertView);
        } else {
            convertView = rcmp.get(position);
            holder = (RecordViewHolder) convertView.getTag();
        }
        return convertView;
    }
}

class RecordViewHolder {
    TextView remarkTV;
    TextView amountTV;
    TextView timeTV;
    ImageView categoryIcon;

    public RecordViewHolder(View itemView, RecordBean record){
        remarkTV = itemView.findViewById(R.id.textView_remark);
        amountTV = itemView.findViewById(R.id.textView_amount);
        timeTV = itemView.findViewById(R.id.textView_time);
        categoryIcon = itemView.findViewById(R.id.imageView_category);

        remarkTV.setText(record.getRemark());

        if (record.getType() == 1){
            amountTV.setText("- "+record.getAmount());
        } else {
            amountTV.setText("+ "+record.getAmount());
        }

        timeTV.setText(DateUtil.getFormattedTime(record.getTimeStamp()));
        categoryIcon.setImageResource(GlobalUtil.getInstance().getResourceIcon(record.getCategory()));
    }

}
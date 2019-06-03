package site.isleti.triwake.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.model.RecordCategoryResBean;
import site.isleti.triwake.model.RecordBean;
import site.isleti.triwake.util.GlobalUtil;

public class RecordCategoryRecyclerAdapter extends RecyclerView.Adapter<RecordCategoryViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    private LinkedList<RecordCategoryResBean> cellList = GlobalUtil.getInstance().costRes;

    public String getSelected() {
        return selected;
    }

    private String selected="";

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    private OnCategoryClickListener onCategoryClickListener;

    public RecordCategoryRecyclerAdapter(Context context){
        GlobalUtil.getInstance().setContext(context);
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        selected = cellList.get(0).title;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecordCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.account_cell_category,parent,false);
        RecordCategoryViewHolder myViewHolder = new RecordCategoryViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecordCategoryViewHolder holder, int position) {

            final RecordCategoryResBean res = cellList.get(position);
            holder.imageView.setImageResource(res.resBlack);
            holder.textView.setText(res.title);
            holder.textView.setText(res.title);
            holder.resItem.setOnClickListener(v -> {
                selected = res.title;
                notifyDataSetChanged();
                //*** 接口回调刷新顶部显示 ***@author：IsLeti***/
                if (onCategoryClickListener!=null){
                    onCategoryClickListener.onClikc(res.title);
                }
            });

        if (holder.textView.getText().toString().equals(selected)){
            holder.background.setBackgroundResource(R.drawable.bgr_account_theme);
        }
        else {
            holder.background.setBackgroundResource(R.drawable.account_bg_edit_remark);
        }

    }

    public void changeType(RecordBean.RecordType type){
        if (type == RecordBean.RecordType.RECORD_TYPE_EXPENSE){
            cellList = GlobalUtil.getInstance().costRes;
        }else if (type == RecordBean.RecordType.RECORD_TYPE_INCOME)
            {
            cellList = GlobalUtil.getInstance().earnRes;
        }
        selected = cellList.get(0).title;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cellList.size();
    }

    public interface OnCategoryClickListener{
        void onClikc(String category);
    }
}


class RecordCategoryViewHolder extends RecyclerView.ViewHolder{

    RelativeLayout background;
    ImageView imageView;
    TextView textView;
    RelativeLayout resItem;

    public RecordCategoryViewHolder(View itemView) {
        super(itemView);
        background = itemView.findViewById(R.id.cell_background);
        imageView = itemView.findViewById(R.id.imageView_category);
        textView = itemView.findViewById(R.id.textView_category);
        resItem = itemView.findViewById(R.id.res_item);
    }
}

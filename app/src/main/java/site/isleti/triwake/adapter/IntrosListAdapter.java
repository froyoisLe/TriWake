package site.isleti.triwake.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import org.greenrobot.eventbus.EventBus;
import java.util.HashMap;
import java.util.LinkedList;
import site.isleti.triwake.R;
import site.isleti.triwake.db.IntrosDBHelper;
import site.isleti.triwake.event.StartUpdateIntrosEvent;
import site.isleti.triwake.model.IntrosBean;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class IntrosListAdapter extends BaseAdapter{

    private static final String TAG = "IntrosListAdapter";

    private LinkedList<IntrosBean> mIntrosBeans;
    private LayoutInflater  mLayoutInflater;
    private Context mContext;
    private int mEditPosition = -1;
    private IntrosDBHelper mHelper;

    /*** 初始化构造器：传入上下文和DataList，获得数据库信息 ***@author：IsLeti***/
    public IntrosListAdapter(Context context,LinkedList<IntrosBean> introspects){
        this.mContext = context;
        this.mIntrosBeans = introspects;
        this.mLayoutInflater = LayoutInflater.from(context);
        mHelper = new IntrosDBHelper(mContext,"Introspect.db",null,1);
    }

    public void setInts(LinkedList<IntrosBean> introspects){
        this.mIntrosBeans = introspects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mIntrosBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mIntrosBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        IntrosViewHolder introsViewHolder;
        //*** HashMap<> 获取View 防止布局复用产生混乱***@author：IsLeti***/
        HashMap<Integer,View> itmp = new HashMap<>();
        if (itmp.get(position) == null){
            convertView = mLayoutInflater.inflate(R.layout.intros_item_view,null);
                introsViewHolder = new IntrosViewHolder(convertView, (IntrosBean) getItem(position));
                convertView.setTag(introsViewHolder);//将ViewHolder作为Tag传给convertView
                itmp.put(position,convertView);//
        }
        else {
            convertView = itmp.get(position);
            introsViewHolder = (IntrosViewHolder) convertView.getTag();
        }

        //*** 按钮预先隐藏 ***@author：IsLeti***/
        introsViewHolder.mIvEdit.setVisibility(View.INVISIBLE);
        introsViewHolder.mIvDelete.setVisibility(View.INVISIBLE);
        if(mEditPosition == position){
            introsViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            introsViewHolder.mIvDelete.setVisibility(View.VISIBLE);
        }
        else {
            introsViewHolder.mIvEdit.setVisibility(View.GONE);
            introsViewHolder.mIvDelete.setVisibility(View.GONE);
        }

        //设置卡片主题背景
        int i = position % 3;
        if (i == 0){
            introsViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_intro_theme));
            introsViewHolder.mIntsDate.setTextColor(mContext.getResources().getColor(R.color.intros_primary));
            introsViewHolder.mIntsTime.setTextColor(mContext.getResources().getColor(R.color.intros_primary));
            introsViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.intros_darker));
        }
        else if (i == 1){
            introsViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_diary_theme));
            introsViewHolder.mIntsDate.setTextColor(mContext.getResources().getColor(R.color.diary_primary));
            introsViewHolder.mIntsTime.setTextColor(mContext.getResources().getColor(R.color.diary_primary));
            introsViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.diary_darker));
        }
        else if (i == 2){
            introsViewHolder.mRelativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bgr_account_theme));
            introsViewHolder.mIntsDate.setTextColor(mContext.getResources().getColor(R.color.account_primary));
            introsViewHolder.mIntsTime.setTextColor(mContext.getResources().getColor(R.color.account_primary));
            introsViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.account_darker));
        }

        //点击item区域显示删除和编辑的图标
        introsViewHolder.mLayout.setOnClickListener(v -> {
            if(introsViewHolder.mIvEdit.getVisibility() == View.VISIBLE){
                introsViewHolder.mIvEdit.setVisibility(View.GONE);
            }
            else {
                introsViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            }
            if(introsViewHolder.mIvDelete.getVisibility() == View.VISIBLE){
                introsViewHolder.mIvDelete.setVisibility(View.GONE);
            }
            else {
                introsViewHolder.mIvDelete.setVisibility(View.VISIBLE);
            }
            if(mEditPosition != position){
                mEditPosition = position;
            }
            mEditPosition = position;
        });

        /*** 跳转进行编辑 EventBus实现***@author：IsLeti***/
        introsViewHolder.mIvEdit.setOnClickListener(v -> {
            //使用EventBus发布当前记录更新事务
            EventBus.getDefault().post(new StartUpdateIntrosEvent(position));
        });

        //delete
        introsViewHolder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(mContext);
                dialogBuilder
                        .withTitle("删除反思")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor(mContext.getResources().getColor(R.color.intros_dark))
                        .withMessage("   您确认要删除吗?")
                        .withMessageColor("#FFFFFF")
                        .withDialogColor(mContext.getResources().getColor(R.color.intros_darker))
                        .withIcon(mContext.getResources().getDrawable(R.drawable.ic_intros))
                        .isCancelableOnTouchOutside(true)
                        .withDuration(500)
                        .withEffect(Slidetop)
                        .withButton1Text("手滑")
                        .withButton2Text("是的")
                        .setButton2Click(v1 -> {
                            String uuid = mIntrosBeans.get(position).getUuid(); //获取预删除条目的UUID
                            mHelper.removeIntro(uuid);//调用DBHelper的删除方法将此uuid的条目删除
                            mIntrosBeans.remove(position);//从视图Adapter的列表中将此位置条目移除
                            notifyDataSetChanged();//通知Adapter更新列表内容变化
                            Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                            dialogBuilder.cancel();
                        })
                        .setButton1Click(v12 -> {
                            Toast.makeText(v12.getContext(), "取消了", Toast.LENGTH_SHORT).show();
                            dialogBuilder.cancel();
                        })
                        .show();
            }
        });

        return convertView;
    }

    public static class IntrosViewHolder {
        private  TextView mIntsDate;
        private  TextView mIntsTime;
        private  ExpandableTextView mIntsContent;
        private TextView mTextView;
        private RelativeLayout mLayout;
        private ImageView mIvEdit;
        private ImageView mIvDelete;
        private RelativeLayout mRelativeLayout;

        IntrosViewHolder(View itemView,IntrosBean introsBean){
            mIntsDate = itemView.findViewById(R.id.introspect_date);
            mIntsTime = itemView.findViewById(R.id.introspect_time);
            mIntsContent = itemView.findViewById(R.id.expand_text_view);
            mTextView = itemView.findViewById(R.id.expandable_text);
            mIntsDate.setText(introsBean.getDate());
            mIntsTime.setText(introsBean.getTime());
            mIntsContent.setText("  " + introsBean.getContent());
            mLayout = itemView.findViewById(R.id.intro_layout);
            mIvEdit = itemView.findViewById(R.id.intro_iv_edit);
            mIvDelete = itemView.findViewById(R.id.intro_iv_delete);
            mRelativeLayout = itemView.findViewById(R.id.intro_item);
        }
    }
}


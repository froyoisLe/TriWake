package site.isleti.triwake.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import site.isleti.triwake.R;
import site.isleti.triwake.db.DiaryDBHelper;
import site.isleti.triwake.event.StartUpdateDiaryEvent;
import site.isleti.triwake.model.DiaryBean;
import site.isleti.triwake.util.GetDate;
import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Flipv;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private static final String TAG = "DiaryAdapter";
    private LayoutInflater mLayoutInflater;
    private List<DiaryBean> mDiaryBeanList;
    private int mEditPosition = -1;
    private Context mContext;
    private DiaryDBHelper mHelper;

    public DiaryAdapter(Context context, List<DiaryBean> mDiaryBeanList){
        mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDiaryBeanList = mDiaryBeanList;
        mHelper = new DiaryDBHelper(mContext, "Diary.db", null, 1);
    }

    public void setDiary(List<DiaryBean> diaryBeans) {
        this.mDiaryBeanList = diaryBeans;
        notifyDataSetChanged();
    }

    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiaryViewHolder(mLayoutInflater.inflate(R.layout.diary_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final DiaryViewHolder diaryViewHolder, final int position) {

        //** 根据日期不同变换图标 ***@author：IsLeti***/
        String dateSystem = GetDate.getDate().toString();
        if(mDiaryBeanList.get(position).getDate().equals(dateSystem)){
            diaryViewHolder.mIvCircle.setImageResource(R.drawable.ic_today_white_24dp);
        }

        diaryViewHolder.mTvDate.setText(mDiaryBeanList.get(position).getDate());
        diaryViewHolder.mTvTitle.setText(mDiaryBeanList.get(position).getTitle());
        diaryViewHolder.mTvContent.setText("   " + mDiaryBeanList.get(position).getContent());

        //*** 编辑按钮 和删除按钮 预先隐藏 ***@author：IsLeti***/
        diaryViewHolder.mIvEdit.setVisibility(View.INVISIBLE);
        diaryViewHolder.mIvDelete.setVisibility(View.INVISIBLE);
        if(mEditPosition == position){
            diaryViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            diaryViewHolder.mIvDelete.setVisibility(View.VISIBLE);
        }
        else {
            diaryViewHolder.mIvEdit.setVisibility(View.GONE);
            diaryViewHolder.mIvDelete.setVisibility(View.GONE);
        }




        //触摸到指定item区域显示隐藏的图标
        diaryViewHolder.mLl.setOnClickListener(v -> {
            if(diaryViewHolder.mIvEdit.getVisibility() == View.VISIBLE){
                diaryViewHolder.mIvEdit.setVisibility(View.GONE);
            }else {
                diaryViewHolder.mIvEdit.setVisibility(View.VISIBLE);
            }
            if(diaryViewHolder.mIvDelete.getVisibility() == View.VISIBLE){
                diaryViewHolder.mIvDelete.setVisibility(View.GONE);
            }else {
                diaryViewHolder.mIvDelete.setVisibility(View.VISIBLE);
            }
            if(mEditPosition != position){
                notifyItemChanged(mEditPosition);
            }
            mEditPosition = position;
        });


        //跳转进行编辑
        diaryViewHolder.mIvEdit.setOnClickListener(v -> {
           EventBus.getDefault().post(new StartUpdateDiaryEvent(position));
        });

        //删除数据操作
        diaryViewHolder.mIvDelete.setOnClickListener(v -> {
            NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(mContext);
            dialogBuilder
              .withTitle("删除日记")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor(mContext.getResources().getColor(R.color.diary_primary))
                    .withMessage("   您确认要删除吗?")
                    .withMessageColor("#FFFFFF")
                    .withDialogColor(mContext.getResources().getColor(R.color.diary_dark))
                    .withIcon(mContext.getResources().getDrawable(R.drawable.ic_book_white_36dp))
                    .isCancelableOnTouchOutside(true)
                    .withDuration(500)
                    .withEffect(Flipv)
                    .withButton2Text("是的")
                    .withButton1Text("手滑")
                    .setButton2Click(v1 -> {
                        String tag = mDiaryBeanList.get(position).getTag();
                        mHelper.removeDiary(tag);
                        mDiaryBeanList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(v1.getContext(), "已删除", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })//删除
                    .setButton1Click(v12 -> {
                        Toast.makeText(mContext, "取消了", Toast.LENGTH_SHORT).show();
                        dialogBuilder.cancel();
                    })  //取消
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return mDiaryBeanList.size();
    }

    /*** 初始化ViewHolder ***@author：IsLeti***/
    static class DiaryViewHolder extends RecyclerView.ViewHolder{
        
        TextView mTvDate;
        TextView mTvTitle;
        ExpandableTextView mTvContent;
        LinearLayout mLlTitle;
        LinearLayout mLl;
        ImageView mIvCircle;
        LinearLayout mLlControl;
        ImageView mIvEdit;
        ImageView mIvDelete;


        DiaryViewHolder(View view){
            super(view);
            mLl = view.findViewById(R.id.item_ll);
            mIvCircle = view.findViewById(R.id.main_iv_circle);
            mTvDate = view.findViewById(R.id.main_tv_date);
            mTvTitle = view.findViewById(R.id.main_tv_title);
            mTvContent = view.findViewById(R.id.expand_text_view);
            mLlTitle = view.findViewById(R.id.main_ll_title);
            mLlControl = view.findViewById(R.id.item_ll_control);
            mIvEdit =  view.findViewById(R.id.main_iv_edit);
            mIvDelete = view.findViewById(R.id.main_iv_delete);

        }
    }
}
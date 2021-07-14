package top.yokey.shopwt.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import top.yokey.shopwt.R;
import top.yokey.shopwt.base.BaseImageLoader;
import top.yokey.base.base.BaseViewHolder;
import top.yokey.base.bean.RefundApplyBean;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 适配器
 *
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class GoodsRefundApplyListAdapter extends RecyclerView.Adapter<GoodsRefundApplyListAdapter.ViewHolder> {

    private final ArrayList<RefundApplyBean.GoodsListBean> arrayList;
    private OnItemClickListener onItemClickListener;

    public GoodsRefundApplyListAdapter(ArrayList<RefundApplyBean.GoodsListBean> arrayList) {
        this.arrayList = arrayList;
        this.onItemClickListener = null;
    }

    @Override
    public int getItemCount() {

        return arrayList.size();

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int positionInt = position;
        final RefundApplyBean.GoodsListBean bean = arrayList.get(position);

        BaseImageLoader.get().displayRadius(bean.getGoodsImg360(), holder.mainImageView);
        holder.nameTextView.setText(bean.getGoodsName());
        holder.specTextView.setText(bean.getGoodsSpec());
        holder.moneyTextView.setText("￥");
        holder.moneyTextView.append(bean.getGoodsPrice());
        holder.numberTextView.setText(bean.getGoodsNum());
        holder.numberTextView.append(" 件");

        holder.mainRelativeLayout.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(positionInt, bean);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_goods_refund_apply, group, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {

        void onClick(int position, RefundApplyBean.GoodsListBean bean);

    }

    class ViewHolder extends BaseViewHolder {

        @ViewInject(R.id.mainRelativeLayout)
        private RelativeLayout mainRelativeLayout;
        @ViewInject(R.id.mainImageView)
        private AppCompatImageView mainImageView;
        @ViewInject(R.id.nameTextView)
        private AppCompatTextView nameTextView;
        @ViewInject(R.id.specTextView)
        private AppCompatTextView specTextView;
        @ViewInject(R.id.moneyTextView)
        private AppCompatTextView moneyTextView;
        @ViewInject(R.id.numberTextView)
        private AppCompatTextView numberTextView;

        private ViewHolder(View view) {
            super(view);
        }

    }

}

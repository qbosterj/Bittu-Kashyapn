package top.yokey.shopwt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import top.yokey.base.base.BaseViewHolder;
import top.yokey.base.bean.AddressSellerBean;
import top.yokey.shopwt.R;

/**
 * 适配器
 *
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class AddressSellerListAdapter extends RecyclerView.Adapter<AddressSellerListAdapter.ViewHolder> {

    private final ArrayList<AddressSellerBean> arrayList;
    private OnItemClickListener onItemClickListener;

    public AddressSellerListAdapter(ArrayList<AddressSellerBean> arrayList) {
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
        final AddressSellerBean bean = arrayList.get(position);

        holder.nameTextView.setText(bean.getSellerName());
        holder.mobileTextView.setText(bean.getTelphone());
        holder.areaTextView.setText(bean.getAreaInfo());
        holder.areaTextView.append(" " + bean.getAddress());
        holder.defaultTextView.setVisibility(bean.getIsDefault().equals("1") ? View.VISIBLE : View.GONE);

        holder.editTextView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEdit(positionInt, bean);
            }
        });

        holder.deleteTextView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDelete(positionInt, bean);
            }
        });

        holder.mainRelativeLayout.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(positionInt, bean);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_address, group, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {

        void onClick(int position, AddressSellerBean bean);

        void onEdit(int position, AddressSellerBean bean);

        void onDelete(int position, AddressSellerBean bean);

    }

    class ViewHolder extends BaseViewHolder {

        @ViewInject(R.id.mainRelativeLayout)
        private RelativeLayout mainRelativeLayout;
        @ViewInject(R.id.nameTextView)
        private AppCompatTextView nameTextView;
        @ViewInject(R.id.mobileTextView)
        private AppCompatTextView mobileTextView;
        @ViewInject(R.id.areaTextView)
        private AppCompatTextView areaTextView;
        @ViewInject(R.id.defaultTextView)
        private AppCompatTextView defaultTextView;
        @ViewInject(R.id.deleteTextView)
        private AppCompatTextView deleteTextView;
        @ViewInject(R.id.editTextView)
        private AppCompatTextView editTextView;

        private ViewHolder(View view) {
            super(view);
        }

    }

}

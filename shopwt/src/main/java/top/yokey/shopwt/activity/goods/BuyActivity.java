package top.yokey.shopwt.activity.goods;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.scrollablelayout.ScrollableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import top.yokey.base.base.BaseDialog;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.GoodsBuyBean;
import top.yokey.base.bean.StoreBuyBean;
import top.yokey.base.model.MemberBuyModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopwt.R;
import top.yokey.shopwt.activity.choose.AddressActivity;
import top.yokey.shopwt.activity.choose.InvoiceActivity;
import top.yokey.shopwt.adapter.StoreBuyListAdapter;
import top.yokey.shopwt.base.BaseActivity;
import top.yokey.shopwt.base.BaseApplication;
import top.yokey.shopwt.base.BaseConstant;

/**
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class BuyActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private ScrollableLayout mainScrollableLayout;
    private RelativeLayout addressRelativeLayout;
    private AppCompatTextView addressNameTextView;
    private AppCompatTextView addressMobileTextView;
    private AppCompatTextView addressAreaTextView;
    private AppCompatTextView payOnlineTextView;
    private AppCompatTextView payOfflineTextView;
    private AppCompatTextView invoiceNoTextView;
    private AppCompatTextView invoiceYesTextView;
    private RecyclerView mainRecyclerView;
    private LinearLayoutCompat redPacketLinearLayout;
    private AppCompatTextView redPacketTextView;
    private AppCompatTextView moneyTextView;
    private AppCompatTextView balanceTextView;

    private long exitTimeLong;
    private String jsonString;
    private GoodsBuyBean goodsBuyBean;
    private StoreBuyListAdapter mainAdapter;
    private ArrayList<StoreBuyBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_goods_buy);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainScrollableLayout = findViewById(R.id.mainScrollableLayout);
        addressRelativeLayout = findViewById(R.id.addressRelativeLayout);
        addressNameTextView = findViewById(R.id.addressNameTextView);
        addressMobileTextView = findViewById(R.id.addressMobileTextView);
        addressAreaTextView = findViewById(R.id.addressAreaTextView);
        payOnlineTextView = findViewById(R.id.payOnlineTextView);
        payOfflineTextView = findViewById(R.id.payOfflineTextView);
        invoiceNoTextView = findViewById(R.id.invoiceNoTextView);
        invoiceYesTextView = findViewById(R.id.invoiceYesTextView);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        redPacketLinearLayout = findViewById(R.id.redPacketLinearLayout);
        redPacketTextView = findViewById(R.id.redPacketTextView);
        moneyTextView = findViewById(R.id.moneyTextView);
        balanceTextView = findViewById(R.id.balanceTextView);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;
        goodsBuyBean = new GoodsBuyBean();
        goodsBuyBean.setCartId(getIntent().getStringExtra(BaseConstant.DATA_ID));
        goodsBuyBean.setIfCart(getIntent().getStringExtra(BaseConstant.DATA_IFCART));
        goodsBuyBean.setPayName("online");

        if (TextUtils.isEmpty(goodsBuyBean.getCartId())) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        setToolbar(mainToolbar, "??????????????????");

        mainArrayList = new ArrayList<>();
        mainAdapter = new StoreBuyListAdapter(mainArrayList);
        BaseApplication.get().setRecyclerView(getActivity(), mainRecyclerView, mainAdapter);

        mainScrollableLayout.getHelper().setCurrentScrollableContainer(mainRecyclerView);

        getData();

    }

    @Override
    public void initEven() {

        addressRelativeLayout.setOnClickListener(view -> BaseApplication.get().start(getActivity(), AddressActivity.class, BaseConstant.CODE_ADDRESS));

        payOnlineTextView.setOnClickListener(view -> {
            goodsBuyBean.setPayName("online");
            payOnlineTextView.setBackgroundResource(R.drawable.selector_border_primary);
            payOfflineTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
        });

        payOfflineTextView.setOnClickListener(view -> {
            goodsBuyBean.setPayName("offline");
            payOnlineTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
            payOfflineTextView.setBackgroundResource(R.drawable.selector_border_primary);
        });

        invoiceYesTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), InvoiceActivity.class, BaseConstant.CODE_INVOICE));

        invoiceNoTextView.setOnClickListener(view -> {
            goodsBuyBean.setInvoiceId("0");
            invoiceYesTextView.setText("????????????");
            invoiceNoTextView.setBackgroundResource(R.drawable.selector_border_primary);
            invoiceYesTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
        });

        balanceTextView.setOnClickListener(view -> balance());

    }

    @Override
    public void onReturn() {

        if (System.currentTimeMillis() - exitTimeLong > BaseConstant.TIME_EXIT) {
            BaseToast.get().showReturnOneMoreTime();
            exitTimeLong = System.currentTimeMillis();
        } else {
            super.onReturn();
        }

    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK) {
            switch (req) {
                case BaseConstant.CODE_ADDRESS:
                    goodsBuyBean.setAddressId(intent.getStringExtra(BaseConstant.DATA_ID));
                    getData();
                    break;
                case BaseConstant.CODE_INVOICE:
                    goodsBuyBean.setInvoiceId(intent.getStringExtra(BaseConstant.DATA_ID));
                    invoiceNoTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
                    invoiceYesTextView.setBackgroundResource(R.drawable.selector_border_primary);
                    invoiceYesTextView.setText("???????????????");
                    invoiceYesTextView.append(intent.getStringExtra(BaseConstant.DATA_CONTENT));
                    break;
                default:
                    break;
            }
        } else {
            if (req == BaseConstant.CODE_ADDRESS) {
                if (TextUtils.isEmpty(goodsBuyBean.getAddressId())) {
                    tipsAddress();
                }
            }
        }
    }

    //???????????????

    private void getData() {

        MemberBuyModel.get().buySetp1(goodsBuyBean.getCartId(), goodsBuyBean.getIfCart(), goodsBuyBean.getAddressId(), new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                jsonString = baseBean.getDatas();
                jsonString = jsonString.replace("[]", "null");
                handlerData();
            }

            @Override
            public void onFailure(String reason) {
                BaseToast.get().show(reason);
                BaseApplication.get().finish(getActivity());
            }
        });

    }

    @SuppressWarnings("StringConcatenationInLoop")
    private void balance() {

        //????????????
        String temp = "";
        for (int i = 0; i < mainArrayList.size(); i++) {
            temp += mainArrayList.get(i).getKey() + "|" + mainArrayList.get(i).getMessage() + ",";
        }
        goodsBuyBean.setPayMessage(temp);
        //???????????????
        temp = "";
        for (int i = 0; i < mainArrayList.size(); i++) {
            if (mainArrayList.get(i).getStoreVoucherInfo() != null) {
                temp = temp + mainArrayList.get(i).getStoreVoucherInfo().getVoucherTId() + "|"
                        + mainArrayList.get(i).getStoreVoucherInfo().getVoucherStoreId() + "|"
                        + mainArrayList.get(i).getStoreVoucherInfo().getVoucherPrice() + ",";
            }
        }
        goodsBuyBean.setVoucher(temp);

        balanceTextView.setEnabled(false);
        balanceTextView.setText("?????????...");

        MemberBuyModel.get().buySetp2(goodsBuyBean, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(baseBean.getDatas());
                    if (jsonObject.getString("payment_code").equals("online")) {
                        BaseApplication.get().startOrderPay(getActivity(), jsonObject.getString("pay_sn"));
                    } else {
                        BaseToast.get().show("?????????????????????????????????...");
                    }
                    BaseApplication.get().finish(getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                    balanceTextView.setEnabled(true);
                    balanceTextView.setText("????????????");
                }
            }

            @Override
            public void onFailure(String reason) {
                balanceTextView.setEnabled(true);
                balanceTextView.setText("????????????");
                BaseToast.get().show(reason);
            }
        });

    }

    private void handlerData() {

        try {
            JSONObject jsonObject;
            Iterator<String> iterator;
            StoreBuyBean storeBuyBean;
            JSONObject mainJSONObject = new JSONObject(jsonString);
            //????????????
            jsonObject = new JSONObject(mainJSONObject.getString("store_cart_list"));
            iterator = jsonObject.keys();
            mainArrayList.clear();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = jsonObject.getString(key);
                storeBuyBean = JsonUtil.json2Bean(value, StoreBuyBean.class);
                storeBuyBean.setKey(key);
                mainArrayList.add(storeBuyBean);
            }
            //????????????
            if (mainJSONObject.getString("address_info").equals("null")) {
                tipsAddress();
                return;
            }
            jsonObject = new JSONObject(mainJSONObject.getString("address_info"));
            goodsBuyBean.setAddressId(jsonObject.getString("address_id"));
            addressNameTextView.setText(jsonObject.getString("true_name"));
            addressMobileTextView.setText(jsonObject.getString("mob_phone"));
            addressAreaTextView.setText(jsonObject.getString("area_info"));
            addressAreaTextView.append(" " + jsonObject.getString("address"));
            jsonObject = mainJSONObject.getJSONObject("address_api");
            goodsBuyBean.setOffPayHash(jsonObject.getString("offpay_hash"));
            goodsBuyBean.setOffPayHashBatch(jsonObject.getString("offpay_hash_batch"));
            //????????????
            if (jsonObject.getString("allow_offpay").equals("1")) {
                payOfflineTextView.setVisibility(View.VISIBLE);
            } else {
                payOfflineTextView.setVisibility(View.GONE);
            }
            //???????????????
            jsonObject = new JSONObject(jsonObject.getString("content"));
            iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = jsonObject.getString(key);
                for (int i = 0; i < mainArrayList.size(); i++) {
                    if (mainArrayList.get(i).getKey().equals(key)) {
                        mainArrayList.get(i).setLogisticsMoney(value);
                    }
                }
            }
            //????????????
            jsonObject = new JSONObject(mainJSONObject.getString("store_final_total_list"));
            iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = jsonObject.getString(key);
                for (int i = 0; i < mainArrayList.size(); i++) {
                    if (mainArrayList.get(i).getKey().equals(key)) {
                        mainArrayList.get(i).setTotalMoney(value);
                    }
                }
            }
            //????????????
            if (TextUtils.isEmpty(mainJSONObject.getString("rpt_info")) || mainJSONObject.getString("rpt_info").equals("null")) {
                redPacketLinearLayout.setVisibility(View.GONE);
            } else {
                redPacketLinearLayout.setVisibility(View.VISIBLE);
                jsonObject = new JSONObject(mainJSONObject.getString("rpt_info"));
                goodsBuyBean.setRpt(jsonObject.getString("rpacket_id") + "|" + jsonObject.getString("rpacket_price"));
                redPacketTextView.setText("??????");
                redPacketTextView.append(jsonObject.getString("rpacket_limit"));
                redPacketTextView.append("???????????????");
                redPacketTextView.append(jsonObject.getString("rpacket_price"));
                redPacketTextView.append("???");
            }
            //??????????????????
            goodsBuyBean.setVatHash(mainJSONObject.getString("vat_hash"));
            //????????????
            String temp = "???????????????" + "<font color='#FF0000'>???" + mainJSONObject.getString("order_amount") + " ???</font>";
            moneyTextView.setText(Html.fromHtml(temp));
            //????????????
            mainAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void tipsAddress() {

        BaseDialog.get().query(getActivity(), "???????????????", "????????????????????????", (dialog, which) -> BaseApplication.get().start(getActivity(), AddressActivity.class, BaseConstant.CODE_ADDRESS), (dialog, which) -> BaseApplication.get().finish(getActivity()));

    }

}

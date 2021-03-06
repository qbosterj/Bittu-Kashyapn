package top.yokey.shopwt.activity.mine;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import top.yokey.base.base.BaseCountTime;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberAccountModel;
import top.yokey.base.model.SeccodeModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopwt.R;
import top.yokey.shopwt.base.BaseActivity;
import top.yokey.shopwt.base.BaseApplication;
import top.yokey.shopwt.base.BaseConstant;

/**
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class ModifyMobileActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private LinearLayoutCompat mobileLinearLayout;
    private AppCompatTextView mobileTextView;
    private AppCompatEditText codeEditText;
    private AppCompatTextView getTextView;
    private AppCompatTextView submitTextView;
    private AppCompatTextView payPassTextView;
    private LinearLayoutCompat payPassLinearLayout;
    private AppCompatEditText payPassEditText;
    private AppCompatTextView confirmTextView;

    private String codeKeyString;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_modify_mobile);
        mainToolbar = findViewById(R.id.mainToolbar);
        mobileLinearLayout = findViewById(R.id.mobileLinearLayout);
        mobileTextView = findViewById(R.id.mobileTextView);
        codeEditText = findViewById(R.id.codeEditText);
        getTextView = findViewById(R.id.getTextView);
        submitTextView = findViewById(R.id.submitTextView);
        payPassTextView = findViewById(R.id.payPassTextView);
        payPassLinearLayout = findViewById(R.id.payPassLinearLayout);
        payPassEditText = findViewById(R.id.payPassEditText);
        confirmTextView = findViewById(R.id.confirmTextView);

    }

    @Override
    public void initData() {

        mobileTextView.setText("????????????????????????");
        mobileTextView.append(BaseApplication.get().getMemberBean().getUserMobile());

        setToolbar(mainToolbar, "??????????????????");

        codeKeyString = "";
        checkPayPass();
        makeCodeKey();

    }

    @Override
    public void initEven() {

        getTextView.setOnClickListener(view -> modifyMobileSetup2());

        submitTextView.setOnClickListener(view -> modifyMobileSetup3());

        payPassTextView.setOnClickListener(view -> {
            mobileLinearLayout.setVisibility(View.GONE);
            payPassLinearLayout.setVisibility(View.VISIBLE);
        });

        confirmTextView.setOnClickListener(view -> checkPayPassword());

    }

    @Override
    public void onReturn() {
        if (payPassLinearLayout.getVisibility() == View.VISIBLE) {
            payPassLinearLayout.setVisibility(View.GONE);
            mobileLinearLayout.setVisibility(View.VISIBLE);
        } else {
            super.onReturn();
        }
    }

    //???????????????

    private void makeCodeKey() {

        SeccodeModel.get().makeCodeKey(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                codeKeyString = JsonUtil.getDatasString(baseBean.getDatas(), "codekey");
            }

            @Override
            public void onFailure(String reason) {
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        makeCodeKey();
                    }
                }.start();
            }
        });

    }

    private void checkPayPass() {

        MemberAccountModel.get().getPayPwdInfo(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                boolean state = JsonUtil.getDatasBoolean(baseBean.getDatas(), "state");
                if (state) {
                    payPassTextView.setVisibility(View.VISIBLE);
                } else {
                    payPassTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseToast.get().show(reason);
            }
        });

    }

    private void checkPayPassword() {

        BaseApplication.get().hideKeyboard(getActivity());

        String password = Objects.requireNonNull(payPassEditText.getText()).toString();

        if (TextUtils.isEmpty(password)) {
            BaseToast.get().show("?????????????????????");
            return;
        }

        confirmTextView.setEnabled(false);
        confirmTextView.setText("?????????...");

        MemberAccountModel.get().checkPayPwd(password, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                confirmTextView.setText("??? ???");
                confirmTextView.setEnabled(false);
                BaseApplication.get().getMemberBean().setMobielState(false);
                BaseApplication.get().getMemberBean().setUserMobile("");
                BaseApplication.get().start(getActivity(), BindMobileActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                confirmTextView.setEnabled(false);
                confirmTextView.setText("??? ???");
            }
        });

    }

    private void modifyMobileSetup2() {

        BaseApplication.get().hideKeyboard(getActivity());

        getTextView.setEnabled(false);
        getTextView.setText("?????????...");

        MemberAccountModel.get().modifyMobileStep2(codeKeyString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                final String smsTime = JsonUtil.getDatasString(baseBean.getDatas(), "sms_time");
                final int time = Integer.parseInt(smsTime);
                //?????????
                new BaseCountTime(time * 1000, BaseConstant.TIME_TICK) {

                    int totalTime = time;

                    @Override
                    public void onTick(long millis) {
                        super.onTick(millis);
                        String temp = "???????????????" + totalTime-- + " S ???";
                        getTextView.setText(temp);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getTextView.setEnabled(true);
                        getTextView.setText("???????????????");
                    }

                }.start();

            }

            @Override
            public void onFailure(String reason) {
                getTextView.setEnabled(true);
                getTextView.setText("???????????????");
                BaseToast.get().show(reason);
            }
        });

    }

    private void modifyMobileSetup3() {

        BaseApplication.get().hideKeyboard(getActivity());

        String code = Objects.requireNonNull(codeEditText.getText()).toString();

        if (TextUtils.isEmpty(code)) {
            BaseToast.get().show("?????????????????????");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        MemberAccountModel.get().modifyMobileStep3(code, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseApplication.get().getMemberBean().setMobielState(false);
                    BaseApplication.get().getMemberBean().setUserMobile("");
                    BaseApplication.get().start(getActivity(), BindMobileActivity.class);
                    BaseApplication.get().finish(getActivity());
                } else {
                    submitTextView.setEnabled(true);
                    submitTextView.setText("??? ???");
                    BaseToast.get().showFailure();
                }
            }

            @Override
            public void onFailure(String reason) {
                submitTextView.setEnabled(true);
                submitTextView.setText("??? ???");
                BaseToast.get().show(reason);
            }
        });

    }

}

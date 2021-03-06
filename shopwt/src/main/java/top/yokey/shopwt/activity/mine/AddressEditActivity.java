package top.yokey.shopwt.activity.mine;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.AddressBean;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberAddressModel;
import top.yokey.base.util.TextUtil;
import top.yokey.shopwt.R;
import top.yokey.shopwt.activity.choose.AreaActivity;
import top.yokey.shopwt.base.BaseActivity;
import top.yokey.shopwt.base.BaseApplication;
import top.yokey.shopwt.base.BaseConstant;

/**
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class AddressEditActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText nameEditText;
    private AppCompatEditText mobileEditText;
    private AppCompatEditText areaEditText;
    private AppCompatEditText addressEditText;
    private SwitchCompat defaultSwitch;
    private AppCompatTextView saveTextView;

    private String cityId;
    private String areaId;
    private String areaInfo;
    private AddressBean addressBean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_address_edit);
        mainToolbar = findViewById(R.id.mainToolbar);
        nameEditText = findViewById(R.id.nameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        areaEditText = findViewById(R.id.areaEditText);
        addressEditText = findViewById(R.id.addressEditText);
        defaultSwitch = findViewById(R.id.defaultSwitch);
        saveTextView = findViewById(R.id.saveTextView);

    }

    @Override
    public void initData() {

        addressBean = (AddressBean) getIntent().getSerializableExtra(BaseConstant.DATA_BEAN);

        if (addressBean == null) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        setToolbar(mainToolbar, "????????????");

        cityId = addressBean.getCityId();
        areaId = addressBean.getAreaId();
        areaInfo = addressBean.getAreaInfo();

        nameEditText.setText(addressBean.getTrueName());
        mobileEditText.setText(addressBean.getMobPhone());
        areaEditText.setText(addressBean.getAreaInfo());
        addressEditText.setText(addressBean.getAddress());
        defaultSwitch.setChecked(addressBean.getIsDefault().equals("1"));
        nameEditText.setSelection(addressBean.getTrueName().length());

    }

    @Override
    public void initEven() {

        areaEditText.setOnClickListener(view -> BaseApplication.get().start(getActivity(), AreaActivity.class, BaseConstant.CODE_AREA));

        saveTextView.setOnClickListener(view -> editAddress());

    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK) {
            if (req == BaseConstant.CODE_AREA) {
                cityId = intent.getStringExtra("city_id");
                areaId = intent.getStringExtra("area_id");
                areaInfo = intent.getStringExtra("area_info");
                areaEditText.setText(areaInfo);
            }
        }
    }

    //???????????????

    private void editAddress() {

        BaseApplication.get().hideKeyboard(getActivity());

        String name = Objects.requireNonNull(nameEditText.getText()).toString();
        String phone = Objects.requireNonNull(mobileEditText.getText()).toString();
        String address = Objects.requireNonNull(addressEditText.getText()).toString();
        String isDefault = defaultSwitch.isChecked() ? "1" : "0";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            BaseToast.get().show("???????????????????????????");
            return;
        }

        if (!TextUtil.isMobile(phone)) {
            BaseToast.get().show("??????????????????????????????");
            return;
        }

        if (TextUtils.isEmpty(cityId)) {
            BaseToast.get().show("??????????????????");
            return;
        }

        saveTextView.setEnabled(false);
        saveTextView.setText("?????????...");

        MemberAddressModel.get().addressEdit(addressBean.getAddressId(), name, phone, cityId, areaId, address, areaInfo, isDefault, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                saveTextView.setEnabled(true);
                saveTextView.setText("????????????");
                BaseToast.get().showSuccess();
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                saveTextView.setEnabled(true);
                saveTextView.setText("????????????");
            }
        });

    }

}

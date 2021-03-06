package top.yokey.shopwt.activity.mine;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;
import java.util.Vector;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberInvoiceModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopwt.R;
import top.yokey.shopwt.base.BaseActivity;
import top.yokey.shopwt.base.BaseApplication;

/**
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class InvoiceAddActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView personTextView;
    private AppCompatTextView companyTextView;
    private LinearLayoutCompat headerLinearLayout;
    private AppCompatEditText headerEditText;
    private AppCompatSpinner titleSpinner;
    private AppCompatTextView saveTextView;

    private String selectString;
    private String titleString;
    private String contentString;
    private Vector<String> mainVector;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_invoice_add);
        mainToolbar = findViewById(R.id.mainToolbar);
        personTextView = findViewById(R.id.personTextView);
        companyTextView = findViewById(R.id.companyTextView);
        headerLinearLayout = findViewById(R.id.headerLinearLayout);
        headerEditText = findViewById(R.id.headerEditText);
        titleSpinner = findViewById(R.id.titleSpinner);
        saveTextView = findViewById(R.id.saveTextView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "????????????");

        mainVector = new Vector<>();
        selectString = "person";
        titleString = "??????";
        contentString = "";

        getInvoiceContent();

    }

    @Override
    public void initEven() {

        personTextView.setOnClickListener(view -> {
            selectString = "person";
            headerLinearLayout.setVisibility(View.GONE);
            personTextView.setBackgroundResource(R.drawable.selector_border_primary);
            companyTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
        });

        companyTextView.setOnClickListener(view -> {
            selectString = "company";
            headerLinearLayout.setVisibility(View.VISIBLE);
            personTextView.setBackgroundResource(R.drawable.selector_border_grey_add);
            companyTextView.setBackgroundResource(R.drawable.selector_border_primary);
        });

        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                contentString = mainVector.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveTextView.setOnClickListener(view -> addInvoice());

    }

    //???????????????

    private void addInvoice() {

        if (selectString.equals("person")) {
            titleString = "??????";
        } else {
            titleString = Objects.requireNonNull(headerEditText.getText()).toString();
        }

        if (TextUtils.isEmpty(selectString) || TextUtils.isEmpty(titleString) || TextUtils.isEmpty(contentString)) {
            BaseToast.get().show("????????????????????????");
            return;
        }

        saveTextView.setEnabled(false);
        saveTextView.setText("?????????...");

        MemberInvoiceModel.get().invoiceAdd(selectString, titleString, contentString, new BaseHttpListener() {
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

    private void getInvoiceContent() {

        MemberInvoiceModel.get().invoiceContentList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "invoice_content_list");
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mainVector.add(jsonArray.getString(i));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, mainVector);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    titleSpinner.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseToast.get().show(reason);
            }
        });

    }

}

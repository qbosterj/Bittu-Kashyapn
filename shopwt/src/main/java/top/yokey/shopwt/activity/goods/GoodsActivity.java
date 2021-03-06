package top.yokey.shopwt.activity.goods;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseCountTime;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.event.GoodsAreaEvent;
import top.yokey.base.event.GoodsBeanEvent;
import top.yokey.base.event.GoodsEvaluateEvent;
import top.yokey.base.event.GoodsGoneEvent;
import top.yokey.base.event.GoodsIdEvent;
import top.yokey.base.event.GoodsShowEvent;
import top.yokey.base.model.GoodsModel;
import top.yokey.shopwt.R;
import top.yokey.shopwt.activity.main.MainActivity;
import top.yokey.shopwt.adapter.BaseFragmentAdapter;
import top.yokey.shopwt.base.BaseActivity;
import top.yokey.shopwt.base.BaseApplication;
import top.yokey.shopwt.base.BaseBusClient;
import top.yokey.shopwt.base.BaseConstant;

/**
 * @author MapleStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/Shopwt-Android
 */

public class GoodsActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private ViewPager mainViewPager;
    private AppCompatTextView[] navigationTextView;

    private String goodsIdString;
    private boolean isShowBoolean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_goods_goods);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainViewPager = findViewById(R.id.mainViewPager);
        AppCompatTextView goodsTextView = findViewById(R.id.goodsTextView);
        AppCompatTextView detailedTextView = findViewById(R.id.detailedTextView);
        AppCompatTextView evaluateTextView = findViewById(R.id.evaluateTextView);
        navigationTextView = new AppCompatTextView[3];
        navigationTextView[0] = goodsTextView;
        navigationTextView[1] = detailedTextView;
        navigationTextView[2] = evaluateTextView;

    }

    @Override
    public void initData() {

        goodsIdString = getIntent().getStringExtra(BaseConstant.DATA_ID);

        if (TextUtils.isEmpty(goodsIdString)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new GoodsFragment());
        fragmentList.add(new DetailedFragment());
        fragmentList.add(new EvaluateFragment());

        mainViewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        mainViewPager.setOffscreenPageLimit(navigationTextView.length);
        isShowBoolean = false;

        setToolbar(mainToolbar, "");
        updateNavigation(0);
        getGoodsInfo();

    }

    @Override
    public void initEven() {

        for (int i = 0; i < navigationTextView.length; i++) {
            final int position = i;
            navigationTextView[i].setOnClickListener(view -> updateNavigation(position));
        }

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateNavigation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onReturn() {

        if (mainViewPager.getCurrentItem() != 0) {
            mainViewPager.setCurrentItem(0);
            return;
        }

        if (isShowBoolean) {
            BaseBusClient.get().post(new GoodsGoneEvent(true));
            return;
        }

        if (BaseApplication.get().inActivityStackTop() && !BaseApplication.get().inActivityStack(MainActivity.class)) {
            BaseApplication.get().start(getActivity(), MainActivity.class);
        }

        super.onReturn();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            onReturn();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK) {
            if (req == BaseConstant.CODE_AREA) {
                final GoodsAreaEvent goodsAreaEvent = new GoodsAreaEvent(intent.getStringExtra("area_id"), intent.getStringExtra("area_info"));
                new BaseCountTime(500, 250) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        BaseBusClient.get().post(goodsAreaEvent);
                    }
                }.start();
            }
        }
    }

    //???????????????

    private void getGoodsInfo() {

        GoodsModel.get().goodsDetailed(goodsIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseBusClient.get().post(new GoodsBeanEvent(baseBean));
            }

            @Override
            public void onFailure(String reason) {
                if (reason.equals("???????????????")) {
                    BaseToast.get().show(reason);
                    BaseApplication.get().finish(getActivity());
                } else {
                    BaseToast.get().show(reason);
                }
            }
        });

    }

    private void updateNavigation(int position) {

        for (AppCompatTextView appCompatTextView : navigationTextView) {
            appCompatTextView.setTextColor(BaseApplication.get().getColors(R.color.grey));
        }

        navigationTextView[position].setTextColor(BaseApplication.get().getColors(R.color.primary));
        mainViewPager.setCurrentItem(position);

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGoodsIdEvent(GoodsIdEvent event) {

        goodsIdString = event.getGoodsId();
        getGoodsInfo();

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGoodsShowEvent(GoodsShowEvent event) {

        isShowBoolean = event.isShow();

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onGoodsEvaluateEvent(GoodsEvaluateEvent event) {

        mainViewPager.setCurrentItem(2);

    }

}

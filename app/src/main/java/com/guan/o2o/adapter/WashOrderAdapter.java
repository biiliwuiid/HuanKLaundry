package com.guan.o2o.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.guan.o2o.R;
import com.guan.o2o.activity.PayActivity;
import com.guan.o2o.activity.UserInfoActivity;
import com.guan.o2o.application.App;
import com.guan.o2o.common.Constant;
import com.guan.o2o.model.WashOrder;
import com.loopj.android.image.SmartImageView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 洗衣篮Adapter
 *
 * @author Guan
 * @file com.guan.o2o.adapter
 * @date 2015/9/21
 * @Version 1.0
 */
public class WashOrderAdapter extends BaseToAdapter<WashOrder> {

    private int mCurrentType;
    private List<WashOrder> mList;
    private ImageView mIvBasketNull;
    private ImageView mIvHave;
    private final int ITEM_NORMAL = 0;
    private final int ITEM_FIRST = 1;
    private final int ITEM_SECOND = 2;
    private final int ITEM_THIRD = 3;
    private final int ITEM_NUM = 4;

    private static FragmentActivity sContext;

    public WashOrderAdapter(FragmentActivity context, List<WashOrder> list,
                            ImageView ivBasketNull, ImageView ivHave) {
        super(context, list);
        sContext = context;
        mList = list;
        mIvBasketNull = ivBasketNull;
        mIvHave = ivHave;
    }

    /**
     * 复写父类方法
     *
     * @return
     */
    @Override
    public int getCount() {
        if (mList.size() == 0) {
            mIvBasketNull.setVisibility(View.VISIBLE);
            mIvHave.setVisibility(View.INVISIBLE);
            return 0;
        } else
            return mList.size() + 3;
    }

    /**
     * 根据position判断item是普通项还是添加项
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int count = mList.size() + 2;
        if (position < count - 2) {
            return ITEM_NORMAL;
        } else if (position == count - 2) {
            return ITEM_FIRST;
        } else if (position == count - 1) {
            return ITEM_SECOND;
        } else {
            return ITEM_THIRD;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_NUM;
    }

    /**
     * 刷新界面
     */
    public void refreshUI() {
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View normalView;
        View firstView;
        View secondView;
        View secondSView;
        View thirdView;
        mCurrentType = getItemViewType(position);

        if (mCurrentType == ITEM_NORMAL) {
            // 普通项
            normalView = convertView;
            final NormalHolder normalHolder;
            if (normalView != null) {
                normalHolder = (NormalHolder) normalView.getTag();
            } else {
                normalView = LayoutInflater.from(sContext).inflate(R.layout.item_basket_normal, null);
                normalHolder = new NormalHolder(normalView);
                normalView.setTag(normalHolder);
            }
            convertView = normalView;

            final WashOrder washOrder = (WashOrder) getItem(position);
            normalHolder.sivCloth.setImageUrl(washOrder.getWashHead(), R.mipmap.ic_pop_bag, R.mipmap.ic_default);
            normalHolder.mNum = washOrder.getWashNum();
            normalHolder.tvNum.setText(String.valueOf(normalHolder.mNum));
            normalHolder.tvCategory.setText(washOrder.getWashCategory());
            final int price = Integer.valueOf(washOrder.getWashPrice().substring(1));
            // 单项总价格
            normalHolder.tvSinglePrice.setText("¥" + price * normalHolder.mNum);

            normalHolder.rbMin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (normalHolder.mNum > 1) {
                        normalHolder.mNum = normalHolder.mNum - 1;
                        normalHolder.tvNum.setText(String.valueOf(normalHolder.mNum));
                        washOrder.setWashNum(normalHolder.mNum);
                        // 刷新界面
                        refreshUI();
                    } else {
                        normalHolder.tvNum.setText(String.valueOf(normalHolder.mNum));
                        washOrder.setWashNum(normalHolder.mNum);
                    }
                    // 单项总价格
                    normalHolder.tvSinglePrice.setText("¥" + price * normalHolder.mNum);
                }
            });
            normalHolder.rbAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    normalHolder.mNum = normalHolder.mNum + 1;
                    normalHolder.tvNum.setText(String.valueOf(normalHolder.mNum));
                    washOrder.setWashNum(normalHolder.mNum);
                    // 刷新界面
                    refreshUI();
                    // 单项总价格
                    normalHolder.tvSinglePrice.setText("¥" + price * normalHolder.mNum);
                }
            });
            normalHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.washOrderList.remove(position);
                    // 刷新界面
                    refreshUI();
                }
            });

        } else if (mCurrentType == ITEM_FIRST) {
            // 添加的第一项
            firstView = convertView;
            final FirstHolder firstHolder;
            if (firstView != null) {
                firstHolder = (FirstHolder) firstView.getTag();
            } else {
                firstView = LayoutInflater.from(sContext).inflate(R.layout.item_basket_first, null);
                firstHolder = new FirstHolder(firstView);
                firstView.setTag(firstHolder);
            }
            convertView = firstView;

            long sTotal = 0;
            // 遍历全部订单
            for (int i = 0; i < getList().size(); i++) {
                WashOrder washOrder = (WashOrder) getItem(i);
                sTotal += washOrder.getWashNum() * Integer.valueOf(washOrder.getWashPrice().substring(1));
            }
            // 洗衣券默认30元（开发：根据服务器传来的数据设置）
            int coupon = 30;
            firstHolder.tvCost.setText("¥" + sTotal);
            firstHolder.tvTheCost.setText("¥" + (sTotal - coupon));

        } else if (mCurrentType == ITEM_SECOND) {
            // 添加的第二项
            boolean isShared = false;
            // 读取SHARED_NAME_USERINFO中的数据
            SharedPreferences preferences_user = sContext.getSharedPreferences(
                    Constant.SHARED_NAME_USERINFO, Context.MODE_PRIVATE);
            // 读取是否存入用户信息到本地，判断显示哪一种布局
            isShared = preferences_user.getBoolean(Constant.SHARED_KEY_FLAG, false);

            if (!isShared) {
                secondView = convertView;
                final SecondHolder secondHolder;
                if (secondView != null) {
                    secondHolder = (SecondHolder) secondView.getTag();
                } else {
                    secondView = LayoutInflater.from(sContext).inflate(R.layout.item_basket_second, null);
                    secondHolder = new SecondHolder(secondView);
                    secondView.setTag(secondHolder);
                }
                convertView = secondView;
            } else {
                secondSView = convertView;
                final SecondSHolder secondSHolder;
                if (secondSView != null) {
                    secondSHolder = (SecondSHolder) secondSView.getTag();
                } else {
                    secondSView = LayoutInflater.from(sContext).inflate(R.layout.item_basket_seconds, null);
                    secondSHolder = new SecondSHolder(secondSView);
                    secondSView.setTag(secondSHolder);
                }
                convertView = secondSView;

                String userName = preferences_user.getString(Constant.SHARED_KEY_NAME, "");
                String userPhone = preferences_user.getString(Constant.SHARED_KEY_USERPHONE, "");
                String userArea = preferences_user.getString(Constant.SHARED_KEY_AREA, "");
                String userCommu = preferences_user.getString(Constant.SHARED_KEY_COMMU, "");
                String userDetailAddr = preferences_user.getString(Constant.SHARED_KEY_DETAIL_ADDR, "");
                secondSHolder.tvName.setText(userName);
                secondSHolder.tvPhone.setText(userPhone);
                secondSHolder.tvAddress.setText(userArea + userCommu + userDetailAddr);
            }

        } else if (mCurrentType == ITEM_THIRD) {
            // 添加的第三项
            thirdView = convertView;
            final ThirdHolder thirdHolder;
            if (thirdView != null) {
                thirdHolder = (ThirdHolder) thirdView.getTag();
            } else {
                thirdView = LayoutInflater.from(sContext).inflate(R.layout.item_basket_third, null);
                thirdHolder = new ThirdHolder(thirdView);
                thirdView.setTag(thirdHolder);
            }
            convertView = thirdView;
        }

        return convertView;
    }

    /**
     * 普通项
     */
    static class NormalHolder {
        @InjectView(R.id.siv_cloth)
        SmartImageView sivCloth;
        @InjectView(R.id.tv_category)
        TextView tvCategory;
        @InjectView(R.id.rb_min)
        RadioButton rbMin;
        @InjectView(R.id.tv_num)
        TextView tvNum;
        @InjectView(R.id.rb_add)
        RadioButton rbAdd;
        @InjectView(R.id.tv_single_price)
        TextView tvSinglePrice;
        @InjectView(R.id.iv_delete)
        ImageView ivDelete;
        int mNum;

        NormalHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * 添加的第一项
     */
    static class FirstHolder {
        @InjectView(R.id.tv_cost)
        TextView tvCost;
        @InjectView(R.id.tv_the_cost)
        TextView tvTheCost;
        @InjectView(R.id.iv_laundry_coupon)
        ImageView ivLaundryCoupon;

        FirstHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * 添加的第二项
     */
    static class SecondHolder {
        SecondHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.rlyt_write_info)
        public void onClick(View view) {
            Intent intent = new Intent(sContext, UserInfoActivity.class);
            sContext.startActivity(intent);
        }
    }

    /**
     * 添加的第二项,另一种布局
     */
    static class SecondSHolder {
        @InjectView(R.id.tv_phone)
        TextView tvPhone;
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.tv_address)
        TextView tvAddress;

        SecondSHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.rlyt_my_info)
        public void onClick(View view) {
            Intent intent = new Intent(sContext, UserInfoActivity.class);
            sContext.startActivity(intent);
        }
    }

    /**
     * 添加的第三项
     */
    static class ThirdHolder {
        @InjectView(R.id.btn_pay)
        Button btnPay;

        ThirdHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @OnClick(R.id.btn_pay)
        public void onClick(View view) {
            Intent intent = new Intent(sContext, PayActivity.class);
            sContext.startActivity(intent);
        }
    }
}

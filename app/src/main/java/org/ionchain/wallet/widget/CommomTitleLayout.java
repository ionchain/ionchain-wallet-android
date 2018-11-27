package org.ionchain.wallet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ionchain.wallet.R;


/**
 * Created by binny on 2018/11/26.
 * 通用标题栏
 */
public class CommomTitleLayout extends LinearLayout {

    private Boolean isLeftImgVisible;//左侧按钮是否可见
    private int leftResId;//左侧资源id

    private Boolean isLeftTvVisible;//左侧文字是否可见
    private String leftTvText;//左侧文字

    private Boolean isRightImgVisible;//右侧按钮是否可见
    private int rightResId;//右侧资源ID

    private Boolean isRightTvVisible;//右侧文字是否可见
    private String rightTvText;//右侧文字

    private Boolean isTitleVisible;//中间标题是否可见
    private String titleText;//中间标题

    private int backgroundResId;//标题整体背景
    private RelativeLayout cus_bg_toolbar;


    public RelativeLayout getCus_bg_toolbar() {
        return cus_bg_toolbar;
    }

    public CommomTitleLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CommomTitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CommomTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolBar);

        /**-------------获取左边按钮属性------------*/
        isLeftImgVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_img_visible, true);
        leftResId = typedArray.getResourceId(R.styleable.CustomToolBar_left_img_src, -1);

        /**-------------获取左边文本属性------------*/
        isLeftTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_left_tv_visible, false);
        if (typedArray.hasValue(R.styleable.CustomToolBar_left_tv_text)) {
            leftTvText = typedArray.getString(R.styleable.CustomToolBar_left_tv_text);
        }
        /**-------------获取右边按钮属性------------*/
        isRightImgVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_img_visible, false);
        rightResId = typedArray.getResourceId(R.styleable.CustomToolBar_right_img_src, -1);
        /**-------------获取右边文本属性------------*/
        isRightTvVisible = typedArray.getBoolean(R.styleable.CustomToolBar_right_tv_visible, false);
        if (typedArray.hasValue(R.styleable.CustomToolBar_right_tv_text)) {
            rightTvText = typedArray.getString(R.styleable.CustomToolBar_right_tv_text);
        }
        /**-------------获取标题属性------------*/
        isTitleVisible = typedArray.getBoolean(R.styleable.CustomToolBar_title_visible, true);
        if (typedArray.hasValue(R.styleable.CustomToolBar_title_text)) {
            titleText = typedArray.getString(R.styleable.CustomToolBar_title_text);
        }
        /**-------------背景颜色------------*/
        backgroundResId = typedArray.getResourceId(R.styleable.CustomToolBar_barBackground, -1);

        typedArray.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.common_title_view_layout, null);

        ImageView left_img = view.findViewById(R.id.left_img);
        ImageView right_img = view.findViewById(R.id.right_img);
        TextView left_tx = view.findViewById(R.id.left_text);
        TextView right_tx = view.findViewById(R.id.right_text);
        TextView title = view.findViewById(R.id.custom_title);
        cus_bg_toolbar = view.findViewById(R.id.cus_title_bg_rv);
        if (!isLeftImgVisible) {
            left_img.setVisibility(GONE);
        }
        if (!isLeftTvVisible) {
            left_tx.setVisibility(GONE);
        }
        if (!isRightImgVisible) {
            right_img.setVisibility(GONE);
        }
        if (!isRightTvVisible) {
            right_tx.setVisibility(GONE);
        }
        if (!isTitleVisible) {
            title.setVisibility(GONE);
        }

        left_tx.setText(leftTvText);
        right_tx.setText(rightTvText);
        title.setText(titleText);


        if(leftResId != -1){
            left_img.setBackgroundResource(leftResId);
        }
        if(rightResId != -1){
            right_img.setBackgroundResource(rightResId);
        }
        if(backgroundResId != -1){
            cus_bg_toolbar.setBackgroundColor(getResources().getColor(backgroundResId));
        }
        //将设置完成之后的View添加到此LinearLayout中
        addView(view, 0);

    }

}

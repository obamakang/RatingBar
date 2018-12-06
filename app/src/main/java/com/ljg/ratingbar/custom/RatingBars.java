package com.ljg.ratingbar.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ljg.ratingbar.customratingbar.R;

import java.math.BigDecimal;

public class RatingBars extends LinearLayout {

    /**
     * 是否可点击
     */
    private boolean mClickable;
    /**
     * 星星总数
     */
    private int starCount;
    /**
     * 星星的点击事件
     */
    private OnRatingChangeListener onRatingChangeListener;
    /**
     * 每个星星的大小
     */
    private float starImageSize;
    /**
     * 每个星星的间距
     */
    private float starPadding;
    /**
     * 星星的显示数量，支持小数点
     */
    private float starStep;
    /**
     * 空白的默认星星图片
     */
    private Drawable starEmptyDrawable;
    /**
     * 选中后的星星填充图片
     */
    private Drawable starFillDrawable;
    /**
     * 半颗星的图片
     */
    private Drawable starHalfDrawable;
    /**
     * 每次点击星星所增加的量是整个还是半个
     */
    private StepSize stepSize;

    /**
     * 设置半星的图片资源文件
     */
    public void setStarHalfDrawable(Drawable starHalfDrawable) {
        this.starHalfDrawable = starHalfDrawable;
    }

    /**
     * 设置满星的图片资源文件
     */
    public void setStarFillDrawable(Drawable starFillDrawable) {
        this.starFillDrawable = starFillDrawable;
    }

    /**
     * 设置空白和默认的图片资源文件
     */
    public void setStarEmptyDrawable(Drawable starEmptyDrawable) {
        this.starEmptyDrawable = starEmptyDrawable;
    }

    /**
     * 设置星星是否可以点击操作
     */
    public void setClickable(boolean clickable) {
        this.mClickable = clickable;
    }

    /**
     * 设置星星点击事件
     */
    public void setOnRatingChangeListener(OnRatingChangeListener onRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener;
    }

    /**
     * 设置星星的大小
     */
    public void setStarImageSize(float starImageSize) {
        this.starImageSize = starImageSize;
    }

    public void setStepSize(StepSize stepSize) {
        this.stepSize = stepSize;
    }


    /**
     * 构造函数
     * 获取xml中设置的资源文件
     */
    public RatingBars(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBars);
        starImageSize = mTypedArray.getDimension(R.styleable.RatingBars_starImageSize, 20);
        starPadding = mTypedArray.getDimension(R.styleable.RatingBars_starPadding, 10);
        starStep = mTypedArray.getFloat(R.styleable.RatingBars_starStep, 1.0f);
        stepSize = StepSize.fromStep(mTypedArray.getInt(R.styleable.RatingBars_stepSize, 1));
        starCount = mTypedArray.getInteger(R.styleable.RatingBars_starCount, 5);
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.RatingBars_starEmpty);
        starFillDrawable = mTypedArray.getDrawable(R.styleable.RatingBars_starFill);
        starHalfDrawable = mTypedArray.getDrawable(R.styleable.RatingBars_starHalf);
        mClickable = mTypedArray.getBoolean(R.styleable.RatingBars_clickable, true);
        mTypedArray.recycle();
        for (int i = 0; i < starCount; ++i) {
            final ImageView imageView = getStarImageView();
            //imageView.setImageDrawable(starEmptyDrawable);
            imageView.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mClickable) {
                                //浮点数的整数部分
                                int fint = (int) starStep;
                                BigDecimal b1 = new BigDecimal(Float.toString(starStep));
                                BigDecimal b2 = new BigDecimal(Integer.toString(fint));
                                //浮点数的小数部分
                                float fPoint = b1.subtract(b2).floatValue();
                                if (fPoint == 0) {
                                    fint -= 1;
                                }
                                int index = indexOfChild(v);
                                if (indexOfChild(v) > fint) {
                                    setStar(indexOfChild(v) + 1);
                                } else if (indexOfChild(v) == fint) {
                                    if (stepSize == StepSize.Full) {//如果是满星 就不考虑半颗星了
                                        if(imageView.getDrawable().getCurrent().getConstantState().equals(starFillDrawable.getConstantState())) {
                                            setStar(indexOfChild(v));
                                        }else if(imageView.getDrawable().getCurrent().getConstantState().equals(starEmptyDrawable.getConstantState())) {
                                            setStar(indexOfChild(v) + 1);
                                        }

                                        return;
                                    }
                                    //点击之后默认每次先增加一颗星，再次点击变为半颗星
                                    if (imageView.getDrawable().getCurrent().getConstantState().equals(starHalfDrawable.getConstantState())) {
                                        setStar(indexOfChild(v));
                                    } else if(imageView.getDrawable().getCurrent().getConstantState().equals(starFillDrawable.getConstantState())) {
                                        setStar(indexOfChild(v) +0.5f);
                                    }else if(imageView.getDrawable().getCurrent().getConstantState().equals(starEmptyDrawable.getConstantState())) {
                                        setStar(indexOfChild(v) + 1);
                                    }
                                } else {
                                    setStar(indexOfChild(v) + 1f);
                                }

                            }
                        }
                    }
            );
            addView(imageView);
        }
        setStar(starStep);
    }

    /**
     * 设置每颗星星的参数
     */
    private ImageView getStarImageView() {
        ImageView imageView = new ImageView(getContext());

        LayoutParams layout = new LayoutParams(
                Math.round(starImageSize), Math.round(starImageSize));//设置每颗星星在线性布局的大小
        layout.setMargins(0, 0,Math.round(starPadding), 0);//设置每颗星星在线性布局的间距
        imageView.setLayoutParams(layout);
        /*如果您希望ImageView调整其边界，请将此设置为true以保持其可绘图的纵横比*/
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(starEmptyDrawable);
        imageView.setMinimumWidth(10);
        imageView.setMaxHeight(10);
        return imageView;

    }

    /**
     * 设置星星的个数
     */

    public void setStar(float rating) {

        if (onRatingChangeListener != null) {
            onRatingChangeListener.onRatingChange(rating);
        }
        this.starStep = rating;
        //浮点数的整数部分
        int fint = (int) rating;
        BigDecimal b1 = new BigDecimal(Float.toString(rating));
        BigDecimal b2 = new BigDecimal(Integer.toString(fint));
        //浮点数的小数部分
        float fPoint = b1.subtract(b2).floatValue();

        //设置选中的星星
        for (int i = 0; i < fint; ++i) {
            ((ImageView) getChildAt(i)).setImageDrawable(starFillDrawable);
        }
        //设置没有选中的星星
        for (int i = fint; i < starCount; i++) {
            ((ImageView) getChildAt(i)).setImageDrawable(starEmptyDrawable);
        }
        //小数点默认增加半颗星
        if (fPoint > 0) {
            ((ImageView) getChildAt(fint)).setImageDrawable(starHalfDrawable);
        }
    }

    /**
     * 操作星星的点击事件
     */
    public interface OnRatingChangeListener {
        /**
         * 选中的星星的个数
         */
        void onRatingChange(float ratingCount);

    }

    /**
     * 星星每次增加的方式整星还是半星，枚举类型
     * 类似于View.GONE
     */
    public enum StepSize {
        Half(0), Full(1);
        int step;

        StepSize(int step) {
            this.step = step;
        }

        public static StepSize fromStep(int step) {
            for (StepSize f : values()) {
                if (f.step == step) {
                    return f;
                }
            }
            throw new IllegalArgumentException();
        }
    }


    /**
     * dip转换px
     */
    public static int dp2px(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    public static int px2dp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}


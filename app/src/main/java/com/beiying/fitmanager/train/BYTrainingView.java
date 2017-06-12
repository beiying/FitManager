package com.beiying.fitmanager.train;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.beiying.fitmanager.R;
import com.beiying.fitmanager.core.ui.LeIconTextButton;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.core.ui.LeUI;
import com.beiying.fitmanager.core.ui.LeView;
import com.beiying.fitmanager.core.utils.LeTextUtil;
import com.beiying.fitmanager.framework.model.BYTrainModel;

/**
 * Created by beiying on 17/6/8.
 */

public class BYTrainingView extends LeView {
    private static final int TITLE_PADDING_LEFT = 16;


    private TextView mTitle;
    private LeIconTextButton mAddTrainBtn;
    private BYTrainingListView mTrainingListView;
    private LeListViewModel<BYTrainModel> mTrainModels;

    private int mTitlePaddingLeft;

    public BYTrainingView(Context context) {
        super(context);

        mTitlePaddingLeft = LeUI.getDensityDimen(context, TITLE_PADDING_LEFT);

        mTitle = new TextView(context);
        mTitle.setText(R.string.train_training_title);
        mTitle.setTextColor(0xFF101010);
        mTitle.setTextSize(15);
        addView(mTitle);

        mAddTrainBtn = new LeIconTextButton(context, R.drawable.add_train, "添加训练", 15);
        mAddTrainBtn.setTextColor(0xFF949494);
        mAddTrainBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAddTrainBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        addView(mAddTrainBtn);

        mTrainModels = new LeListViewModel<>();
        BYTrainModel model1 = new BYTrainModel();
        model1.mName = "动感单车";
        model1.mIcon = "";
        model1.mDescription = "周期性的有氧运动，可以有效的燃烧和消耗人体的热量从而达到减肥的目的";
        model1.mTime = 1080;
        mTrainModels.add(model1);
        BYTrainModel model2 = new BYTrainModel();
        model2.mName = "划船机";
        model2.mIcon = "";
        model2.mDescription = "提高人的心肺功能，促进血液的循环，提高中枢神经对全身肌肉的支配效果";
        model2.mTime = 1500;
        mTrainModels.add(model2);
        BYTrainModel model3 = new BYTrainModel();
        model3.mName = "跑步机";
        model3.mIcon = "";
        model3.mDescription = "促进全身血液循环，快速消除疲劳，增强体力和精力";
        model3.mTime = 1200;
        mTrainModels.add(model3);

        mTrainingListView = new BYTrainingListView(context, mTrainModels);
        addView(mTrainingListView);

        setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        LeUI.measureExactly(mTitle, LeTextUtil.getTextWidth(mTitle.getPaint(),mTitle.getText().toString()), LeTextUtil.getPaintHeight(mTitle.getPaint()));
        LeUI.measureExactly(mAddTrainBtn, width, height);
        LeUI.measureExactly(mTrainingListView, width, height);

        height = (mTitle.getMeasuredHeight() + mTrainingListView.getMeasuredHeight() + mTitlePaddingLeft * 2);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int offsetX = mTitlePaddingLeft;
        int offsetY = mTitlePaddingLeft;
        LeUI.layoutViewAtPos(mTitle, offsetX, offsetY);

        offsetX = getMeasuredWidth() - mTitlePaddingLeft - mAddTrainBtn.getMeasuredWidth();
        LeUI.layoutViewAtPos(mAddTrainBtn, offsetX, offsetY);

        offsetX = 0;
        offsetY += (mTitlePaddingLeft + mTitle.getMeasuredHeight());
        LeUI.layoutViewAtPos(mTrainingListView, offsetX, offsetY);
    }
}

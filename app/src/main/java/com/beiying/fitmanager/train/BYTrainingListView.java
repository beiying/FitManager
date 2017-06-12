package com.beiying.fitmanager.train;

import android.content.Context;
import android.view.View;

import com.beiying.fitmanager.core.ui.LeListView;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.framework.model.BYTrainModel;

/**
 * Created by beiying on 17/6/8.
 */

public class BYTrainingListView extends LeListView {

    public BYTrainingListView(Context context, LeListViewModel<BYTrainModel> model) {
        super(context, model);
    }

    @Override
    protected View getListItem(int index, View convertView) {
        BYTrainModel model = (BYTrainModel) mModel.get(index);
        if (convertView == null) {
            convertView = new BYTrainItemView(getContext());
        }
        if (convertView instanceof BYTrainItemView) {
            ((BYTrainItemView) convertView).setModel(model);
        }
        return convertView;
    }
}

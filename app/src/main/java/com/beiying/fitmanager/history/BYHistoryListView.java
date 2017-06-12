package com.beiying.fitmanager.history;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.beiying.fitmanager.core.ui.LeListView;
import com.beiying.fitmanager.core.ui.LeListViewModel;
import com.beiying.fitmanager.framework.model.BYBikeTrainModel;

public class BYHistoryListView extends LeListView implements OnClickListener {

	public BYHistoryListView(Context context, LeListViewModel<BYBikeTrainModel> model) {
		super(context, model);
		setWillNotDraw(false);
	}

	@Override
	protected View getListItem(int index, View convertView) {
		BYBikeTrainModel fitnessBikeModel = (BYBikeTrainModel) mModel.get(index);
		if (convertView == null) {
			convertView = new BYHistoryItemView(getContext());
			convertView.setOnClickListener(this);
		}
		if (convertView instanceof BYHistoryItemView) {
			((BYHistoryItemView) convertView).setModel(fitnessBikeModel);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}

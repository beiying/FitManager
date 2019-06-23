package com.beiying.demo.user.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import com.beiying.demo.MainActivity;

/**
 * Created by beiying on 19/2/27.
 */

public class UserProfileView extends FrameLayout {
    private UserProfileViewModel mViewModel;

    public UserProfileView(@NonNull Context context) {
        super(context);

        mViewModel = ViewModelProviders.of((FragmentActivity) context).get(UserProfileViewModel.class);
        mViewModel.init(0);
        mViewModel.getUser().observe((MainActivity)context, user -> {

        });
    }
}

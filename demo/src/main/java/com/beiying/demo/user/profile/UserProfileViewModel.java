package com.beiying.demo.user.profile;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.beiying.demo.user.User;
import com.beiying.demo.user.UserRepository;

import javax.inject.Inject;

/**
 * Created by beiying on 19/2/27.
 */

public class UserProfileViewModel extends ViewModel {
    private int mUserId;
    private MutableLiveData<User> mUser;
    private UserRepository mUserRepo;


    @Inject
    public UserProfileViewModel(UserRepository userRepo) {
        this.mUserRepo = userRepo;
    }

    public void init(int userId) {
        this.mUserId = userId;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }
}

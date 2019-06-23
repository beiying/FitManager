package com.beiying.demo.user;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.beiying.demo.BR;

/**
 * Created by beiying on 19/2/25.
 */

public class User {
    private String firstName;
    private String lastName;
    private int gender;
    public User(String firstName, String lastName, int gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    private static class StaticUser {
        public final ObservableField<String> firstName = new ObservableField<>();
        public final ObservableField<String> lastName = new ObservableField<>();
        public final ObservableInt age = new ObservableInt();
    }

    private static class ObservableUser extends BaseObservable{
        private String firstName;
        private String lastName;

        @Bindable
        public String getFirstName() {
            return this.firstName;
        }

        @Bindable
        public String getLastName() {
            return this.lastName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
            notifyPropertyChanged(BR.firstName);
        }
    }
}

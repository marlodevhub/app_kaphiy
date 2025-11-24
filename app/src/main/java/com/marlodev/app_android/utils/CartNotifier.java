package com.marlodev.app_android.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CartNotifier {

    private static final MutableLiveData<Boolean> cartUpdated = new MutableLiveData<>();

    public static void notifyCartUpdated() {
        cartUpdated.postValue(true);
    }

    public static LiveData<Boolean> getCartUpdated() {
        return cartUpdated;
    }
}

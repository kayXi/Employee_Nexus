package com.example.employee_nexus.ui.clockIn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClockInViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ClockInViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
package com.example.employee_nexus.ui.calloff;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CallOffViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CallOffViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Call Off fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
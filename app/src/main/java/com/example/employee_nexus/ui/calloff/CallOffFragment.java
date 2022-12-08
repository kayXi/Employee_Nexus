package com.example.employee_nexus.ui.calloff;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employee_nexus.databinding.FragmentCalloffBinding;

import java.util.Calendar;

public class CallOffFragment extends Fragment {

    private FragmentCalloffBinding binding;
    private DatePickerDialog picker;
    private EditText eText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CallOffViewModel notificationsViewModel =
                new ViewModelProvider(this).get(CallOffViewModel.class);

        binding = FragmentCalloffBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eText=(EditText) binding.callOffDatePicker;
        eText.setInputType(InputType.TYPE_NULL);
        eText.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getActivity(),
                    (view, year1, monthOfYear, dayOfMonth) -> eText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year1), year, month, day);
            picker.show();
        });

        final TextView textView = binding.textCalloff;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
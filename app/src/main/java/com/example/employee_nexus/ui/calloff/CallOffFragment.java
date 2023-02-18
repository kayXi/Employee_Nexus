package com.example.employee_nexus.ui.calloff;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.employee_nexus.LoginActivity;
import com.example.employee_nexus.databinding.FragmentCalloffBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CallOffFragment extends Fragment {

    private FragmentCalloffBinding binding;
    private DatePickerDialog picker;
    private EditText eText;
    private FirebaseFirestore db;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CallOffViewModel notificationsViewModel =
                new ViewModelProvider(this).get(CallOffViewModel.class);

        context = getActivity();
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

        Button submit = binding.callOffConfirm;
        submit.setOnClickListener(v -> {

            db = FirebaseFirestore.getInstance();
            //there's a calloffs collection within the user
            //add to this an entry with date, reason, and type fields (strings)
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();
            Map<String, Object> calloffData = new HashMap<>();
            if (picker != null) {
                DatePicker dp = picker.getDatePicker();
                String dateString = (String.valueOf(dp.getMonth()) + "." + String.valueOf(dp.getDayOfMonth()) + "." + String.valueOf(dp.getYear()));



                String reasonString = "";
                String typeString = "";

                int rbid = binding.reasonGroup.getCheckedRadioButtonId();
                View rb = binding.reasonGroup.findViewById(rbid);
                int i = binding.reasonGroup.indexOfChild(rb);

                switch (i) {
                    case 0:
                        reasonString = "weather";
                        break;
                    case 1:
                        reasonString = "illness";
                        break;
                    case 2:
                        reasonString = "emergency";
                        break;
                }

                rbid = binding.typeGroup.getCheckedRadioButtonId();
                rb = binding.typeGroup.findViewById(rbid);
                i = binding.typeGroup.indexOfChild(rb);

                switch (i) {
                    case 0:
                        typeString = "paid";
                        break;
                    case 1:
                        typeString = "unpaid";
                        break;
                    case 2:
                        typeString = "unexcused";
                        break;
                }

                calloffData.put("date", dateString);
                calloffData.put("reason", reasonString);
                calloffData.put("type", typeString);

                //check that values are non-null
                if (dateString.equals("..") || reasonString.equals("") || typeString.equals("")) {
                    Toast.makeText(context.getApplicationContext(), "Please enter all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    //check if a calloff entry for the currently selected date already exists, if so then don't make a new one
                    DocumentReference d = db.collection("Employees").document(userId).collection("calloffs").document(dateString);
                    d.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                Log.w("calloff", "Calloff entry already exists for desired date");
                                Toast.makeText(context.getApplicationContext(), "There is already a Call Off Request for this date.", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection("Employees").document(userId).collection("calloffs").document(dateString)
                                        .set(calloffData)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("calloff", "Error writing to database!", e);
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Toast.makeText(context.getApplicationContext(), "Submission Successful!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            } else {Toast.makeText(context.getApplicationContext(), "Please enter all fields.", Toast.LENGTH_SHORT).show();}
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
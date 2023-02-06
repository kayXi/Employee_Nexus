package com.example.employee_nexus.ui.home;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.employee_nexus.R;
import com.example.employee_nexus.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private TextView hName;
    private TextView hID;
    private TextView hDays;
    private ListenerRegistration lr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hName = (TextView)binding.homeName;
        hID = (TextView) binding.homeID;
        hDays = (TextView) binding.homeDays;

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        final DocumentReference docRef = db.collection("Employees").document(userId);
        lr =
                docRef.addSnapshotListener((DocumentSnapshot snapshot,
                                            FirebaseFirestoreException e) -> {
                    if (e != null) {
                        Log.w("homepage", "Listen failed.", e);
                        return;
                    }
                    if (snapshot!= null && snapshot.exists()){
                        Log.d("homepage","Current data: " + snapshot.getData());
                        hName.setText("" + snapshot.get("name"));
                        hID.setText("" + snapshot.get("emp_id"));
                        hDays.setText("Paid Days " + snapshot.get("paid_days") + "\t\tUnpaid Days " + snapshot.get("unpaid_days") + "\t\tUnexcused Days " + snapshot.get("unexcused"));

                    }
                    else {
                        Log.d("homepage","Current data: null");
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        lr.remove();
    }
}
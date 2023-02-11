package com.example.employee_nexus.ui.home;

import static android.content.ContentValues.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private TextView hName;
    private TextView hID;
    private TextView hDays;
    private TextView hNews;
    private ImageView hNewsImage;
    private TextView hWeather;

    private Context con;
    private ListenerRegistration lr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        hName = (TextView) binding.homeName;
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
                    if (snapshot != null && snapshot.exists()) {
                        Log.d("homepage", "Current data: " + snapshot.getData());
                        hName.setText("" + snapshot.get("name"));
                        hID.setText("" + snapshot.get("emp_id"));
                        hDays.setText("Paid Days " + snapshot.get("paid_days") + "\t\tUnpaid Days " + snapshot.get("unpaid_days") + "\t\tUnexcused Days " + snapshot.get("unexcused"));

                    } else {
                        Log.d("homepage", "Current data: null");
                    }
                });

        //news & announcements
        hNews = (TextView) binding.homeAnnouncements;
        hNewsImage = (ImageView) binding.homeAnnouncementsImg;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //API will return a JSON that contains news information
        //for the moment, it's the file located at https://newsdata.io/api/1/news?apikey=pub_16894052405df02e4c531ab8c582e36006da9&q=nexus
        //this api key only has 200 daily accesses allowed
        //setting up this section to run asynchronously should solve loading time on homepage issue

        try {
            //Log.w("homepage", "begin try block");
            URL conn = new URL("https://newsdata.io/api/1/news?apikey=pub_16894052405df02e4c531ab8c582e36006da9&q=math");
            //Log.w("homepage", "URL connected");
            BufferedReader apiResult = new BufferedReader(new InputStreamReader(conn.openStream()));
            //Log.w("homepage", "buffered reader on input stream");
            StringBuilder sb = new StringBuilder();
            //Log.w("homepage", "string builder initialized");
            String line = null;
            //Log.w("homepage", "beginning string building");

            while ((line = apiResult.readLine()) != null) {
                sb.append(line);
            }
            //Log.w("homepage", "string built");
            apiResult.close();
            //Log.w("homepage", "stream closed");
            String apiJSON = sb.toString();
            //Log.w("homepage", "string generated");

            JSONObject obj = new JSONObject(apiJSON);
        String status = obj.getString("status");
        JSONArray articles = obj.getJSONArray("results");
        Integer articleCount = obj.getInt("totalResults");

        //within the first entry of the JSON array is the article we want to display
        JSONObject article = articles.getJSONObject(0);
        hNews.setText(article.getString("title"));
        Uri.Builder builder = new Uri.Builder();
        builder.appendPath(article.getString("image_url"));
        Uri imageLocation = builder.build();
        hNewsImage.setImageURI(imageLocation);

        } catch (MalformedURLException e) {
            Log.w("homepage", "API URL is wrong");
        } catch (UnsupportedEncodingException e) {
            Log.w("homepage", "Encoding is wrong");
        } catch (JSONException e) {
            Log.w("homepage", "JSON issue");
        } catch (IOException e) {
            Log.w("homepage", "IO issue");
        }

        // @+id/homeWeather
        // http://api.weatherapi.com/v1/current.json?key=dd5f1b9da9564f74af5162742231102&q=17057&aqi=no

        hWeather = (TextView) binding.homeWeather;

        try {
            Log.w("homepage", "begin try block");
            URL conn2 = new URL("http://api.weatherapi.com/v1/current.json?key=dd5f1b9da9564f74af5162742231102&q=17057&aqi=no");
            Log.w("homepage", "URL connected");
            BufferedReader weatherApiResult = new BufferedReader(new InputStreamReader(conn2.openStream()));
            Log.w("homepage", "buffered reader on input stream");
            StringBuilder sb2 = new StringBuilder();
            Log.w("homepage", "string builder initialized");
            String line = null;
            Log.w("homepage", "beginning string building");

            while ((line = weatherApiResult.readLine()) != null) {
                sb2.append(line);
            }
            //Log.w("homepage", "string built");
            weatherApiResult.close();
            //Log.w("homepage", "stream closed");
            String apiJSON = sb2.toString();
            //Log.w("homepage", "string generated");

            JSONObject currentWeatherObj = new JSONObject(apiJSON);
            currentWeatherObj = currentWeatherObj.getJSONObject("current");
            Double currentTemp = currentWeatherObj.getDouble("temp_f");

            Log.w("homepage", "temp is " + currentTemp.toString());
            hWeather.setText(currentTemp.toString());

        } catch (MalformedURLException e) {
            Log.w("homepage", "API URL is wrong");
        } catch (UnsupportedEncodingException e) {
            Log.w("homepage", "Encoding is wrong");
        } catch (JSONException e) {
            Log.w("homepage", "JSON issue");
        } catch (IOException e) {
            Log.w("homepage", "IO issue");
        }



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        lr.remove();
    }
}
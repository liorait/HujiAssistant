package huji.postpc2021.hujiassistant.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import huji.postpc2021.hujiassistant.HujiAssistentApplication;
import huji.postpc2021.hujiassistant.LocalDataBase;
import huji.postpc2021.hujiassistant.R;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class LoadDataMainActivity extends AppCompatActivity {

    LocalDataBase db;
    Button askPermissionsButton;
    FirebaseUser currentUser;
    TextView loadingEditText;
    LottieAnimationView lottieAnimationLoadingOrder;
    public static final int REQUEST_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_screen);

        askPermissionsButton = findViewById(R.id.ask_permissions_button);
        lottieAnimationLoadingOrder = findViewById(R.id.lottieAnimationLoadingOrder);
        loadingEditText = findViewById(R.id.loadingEditText);
        lottieAnimationLoadingOrder.setVisibility(View.INVISIBLE);
        loadingEditText.setVisibility(View.INVISIBLE);
        askPermissionsButton.setVisibility(View.INVISIBLE);

        db = HujiAssistentApplication.getInstance().getDataBase();

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.INTERNET};

        currentUser = db.getUsersAuthenticator().getCurrentUser();
        // Update current language
        setLocale(db.loadLocale());

        // check permissions
        if (!checkPermissions(permissions)){
            lottieAnimationLoadingOrder.setVisibility(View.VISIBLE);
            loadingEditText.setVisibility(View.VISIBLE);
            // update UI when DB finish to load the initial data
            db.firstLoadFlagLiveData.observe(this, isLoad -> {
                if (isLoad) {
                    updateUI(currentUser);
                }
            });
        }
        else {
            requestPermissions(permissions);
        }

        askPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(permissions);
            }
        });
    }

    /**
     * if there is a logged in user -> navigate to the map screen, else navigate to the login screen
     *
     * @param firebaseUser - the currently logged in user, null if no user os logged in
     */
    private void updateUI(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            LocalDataBase db = HujiAssistentApplication.getInstance().getDataBase();
            db.setCurrentUser(firebaseUser);
            db.currentUserLiveData.observe(this, user -> {
                // If the user is already logged in
                if (user != null) {
                    startActivity(new Intent(this, MainScreenActivity.class));
                    finish();
                }
            });
            // If the user needs to logged in
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    // check permissions
    private boolean checkPermissions(String[] permissions) {
        for (String eachPermission : permissions) {
            if (ContextCompat.checkSelfPermission(this, eachPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    // Update the current language
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        db.saveLocale(lang);
    }

    // In case there is at least one unapproved permission
    private void requestPermissions(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        // check which permissions are not granted
        for (String eachPermission : permissions) {
            if (ContextCompat.checkSelfPermission(this, eachPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(eachPermission);
            }
        }
        // ask for those permissions
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSION_CODE);
        } else {
            updateUI(currentUser);
        }
    }

    //  Result from asking for permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                if (askPermissionsButton.getVisibility() == View.INVISIBLE){
                    askPermissionsButton.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this, R.string.permissions_error, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        updateUI(currentUser);
    }
}

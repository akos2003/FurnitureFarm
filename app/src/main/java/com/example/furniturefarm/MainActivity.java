package com.example.furniturefarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity
        extends AppCompatActivity{
    public static String NOTIFICATION_CHANNEL_ID = "1001";
    public static String default_notification_id = "default";
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText usernameEdit;
    EditText passwordEdit;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEdit = findViewById(R.id.editTextUserName);
        passwordEdit = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }


    public void login(View view) {
        String email = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if(!(email.isEmpty() || password.isEmpty())){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                startShopping();
            } else {
                Toast.makeText(MainActivity.this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Hiba!")
                    .setMessage("Adj meg bejelentkezési adatokat!");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        schecduleNotification(getNotification("Köszönjük, hogy appunkat választotta!"),3000);
    }

    private void startShopping() {
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }



    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                startShopping();
            } else {
                Toast.makeText(MainActivity.this, "Hiba: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", usernameEdit.getText().toString());
        editor.putString("password", passwordEdit.getText().toString());
        editor.apply();

    }
    private void schecduleNotification(Notification notification, int delay){
        Intent notifIntent = new Intent(this, NotificationBroadcast.class);
        notifIntent.putExtra(NotificationBroadcast.NOTIFICATIONID,1);
        notifIntent.putExtra(NotificationBroadcast.NOTIFICATION,notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,notifIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long futureMils = SystemClock.elapsedRealtime()+delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureMils,pendingIntent);
    }
    private Notification getNotification(String content){
        NotificationCompat.Builder nbuilder = new NotificationCompat.Builder(this, default_notification_id);

        nbuilder.setContentTitle("Üdvözlünk!");
        nbuilder.setContentText(content);
        nbuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        nbuilder.setAutoCancel(true);
        nbuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return nbuilder.build();
    }
}
package com.fatecipg.chatcomfirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText senhaEditText;
    private FirebaseAuth mAuth;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE_GPS = 1011;
    private double latitudeAtual;
    private double longitudeAtual;
    private String textoBranco = "";
    private TextView mensagemTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginEditText = findViewById(R.id.loginEditText);
        senhaEditText = findViewById(R.id.senhaEditText);
        mAuth = FirebaseAuth.getInstance();

        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        mensagemTextView = findViewById(R.id.mensagemTextView);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

             if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates
                        (LocationManager.GPS_PROVIDER,0, 0, locationListener);
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION}, 1011);
            }
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri =
                        Uri.parse(String.format("geo:%f,%f?q=%s",
                                latitudeAtual, longitudeAtual, textoBranco));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                latitudeAtual = lat;
                longitudeAtual = lon;
                mensagemTextView.setText(String.format("Lat: %f, Long: %f", lat,
                        lon));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }


    }

    public void irParaCadastro(View view) {
        startActivity (new Intent(this, NovoUsuarioActivity.class));
    }
 /*   public void fazerLogin(View view){
        String login =
                loginEditText.getText().toString();
        String senha =
                senhaEditText.getText().toString();
        mAuth.signInWithEmailAndPassword(
                login,
                senha
        ).addOnSuccessListener((authResult ->
            startActionMode(

                    (ActionMode.Callback) new Intent(
                            this,
                            ChatActivity.class
                    )

            )
        )).addOnFailureListener((exception) ->{
            exception.printStackTrace();
        });
    }*/
 public void fazerLogin (View view){
     String login = loginEditText.getEditableText().toString();
     String senha = senhaEditText.getEditableText().toString();
     mAuth.signInWithEmailAndPassword(login, senha)
             .addOnSuccessListener((result) -> {
                 startActivity (new Intent (this, ChatActivity.class));})
             .addOnFailureListener((exception) -> {
                 exception.printStackTrace();
                 Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
             });
 }

}

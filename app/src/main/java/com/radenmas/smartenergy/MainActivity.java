package com.radenmas.smartenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CircularProgressBar circleTegangan, circleArus, circleDaya, circleCoshPhi, circleFrekuensi, circleEnergy;
    private TextView valueTegangan, valueArus, valueDaya, valueCoshPhi, valueFrekuensi, valueEnergy, totalPrice;
    private Spinner spinnerGol;
    private ImageView imgStateLight, imgSwitchLamp, imgStatePower, imgSwitchPower;
    private MaterialButton btnReset;
    private ProgressBar progressBar;

    private DatabaseReference dataSensor;

    private String statusLight, statusPower;
    private ArrayAdapter<String> adapter;

    private DatabaseReference relay1 = FirebaseDatabase.getInstance().getReference("relay1");
    private DatabaseReference relay2 = FirebaseDatabase.getInstance().getReference("relay2");
    private DatabaseReference reset1 = FirebaseDatabase.getInstance().getReference("reset1");

    private String[] Item = {"R-1/TR", "R-1M/TR", "R-1/TR", "R-1/TR", "R-2/TR", "R-3/TR"};
    private float gol;
    private int selectSpin = 0;
    private double total = 0;
    private String dataEnergi;
    private float floatEnergi = 0;

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();

        TextView textview = new TextView(MainActivity.this);
        textview.setText("Smart");
        textview.setTextColor(Color.WHITE);
        textview.setTextSize(18);
        bar.setCustomView(textview);


        initView();

        //setDatabase
        dataSensor = FirebaseDatabase.getInstance().getReference("Smart Energy");

        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);

        //getKondisi
        statusLight = data.getString("statusLight", "");
        statusPower = data.getString("statusPower", "");
        switch (statusLight) {
            case "On":
                imgStateLight.setVisibility(View.VISIBLE);
                imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                break;
            case "Off":
                imgStateLight.setVisibility(View.INVISIBLE);
                imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                break;
        }
        switch (statusPower) {
            case "On":
                imgStatePower.setVisibility(View.VISIBLE);
                imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                break;
            case "Off":
                imgStatePower.setVisibility(View.INVISIBLE);
                imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                break;
        }

        //set data Digital Circle
        setDataCircle();

        //setAdapter
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Item);

        //setAdapter ke Spinner
        spinnerGol.setAdapter(adapter);

        spinnerGol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectSpin = i;
                updatePrice();
                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgSwitchLamp.setOnClickListener(this);
        imgSwitchPower.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    private void initView() {
        circleTegangan = findViewById(R.id.circleTegangan);
        circleArus = findViewById(R.id.circleArus);
        circleDaya = findViewById(R.id.circleDaya);
        circleCoshPhi = findViewById(R.id.circleCoshPhi);
        circleFrekuensi = findViewById(R.id.circleFrekuensi);
        circleEnergy = findViewById(R.id.circleEnergy);

        valueTegangan = findViewById(R.id.valueTegangan);
        valueArus = findViewById(R.id.valueArus);
        valueDaya = findViewById(R.id.valueDaya);
        valueCoshPhi = findViewById(R.id.valueCoshPhi);
        valueFrekuensi = findViewById(R.id.valueFrekuensi);
        valueEnergy = findViewById(R.id.valueEnergy);
        totalPrice = findViewById(R.id.totalPrice);

        spinnerGol = findViewById(R.id.spinnerGol);

        imgStateLight = findViewById(R.id.imgStateLight);
        imgSwitchLamp = findViewById(R.id.imgSwitchLamp);
        imgStatePower = findViewById(R.id.imgStatePower);
        imgSwitchPower = findViewById(R.id.imgSwitchPower);

        btnReset = findViewById(R.id.btnReset);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setDataCircle() {
        //setnilai dari database
        Query sortir = dataSensor.limitToLast(1);
        sortir.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    DecimalFormat decimal = new DecimalFormat("#");
                    DecimalFormat koma = new DecimalFormat("#.##");

                    String dataVolt = child.child("volt").getValue().toString();
                    String dataArus = child.child("arus").getValue().toString();
                    String dataPower = child.child("power").getValue().toString();
                    String dataCoshPhi = child.child("cos_phi").getValue().toString();
                    String dataFrekuensi = child.child("frequensi").getValue().toString();
                    dataEnergi = child.child("energy").getValue().toString();

                    float floatVolt = Float.parseFloat(dataVolt);
                    float floatArus = Float.parseFloat(dataArus);
                    float floatPower = Float.parseFloat(dataPower);
                    float floatCoshPhi = Float.parseFloat(dataCoshPhi);
                    float floatFrekuensi = Float.parseFloat(dataFrekuensi);
                    floatEnergi = Float.parseFloat(dataEnergi);

                    //setData to TextView
                    valueTegangan.setText(decimal.format(floatVolt));
                    valueArus.setText(koma.format(floatArus));
                    valueDaya.setText(decimal.format(floatPower));
                    valueCoshPhi.setText(koma.format(floatCoshPhi));
                    valueFrekuensi.setText(decimal.format(floatFrekuensi));
                    valueEnergy.setText(koma.format(floatEnergi));

                    //setData to Circle
                    circleTegangan.setProgress(floatVolt);
                    circleArus.setProgress(floatArus);
                    circleDaya.setProgress(floatPower);
                    circleCoshPhi.setProgress(floatCoshPhi);
                    circleFrekuensi.setProgress(floatFrekuensi);
                    circleEnergy.setProgress(floatEnergi);

                    updatePrice();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void Tegangan(View view) {
        pindahActivity("Tegangan");
    }

    public void Arus(View view) {
        pindahActivity("Arus");
    }

    public void Daya(View view) {
        pindahActivity("Daya");
    }

    public void CoshPhi(View view) {
        pindahActivity("CosPhi");
    }

    public void Frekuensi(View view) {
        pindahActivity("Frekuensi");
    }

    public void Energy(View view) {
        pindahActivity("Energy");
    }

    private void pindahActivity(String type) {
        Intent intent = new Intent(MainActivity.this, DetailServer.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();

        //getKondisi
        statusLight = data.getString("statusLight", "");
        statusPower = data.getString("statusPower", "");
        switch (view.getId()) {
            case R.id.imgSwitchLamp:
                if (statusLight.equals("")) {
                    //off Lampu
                    editor.putString("statusLight", "On");
                    editor.apply();
                    relay1.setValue("1");
                    imgStateLight.setVisibility(View.VISIBLE);
                    imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                } else if (statusLight.equals("Off")) {
                    //off Lampu
                    editor.putString("statusLight", "On");
                    editor.apply();
                    relay1.setValue("1");
                    imgStateLight.setVisibility(View.VISIBLE);
                    imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                } else if (statusLight.equals("On")) {
                    //on Lampu
                    editor.putString("statusLight", "Off");
                    editor.apply();
                    relay1.setValue("0");
                    imgStateLight.setVisibility(View.INVISIBLE);
                    imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                }
                break;

            case R.id.imgSwitchPower:
                if (statusPower.equals("")) {
                    //off Power
                    editor.putString("statusPower", "On");
                    editor.apply();
                    relay2.setValue("1");
                    imgStatePower.setVisibility(View.VISIBLE);
                    imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                } else if (statusPower.equals("Off")) {
                    //off Power
                    editor.putString("statusPower", "On");
                    editor.apply();
                    relay2.setValue("1");
                    imgStatePower.setVisibility(View.VISIBLE);
                    imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                } else if (statusPower.equals("On")) {
                    //on Power
                    editor.putString("statusPower", "Off");
                    editor.apply();
                    relay2.setValue("0");
                    imgStatePower.setVisibility(View.INVISIBLE);
                    imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                }
                break;

            case R.id.btnReset:
//                btnReset.setVisibility(View.INVISIBLE);
//                reset1.setValue("1");
//                new Handler().postDelayed(() -> {
//                    btnReset.setVisibility(View.VISIBLE);
//                    reset1.setValue("0");
//                }, 5000);

                startActivity(new Intent(MainActivity.this, DetailServer.class));
        }
    }

    public void updatePrice() {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        switch (selectSpin) {
            case 0:
                gol = 274;
                break;
            case 1:
                gol = 1352;
                break;
            case 2:
                gol = 1444;
                break;
            case 3:
                gol = 1444;
                break;
            case 4:
                gol = 1444;
                break;
            case 5:
                gol = 1444;
                break;
        }

        total = floatEnergi * gol;
        totalPrice.setText(formatRupiah.format(total));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                startActivity(new Intent(MainActivity.this, InfoAppActivity.class));
                break;
            case R.id.menu_close:
                onBackPressed();
                break;
            case R.id.menu_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Ingin Log out dari akun?")
                        .setPositiveButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Ya", (dialogInterface, i) -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        })
                        .show();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Ingin keluar dari aplikasi?")
                .setPositiveButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNegativeButton("Ya", (dialogInterface, i) -> finish())
                .show();
    }
}

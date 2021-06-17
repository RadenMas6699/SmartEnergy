package com.radenmas.smartenergy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.radenmas.smartenergy.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private DatabaseReference dataSensor;

    private String statusLight, statusPower;
    private ArrayAdapter<String> adapter;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    private DatabaseReference relay1 = FirebaseDatabase.getInstance().getReference("relay1");
    private DatabaseReference relay2 = FirebaseDatabase.getInstance().getReference("relay2");

    private String[] Item = {"R-1/TR", "R-1M/TR", "R-1/TR", "R-1/TR", "R-2/TR", "R-3/TR"};

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setDatabase
        dataSensor = FirebaseDatabase.getInstance().getReference("Smart Energy");

        //setSharedPreff
        data = getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = data.edit();

        //getKondisi
        statusLight = data.getString("statusLight", "");
        statusPower = data.getString("statusPower", "");
        switch (statusLight) {
            case "On":
                binding.imgStateLight.setVisibility(View.VISIBLE);
                binding.imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                break;
            case "Off":
                binding.imgStateLight.setVisibility(View.INVISIBLE);
                binding.imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                break;
        }
        switch (statusPower) {
            case "On":
                binding.imgStatePower.setVisibility(View.VISIBLE);
                binding.imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                break;
            case "Off":
                binding.imgStatePower.setVisibility(View.INVISIBLE);
                binding.imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                break;
        }

        //setAdapter
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Item);

        //setAdapter ke Spinner
        binding.spinnerGol.setAdapter(adapter);

        //set data Digital Circle
        setDataCircle();

        binding.imgSwitchLamp.setOnClickListener(this);
        binding.imgSwitchPower.setOnClickListener(this);
    }

    private void setDataCircle() {
        //setnilai dari database
        Query sortir = dataSensor.limitToLast(1);
        sortir.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    DecimalFormat df = new DecimalFormat("#");

                    String dataVolt = child.child("volt").getValue().toString();
                    String dataArus = child.child("arus").getValue().toString();
                    String dataPower = child.child("power").getValue().toString();
                    String dataCoshPhi = child.child("cos_phi").getValue().toString();
                    String dataFrekuensi = child.child("frequensi").getValue().toString();
                    String dataEnergi = child.child("energy").getValue().toString();

                    float floatVolt = Float.parseFloat(dataVolt);
                    float floatArus = Float.parseFloat(dataArus);
                    float floatPower = Float.parseFloat(dataPower);
                    float floatCoshPhi = Float.parseFloat(dataCoshPhi);
                    float floatFrekuensi = Float.parseFloat(dataFrekuensi);
                    float floatEnergi = Float.parseFloat(dataEnergi);

                    //setData to TextView
                    binding.valueTegangan.setText(df.format(floatVolt));
                    binding.valueArus.setText(df.format(floatArus));
                    binding.valueDaya.setText(df.format(floatPower));
                    binding.valueCoshPhi.setText(df.format(floatCoshPhi));
                    binding.valueFrekuensi.setText(df.format(floatFrekuensi));
                    binding.valueEnergy.setText(df.format(floatEnergi));

                    //setData to Circle
                    binding.circleTegangan.setProgress(floatVolt);
                    binding.circleArus.setProgress(floatArus);
                    binding.circleDaya.setProgress(floatPower);
                    binding.circleCoshPhi.setProgress(floatCoshPhi);
                    binding.circleFrekuensi.setProgress(floatFrekuensi);
                    binding.circleEnergy.setProgress(floatEnergi);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void InfoApp(View view) {
        startActivity(new Intent(MainActivity.this, InfoAppActivity.class));
    }

    public void Exit(View view) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Ingin keluar dari aplikasi?")
                .setPositiveButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
                .setNegativeButton("Ya", (dialogInterface, i) -> finish())
                .show();
    }

    public void Tegangan(View view) {
        pindahActivity("Tegangan", "volt");
    }

    public void Arus(View view) {
        pindahActivity("Arus", "arus");
    }

    public void Daya(View view) {
        pindahActivity("Daya", "power");
    }

    public void CoshPhi(View view) {
        pindahActivity("CosPhi", "cos_phi");
    }

    public void Frekuensi(View view) { pindahActivity("Frekuensi", "frequensi"); }

    public void Energy(View view) {
        pindahActivity("Energy", "energy");
    }

    private void pindahActivity(String type, String reff) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("reff", reff);
        startActivity(intent);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onClick(View view) {
        //getKondisi
        statusLight = data.getString("statusLight", "");
        statusPower = data.getString("statusPower", "");
        switch (view.getId()) {
            case R.id.imgSwitchLamp:
                if (statusLight.equals("Off")) {
                    //off Lampu
                    editor.putString("statusLight", "On");
                    editor.apply();
                    relay1.setValue("1");
                    binding.imgStateLight.setVisibility(View.VISIBLE);
                    binding.imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                }
                if (statusLight.equals("On")) {
                    //on Lampu
                    editor.putString("statusLight", "Off");
                    editor.apply();
                    relay1.setValue("0");
                    binding.imgStateLight.setVisibility(View.INVISIBLE);
                    binding.imgSwitchLamp.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                }
                break;

            case R.id.imgSwitchPower:
                if (statusPower.equals("Off")) {
                    //off Power
                    editor.putString("statusPower", "On");
                    editor.apply();
                    relay2.setValue("1");
                    binding.imgStatePower.setVisibility(View.VISIBLE);
                    binding.imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_on));
                }
                if (statusPower.equals("On")) {
                    //on Power
                    editor.putString("statusPower", "Off");
                    editor.apply();
                    relay2.setValue("0");
                    binding.imgStatePower.setVisibility(View.INVISIBLE);
                    binding.imgSwitchPower.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_off));
                }
                break;
        }
    }


}
package com.radenmas.smartenergy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class Cadangan extends AppCompatActivity {




//    private SwitchCompat switchLight, switchPower;
//    private MaterialTextView onLight, offLight, onPower, offPower;
//
//    DatabaseReference relay1 = FirebaseDatabase.getInstance().getReference("relay1");
//    DatabaseReference relay2 = FirebaseDatabase.getInstance().getReference("relay2");

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        switchLight = findViewById(R.id.switchLight);
//        switchPower = findViewById(R.id.switchPower);
//        onLight = findViewById(R.id.onLight);
//        offLight = findViewById(R.id.offLight);
//        onPower = findViewById(R.id.onPower);
//        offPower = findViewById(R.id.offPower);
//
//        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = data.edit();
//
//        //getKondisi
//        String statusLight = data.getString("statusLight", "");
//        String statusPower = data.getString("statusPower", "");

//        switch (statusLight) {
//            case "On":
//                lightOn();
//                switchLight.setChecked(true);
//                break;
//            default:
//                lightOff();
//                switchLight.setChecked(false);
//                break;
//        }
//        switch (statusPower) {
//            case "On":
//                powerOn();
//                switchPower.setChecked(true);
//                break;
//            default:
//                powerOff();
//                switchPower.setChecked(false);
//                break;
//        }

//        switchLight.setOnCheckedChangeListener(this);
//
//        switchPower.setOnCheckedChangeListener(this);

    }

//    private void powerOff() {
//        relay2.setValue("0");
//        offPower.setTextColor(getResources().getColor(R.color.blueLight));
//        onPower.setTextColor(getResources().getColor(R.color.whiteSmoke));
//    }
//
//    private void powerOn() {
//        relay2.setValue("1");
//        onPower.setTextColor(getResources().getColor(R.color.toscaLight));
//        offPower.setTextColor(getResources().getColor(R.color.whiteSmoke));
//    }
//
//    private void lightOff() {
//        relay1.setValue("0");
//        offLight.setTextColor(getResources().getColor(R.color.blueLight));
//        onLight.setTextColor(getResources().getColor(R.color.whiteSmoke));
//    }
//
//    private void lightOn() {
//        relay1.setValue("1");
//        onLight.setTextColor(getResources().getColor(R.color.toscaLight));
//        offLight.setTextColor(getResources().getColor(R.color.whiteSmoke));
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.menu_info:
//                Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_logout:
//                Toast.makeText(this, "Keluar", Toast.LENGTH_SHORT).show();
//                break;
//
//        }
//        return true;
//    }

//    @Override
//    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//        SharedPreferences data = getSharedPreferences("data", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = data.edit();
//        if (switchLight.isChecked()) {
//            lightOn();
//            editor.putString("statusLight", "On");
//        } else {
//            lightOff();
//            editor.putString("statusLight", "Off");
//        }
//        if (switchPower.isChecked()) {
//            powerOn();
//            editor.putString("statusPower", "On");
//        } else {
//            powerOff();
//            editor.putString("statusPower", "Off");
//        }
//        editor.apply();
//    }

//    public void Tegangan(View view) {
//        pindahActivity("Tegangan");
//    }
//
//    public void Arus(View view) {
//        pindahActivity("Arus");
//    }
//
//    public void Daya(View view) {
//        pindahActivity("Daya");
//    }
//
//    public void CoshPhi(View view) {
//        pindahActivity("CosPhi");
//    }
//
//    public void Energy(View view) {
//        pindahActivity("Energy");
//    }
//
//    private void pindahActivity(String tegangan) {
//        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//        intent.putExtra("type", tegangan);
//        startActivity(intent);
//    }
//
//    public void InfoApp(View view) {
//        startActivity(new Intent(MainActivity.this, DetailActivity.class));
//    }
//
//    public void Exit(View view) {
//        new AlertDialog.Builder(MainActivity.this)
//                .setMessage("Ingin keluar dari aplikasi?")
//                .setPositiveButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss())
//                .setNegativeButton("Ya", (dialogInterface, i) -> finish())
//                .show();
//    }
}
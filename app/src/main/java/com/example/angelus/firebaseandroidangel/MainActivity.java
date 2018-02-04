package com.example.angelus.firebaseandroidangel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button buttonRegistrar, buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRegistrar = (Button)findViewById(R.id.buttonRegistrarse);
        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent miIntento = new Intent(MainActivity.this, PantallaRegistro.class);
                startActivity(miIntento);

            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntento2 = new Intent(MainActivity.this, PantallaLogin.class);
                startActivity(miIntento2);
            }
        });



    }
}

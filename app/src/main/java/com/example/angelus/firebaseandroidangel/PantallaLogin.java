package com.example.angelus.firebaseandroidangel;

import android.content.Intent;
import android.media.AudioRecord;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class PantallaLogin extends AppCompatActivity {
    private EditText miPassword;
    private Button  miButtonLogin;
    private AutoCompleteTextView miCorreo;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);

        miCorreo = (AutoCompleteTextView)findViewById(R.id.ediTextLoginEmail);
        miPassword = (EditText)findViewById(R.id.editTextLoginPassword);
        //miCorreo = (EditText)findViewById(R.id.ediTextLoginEmail);

        miButtonLogin = (Button)findViewById(R.id.buttonLOGIN);


        miButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = miCorreo.getText().toString();
                String password = miPassword.getText().toString();

                loguearse(correo, password);

            }
        });



    }

    private void loguearse(String correo, String password){

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser miUser = mAuth.getCurrentUser();
                    Toast.makeText(PantallaLogin.this, "Correcto, adelante: "+miUser.getUid(), Toast.LENGTH_SHORT).show();
                    Intent miIntento = new Intent(getApplicationContext(), PantallaUser.class);
                    startActivity(miIntento);

                } else{

                    Toast.makeText(PantallaLogin.this, "Sign in Failed "+task.getException(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}

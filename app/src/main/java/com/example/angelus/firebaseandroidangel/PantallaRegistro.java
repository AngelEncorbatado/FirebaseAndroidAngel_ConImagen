package com.example.angelus.firebaseandroidangel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PantallaRegistro extends AppCompatActivity {
private EditText ediCorreo, ediPassword;
private Button buttonRegistrar;
private FirebaseAuth mAuth;
private FirebaseAuth.AuthStateListener mAuthListener;
private String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registro);

        ediCorreo = (EditText)findViewById(R.id.editTextCorreo);
        ediPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonRegistrar = (Button)findViewById(R.id.buttonFinalRegistrar);


        mAuth = FirebaseAuth.getInstance();


        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ediCorreo.getText().toString();
                password = ediPassword.getText().toString();

                registrar(email,password);

            }
        });

    }


    public void registrar(String email, String password){



        mAuth = FirebaseAuth.getInstance();//IMPORTANTE REPETIR ESTA LINEA

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information

                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Authentication successfull."+user.getUid(),
                            Toast.LENGTH_SHORT).show();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

            }
        });


    }
    private void updateUI(FirebaseUser user) {

        if (user != null) {

            Intent miIntento = new Intent(this, PantallaNewUser.class);
            miIntento.putExtra("correo", email);//PASO el correo para que no se tenga que volver a completar en la siguiente pantalla
            startActivity(miIntento);

            Toast.makeText(this, "Ya estas logeado", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(this, "No estas logeado", Toast.LENGTH_SHORT).show();

        }
    }

}

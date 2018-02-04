package com.example.angelus.firebaseandroidangel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PantallaNewUser extends AppCompatActivity {
private EditText alias, correo, nombreCompleto, direccion;
private Button buttonAñadir;
private DatabaseReference bbdd; //DatabaseReferente nos da la referencia a nuestra base de datos
private String strAlias, strCorreo, strNombreCompleto, strDireccion;
private String textoMomento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_new_user);

        Toast.makeText(this, "Ya estas logeado", Toast.LENGTH_SHORT).show();

        bbdd = FirebaseDatabase.getInstance().getReference("usuarios"); //aqui es donde le decimos en el nodo que tiene que mirar

        alias = (EditText)findViewById(R.id.editTextAlias);
        correo = (EditText)findViewById(R.id.editTextCorreo);
        nombreCompleto = (EditText)findViewById(R.id.editTextNombreCompleto);
        direccion = (EditText)findViewById(R.id.editTextDireccion);

        buttonAñadir = (Button)findViewById(R.id.buttonAñadoUser);

        Bundle miBundle = getIntent().getExtras();
        correo.setText(miBundle.getString("correo"));//bueno, aqui he recogido el correo de la pantalla anterior, para que no se tenga que volver a completar
        correo.setEnabled(false);

        buttonAñadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strAlias = alias.getText().toString();
                strCorreo = correo.getText().toString();
                strDireccion = direccion.getText().toString();
                strNombreCompleto = nombreCompleto.getText().toString();

                FirebaseUser miFireUser = FirebaseAuth.getInstance().getCurrentUser();
                String miUID = miFireUser.getUid();



                    if(!TextUtils.isEmpty(strAlias)){ //comprobaciones de campos vacios
                        if(!TextUtils.isEmpty(strCorreo)){
                            if(!TextUtils.isEmpty(strNombreCompleto)){
                                if(!TextUtils.isEmpty(strDireccion)){




                                    Usuario user = new Usuario(strAlias, strCorreo, strDireccion, strNombreCompleto, miUID);

                                    String clave = bbdd.push().getKey();

                                    bbdd.child(clave).setValue(user); //decimos que cree la tabla de nombre clave y le ponga tot el objeto Usuario a piñon

                                    Toast.makeText(PantallaNewUser.this, "Usuario añadido", Toast.LENGTH_SHORT).show();

                                    Intent miIntent = new Intent(getApplicationContext(), PantallaUser.class);

                                    startActivity(miIntent);
                                    finish();//con esto evitamos que pueda volver atras y crear varios newUser el mismo UIDuser

                                }else{
                                    Toast.makeText(PantallaNewUser.this, "Campo Direccion vacio.", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Toast.makeText(PantallaNewUser.this, "Campo Nombre Completo vacio.", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(PantallaNewUser.this, "Campo Correo vacio.", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(PantallaNewUser.this, "Campo Alias vacio.", Toast.LENGTH_SHORT).show();
                    }

            }
        });



    }


}

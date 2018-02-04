package com.example.angelus.firebaseandroidangel;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.ArrayList;


public class PantallaUser extends AppCompatActivity {
    private Button miButtonMisProductos, miButtonAnadoProduc, miButtonModificoProduc, miButtonBorroProduc, miButtonBuscoProduc, miButtonTodoTerreno;
    private TextView reCargoUser;
    private EditText miEditextNombre, miEditextDescripción, miEditextPrecio;
    private RadioGroup miRadiogroup;
    private RadioButton miRadioButtonCategoria, miRadioButtonUsuario;
    private Spinner miSpinnerCategoria;
    private String [] misCategorias;
    private ArrayAdapter<String> miArrayAdapter;

    private Button miButtonGaleria;
    private ImageView miImagen;
    private StorageReference mStorageRef;
    private StorageReference mStorageRedImagenes;
    private String miRutaReferencia, miNombredeArchivo, miUIDUserMomentaneo;
    private Uri miUri;
    private UploadTask uploadTask;

    private ListView miListView;
    private Adapter miAdapter;//Invoco a esta clase que he creado para rellenar con sus metodos mi ListView

    private DatabaseReference bbdd; //DatabaseReferente nos da la referencia a nuestra base de datos
    private DatabaseReference bbdd2;//esta es para los productos de un usuario
    private DatabaseReference bbdd3;//esta es para todos los productos
    private FirebaseAuth miAuth;

    private ArrayList<Producto> miArrayListProductos;
    private ArrayList<Usuario> miArrayListUsuarios;
    private String miStringEditableParaRadioGroup;

    private static final int SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_user);

        Toast.makeText(this, "Ya estas logeado", Toast.LENGTH_SHORT).show();

        //botones que el usuario interaccionará en su perfil
        miButtonMisProductos = (Button)findViewById(R.id.buttonMisProductos);
        miButtonAnadoProduc = (Button)findViewById(R.id.buttonAnadirProducto);
        miButtonModificoProduc = (Button)findViewById(R.id.buttonModificarProducto);
        miButtonBorroProduc = (Button)findViewById(R.id.buttonBorrarProducto);
        miButtonBuscoProduc = (Button)findViewById(R.id.buttonBusquedaProductos);
        miButtonTodoTerreno = (Button)findViewById(R.id.buttonTodoTerreno);//Button todoTerreno importante para los cambios

        //COSAS DE IMAGENES
        miButtonGaleria = (Button)findViewById(R.id.buttonGaleria);//Este es el boton que varga la GALERIA!!
        miImagen = (ImageView)findViewById(R.id.miImagen);//ZONA DONDE CARGO LA IMAGEN


        //Aun sin implementar
        reCargoUser = (TextView)findViewById(R.id.textLogotipo);//con este recargamos la pagina, tipo logo


        //EdiText que recogen la información y modifican la base de datos. Tambien hay un spinner

        miEditextNombre = (EditText)findViewById(R.id.editTextNombreProducto);
        miEditextDescripción = (EditText)findViewById(R.id.editTextDescripciónProducto);
        miEditextPrecio = (EditText)findViewById(R.id.editTextPrecioProducto);
        miSpinnerCategoria = (Spinner)findViewById(R.id.spinnerCategoria);//este es el spinner

        //LISTVIEW QUE CARGAREMOS
        miListView = (ListView)findViewById(R.id.listViewZonaCargo);

        //RADIOBUTONS QUE OBTENDREMOS
        miRadiogroup = (RadioGroup) findViewById(R.id.radioGroupTotal);
        miRadioButtonUsuario =(RadioButton)findViewById(R.id.radioButtonPorUsuario);
        miRadioButtonCategoria = (RadioButton)findViewById(R.id.radioButtonPorCategoria);
        miStringEditableParaRadioGroup = "vacio";


        //AL PRINCIPIO ESTOS CAMPOS ESTAN DESABILITADOS HASTA QUE PRESIONE EL BOTON NECESARIO
        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setVisibility(View.INVISIBLE);
        miRadiogroup.setVisibility(View.INVISIBLE);
        miButtonGaleria.setVisibility(View.INVISIBLE);
        miImagen.setVisibility(View.INVISIBLE);


        //relleno el spinner con la lista de categorias para PRODUCTOS disponibles;
        misCategorias = new String[]{"Tecnologia", "Coches","Hogar","Varios"};
        miArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, misCategorias);


        miArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//con esta linea el spinner sale hacia arriba
        miSpinnerCategoria.setPrompt("Escoge categoria");//intentamos ponerle un titulo al spinner, pero no conseguimos que pase

        miSpinnerCategoria.setAdapter(miArrayAdapter);//introducimos el arrayAdapter en el spinner


        mStorageRef = FirebaseStorage.getInstance().getReference();//Aqui guardamos la referencia a FirebaseStorage para luego subir alli la imagen
        buscoMisProductos();




        miListView.setEnabled(true);
        miListView.setClickable(true);
                miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//ESTO ES PARA QUE CAMBIE EL CONTENIDO DE UNOS DE LOS LINEARLAYOUT QUE CONTIENE INFO
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {//ESTE METODO NO FUNCIONA POR ALGUNE XTRAÑO MOTIVO

                        Toast.makeText(PantallaUser.this, "hola : "+position, Toast.LENGTH_SHORT).show(); //tengo un problema es que no me deja entrar aqui

                        Producto pr1 = (Producto)adapterView.getItemAtPosition(position);

                        Toast.makeText(PantallaUser.this, ""+pr1.getNombre(), Toast.LENGTH_SHORT).show();

                        String nombre = miArrayListProductos.get(position).getNombre();
                        String descripcion =  miArrayListProductos.get(position).getDescripcion();
                        String categoria = miArrayListProductos.get(position).getCategoria();
                        String precio = miArrayListProductos.get(position).getPrecio();


                    }
                });


        //BUTTON GALERIA IMPORTANTE
        miButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");//al seleccionar este tipo image/* decimos que nos muestre todos los archivos de tipo imagen
                intent.setAction(Intent.ACTION_GET_CONTENT);//y el Action_get_content nos sirve para guardar en el data lo que hemos clickado de una forma dinamica, tambien serviria Action_Pick pero funciona peor y esta deprecated
                startActivityForResult(
                        Intent.createChooser(intent, "Seleccione una imagen"),
                        SELECT_FILE);
            }
        });


        //EMPEZAMOS CON LA MANDANGA

        miButtonMisProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

            buscoMisProductos();

            }
        });

        miButtonAnadoProduc.setOnClickListener(new View.OnClickListener() {//Listener para visualizar los campos de añadir producto
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

             anadoProductoCamposVisibles();
            }
        });
        miButtonBorroProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

             borroProductoCamposVisibles();


            }
        });
        miButtonModificoProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                miEditextNombre.setEnabled(true);//pongamoslo habilitado por si las moscas
                modificoCamposProductosactivar();

            }
        });

        miButtonBuscoProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                buscoCamposProductosActivar();
            }
        });

        miRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//AQUI TENEMOS LOS CAMBIOS de VIEWS SEGUN ESCOJA BUSCANDO EN EL RADIOGROUP
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(miRadioButtonUsuario.isChecked()){
                    miEditextNombre.setVisibility(View.VISIBLE);
                    miSpinnerCategoria.setVisibility(View.INVISIBLE);

                    miStringEditableParaRadioGroup = "usuario";

                }
                if(miRadioButtonCategoria.isChecked()){
                    miSpinnerCategoria.setVisibility(View.VISIBLE);
                    miEditextNombre.setVisibility(View.INVISIBLE);

                    miStringEditableParaRadioGroup = "categoria";

                }
            }
        });

        //BUTTON MULTIUSOSDEPENDIENDO DE LA ACCIÓN AQUI HAY MUCHA MANDANGA
        miButtonTodoTerreno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        if(miButtonTodoTerreno.getText().toString().equals("AÑADIR")) {
            if (hayAlgunCampoVacio() == true) {//Si nos fijamos, llamamos a un metodo que hemos creado hayAlgunCampoVacio(), que devuelve True si estan tots completos

                String nombre = miEditextNombre.getText().toString();
                String descripcion = miEditextDescripción.getText().toString();//Recogemos los valores que necesitamos para nuestro nuevo producto
                String categoria = miSpinnerCategoria.getSelectedItem().toString();
                String precio = miEditextPrecio.getText().toString();
                ImageView imagen = miImagen;


                //A la hora de crear las tablas decimos que se tiene que posicionar como nodo en el UID del usuario, por lo tanto siempre crearemos los objetos adentro
                miAuth = FirebaseAuth.getInstance();
                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                Producto nuevoProducto = new Producto(nombre, descripcion, categoria, precio, null);
                String clave = bbdd2.push().getKey();//recogemos clave para dar nombre a la nueva tabla hija

                bbdd2.child(clave).setValue(nuevoProducto);//entonces a la hija(.child), de nombre clave, le plantamos tot el objeto a piñon con .setValue(Producto)


                //PARTE EXTRA PARA BUSCAR, aunque estemos en añadir , DEBIDO A QUE ES MAS FÁCIL CREAR NODOS APARTE, TAMBIEN CREAREMOS UN NODO CON TODOS LOS PRODUCTOS, FUERA DE UN USUARIO
                bbdd3 = FirebaseDatabase.getInstance().getReference("todosproductos");

                bbdd3.child(clave).setValue(nuevoProducto);

                //¡¡¡¡¡PARTE PARA SUBIR LA IMAGEN USANDO StorageREference varios!!!!!

                if(miUri != null) {//comprobamos que haya escogido una foto
                    Uri file = miUri;//este miUri lo guarde desde el método onActivityResult
                    StorageReference riversRef = mStorageRef.child("images/" + miUIDUSER + "/" + clave + "/"+  miNombredeArchivo); //vease que miNombredeArchivo lo recogemos en el onActivityResult,abajo del tot está
                    //Tambien vease en la linea de arriba que guardamos las imagenes en una subcarpeta llamada miUIDUSER que es la el UID recogido unas linea arriba a FirebaseUser miUser
                    //Tambien vease en la linea de arriba que guardamos las imagenes en una subcarpeta llamada clave, que es la clave del producto, así luego podremos recuperarlos
                    uploadTask = riversRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads

                            Toast.makeText(PantallaUser.this, "Algo ha fallado", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    });
                }else{
                    Toast.makeText(PantallaUser.this, "Imagen de producto vacia", Toast.LENGTH_SHORT).show();
                }
                miUri = null; //Vuelvo a poner el Uri a null para obligar a que escoja una imagen, y así pase por el onActivityResult la nueva ruta de imagen

                Toast.makeText(PantallaUser.this, "Producto añadido", Toast.LENGTH_SHORT).show();

                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

            }
        }

        if(miButtonTodoTerreno.getText().toString().equals("BORRAR")){

                if(!TextUtils.isEmpty(miEditextNombre.getText().toString())){

                    miAuth = FirebaseAuth.getInstance();
                    FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                    String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                    bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                    //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA SOLO BORRAR PRODUCTOS PROPIOS.....
                    bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE BORRE LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                    final String nombreMomentaneo = miEditextNombre.getText().toString();
                    Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                    q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int contador = 0;

                            for(DataSnapshot dataSnapshot3: dataSnapshot.getChildren()){

                                String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                                    DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                                        ref.removeValue(); //ya tenemos nuestra tabla referenciada y lista, la borramos con removeValue()

                                    Toast.makeText(PantallaUser.this, "El producto de nombre: "+nombreMomentaneo + " se ha borrado correctamente", Toast.LENGTH_SHORT).show();
                                    contador = 1;
                            }
                            if(contador == 0){
                                Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                            }
                            contador = 0;

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }else{
                    Toast.makeText(PantallaUser.this, "El campo nombre esta vacio", Toast.LENGTH_SHORT).show();
                }

            limpioCamposProductos();//limpiamos primero
            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

        }
        if(miButtonTodoTerreno.getText().toString().equals("MODIFICAR")){


            if(!TextUtils.isEmpty(miEditextNombre.getText().toString())){

                miAuth = FirebaseAuth.getInstance();
                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                final String nombreMomentaneo = miEditextNombre.getText().toString();
                Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int contador = 0;

                        for(DataSnapshot dataSnapshot3: dataSnapshot.getChildren()){

                            String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                            DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                            Producto miProd = dataSnapshot3.getValue(Producto.class);//recogemos un objeto de tipo Producto para que se sepan los campos


                            pasoTransitorioHaciaNingunaParte(miProd); //DESDE ESTE METODO PREPARAMOS LOS EDITEXT CON LOS VALORES ORIGINALES, PARA QUE SOLO CAMBIE LOS QUE NECESITE


                            Toast.makeText(PantallaUser.this, "El producto de nombre: "+nombreMomentaneo + " esta listo, cambia los valores que desees", Toast.LENGTH_SHORT).show();
                            contador = 1;
                        }
                        if(contador == 0){
                            Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                        }
                        contador = 0;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }else{
                Toast.makeText(PantallaUser.this, "El campo nombre esta vacio", Toast.LENGTH_SHORT).show();
            }


        }
        if(miButtonTodoTerreno.getText().toString().equals("GUARDAR CAMBIOS")){
            if(hayAlgunCampoVacio() == true){
                miAuth = FirebaseAuth.getInstance();
                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                final String nombreMomentaneo = miEditextNombre.getText().toString();//ESTE ES EL CAMPO QUE SE LE PASA A LA QUERY

                final String descripcionMomentanea = miEditextDescripción.getText().toString();//ESTOS CAMPOS LOS RECOJO ANTES DEL addLISTENERFORSINGLE para pasarselos al nuevo objeto(!!BIS-5-!!)
                final String categoriaMomentanea = miSpinnerCategoria.getSelectedItem().toString();
                final String precioMomentaneo = miEditextPrecio.getText().toString();

                Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int contador = 0;

                        for(DataSnapshot dataSnapshot3: dataSnapshot.getChildren()){

                            String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                            DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                            Producto miProd = new Producto(nombreMomentaneo, descripcionMomentanea, categoriaMomentanea, precioMomentaneo,null);//DE ESTE OBJETO HABLABA EN EL (!!BIS-5-!!)

                            ref.setValue(miProd);//Y finalmente le cambiamos tot el objeto de golpe, debido a que asi podemos modificar varios campos

                            Toast.makeText(PantallaUser.this, "El producto de nombre: "+nombreMomentaneo + " Ha sido modificado", Toast.LENGTH_SHORT).show();
                            contador = 1;
                        }
                        if(contador == 0){
                            Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                        }
                        contador = 0;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            limpioCamposProductos();//limpiamos primero
            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

        }

        if((miButtonTodoTerreno.getText().toString().equals("BUSCAR"))){

        if(!miStringEditableParaRadioGroup.equals("vacio")){

            if(miStringEditableParaRadioGroup.equals("categoria")) {
                String categoria = miSpinnerCategoria.getSelectedItem().toString();
                buscoTodosLosProductosPorCategoria(categoria);//le enviamos la categoria del spinner para que llene el ListView con esa categoria
            }
            if(miStringEditableParaRadioGroup.equals("usuario")){

                String usuario = miEditextNombre.getText().toString();
                //Usuario user =  buscoTodosLosproductosPorUsuario(usuario).get(0);
                //Toast.makeText(PantallaUser.this, ""+user.getAlias(), Toast.LENGTH_SHORT).show();
              buscoTodosLosproductosPorUsuario(usuario);

            }

        }else{
            Toast.makeText(PantallaUser.this, "No has seleccionado el radioButton", Toast.LENGTH_SHORT).show();
        }


            limpioCamposProductos();//limpiamos primero
            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

        }


            }
        });


    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private void buscoMisProductos(){

        miAuth = FirebaseAuth.getInstance();
        FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
        String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....

        bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
        //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
        bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....


        miUIDUserMomentaneo = miUIDUSER;





        bbdd2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                miArrayListProductos = new ArrayList<Producto>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){


                    final Producto producto = dataSnapshot1.getValue(Producto.class);

                    String claveMomentanea = dataSnapshot1.getKey();//ESta linea añadida el 03/02/2018,es para StorageReference de las imagenes de este usario y producto



                    mStorageRedImagenes = mStorageRef.child("images/"+miUIDUserMomentaneo+"/"+claveMomentanea+"/altavoz.png");


                    Log.d("PATH", "images/"+miUIDUserMomentaneo+"/"+claveMomentanea+"/altavoz.png");

                    mStorageRedImagenes.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'


                            Log.d("DENTRO DE ONSUCCES", uri.getPath());
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(uri);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            ImageView miImagencilla = null;
                            //miImagencilla.setImageBitmap(bmp);
                            //https://firebasestorage.googleapis.com/v0/b/fir-androidangel.appspot.com/o/images%2FNI6e8KqBzJfLYco7SGPb9MyvV6j2%2F-L4UxVYdG6MenFR0hs6S%2Faltavoz.png?alt=media&token=59f600d0-65ee-44b1-a200-e0ee29bb7069
                            //https://firebasestorage.googleapis.com/v0/b/fir-androidangel.appspot.com/o/images%2FNI6e8KqBzJfLYco7SGPb9MyvV6j2%2F-L4UxVYdG6MenFR0hs6S%2Faltavoz.png?alt=media&token=59f600d0-65ee-44b1-a200-e0ee29bb7069
                            miImagen.setImageBitmap(bmp);

                            producto.setMiImagen(miImagen);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });




                    miArrayListProductos.add(producto);
                }

                miAdapter = new Adapter(getApplicationContext(), miArrayListProductos);//veamos que hay una clase creada nuestra llamada Adapter, que recibe un arrayList de objetos Producto
                miListView.setAdapter(miAdapter);//finalmente le colocamos el adaptador al listView


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void buscoTodosLosProductosPorCategoria(final String categoriaFinalisima){//recibimos una categoria del spinner

        miAuth = FirebaseAuth.getInstance();

        bbdd = FirebaseDatabase.getInstance().getReference("todosproductos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE todosproductos.....


        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                miArrayListProductos = new ArrayList<Producto>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Producto producto = dataSnapshot1.getValue(Producto.class);

                    if(producto.getCategoria().equals(categoriaFinalisima))//si esta categoria es igual a la del producto que observamos, entonces la guardamos en el arrayList que le pondremos al adapter
                    miArrayListProductos.add(producto);
                }

                miAdapter = new Adapter(getApplicationContext(), miArrayListProductos);//veamos que hay una clase creada nuestra llamada Adapter, que recibe un arrayList de objetos Producto
                miListView.setAdapter(miAdapter);//finalmente le colocamos el adaptador al listView


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void buscoTodosLosproductosPorUsuario(final String usuarioFinal){

        miArrayListUsuarios = new ArrayList<Usuario>();
        final Usuario miUserFinalisimo = new Usuario();

        miAuth = FirebaseAuth.getInstance();

        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE usuarios.....
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Usuario usuario = dataSnapshot1.getValue(Usuario.class);


                    if(usuario.getAlias().equals(usuarioFinal)) {

                        miArrayListUsuarios.add(usuario);
                    }
                }
                if(miArrayListUsuarios.size() == 0){
                    Toast.makeText(PantallaUser.this, "No existe ningun usuario llamado asi", Toast.LENGTH_SHORT).show();
                }

                if(miArrayListUsuarios.size()>0) {
                    Toast.makeText(getApplicationContext(), "" + miArrayListUsuarios.get(0).getAlias(), Toast.LENGTH_SHORT).show();
                    Usuario user =  miArrayListUsuarios.get(0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void anadoProductoCamposVisibles(){
        miEditextNombre.setVisibility(View.VISIBLE);
        miEditextDescripción.setVisibility(View.VISIBLE);
        miEditextPrecio.setVisibility(View.VISIBLE);
        miSpinnerCategoria.setVisibility(View.VISIBLE);
        miButtonTodoTerreno.setText("AÑADIR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);
        miButtonGaleria.setVisibility(View.VISIBLE);
        miImagen.setVisibility(View.VISIBLE);
    }
    private void anadoProductoCamposInVisibles(){
        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setVisibility(View.INVISIBLE);
        miRadiogroup.setVisibility(View.INVISIBLE);
        miButtonGaleria.setVisibility(View.INVISIBLE);
    }
    private void borroProductoCamposVisibles(){
        miEditextNombre.setVisibility(View.VISIBLE);//SOLO ESTE ES VISIBLE
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setText("BORRAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);//BUENO, APARTE DEL BOTON
    }
    private void limpioCamposProductos(){
        miEditextNombre.setText("");
        miEditextDescripción.setText("");
        miEditextPrecio.setText("");
        miSpinnerCategoria.setSelected(false);
        miButtonTodoTerreno.setText("TODOTERRENO");
        miImagen.setImageBitmap(null);

    }
    private void modificoCamposProductosactivar(){

        miEditextNombre.setVisibility(View.VISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setText("MODIFICAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);

    }
    private void buscoCamposProductosActivar(){

        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);//campito
        miButtonTodoTerreno.setText("BUSCAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);
        miRadiogroup.setVisibility(View.VISIBLE);

    }
    private void pasoTransitorioHaciaNingunaParte(Producto producto){
        miEditextNombre.setText(producto.getNombre());
        miEditextNombre.setEnabled(false);

        miEditextDescripción.setText(producto.getDescripcion());
        miEditextDescripción.setVisibility(View.VISIBLE);

        int numTransitorio = 0;
        switch (producto.getCategoria()){
            case "Hogar":
                numTransitorio = 2;
                break;
            case "Tecnologia":
                numTransitorio = 0;
            break;
            case "Coches":
                numTransitorio = 1;
                break;
            case "Varios":
                numTransitorio = 3;
                break;

        }

        miSpinnerCategoria.setSelection(numTransitorio,true);
        miSpinnerCategoria.setVisibility(View.VISIBLE);

        miEditextPrecio.setText(producto.getPrecio());
        miEditextPrecio.setVisibility(View.VISIBLE);

        miButtonTodoTerreno.setText("GUARDAR CAMBIOS");//COMO VEMOS ESTE PUNTO ES CLAVE A LA HORA DE DESPUES ARRIBA DISCREPAR POR EL TEXTO DEL BOTON

    }

    private boolean hayAlgunCampoVacio(){

        String nombre = miEditextNombre.getText().toString();
        String descripcion = miEditextDescripción.getText().toString();
        String categoria = miSpinnerCategoria.getSelectedItem().toString();
        String precio = miEditextPrecio.getText().toString();
        boolean todoCorrecto = false;

        if (!TextUtils.isEmpty(nombre)) {
            if (!TextUtils.isEmpty(descripcion)) {
                if (!TextUtils.isEmpty(categoria)) {
                    if (!TextUtils.isEmpty(precio)) {

                        todoCorrecto = true;
                    } else {
                        Toast.makeText(this, "Campo nombre vacio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Campo descripción vacio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Campo categoría vacio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Campo precio vacio", Toast.LENGTH_SHORT).show();
        }
        return todoCorrecto;
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = null;
        Uri selectedImage;

        String filePath = null;
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = data.getData();
                    String selectedPath = selectedImage.getPath();

                    miUri = selectedImage;//guardamos esta Uri en una variable para luego pasarselo como referenciaDePath al método que sube imagenes


                    DocumentFile miRare = DocumentFile.fromSingleUri(getApplicationContext(),selectedImage);//con esta clase conseguimos coger el nombre real del archivo
                    miNombredeArchivo = miRare.getName();

                    Toast.makeText(this, miRare.getName(), Toast.LENGTH_SHORT).show();


                    miRutaReferencia = selectedPath;//me guardo este string para usarlo luego en el metodo añadir(de textButton) de arriba casi del tot, al subirlo a Cloud Storage

                    if (requestCode == SELECT_FILE) {

                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(selectedImage);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            // Transformamos la URI de la imagen a inputStream y este a un Bitmap
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                            // Ponemos nuestro bitmap en un ImageView que tengamos en la vista

                            miImagen.setImageBitmap(bmp);

                        }
                    }
                }
                break;
        }

    }
}

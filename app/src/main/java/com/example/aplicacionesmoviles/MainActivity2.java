package com.example.aplicacionesmoviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private FirebaseFirestore mifirestore;
    Button bt_guardar, bt_mostrar, bt_eliminar, bt_modificar,bt_volver;
    EditText txt_nombre, txt_edad, txt_carrera, txt_rut;
    TextView txtDatosMostrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mifirestore = FirebaseFirestore.getInstance();

        bt_mostrar = findViewById(R.id.bt_mostrar);
        bt_eliminar = findViewById(R.id.bt_eliminar);
        bt_modificar = findViewById(R.id.bt_modificar);
        txt_nombre = findViewById(R.id.txt_nombre);
        txt_edad = findViewById(R.id.txt_edad);
        txt_carrera = findViewById(R.id.txt_carrera);
        txt_rut = findViewById(R.id.txt_rut);
        txtDatosMostrados = findViewById(R.id.txt_datos_mostrados);
        bt_volver = findViewById(R.id.bt_volver);

        Intent intent = getIntent();
        if (intent != null) {
            String rut = intent.getStringExtra("rut");
            String nombre = intent.getStringExtra("nombre");
            String edad = intent.getStringExtra("edad");
            String carrera = intent.getStringExtra("carrera");

            txt_rut.setText(rut);
            txt_nombre.setText(nombre);
            txt_edad.setText(edad);
            txt_carrera.setText(carrera);
        }
        bt_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bt_guardar = findViewById(R.id.bt_siguiente);
        bt_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rut = txt_rut.getText().toString();
                String nombre = txt_nombre.getText().toString();
                String edad = txt_edad.getText().toString();
                String carrera = txt_carrera.getText().toString();

                guardarDatos(rut, nombre, Integer.parseInt(edad), carrera);
            }
        });

        bt_mostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatos();
            }
        });

        bt_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rut = txt_rut.getText().toString();
                eliminarPorRut(rut);
            }
        });

        bt_modificar = findViewById(R.id.bt_modificar);
        bt_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rutBuscar = txt_rut.getText().toString();
                String nuevoNombre = txt_nombre.getText().toString();
                int nuevaEdad = Integer.parseInt(txt_edad.getText().toString());
                String nuevaCarrera = txt_carrera.getText().toString();

                modificarPorRut(rutBuscar, nuevoNombre, nuevaEdad, nuevaCarrera);
            }
        });
    }

    public void guardarDatos(String rut, String nombreAl, int edadAl, String carreraAl) {
        mifirestore.collection("alumnos")
                .whereEqualTo("rut", rut)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("rut", rut);
                            map.put("nombreAl", nombreAl);
                            map.put("edadAl", edadAl);
                            map.put("carreraAl", carreraAl);

                            mifirestore.collection("alumnos").add(map)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getApplicationContext(), "Datos Ingresados", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Errores al guardar datos", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "El rut ya está en uso", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al verificar el rut", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void eliminarPorRut(String rut) {
        mifirestore.collection("alumnos")
                .whereEqualTo("rut", rut)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();

                            mifirestore.collection("alumnos").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Documento eliminado correctamente", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error al eliminar documento", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al buscar el documento", Toast.LENGTH_LONG).show();
                    }
                });
    }



    public void mostrarDatos() {
        final StringBuilder datosMostrados = new StringBuilder();
        mifirestore.collection("alumnos").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String rut = documentSnapshot.getString("rut");
                            String nombre = documentSnapshot.getString("nombreAl");
                            int edad = documentSnapshot.getLong("edadAl").intValue();
                            String carrera = documentSnapshot.getString("carreraAl");

                            String datos = String.format("Rut: %s\nNombre: %s\nEdad: %d\nCarrera: %s\n\n", rut, nombre, edad, carrera);
                            datosMostrados.append(datos);
                        }

                        txtDatosMostrados.setText(datosMostrados.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al obtener datos", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void modificarPorRut(String rutBuscar, String nuevoNombre, int nuevaEdad, String nuevaCarrera) {
        mifirestore.collection("alumnos")
                .whereEqualTo("rut", rutBuscar)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("nombreAl", nuevoNombre);
                            updates.put("edadAl", nuevaEdad);
                            updates.put("carreraAl", nuevaCarrera);

                            mifirestore.collection("alumnos").document(documentId)
                                    .update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Documento modificado correctamente", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error al modificar documento", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al buscar el documento", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
package com.example.angelus.firebaseandroidangel;


public class Usuario {

    private String alias;
    private String correo;
    private String direccion;
    private String nombreCompleto;
    private String miuid;//referencia de la autentificcion del usuario

    public Usuario(){ //importante poner un constructor por defecto para evitar problemas a la hora de recuperar los datos

    }

    public Usuario(String alias, String correo, String direccion, String nombreCompleto, String miuid) {
        this.alias = alias;
        this.correo = correo;
        this.direccion = direccion;
        this.nombreCompleto = nombreCompleto;
        this.miuid = miuid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public String getMiUID() {
        return miuid;
    }

    public void setMiUID(String miUID) {
        this.miuid = miuid;
    }
}

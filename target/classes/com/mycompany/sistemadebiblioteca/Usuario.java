package com.mycompany.sistemadebiblioteca;

/**
 * Representa un usuario registrado en la biblioteca.
 */
public class Usuario {

    String identificacion;
    String nombre;
    String contacto;

    public Usuario(String identificacion, String nombre,
                   String contacto) {

        this.identificacion = identificacion;
        this.nombre = nombre;
        this.contacto = contacto;
    }
}

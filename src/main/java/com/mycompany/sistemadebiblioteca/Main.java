/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.sistemadebiblioteca;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.IOException;

public class Main {

    static Scanner teclado = new Scanner(System.in);

    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static ArrayList<Libro> libros = new ArrayList<>();
    static ArrayList<Prestamo> prestamos = new ArrayList<>();

    static int siguientePrestamo = 1;

    static final Path CARPETA = Path.of("datos_biblioteca");
    static final Path ARCHIVO_USUARIOS = CARPETA.resolve("usuarios.txt");
    static final Path ARCHIVO_LIBROS = CARPETA.resolve("libros.txt");
    static final Path ARCHIVO_PRESTAMOS = CARPETA.resolve("prestamos.txt");

    public static void main(String[] args) {

        cargarDatos();

        int opcion;

        do {
            System.out.println("\n===== SISTEMA DE BIBLIOTECA NUEVA Y MEJORADA =====");
            System.out.println("1. Registrar usuario");
            System.out.println("2. Consultar usuarios");
            System.out.println("3. Registrar libro");
            System.out.println("4. Consultar libros");
            System.out.println("5. Prestar libro");
            System.out.println("6. Devolver libro");
            System.out.println("7. Consultar disponibilidad");
            System.out.println("8. Consultar historial de prestamos");
            System.out.println("9. Consultar prestamos vencidos");
            System.out.println("10. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = leerNumero();

            switch (opcion) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    consultarUsuarios();
                    break;
                case 3:
                    registrarLibro();
                    break;
                case 4:
                    consultarLibros();
                    break;
                case 5:
                    prestarLibro();
                    break;
                case 6:
                    devolverLibro();
                    break;
                case 7:
                    consultarDisponibilidad();
                    break;
                case 8:
                    consultarHistorial();
                    break;
                case 9:
                    consultarVencidos();
                    break;
                case 10:
                    guardarDatos();
                    System.out.println("Gracias por utilizar el sistema.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 10);

        teclado.close();
    }

 

    public static void registrarUsuario() {

        System.out.println("\n--- REGISTRO DE USUARIO ---");

        String identificacion = leerTexto("Identificacion: ");

        if (buscarUsuario(identificacion) != null) {
            System.out.println("La identificacion ya existe.");
            return;
        }

        String nombre = leerTexto("Nombre: ");
        String contacto = leerTexto("Telefono o correo: ");

        usuarios.add(new Usuario(identificacion, nombre, contacto));

        guardarDatos();

        System.out.println("Usuario registrado correctamente.");
    }

    public static void consultarUsuarios() {

        System.out.println("\n--- LISTA DE USUARIOS ---");

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        for (Usuario u : usuarios) {
            System.out.println("------------------------");
            System.out.println("Identificacion: " + u.identificacion);
            System.out.println("Nombre: " + u.nombre);
            System.out.println("Contacto: " + u.contacto);
        }
    }

 

    public static void registrarLibro() {

        System.out.println("\n--- REGISTRO DE LIBRO ---");

        String codigo = leerTexto("Codigo del libro: ");

        if (buscarLibro(codigo) != null) {
            System.out.println("El codigo ya existe.");
            return;
        }

        String titulo = leerTexto("Titulo: ");
        String autor = leerTexto("Autor: ");

        libros.add(new Libro(codigo, titulo, autor));

        guardarDatos();

        System.out.println("Libro registrado correctamente.");
    }

    public static void consultarLibros() {

        System.out.println("\n--- LISTA DE LIBROS ---");

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        for (Libro l : libros) {
            System.out.println("------------------------");
            System.out.println("Codigo: " + l.codigo);
            System.out.println("Titulo: " + l.titulo);
            System.out.println("Autor: " + l.autor);
            System.out.println("Estado: "
                    + (l.disponible ? "Disponible" : "Prestado"));
        }
    }

 

    public static void prestarLibro() {

        System.out.println("\n--- PRESTAMO DE LIBRO ---");

        String identificacion = leerTexto(
                "Identificacion del usuario: ");

        Usuario usuario = buscarUsuario(identificacion);

        if (usuario == null) {
            System.out.println("El usuario no esta registrado.");
            return;
        }

        String codigo = leerTexto("Codigo del libro: ");

        Libro libro = buscarLibro(codigo);

        if (libro == null) {
            System.out.println("El libro no existe.");
            return;
        }

        if (!libro.disponible) {
            System.out.println("El libro ya esta prestado.");
            return;
        }

        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaLimite = fechaPrestamo.plusDays(15);

        Prestamo nuevoPrestamo = new Prestamo(
                siguientePrestamo,
                usuario,
                libro,
                fechaPrestamo,
                fechaLimite
        );

        prestamos.add(nuevoPrestamo);

        siguientePrestamo++;

        libro.disponible = false;

        guardarDatos();

        System.out.println("Prestamo registrado correctamente.");
        System.out.println("Numero de prestamo: " + nuevoPrestamo.id);
        System.out.println("Usuario: " + usuario.nombre);
        System.out.println("Libro: " + libro.titulo);
        System.out.println("Fecha de prestamo: " + fechaPrestamo);
        System.out.println("Fecha limite de devolucion: " + fechaLimite);
    }



    public static void devolverLibro() {

        System.out.println("\n--- DEVOLUCION DE LIBRO ---");

        String codigo = leerTexto("Codigo del libro: ");

        Libro libro = buscarLibro(codigo);

        if (libro == null) {
            System.out.println("El libro no existe.");
            return;
        }

        if (libro.disponible) {
            System.out.println("El libro no tiene un prestamo activo.");
            return;
        }

        for (Prestamo p : prestamos) {

            if (p.libro.codigo.equals(libro.codigo) && p.activo) {

                p.activo = false;
                p.fechaRealDevolucion = LocalDate.now();

                libro.disponible = true;

                guardarDatos();

                System.out.println("Devolucion registrada correctamente.");
                System.out.println("Usuario: " + p.usuario.nombre);
                System.out.println("Libro: " + libro.titulo);
                System.out.println("Fecha de devolucion: "
                        + p.fechaRealDevolucion);

                return;
            }
        }

        System.out.println("No se encontro un prestamo activo.");
    }

 

    public static void consultarDisponibilidad() {

        System.out.println("\n--- DISPONIBILIDAD DE LIBROS ---");

        String codigo = leerTexto("Codigo del libro: ");

        Libro libro = buscarLibro(codigo);

        if (libro == null) {
            System.out.println("El libro no existe.");
        } else if (libro.disponible) {
            System.out.println("El libro esta disponible.");
        } else {
            System.out.println("El libro esta prestado.");
        }
    }



    public static void consultarHistorial() {

        System.out.println("\n--- HISTORIAL DE PRESTAMOS ---");

        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos registrados.");
            return;
        }

        for (Prestamo p : prestamos) {

            System.out.println("------------------------");
            System.out.println("Prestamo numero: " + p.id);
            System.out.println("Usuario: " + p.usuario.nombre);
            System.out.println("Identificacion: "
                    + p.usuario.identificacion);
            System.out.println("Libro: " + p.libro.titulo);
            System.out.println("Fecha de prestamo: " + p.fechaPrestamo);
            System.out.println("Fecha limite: " + p.fechaLimite);

            if (p.activo) {
                System.out.println("Estado: Prestamo activo");
            } else {
                System.out.println("Estado: Devuelto");
                System.out.println("Fecha real de devolucion: "
                        + p.fechaRealDevolucion);
            }
        }
    }


    public static void consultarVencidos() {

        System.out.println("\n--- PRESTAMOS VENCIDOS ---");

        LocalDate hoy = LocalDate.now();
        boolean encontrado = false;

        for (Prestamo p : prestamos) {

            if (p.activo && p.fechaLimite.isBefore(hoy)) {

                encontrado = true;

                System.out.println("------------------------");
                System.out.println("Usuario: " + p.usuario.nombre);
                System.out.println("Libro: " + p.libro.titulo);
                System.out.println("Fecha limite: " + p.fechaLimite);
                System.out.println("Dias de retraso: "
                        + java.time.temporal.ChronoUnit.DAYS.between(
                                p.fechaLimite, hoy));
            }
        }

        if (!encontrado) {
            System.out.println("No hay prestamos vencidos.");
        }
    }

 

    public static Usuario buscarUsuario(String identificacion) {

        for (Usuario u : usuarios) {

            if (u.identificacion.equalsIgnoreCase(identificacion)) {
                return u;
            }
        }

        return null;
    }

    public static Libro buscarLibro(String codigo) {

        for (Libro l : libros) {

            if (l.codigo.equalsIgnoreCase(codigo)) {
                return l;
            }
        }

        return null;
    }



    public static String leerTexto(String mensaje) {

        String texto;

        do {
            System.out.print(mensaje);
            texto = teclado.nextLine().trim();

            if (texto.isEmpty()) {
                System.out.println("Este campo no puede estar vacio.");
            }

        } while (texto.isEmpty());

        return texto;
    }

    public static int leerNumero() {

        while (true) {

            try {
                return Integer.parseInt(teclado.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.print("Ingrese un numero valido: ");
            }
        }
    }

 

    public static String codificar(String texto) {

        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodificar(String texto) {

        return new String(
                Base64.getDecoder().decode(texto),
                StandardCharsets.UTF_8);
    }

    public static void guardarDatos() {

        try {

            Files.createDirectories(CARPETA);

            ArrayList<String> datosUsuarios = new ArrayList<>();

            for (Usuario u : usuarios) {

                datosUsuarios.add(
                        codificar(u.identificacion) + ";"
                        + codificar(u.nombre) + ";"
                        + codificar(u.contacto)
                );
            }

            Files.write(ARCHIVO_USUARIOS, datosUsuarios,
                    StandardCharsets.UTF_8);

            ArrayList<String> datosLibros = new ArrayList<>();

            for (Libro l : libros) {

                datosLibros.add(
                        codificar(l.codigo) + ";"
                        + codificar(l.titulo) + ";"
                        + codificar(l.autor) + ";"
                        + l.disponible
                );
            }

            Files.write(ARCHIVO_LIBROS, datosLibros,
                    StandardCharsets.UTF_8);

            ArrayList<String> datosPrestamos = new ArrayList<>();

            for (Prestamo p : prestamos) {

                String fechaReal = p.fechaRealDevolucion == null
                        ? "-"
                        : p.fechaRealDevolucion.toString();

                datosPrestamos.add(
                        p.id + ";"
                        + codificar(p.usuario.identificacion) + ";"
                        + codificar(p.libro.codigo) + ";"
                        + p.fechaPrestamo + ";"
                        + p.fechaLimite + ";"
                        + fechaReal + ";"
                        + p.activo
                );
            }

            Files.write(ARCHIVO_PRESTAMOS, datosPrestamos,
                    StandardCharsets.UTF_8);

        } catch (IOException e) {

            System.out.println("Error al guardar los datos: "
                    + e.getMessage());
        }
    }



    public static void cargarDatos() {

        try {

            Files.createDirectories(CARPETA);

            // Cargar usuarios

            if (Files.exists(ARCHIVO_USUARIOS)) {

                for (String linea : Files.readAllLines(
                        ARCHIVO_USUARIOS, StandardCharsets.UTF_8)) {

                    try {
                        String[] d = linea.split(";", -1);

                        Usuario u = new Usuario(
                                decodificar(d[0]),
                                decodificar(d[1]),
                                decodificar(d[2])
                        );

                        if (buscarUsuario(u.identificacion) == null) {
                            usuarios.add(u);
                        }

                    } catch (RuntimeException e) {
                        System.out.println("Se omitio un usuario con datos invalidos.");
                    }
                }
            }

            // Cargar libros

            if (Files.exists(ARCHIVO_LIBROS)) {

                for (String linea : Files.readAllLines(
                        ARCHIVO_LIBROS, StandardCharsets.UTF_8)) {

                    try {
                        String[] d = linea.split(";", -1);

                        Libro l = new Libro(
                                decodificar(d[0]),
                                decodificar(d[1]),
                                decodificar(d[2])
                        );

                        l.disponible = Boolean.parseBoolean(d[3]);

                        if (buscarLibro(l.codigo) == null) {
                            libros.add(l);
                        }

                    } catch (RuntimeException e) {
                        System.out.println("Se omitio un libro con datos invalidos.");
                    }
                }
            }

            // Cargar prestamos

            if (Files.exists(ARCHIVO_PRESTAMOS)) {

                for (String linea : Files.readAllLines(
                        ARCHIVO_PRESTAMOS, StandardCharsets.UTF_8)) {

                    try {
                        String[] d = linea.split(";", -1);

                        int id = Integer.parseInt(d[0]);

                        Usuario usuario = buscarUsuario(decodificar(d[1]));
                        Libro libro = buscarLibro(decodificar(d[2]));

                        if (usuario == null || libro == null) {
                            continue;
                        }

                        LocalDate fechaPrestamo = LocalDate.parse(d[3]);
                        LocalDate fechaLimite = LocalDate.parse(d[4]);

                        LocalDate fechaReal = d[5].equals("-")
                                ? null
                                : LocalDate.parse(d[5]);

                        boolean activo = Boolean.parseBoolean(d[6]);

                        if (activo && (fechaReal != null || libro.disponible)) {
                            System.out.println("Se omitio un prestamo inconsistente.");
                            continue;
                        }

                        if (!activo && fechaReal == null) {
                            System.out.println("Se omitio un prestamo inconsistente.");
                            continue;
                        }

                        Prestamo p = new Prestamo(
                                id, usuario, libro,
                                fechaPrestamo, fechaLimite);

                        p.fechaRealDevolucion = fechaReal;
                        p.activo = activo;

                        prestamos.add(p);

                        if (activo) {
                            libro.disponible = false;
                        }

                        if (id >= siguientePrestamo) {
                            siguientePrestamo = id + 1;
                        }

                    } catch (RuntimeException e) {
                        System.out.println("Se omitio un prestamo con datos invalidos.");
                    }
                }
            }

            System.out.println("Datos cargados correctamente.");

        } catch (IOException e) {

            System.out.println("Error al cargar los datos: "
                    + e.getMessage());
        }
    }

 

    static class Usuario {

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

 

    static class Libro {

        String codigo;
        String titulo;
        String autor;
        boolean disponible;

        public Libro(String codigo, String titulo, String autor) {

            this.codigo = codigo;
            this.titulo = titulo;
            this.autor = autor;
            this.disponible = true;
        }
    }

  

    static class Prestamo {

        int id;
        Usuario usuario;
        Libro libro;
        LocalDate fechaPrestamo;
        LocalDate fechaLimite;
        LocalDate fechaRealDevolucion;
        boolean activo;

        public Prestamo(int id, Usuario usuario, Libro libro,
                LocalDate fechaPrestamo, LocalDate fechaLimite) {

            this.id = id;
            this.usuario = usuario;
            this.libro = libro;
            this.fechaPrestamo = fechaPrestamo;
            this.fechaLimite = fechaLimite;
            this.fechaRealDevolucion = null;
            this.activo = true;
        }
    }
}

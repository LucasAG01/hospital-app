package org.example.hospital;

import java.sql.*;

public class hospitalApp {

    // APARTADO 1 CRear Especialidad

    public static void crearEspecialidad(String nombreEspecialidad){
        String sql = "INSERT INTO especialidades (nombre_especialidad) VALUES (?)";

        try(Connection con = conexionDB.getConexionPostgre();
        PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, nombreEspecialidad);
            int filas = ps.executeUpdate();
            System.out.println("Especialidad añadida: "+nombreEspecialidad+ filas +" fila"  );

        } catch (SQLException e) {
            System.err.println("Error al insertar el especialidad: "+nombreEspecialidad);
        }
    }


    // APARTADO 2 Crear médico

    public static void crearMedico(String nombreMedico, String nif, int telefono, String email){
        String sql = "INSERT INTO medicos (nombre_medico, datos_contacto) values (?,ROW(?,?,?,?))";

        try (Connection con = conexionDB.getConexionPostgre();
        PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, nombreMedico);
            ps.setString(2, nombreMedico);
            ps.setString(3, nif);
            ps.setString(4, String.valueOf(telefono));
            ps.setString(5, email);

            int filas = ps.executeUpdate();
            System.out.println("Médico añadido: "+nombreMedico+ filas +" fila"  );

        } catch (SQLException e) {
            System.err.println("Error al crear medico "+e.getMessage());
            e.printStackTrace();
        }
    }









}

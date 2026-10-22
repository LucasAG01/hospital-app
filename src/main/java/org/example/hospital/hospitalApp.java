package org.example.hospital;

import java.sql.*;
import java.time.LocalDate;

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
            System.out.println("Médico añadido: "+nombreMedico+ " "+filas +" fila"  );

        } catch (SQLException e) {
            System.err.println("Error al crear medico "+e.getMessage());
            e.printStackTrace();
        }
    }





    // Apartado 3 Eliminar médico por ID

    public static void eliminarMedico(int id){
        String sqlSelect = "SELECT nombre_medico FROM medicos WHERE id_medico = ?";
        String sqlDelete = "DELETE FROM medicos WHERE id_medico = ?";

        try(Connection con = conexionDB.getConexionPostgre()){

           // Comprobar si existe
            boolean existe = false;
            String nombreMedico = "";

            try (PreparedStatement ps = con.prepareStatement(sqlSelect)){
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        existe = true;
                        nombreMedico = rs.getString("nombre_medico");
                    }
                }
            }

            // Si existe borrar

            if(existe){
                try (PreparedStatement ps = con.prepareStatement(sqlDelete)){
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    System.out.println("Medico eliminado");
                }
            }else {
                System.out.println("Medico no encontrado");
            }


        } catch (SQLException e) {
            System.err.println("Error al eliminar medico "+e.getMessage());
            if(e.getMessage().contains("llave foránea")|| e.getMessage().contains("foreign key")){
                System.out.println("medico no borrado, tiene tratamientos");
            }
        }
    }




    // Apartado 4 Crear nuevo paciente

    public static void crearPaciente(String nombre, String email, LocalDate fechaNacimiento) {

        String sql = "INSERT INTO pacientes (nombre, email, fecha_nacimiento) VALUES (?,?,?)";

        try(Connection con = conexionDB.getConexionMysql();
        PreparedStatement ps = con.prepareStatement(sql)){

            ps.setString(1, nombre);
            ps.setString(2, email);

            // Conversion Localdate a java.sql.date
            ps.setDate(3, java.sql.Date.valueOf(fechaNacimiento));

            int filas = ps.executeUpdate();
            System.out.println("Paciente creado: "+ nombre+" ("+filas+" fila)");


        } catch (SQLException e) {
            System.err.println("Error al crear paciente "+e.getMessage());
            e.printStackTrace();
        }
    }



    // Apartado 5
    // ON DELETE CASCADE -> Esto significa que mysql borra sus citas y tratamientos asociados.

    public static void eliminarPaciente(int id){

        String sqlSelect = "SELECT nombre FROM pacientes WHERE id_paciente = ?";
        String sqlDelete = "DELETE FROM pacientes WHERE id_paciente = ?";

        try(Connection con = conexionDB.getConexionMysql()){

            // Busqueda
            boolean existe = false;
            String nombrePaciente = "";
            try (PreparedStatement ps = con.prepareStatement(sqlSelect)){
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        existe = true;
                        nombrePaciente = rs.getString("nombre");
                    }else {
                        System.out.println("Paciente no encontrado");
                    }
                }
            }

            // Borrado
            if(existe){
                try (PreparedStatement ps = con.prepareStatement(sqlDelete)){
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    System.out.println("Paciente eliminado con ID:"+id+ " nombre: "+nombrePaciente);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar paciente "+e.getMessage());
            e.printStackTrace();
        }

    }


    // Apartado 6 Crear tratamiento
    /*
        1. Postgre busca id_especialidad a partir del nombreEspecialidad
        2. Postgre busca el id_medico  apartir de nifMedico. (en datos_contacto)
        3. Postgre Calcular cual será el nuevo IdTratamiento (siguitne numero)
        4. Postgre Insertar tratamiento en su tabla
        5. Mysql Insertar el mismo tratamiento (same ID) en su tabla

        Acceder al NIF tipo compuesto
        SELECT id_medico FROM medicos WHERE (datos_contacto).nif = ?
         */

    public static void crearTratamiento(String nombre, String descripcion, String nombreEspecialidad, String nifMedico){

        String urlMysql ="jdbc:mysql://localhost:3306/hospital_mysql";

        try(Connection conPostgre = conexionDB.getConexionPostgre();
        Connection conMysql = conexionDB.getConexionMysql()){

            // 1. Obtener ID especialidad

            int idEspecialidad = -1;
            String sqlEspe= "SELECT id_especialidad FROM hospital.especialidades WHERE nombre_especialidad = ?";
            try(PreparedStatement ps = conPostgre.prepareStatement(sqlEspe)){
                    ps.setString(1, nombreEspecialidad);
                    try (ResultSet rs = ps.executeQuery()){
                        if (rs.next()) {
                            idEspecialidad = rs.getInt("id_especialidad");
                        }else{
                            System.out.println("Paciente no encontrado");
                            return;
                        }
                    }
            }

            // 2. Obtener id del médico por nif
            int idMedico = -1;

            // (datos_contacto).nif para acceder tipo compuesto
            String sqlMed= "SELECT id_medico FROM medicos WHERE (datos_contacto).nif = ?";
            try(PreparedStatement ps = conPostgre.prepareStatement(sqlMed)){
                ps.setString(1, nifMedico);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        idMedico = rs.getInt("id_medico");
                    }else{
                        System.out.println("Error. Medico con nif:"+nifMedico +" no encontrado");
                    }
                }
            }

            // 3. Calcular el nueov ID de tratamiento

            //Buscar id mas alto actual + 1
            int nuevoIdTratamiento = 1;
            String sqlMax = "SELECT COALESCE(MAX(id_tratamiento),0) + 1 AS nuevo_id FROM hospital.tratamientos";

            try(PreparedStatement ps =  conPostgre.prepareStatement(sqlMax);
            ResultSet rs = ps.executeQuery()){

                if (rs.next()) {
                    nuevoIdTratamiento = rs.getInt("nuevo_id");
                }

            }

            // 4. Intertar en POStgreSQL
            String sqlPostgre = "INSERT INTO hospital.tratamientos (id_tratamiento, id_medico, id_especialidad) VALUES (?,?,?)";
            try(PreparedStatement ps = conPostgre.prepareStatement(sqlPostgre)){
                ps.setInt(1, nuevoIdTratamiento);
                ps.setInt(2, idMedico);
                ps.setInt(3, idEspecialidad);
                ps.executeUpdate();
            }

            // 5. Insertar en mysql con el mismo ID

            String SQLMysql= "INSERT INTO tratamientos (id_tratamiento, nombre_tratamiento, descripcion) VALUES (?,?,?)";

            try(PreparedStatement ps = conMysql.prepareStatement(SQLMysql)){
                ps.setInt(1, nuevoIdTratamiento);
                ps.setString(2, nombre); //nombre al principio de la funcion que pasamos
                ps.setString(3, descripcion);
                ps.executeUpdate();
            }

            System.out.println("Tratamiento agregado en ambas BD: "+nuevoIdTratamiento);

        } catch (SQLException e) {
            System.err.println("Error al crear tratamiento "+e.getMessage());
            e.printStackTrace();
        }
    }




    // Apartado 7 Elimianr Tratamiento por nombre

    // mysql -> tiene nombre_tratamiento, mysql NO, solo id

    // 1. Buscar el Id por nombre een Mysql
    // 2. Borrar tratamiento (y relaciones salas_tratamientos)
    // 3. Borrar tratamiento een Mysql

    // Como no está ON DELETE CASCADE en salas_tratamientos, primero hay que borrar la relacion.

    public static void eliminarTratamientoPorNombre (String nombre){

        try(Connection conMysql = conexionDB.getConexionMysql();
        Connection conPostgre = conexionDB.getConexionPostgre()){

            // 1.- Buscar Id en MYSQL

            int idTratamiento = -1;
            String sqlBuscar = "SELECT id_tratamiento FROM tratamientos WHERE nombre_tratamiento = ?";

            try(PreparedStatement ps = conMysql.prepareStatement(sqlBuscar)){
                ps.setString(1, nombre);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        idTratamiento = rs.getInt("id_tratamiento");
                    }else {
                        System.out.println("tratamiento no encontrado por nombre");
                        return;
                    }
                }
            }


            // 2.- Borrar een Postgre (primero tabla intermedia)

                // Borrar de salas_tratamientos
            String sqlDelSalas = "DELETE FROM hospital.salas_tratamientos WHERE id_tratamiento = ?";
            try(PreparedStatement ps = conPostgre.prepareStatement(sqlDelSalas)){
                ps.setInt(1, idTratamiento);
                ps.executeUpdate();
            }

                // Borrar tratamieentos
            String sqlDelPostgre = "DELETE FROM hospital.tratamientos WHERE id_tratamiento = ?";
            try(PreparedStatement ps = conPostgre.prepareStatement(sqlDelPostgre)){
                ps.setInt(1, idTratamiento);
                ps.executeUpdate();
            }


            // 3.- Borrar en Mysql, aquñi como hay DELETE CASCADE, se borrarán relaciones
            String delMysql = "DELETE FROM tratamientos WHERE id_tratamiento = ?";
            try(PreparedStatement ps = conMysql.prepareStatement(delMysql)){
                ps.setInt(1, idTratamiento);
                int filas = ps.executeUpdate();

                System.out.println("Tratamiento '" + nombre + "' (ID: " + idTratamiento + ") eliminado de ambas bases de datos.");

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // Apartado 8 Listar tratamientos con menos de X pacientes asignados
    public static void listarTratamientosConPocosPacientes(int cantidad){

        String sql = "SELECT t.nombre_tratamiento, COUNT(pt.id_paciente) AS total_pacientes "
                + "FROM tratamientos t "
                + "LEFT JOIN pacientes_tratamientos pt ON t.id_tratamiento = pt.id_tratamiento "
                + "GROUP BY t.id_tratamiento, t.nombre_tratamiento "
                + "HAVING COUNT(pt.id_paciente) < ?";

        try(Connection con = conexionDB.getConexionMysql();
        PreparedStatement ps = con.prepareStatement(sql)){

            ps.setInt(1, cantidad);
            try (ResultSet rs = ps.executeQuery()){
                System.out.println("Tratamientos con pocos pacientes " + cantidad);

                while (rs.next()) {
                    String nombre = rs.getString("nombre_tratamiento");
                    int total =  rs.getInt("total_pacientes");
                    System.out.println("Tratamiento: "+nombre+ " | Pacientes: "+total);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tratamientos: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // Apartado 9 Obtener el total de citas realizadas por cada paciente
    public static void obtenerTotalCitasPorPaciente(){

        String sql = "SELECT p.nombre, COUNT(c.id_cita) AS total_citas "
                + "FROM pacientes p "
                + "LEFT JOIN citas c ON p.id_paciente = c.id_paciente "
                + "GROUP BY p.id_paciente, p.nombre";

        try (Connection con = conexionDB.getConexionMysql();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

            System.out.println("--- Total de citas por paciente ---");

            while (rs.next()) {
                String nombrePaciente = rs.getString("nombre");
                int totalCitas = rs.getInt("total_citas");
                System.out.println("Paciente: " + nombrePaciente + " | Citas: " + totalCitas);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total de citas: " + e.getMessage());
            e.printStackTrace();
        }
    }















}

package org.example.hospital;

import java.sql.*;


public class conexionDB {

    // MYSQL
    private static final String URL_MYSQL = "jdbc:mysql://localhost:3306/hospital_mysql"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER_MYSQL = "root";
    private static final String PASS_MYSQL = "Lucas2001";

    // POSTGRE

    private static final String URL_POSTGRE = "jdbc:postgresql://localhost:5432/hospital_postgre"
            + "?currentSchema=hospital";
    private static final String USER_POSTGRE = "lucas";
    private static final String PASS_POSTGRE = "Lucas2001";



    // COnExiones

    private static Connection conexionMysql = null;
    private static Connection conexionPostgre = null;


    // COnstructor

    private conexionDB(){}

    // DEvuleve conexion Msql
    public static Connection getConexionMysql() throws SQLException {

        if(conexionMysql == null || conexionMysql.isClosed()){
            conexionMysql = DriverManager.getConnection(URL_MYSQL, USER_MYSQL, PASS_MYSQL);
        }
        return conexionMysql;
    }


    public static Connection getConexionPostgre() throws SQLException {

        if (conexionPostgre == null || conexionPostgre.isClosed()){
            conexionPostgre = DriverManager.getConnection(URL_POSTGRE,USER_POSTGRE,PASS_POSTGRE);
        }
        return conexionPostgre;
    }


    // CERRAR conexiones al final

    public static void cerrar(){
        try{
            if(conexionMysql != null && !conexionMysql.isClosed()){
                conexionMysql.close();
                System.out.println("MYSQL Cerrado");
            }
            if(conexionPostgre != null && !conexionPostgre.isClosed()){
                conexionPostgre.close();
                System.out.println("POSTGRE Cerrado");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion"+ e.getMessage());
        }
    }

}

package org.example.hospital;

import java.time.LocalDate;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        System.out.println("==================================================");
        System.out.println("   SISTEMA DE GESTIÓN HOSPITALARIA       ");
        System.out.println("==================================================");

        do{
            System.out.println("\n--- MENÚ DE OPCIONES ---");
            System.out.println("1.  Crear nueva especialidad (PostgreSQL)");
            System.out.println("2.  Crear nuevo médico (PostgreSQL)");
            System.out.println("3.  Eliminar médico por ID (PostgreSQL)");
            System.out.println("4.  Crear nuevo paciente (MySQL)");
            System.out.println("5.  Eliminar paciente por ID (MySQL)");
            System.out.println("6.  Crear nuevo tratamiento (MySQL + PostgreSQL)");
            System.out.println("7.  Eliminar tratamiento por nombre (MySQL + PostgreSQL)");
            System.out.println("8.  Listar tratamientos con pocos pacientes (MySQL)");
            System.out.println("9.  Total de citas por paciente (MySQL)");
            System.out.println("10. Cantidad de tratamientos por sala (PostgreSQL)");

            System.out.println("0.  Salir y cerrar conexiones");
            System.out.print("\nElige una opción: ");




            try{

                opcion = sc.nextInt();
                sc.nextLine();


                switch (opcion){
                    case 1:
                        System.out.println("Nombre de nueva especialidad");
                        hospitalApp.crearEspecialidad("EspecialidadNueva");
                        break;

                    case 2:
                        System.out.println("Creacion ");
                        hospitalApp.crearMedico("Dr. lucas", "11223344D", 600777888, "lucas@hospital.com");
                        break;

                    case 3:
                        System.out.println("Eliminacion ID ");
                        hospitalApp.eliminarMedico(1);
                        hospitalApp.eliminarMedico(99);
                        break;

                    case 4:
                        System.out.println("creacion paciente");
                        hospitalApp.crearPaciente("PAco Gente", "paccoo@gmail.com", LocalDate.of(1967,12,25));
                        break;

                    case 5:
                        System.out.println("ELiminacion paciente");
                        hospitalApp.eliminarPaciente(1);
                        hospitalApp.eliminarPaciente(99);
                        break;

                        case 6:
                            System.out.println("crear tratamiento");
                            hospitalApp.crearTratamiento("Limpieza oral", "Limpieza vias orales", "Traumatología", "12345678A");
                        break;

                        case 7:
                            System.out.println("Eliminar tratamiento");
                            hospitalApp.eliminarTratamientoPorNombre("Limpieza oral");
                            hospitalApp.eliminarTratamientoPorNombre("Tratamiento Inexistente");
                            break;
                    case 8:
                        System.out.print("Mostrar tratamientos con menos de X pacientes. Introduce X: ");
                        int cant = sc.nextInt();
                        sc.nextLine();
                        hospitalApp.listarTratamientosConPocosPacientes(cant);
                        break;
                        case 9:
                            hospitalApp.obtenerTotalCitasPorPaciente();
                            break;

                    case 10:
                        hospitalApp.obtenerCantidadTratamientosPorSala();
                        break;

                    case 0:
                            System.out.println("Saliendo del ejercicio");
                            break;

                            default:
                                System.out.println("Opcion no valida. Inténtalo de nuevo");


                }




            }catch(Exception e){
                System.out.println("Error, introduce numero valido");
                sc.nextLine();
                opcion = -1;
            }


        }while(opcion != 0);

        conexionDB.cerrar();
        sc.close();
        System.out.println("FINALIZADO");


    }
}

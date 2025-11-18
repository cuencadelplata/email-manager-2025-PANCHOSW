package com.ucp.gestor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MainDemo {
    public static void main(String[] args) {
        GestorEmails gestor = new GestorEmails();

        // contactos
        Contacto prof = gestor.crearContacto("Juan Perez", "juan.perez@ucp.edu.ar");
        Contacto alumno = gestor.crearContacto("María López", "maria.lopez@gmail.com");
        Contacto director = gestor.crearContacto("Directora", "director@ucp.edu.ar");

        // crear y guardar borrador
        Email e1 = gestor.crearEmail(prof, "Práctica 1", "Recordatorio entrega TP", Arrays.asList(alumno));
        System.out.println("Borradores: " + gestor.getTodosEnBandeja(BandejaType.BORRADORES).size());

        // enviar email
        gestor.enviar(e1);
        System.out.println("Enviados: " + gestor.getTodosEnBandeja(BandejaType.ENVIADOS).size());
        System.out.println("Entrada: " + gestor.getTodosEnBandeja(BandejaType.ENTRADA).size());

        // crear más emails en entrada
        Email e2 = gestor.crearEmail(director, "Reunión", "Reunión con Directora @ucp.edu.ar", Arrays.asList(alumno));
        gestor.enviar(e2);

        Email e3 = gestor.crearEmail(prof, "Info UCP", "Todo sobre @ucp.edu.ar", Arrays.asList(alumno, director));
        gestor.enviar(e3);

        // ---------------------------------------
        // Filtros: ejemplo funcional
        // ---------------------------------------
        // filtro1: buscar correos que contengan "@ucp.edu.ar" en remitente o en destinatarios
        Predicate<Email> contieneUCP = email ->
                (email.getRemitente() != null && email.getRemitente().getEmail().contains("@ucp.edu.ar")) ||
                email.getPara().stream().anyMatch(c -> c.getEmail().contains("@ucp.edu.ar"));

        Filtro filtroUCP = new Filtro("Correos UCP", contieneUCP);

        // filtro2: asunto contiene "Reunión"
        Filtro filtroReunion = new Filtro("Asunto Reunión", email ->
                email.getAsunto() != null && email.getAsunto().toLowerCase().contains("reunión".toLowerCase())
        );

        // combinamos (AND)
        Filtro combinado = filtroUCP.and(filtroReunion);

        List<Email> encontrados = gestor.aplicarFiltroSobreBandeja(combinado, BandejaType.ENTRADA);
        System.out.println("Encontrados por filtro combinado en ENTRADA: " + encontrados.size());
        encontrados.forEach(System.out::println);

        // búsqueda de texto libre en bandeja entrada
        List<Email> busqueda = gestor.buscarTextoEnBandeja(BandejaType.ENTRADA, "ucp");
        System.out.println("Busqueda 'ucp' en ENTRADA: " + busqueda.size());
    }
}

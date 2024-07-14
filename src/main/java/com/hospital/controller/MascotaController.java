package com.hospital.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.entity.Ingreso;
import com.hospital.entity.Mascota;
import com.hospital.exception.MascotaNoEncontradaException;
import com.hospital.services.MascotaServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api")
public class MascotaController {

	@Autowired
	private MascotaServiceImpl mascotaService;
	

	/**
	 *  Peticion 1
	 *  
	 *  Usando una petición HTTP GET + el ID de la mascota: 
	 *  Se devolverá todos los datos pertenecientes a la mascota cuyo ID coincida con {idMascota}
	 */
	
    @GetMapping("/mascota/{idMascota}")
    @Operation(summary = "Listado de los datos de una mascota por su ID")
    public ResponseEntity<?> obternerMascotaPorId(@Parameter(description="ID de la mascota a obtener") @PathVariable Long idMascota) {
        Optional<Mascota> mascota = mascotaService.obtenerMascotaPorId(idMascota);
        if (mascota.isPresent()) {
            return ResponseEntity.ok(mascota.get());
        } else {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la mascota con ID: " + idMascota);
        }
    }
    
	/**
	 *  Peticion 2
	 *  
	 *  Usando una petición HTTP GET + el ID de la mascota:
	 *  Devolverá un listado con todos los ingresos que ha tenido la mascota con {ID idMascota}
	 */
    
    @GetMapping("/mascota/{idMascota}/ingreso")
    @Operation(summary = "Listado de los ingresos de una mascota por su ID")
    public ResponseEntity<?> obtenerIngresosPorMascotaId(@Parameter(description="ID de la mascota a obtener") @PathVariable Long idMascota) {
        try {
            List<Ingreso> ingresos = mascotaService.buscarIngresosPorMascotaId(idMascota);
            return ResponseEntity.ok(ingresos);
        } catch (MascotaNoEncontradaException e) {
        	return ((BodyBuilder) ResponseEntity.notFound()).body(e.getMessage());
        }
    }
	
	/**
	 *  Peticion 3
	 *  
	 *  Usando una petición HTTP POST + un JSON con los datos de una mascota:
	 *  Se guardará en base de datos dicha mascota y se devolverá un JSON con toda la información guardada
	 *  (que debe incluir el ID de esa mascota en el sistema).
	 */
    
    @PostMapping("/mascota")
    @Operation(summary = "Insertar mascota en el sistema")
    public ResponseEntity<?> crearMascota(@RequestBody Mascota mascota) {
        try {
            Mascota mascotaCreada = mascotaService.crearMascota(mascota);
            return ResponseEntity.status(HttpStatus.CREATED).body(mascotaCreada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la mascota: " + e.getMessage());
        }
    }

    
	/**
	 *  Peticion 4
	 *  
	 *  Usando una petición HTTP DELETE + el ID de la mascota:
	 *  Da de baja la mascota en el sistema (no se podrá hacer ninguna operación con ella),
	 *  pero NO la borra de base de datos
	 */
	
    @DeleteMapping("/mascota/{idMascota}")
    @Operation(summary = "Dar de baja a una mascota por su ID")
    public ResponseEntity<String> darDeBajaMascota(@Parameter(description="ID de la mascota para dar de baja") @PathVariable Long idMascota) {
        boolean bajaExitosa = mascotaService.darDeBajaMascota(idMascota);

        if (bajaExitosa) {
            return ResponseEntity.ok("Mascota dada de baja correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la mascota con ID: " + idMascota);
        }
    }
	
	
}

package com.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.entity.Ingreso;
import com.hospital.exception.FechaFinalizacionRequeridaException;
import com.hospital.exception.FechaFormatoInvalidoException;
import com.hospital.exception.IngresoNoEncontradoException;
import com.hospital.exception.MascotaException;
import com.hospital.exception.RegistradorNoAutorizadoException;
import com.hospital.services.IngresoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api")
public class IngresoController {
	
    @Autowired
    private IngresoServiceImpl ingresoService;
    
	/**
	 *  Peticion 1
	 * 
	 *  Listado de todos los ingresos que tiene registrados el Hospital Clínico Veterinario.
	 */
	
    @GetMapping("/ingreso")
    @Operation(summary = "Listado de todos los ingresos")
    public List<Ingreso> obtenerTodosIngresos() {
        return ingresoService.obtenerTodosIngresos();
    }
    
	/**
	 *  Peticion 2
	 *  
	 *  Usando una petición HTTP POST + un JSON con el ID de la mascota (previamente registrada) y fecha de ingreso: 
	 *  Creará un nuevo ingreso en base de datos para la mascota indicada, con estado “ALTA”
	 */
	
    @PostMapping("/ingreso/{mascotaId}")
    @Operation(summary = "Insertar ingreso de una mascota")
    public ResponseEntity<?> crearIngreso(
    		@Parameter(description="ID de la mascota a insertar")
    		@PathVariable Long mascotaId, 
    		@RequestBody Ingreso ingresoRequest) {
        try {
            Ingreso ingresoCreado = ingresoService.crearIngreso(mascotaId, ingresoRequest.getFechaAlta(), ingresoRequest.getDniRegistrador());
            return ResponseEntity.status(HttpStatus.CREATED).body(ingresoCreado);
        } catch (MascotaException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RegistradorNoAutorizadoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
	/**
	 *  Peticion 3
	 *  
	 *  Usando una petición HTTP PUT + el ID de la mascota y del ingreso + JSON de datos: 
	 *  Modificará la información de un ingreso, cambiando su estado y/o la fecha de fin de ingreso
	 *  
	 */
	
    @PutMapping("/ingreso/{mascotaId}/{ingresoId}")
    @Operation(summary = "Actualizar ingreso")
    public ResponseEntity<Ingreso> actualizarIngreso(
    		@Parameter(description="ID de la mascota a actualizar")
    		@PathVariable Long mascotaId,
    		@Parameter(description="ID del ingreso a actualizar")
            @PathVariable Long ingresoId,
            @RequestBody Ingreso ingresoDetalles) throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException {

        Ingreso ingresoActualizado = ingresoService.actualizarIngreso(mascotaId, ingresoId, ingresoDetalles);
        return ResponseEntity.ok(ingresoActualizado);
    }
    
	/**
	 *  Peticion 4
	 *  
	 *  Usando una petición HTTP DELETE + el ID del ingreso: 
	 *  Modifica el estado del ingreso a “ANULADO”
	 * 
	 */
	
    @DeleteMapping("/ingreso/{ingresoId}")
    @Operation(summary = "Modificar ingreso a ANULADO")
    public ResponseEntity<Void> anularIngreso(@Parameter(description="ID del ingreso a modificar") @PathVariable Long ingresoId) throws IngresoNoEncontradoException {
        ingresoService.anularIngreso(ingresoId);
        return ResponseEntity.noContent().build();
    }
	
	
	
	
}

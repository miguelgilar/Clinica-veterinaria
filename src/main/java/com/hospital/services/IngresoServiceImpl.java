package com.hospital.services;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.entity.Estado;
import com.hospital.entity.Ingreso;
import com.hospital.entity.Mascota;
import com.hospital.exception.FechaFinalizacionRequeridaException;
import com.hospital.exception.FechaFormatoInvalidoException;
import com.hospital.exception.IngresoNoEncontradoException;
import com.hospital.exception.MascotaException;
import com.hospital.exception.RegistradorNoAutorizadoException;
import com.hospital.repository.IngresoRepository;
import com.hospital.repository.MascotaRepository;

@Service
public class IngresoServiceImpl {
	
    @Autowired
    private IngresoRepository ingresoRepository;
    
    @Autowired
    private MascotaRepository mascotaRepository;
    
	/**
	 *  Peticion 1
	 *  
	 *  Usando una petición HTTP GET: 
	 *  Devolverá una lista de todos los ingresos que tiene registrados el Hospital Clínico Veterinario.
	 */
	
    public List<Ingreso> obtenerTodosIngresos() {
        return ingresoRepository.findAll();
    }
    
	/**
	 *  Peticion 2
	 *  
	 *  Usando una petición HTTP POST + un JSON con el ID de la mascota (previamente registrada) y fecha de ingreso: 
	 *  Creará un nuevo ingreso en base de datos para la mascota indicada, con estado “ALTA”
	 */
	
    public Ingreso crearIngreso(Long mascotaId, LocalDate fechaAlta, String dniRegistrador) 
            throws MascotaException, RegistradorNoAutorizadoException {
        Optional<Mascota> mascotaOptional = mascotaRepository.findById(mascotaId);

        if (mascotaOptional.isPresent()) {
            Mascota mascota = mascotaOptional.get();

            if (!mascota.getDniResponsable().equals(dniRegistrador)) {
                throw new RegistradorNoAutorizadoException("El registrador no está autorizado para registrar esta mascota.");
            }

            Ingreso ingreso = new Ingreso();
            ingreso.setFechaAlta(fechaAlta);
            ingreso.setEstado(Estado.ALTA);
            ingreso.setMascota(mascota); // Aquí se asigna la mascota a la entidad ingreso
            ingreso.setDniRegistrador(dniRegistrador);

            return ingresoRepository.save(ingreso);
        } else {
            throw new MascotaException("Mascota no encontrada");
        }
    }
	
	
	/**
	 *  Peticion 3
	 *  
	 *  Usando una petición HTTP PUT + el ID de la mascota y del ingreso + JSON de datos: 
	 *  Modificará la información de un ingreso, cambiando su estado y/o la fecha de fin de ingreso
	 *  
	 */
	
    public Ingreso actualizarIngreso(Long mascotaId, Long ingresoId, Ingreso ingresoDetalles) 
            throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException {
        Optional<Ingreso> optionalIngreso = ingresoRepository.findById(ingresoId);

        if (!optionalIngreso.isPresent()) {
            throw new IngresoNoEncontradoException("Ingreso no encontrado con ID : " + ingresoId);
        }

        Ingreso ingreso = optionalIngreso.get();

        // Validar que un ingreso en estado FINALIZADO tenga fecha de finalización
        if (ingresoDetalles.getEstado() == Estado.FINALIZADO) {
            String fechaFinalizacion = ingresoDetalles.getFechaFinalizacion();
            if (fechaFinalizacion == null || fechaFinalizacion.trim().isEmpty()) {
                throw new FechaFinalizacionRequeridaException("El estado FINALIZADO requiere una fecha de finalización válida.");
            }

            // Validar que la fecha esté en el formato correcto
            try {
                LocalDate.parse(fechaFinalizacion);
            } catch (DateTimeParseException e) {
                throw new FechaFormatoInvalidoException("Formato de fecha inválido. Por favor, use el formato yyyy-MM-dd.");
            }
        }

        // Actualizar el ingreso con los detalles proporcionados
        ingreso.setEstado(ingresoDetalles.getEstado());
        ingreso.setFechaFinalizacion(ingresoDetalles.getFechaFinalizacion());

        return ingresoRepository.save(ingreso);
    }
    
	/**
	 *  Peticion 4
	 *  
	 *  Usando una petición HTTP DELETE + el ID del ingreso: 
	 *  Modifica el esado del ingreso a “ANULADO”
	 * 
	 */
	
    public void anularIngreso(Long id) throws IngresoNoEncontradoException {
        Optional<Ingreso> ingresoOptional = ingresoRepository.findById(id);
        if (ingresoOptional.isPresent()) {
            Ingreso ingreso = ingresoOptional.get();
            ingreso.setEstado(Estado.ANULADO);
            ingresoRepository.save(ingreso);
        } else {
            throw new IngresoNoEncontradoException("Ingreso no encontrado con id: " + id);
        }
    }
    
}

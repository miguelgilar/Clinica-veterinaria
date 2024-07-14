package com.hospital.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hospital.entity.Ingreso;
import com.hospital.entity.Mascota;
import com.hospital.exception.MascotaNoEncontradaException;
import com.hospital.repository.IngresoRepository;
import com.hospital.repository.MascotaRepository;

@Service
public class MascotaServiceImpl {

	@Autowired
	private MascotaRepository mascotaRepository;
	
	@Autowired
	private IngresoRepository ingresoRepository;
	
	/**
	 *  Peticion 1
	 *  
	 *  Usando una petición HTTP GET + el ID de la mascota: 
	 *  Se devolverá todos los datos pertenecientes a la mascota cuyo ID coincida con {idMascota}
	 */
	
    public Optional<Mascota> obtenerMascotaPorId(Long idMascota) {
        return mascotaRepository.findById(idMascota);
    }
    
	/**
	 *  Peticion 2
	 *  
	 *  Usando una petición HTTP GET + el ID de la mascota:
	 *  Devolverá un listado con todos los ingresos que ha tenido la mascota con {ID idMascota}
	 */
	
    public List<Ingreso> buscarIngresosPorMascotaId(Long idMascota) {
        List<Ingreso> ingresos = ingresoRepository.findByMascotaId(idMascota);
        if (ingresos.isEmpty()) {
            throw new MascotaNoEncontradaException("No se encontraron ingresos para la mascota con ID: " + idMascota);
        }
        return ingresos;
    }
    
	/**
	 *  Peticion 3
	 *  
	 *  Usando una petición HTTP POST + un JSON con los datos de una mascota:
	 *  Se guardará en base de datos dicha mascota y se devolverá un JSON con toda la información guardada
	 *  (que debe incluir el ID de esa mascota en el sistema).
	 */
	
    public Mascota crearMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }
    
	/**
	 *  Peticion 4
	 *  
	 *  Usando una petición HTTP DELETE + el ID de la mascota:
	 *  Da de baja la mascota en el sistema (no se podrá hacer ninguna operación con ella),
	 *  pero NO la borra de base de datos
	 */
	
    public boolean darDeBajaMascota(Long id) {
        
        Mascota mascota = mascotaRepository.findById(id).orElse(null);

        if (mascota != null) {
            mascota.setActiva(false); 
            mascotaRepository.save(mascota); 
            return true;
        } else {
            return false; 
        }
    }
    
    
}

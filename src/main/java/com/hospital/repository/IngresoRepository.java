package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Ingreso;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Long>{

	 List<Ingreso> findByMascotaId(Long idMascota);
	
}

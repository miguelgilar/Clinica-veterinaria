package com.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long>{

}

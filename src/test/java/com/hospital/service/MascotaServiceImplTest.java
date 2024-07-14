package com.hospital.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.hospital.entity.Estado;
import com.hospital.entity.Ingreso;
import com.hospital.entity.Mascota;
import com.hospital.exception.MascotaNoEncontradaException;
import com.hospital.repository.IngresoRepository;
import com.hospital.repository.MascotaRepository;
import com.hospital.services.IngresoServiceImpl;
import com.hospital.services.MascotaServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;
    
    @Mock
    private IngresoRepository ingresoRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;
    
    @InjectMocks
    private IngresoServiceImpl ingresoService;
    
    @SuppressWarnings("deprecation")
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this); 
    }
    
    
    // Test 1. Obtener datos de mascota
    
    @Test
    public void testObtenerMascotaPorId() {

        Long idMascota = 1L;
        Mascota mascotaMock = new Mascota();
        mascotaMock.setId(idMascota);
        mascotaMock.setEspecie("Perro");
        mascotaMock.setRaza("Labrador");
        mascotaMock.setEdad(3);
        mascotaMock.setCodigo(12345);
        mascotaMock.setDniResponsable("12345678A");
        mascotaMock.setActiva(true);

        when(mascotaRepository.findById(idMascota)).thenReturn(Optional.of(mascotaMock));

        Optional<Mascota> resultado = mascotaService.obtenerMascotaPorId(idMascota);

        assertEquals(mascotaMock.getId(), resultado.get().getId());
        assertEquals(mascotaMock.getEspecie(), resultado.get().getEspecie());
        assertEquals(mascotaMock.getRaza(), resultado.get().getRaza());
        assertEquals(mascotaMock.getEdad(), resultado.get().getEdad());
        assertEquals(mascotaMock.getCodigo(), resultado.get().getCodigo());
        assertEquals(mascotaMock.getDniResponsable(), resultado.get().getDniResponsable());
        assertEquals(mascotaMock.isActiva(), resultado.get().isActiva());
    }
    
    
    // Test 2. Listado de todos los ingresos de una mascota
    
    @Test
    public void testBuscarIngresosPorMascotaId_IngresosEncontrados() {
    	
        Long idMascota = 1L;

        Ingreso ingreso1 = new Ingreso();
        ingreso1.setId(1L);
        ingreso1.setFechaAlta(LocalDate.of(2024, 7, 10));
        ingreso1.setFechaFinalizacion("2024-07-15");
        ingreso1.setMascota(new Mascota());
        ingreso1.getMascota().setId(idMascota);
        ingreso1.setEstado(Estado.ALTA);
        ingreso1.setDniRegistrador("12345678A");

        Ingreso ingreso2 = new Ingreso();
        ingreso2.setId(2L);
        ingreso2.setFechaAlta(LocalDate.of(2024, 6, 15));
        ingreso2.setFechaFinalizacion("2024-06-20");
        ingreso2.setMascota(new Mascota());
        ingreso2.getMascota().setId(idMascota);
        ingreso2.setEstado(Estado.HOSPITALIZACION);
        ingreso2.setDniRegistrador("98765432B");

        List<Ingreso> ingresosMock = new ArrayList<>();
        ingresosMock.add(ingreso1);
        ingresosMock.add(ingreso2);

        when(ingresoRepository.findByMascotaId(idMascota)).thenReturn(ingresosMock);

        List<Ingreso> resultados = mascotaService.buscarIngresosPorMascotaId(idMascota);

        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertEquals(2, resultados.size());

        Ingreso resultado1 = resultados.get(0);
        assertEquals(ingreso1.getId(), resultado1.getId());
        assertEquals(ingreso1.getFechaAlta(), resultado1.getFechaAlta());
        assertEquals(ingreso1.getFechaFinalizacion(), resultado1.getFechaFinalizacion());
        assertEquals(ingreso1.getEstado(), resultado1.getEstado());
        assertEquals(ingreso1.getDniRegistrador(), resultado1.getDniRegistrador());

        Ingreso resultado2 = resultados.get(1);
        assertEquals(ingreso2.getId(), resultado2.getId());
        assertEquals(ingreso2.getFechaAlta(), resultado2.getFechaAlta());
        assertEquals(ingreso2.getFechaFinalizacion(), resultado2.getFechaFinalizacion());
        assertEquals(ingreso2.getEstado(), resultado2.getEstado());
        assertEquals(ingreso2.getDniRegistrador(), resultado2.getDniRegistrador());

        verify(ingresoRepository, times(1)).findByMascotaId(idMascota);
    }
    
    

    
    @Test
    public void testBuscarIngresosPorMascotaId_IngresosNoEncontrados() {

        Long idMascota = 2L;

        when(ingresoRepository.findByMascotaId(idMascota)).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(MascotaNoEncontradaException.class, () -> {
            mascotaService.buscarIngresosPorMascotaId(idMascota);
        });

        String expectedMessage = "No se encontraron ingresos para la mascota con ID: " + idMascota;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(ingresoRepository, times(1)).findByMascotaId(idMascota);
    }
    
    
    // Test 3. Crear mascota
    
    @Test
    public void testCrearMascota() {

        Mascota mascota = new Mascota();
        mascota.setEspecie("Perro");
        mascota.setRaza("Labrador");
        mascota.setEdad(3);
        mascota.setCodigo(1);
        mascota.setDniResponsable("12345678A");
        mascota.setActiva(true);

        when(mascotaRepository.save(ArgumentMatchers.any(Mascota.class))).thenReturn(mascota);

        Mascota resultado = mascotaService.crearMascota(mascota);


        verify(mascotaRepository, times(1)).save(ArgumentMatchers.any(Mascota.class));

        assertEquals("Perro", resultado.getEspecie());
        assertEquals("Labrador", resultado.getRaza());
        assertEquals(3, resultado.getEdad());
        assertEquals(1, resultado.getCodigo());
        assertEquals("12345678A", resultado.getDniResponsable());
        assertTrue(resultado.isActiva());
    }


    // Test 4. Dar de baja a una mascota
    
    @Test
    void testDarDeBajaMascotaExistente() {

        Long idMascotaExistente = 1L;
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setId(idMascotaExistente);
        mascotaExistente.setActiva(true);

        when(mascotaRepository.findById(idMascotaExistente))
            .thenReturn(Optional.of(mascotaExistente));

        boolean resultado = mascotaService.darDeBajaMascota(idMascotaExistente);

        assertTrue(resultado);
        assertFalse(mascotaExistente.isActiva());

        verify(mascotaRepository, times(1)).save(mascotaExistente);
    }
    
    
    
}

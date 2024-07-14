package com.hospital.service;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import com.hospital.services.IngresoServiceImpl;


public class IngresoServiceImplTest {

    @Mock
    private IngresoRepository ingresoRepository;
    
    @Mock
    private MascotaRepository mascotaRepository;
    
    @InjectMocks
    private IngresoServiceImpl ingresoService;
    
    private Ingreso ingreso;
    private Ingreso ingresoDetalles;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ingreso = new Ingreso();
        ingreso.setId(1L);
        ingreso.setEstado(Estado.ALTA);

        ingresoDetalles = new Ingreso();
        ingresoDetalles.setEstado(Estado.FINALIZADO);
        ingresoDetalles.setFechaFinalizacion("2024-07-14");
    }
    
    
    // Test 1. Listado todos los ingresos
    
    @Test
    public void testObtenerTodosIngresos() {

        Mascota mascota1 = new Mascota();
        mascota1.setId(1L);
        mascota1.setEspecie("Perro");
        mascota1.setRaza("Labrador");
        mascota1.setEdad(5);
        mascota1.setCodigo(1001);
        mascota1.setDniResponsable("11111111A");
        mascota1.setActiva(true);

        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setEspecie("Gato");
        mascota2.setRaza("Siames");
        mascota2.setEdad(3);
        mascota2.setCodigo(1002);
        mascota2.setDniResponsable("22222222B");
        mascota2.setActiva(true);

        Ingreso ingreso1 = new Ingreso();
        ingreso1.setId(1L);
        ingreso1.setFechaAlta(LocalDate.of(2023, 1, 15));
        ingreso1.setFechaFinalizacion("2023-02-28");
        ingreso1.setMascota(mascota1);  // Asociar la mascota 1
        ingreso1.setEstado(Estado.ALTA);
        ingreso1.setDniRegistrador("12345678A");

        Ingreso ingreso2 = new Ingreso();
        ingreso2.setId(2L);
        ingreso2.setFechaAlta(LocalDate.of(2023, 3, 20));
        ingreso2.setFechaFinalizacion("2023-04-30");
        ingreso2.setMascota(mascota2);  // Asociar la mascota 2
        ingreso2.setEstado(Estado.HOSPITALIZACION);
        ingreso2.setDniRegistrador("98765432B");

        List<Ingreso> listaIngresos = Arrays.asList(ingreso1, ingreso2);

        when(ingresoRepository.findAll()).thenReturn(listaIngresos);

        List<Ingreso> resultado = ingresoService.obtenerTodosIngresos();

        verify(ingresoRepository).findAll();

        assertEquals(2, resultado.size());  // Verificar que haya dos ingresos en la lista
        assertEquals("12345678A", resultado.get(0).getDniRegistrador());  // Verificar el DNI del primer ingreso
        assertEquals("98765432B", resultado.get(1).getDniRegistrador());  // Verificar el DNI del segundo ingreso
    }
    
    
    
    // Test 2. Añadir nuevo ingreso
    
    @Test
    public void testCrearIngreso_MascotaExistente_RegistradorAutorizado() throws MascotaException, RegistradorNoAutorizadoException {

        Long mascotaId = 1L;
        LocalDate fechaAlta = LocalDate.now();
        String dniRegistrador = "12345678A";

        Mascota mascota = new Mascota();
        mascota.setId(mascotaId);
        mascota.setDniResponsable(dniRegistrador);

        when(mascotaRepository.findById(mascotaId)).thenReturn(Optional.of(mascota));
        when(ingresoRepository.save(ArgumentMatchers.any(Ingreso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ingreso ingreso = ingresoService.crearIngreso(mascotaId, fechaAlta, dniRegistrador);

        assertNotNull(ingreso);
        assertEquals(mascota, ingreso.getMascota());
        assertEquals(fechaAlta, ingreso.getFechaAlta());
        assertEquals(Estado.ALTA, ingreso.getEstado());
        assertEquals(dniRegistrador, ingreso.getDniRegistrador());

        verify(mascotaRepository, times(1)).findById(mascotaId);
        verify(ingresoRepository, times(1)).save(ArgumentMatchers.any(Ingreso.class));
    }
    
    
    @Test
    public void testCrearIngreso_RegistradorNoAutorizado() {

        Long mascotaId = 1L;
        LocalDate fechaAlta = LocalDate.now();
        String dniRegistrador = "12345678A";
        String dniRegistradorIncorrecto = "87654321B";

        Mascota mascota = new Mascota();
        mascota.setId(mascotaId);
        mascota.setDniResponsable(dniRegistradorIncorrecto);

        when(mascotaRepository.findById(mascotaId)).thenReturn(Optional.of(mascota));

        RegistradorNoAutorizadoException exception = assertThrows(RegistradorNoAutorizadoException.class, () ->
                ingresoService.crearIngreso(mascotaId, fechaAlta, dniRegistrador));

        assertEquals("El registrador no está autorizado para registrar esta mascota.", exception.getMessage());

        verify(mascotaRepository, times(1)).findById(mascotaId);
        verify(ingresoRepository, never()).save(ArgumentMatchers.any(Ingreso.class));
    }
    
    
    // Test 3. Actualizar ingreso
    
    @Test
    public void testActualizarIngreso_IngresoNoEncontrado() {
        when(ingresoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IngresoNoEncontradoException.class, () -> {
            ingresoService.actualizarIngreso(1L, 1L, ingresoDetalles);
        });

        verify(ingresoRepository, times(1)).findById(anyLong());
    }
    
    @Test
    public void testActualizarIngreso_FechaFinalizacionRequerida() {
        when(ingresoRepository.findById(anyLong())).thenReturn(Optional.of(ingreso));
        ingresoDetalles.setFechaFinalizacion(null);

        assertThrows(FechaFinalizacionRequeridaException.class, () -> {
            ingresoService.actualizarIngreso(1L, 1L, ingresoDetalles);
        });

        verify(ingresoRepository, times(1)).findById(anyLong());
    }
    

    @Test
    public void testActualizarIngreso_Exito() throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException{

        when(ingresoRepository.findById(anyLong())).thenReturn(Optional.of(ingreso));
        
        when(ingresoRepository.save(ArgumentMatchers.any(Ingreso.class))).thenReturn(ingreso);

        Ingreso actualizado = ingresoService.actualizarIngreso(1L, 1L, ingresoDetalles);

        assertNotNull(actualizado);
        assertEquals(Estado.FINALIZADO, actualizado.getEstado());
        assertEquals("2024-07-14", actualizado.getFechaFinalizacion());

        verify(ingresoRepository, times(1)).findById(anyLong());
        verify(ingresoRepository, times(1)).save(ArgumentMatchers.any(Ingreso.class));
    }
    
    
    // Test 4. Anular ingreso
    
    @Test
    public void testAnularIngresoExistente() throws IngresoNoEncontradoException {

        Long idIngresoExistente = 1L;
        Ingreso ingresoExistente = new Ingreso();
        ingresoExistente.setId(idIngresoExistente);
        ingresoExistente.setEstado(Estado.ALTA);

        when(ingresoRepository.findById(idIngresoExistente))
            .thenReturn(Optional.of(ingresoExistente));

        ingresoService.anularIngreso(idIngresoExistente);

        assertEquals(Estado.ANULADO, ingresoExistente.getEstado());
        verify(ingresoRepository, times(1)).findById(idIngresoExistente);
        verify(ingresoRepository, times(1)).save(ingresoExistente);
    }
    
    @Test
    public void testAnularIngresoNoExistente() {

        Long idIngresoNoExistente = 2L;

        when(ingresoRepository.findById(idIngresoNoExistente))
            .thenReturn(Optional.empty());

        assertThrows(IngresoNoEncontradoException.class, () -> {
            ingresoService.anularIngreso(idIngresoNoExistente);
        });
        verify(ingresoRepository, times(1)).findById(idIngresoNoExistente);
        verify(ingresoRepository, never()).save(ArgumentMatchers.any());
    }
    
    
    
    
    
    
}

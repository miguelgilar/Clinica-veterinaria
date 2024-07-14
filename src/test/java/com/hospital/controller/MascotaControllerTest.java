package com.hospital.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.hospital.entity.Ingreso;
import com.hospital.entity.Mascota;
import com.hospital.exception.MascotaNoEncontradaException;
import com.hospital.services.MascotaServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
public class MascotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MascotaServiceImpl mascotaService;
    
    
	// Test 1. Obtener mascota por ID
	
    @Test
    public void testObternerMascotaPorId_Success() throws NoSuchFieldException, IllegalAccessException {

        Long idMascota = 1L;
        Mascota mascota = new Mascota();
        mascota.setId(idMascota);

        MascotaServiceImpl mascotaService = mock(MascotaServiceImpl.class);
        when(mascotaService.obtenerMascotaPorId(eq(idMascota))).thenReturn(Optional.of(mascota));

        MascotaController controller = new MascotaController();

        Field mascotaServiceField = MascotaController.class.getDeclaredField("mascotaService");
        mascotaServiceField.setAccessible(true);
        mascotaServiceField.set(controller, mascotaService);

        ResponseEntity<?> response = controller.obternerMascotaPorId(idMascota);

        verify(mascotaService, times(1)).obtenerMascotaPorId(eq(idMascota));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mascota, response.getBody());
    }
    
    @Test
    public void testObternerMascotaPorId_NotFound() throws NoSuchFieldException, IllegalAccessException {

        Long idMascota = 1L;

        MascotaServiceImpl mascotaService = mock(MascotaServiceImpl.class);
        when(mascotaService.obtenerMascotaPorId(eq(idMascota))).thenReturn(Optional.empty());

        MascotaController controller = new MascotaController();

        Field mascotaServiceField = MascotaController.class.getDeclaredField("mascotaService");
        mascotaServiceField.setAccessible(true);
        mascotaServiceField.set(controller, mascotaService);

        ResponseEntity<?> response = controller.obternerMascotaPorId(idMascota);

        verify(mascotaService, times(1)).obtenerMascotaPorId(eq(idMascota));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No se encontró la mascota con ID: " + idMascota, response.getBody());
    }
    
    // Test 2. Listado de ingresos de una mascota
    
    @Test
    public void testObtenerIngresosPorMascotaId_Success() throws NoSuchFieldException, IllegalAccessException, MascotaNoEncontradaException {

        Long idMascota = 1L;
        List<Ingreso> ingresos = Collections.singletonList(new Ingreso());

        MascotaServiceImpl mascotaService = mock(MascotaServiceImpl.class);
        when(mascotaService.buscarIngresosPorMascotaId(eq(idMascota))).thenReturn(ingresos);

        MascotaController controller = new MascotaController();

        Field mascotaServiceField = MascotaController.class.getDeclaredField("mascotaService");
        mascotaServiceField.setAccessible(true);
        mascotaServiceField.set(controller, mascotaService);

        ResponseEntity<?> response = controller.obtenerIngresosPorMascotaId(idMascota);

        verify(mascotaService, times(1)).buscarIngresosPorMascotaId(eq(idMascota));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ingresos, response.getBody());
    }
    
    @Test
    public void testObtenerIngresosPorMascotaId_MascotaNoEncontradaException() throws NoSuchFieldException, IllegalAccessException, MascotaNoEncontradaException {
        
    	Long idMascota = 1L;
        String errorMessage = "Mascota no encontrada";

        MascotaServiceImpl mascotaService = mock(MascotaServiceImpl.class);
        when(mascotaService.buscarIngresosPorMascotaId(eq(idMascota))).thenThrow(new MascotaNoEncontradaException(errorMessage));

        MascotaController controller = new MascotaController();

        Field mascotaServiceField = MascotaController.class.getDeclaredField("mascotaService");
        mascotaServiceField.setAccessible(true);
        mascotaServiceField.set(controller, mascotaService);

        ResponseEntity<?> response = controller.obtenerIngresosPorMascotaId(idMascota);

        verify(mascotaService, times(1)).buscarIngresosPorMascotaId(eq(idMascota));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
    
    
    
    // Test 3. Crear mascota
    
    @Test
    public void testCrearMascota_Success() throws Exception {

        Mascota mascotaCreada = new Mascota();
        mascotaCreada.setId(1L);
        mascotaCreada.setEspecie("Perro");
        mascotaCreada.setRaza("Labrador");
        mascotaCreada.setEdad(3);
        mascotaCreada.setCodigo(123456);
        mascotaCreada.setDniResponsable("12345678A");
        mascotaCreada.setActiva(true);

        when(mascotaService.crearMascota(ArgumentMatchers.any(Mascota.class))).thenReturn(mascotaCreada);

        String mascotaJson = "{\"especie\": \"Perro\", \"raza\": \"Labrador\", \"edad\": 3, \"codigo\": 123456, \"dniResponsable\": \"12345678A\", \"activa\": true}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/mascota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mascotaJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.especie").value("Perro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.raza").value("Labrador"));
    }
    
    @Test
    public void testCrearMascota_Error() throws Exception {

        when(mascotaService.crearMascota(ArgumentMatchers.any(Mascota.class))).thenThrow(new RuntimeException("Error al crear la mascota"));

        String mascotaJson = "{\"especie\": \"Perro\", \"raza\": \"Labrador\", \"edad\": 3, \"codigo\": 123456, \"dniResponsable\": \"12345678A\", \"activa\": true}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/mascota")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mascotaJson))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error al crear la mascota: Error al crear la mascota"));
    }
    
    
    
    // Test 4. Dar de baja a una mascota del sistema
    
    @Test
    public void testDarDeBajaMascota_Success() throws Exception {

        Long idMascota = 1L;
        when(mascotaService.darDeBajaMascota(idMascota)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/mascota/{idMascota}", idMascota))
                .andExpect(status().isOk())
                .andExpect(content().string("Mascota dada de baja correctamente"));
    }
    
    @Test
    public void testDarDeBajaMascota_NotFound() throws Exception {
    	
        Long idMascota = 1L;
        when(mascotaService.darDeBajaMascota(idMascota)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/mascota/{idMascota}", idMascota))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No se encontró la mascota con ID: " + idMascota));
    }
    
    
}

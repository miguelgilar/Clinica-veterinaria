package com.hospital.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.hospital.entity.Ingreso;
import com.hospital.exception.FechaFinalizacionRequeridaException;
import com.hospital.exception.FechaFormatoInvalidoException;
import com.hospital.exception.IngresoNoEncontradoException;
import com.hospital.exception.MascotaException;
import com.hospital.exception.RegistradorNoAutorizadoException;
import com.hospital.services.IngresoServiceImpl;

@WebMvcTest(controllers = IngresoController.class)
@ExtendWith(MockitoExtension.class)
public class IngresoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngresoServiceImpl ingresoService;
    
	// Test 1. Listado todos los ingresos de una mascota
    
    @Test
    public void obtenerTodosIngresos_DeberiaRetornarListaDeIngresos() throws Exception {

        List<Ingreso> listaIngresos = new ArrayList<>();

        given(ingresoService.obtenerTodosIngresos()).willReturn(listaIngresos);

        mockMvc.perform(get("/api/ingreso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    
    // Test 2. Crear ingreso
    
    @Test
    public void testCrearIngreso_Success() throws MascotaException, RegistradorNoAutorizadoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        LocalDate fechaAlta = LocalDate.now();
        String dniRegistrador = "12345678A";

        Ingreso ingresoRequest = new Ingreso();
        ingresoRequest.setFechaAlta(fechaAlta);
        ingresoRequest.setDniRegistrador(dniRegistrador);

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        Ingreso ingresoCreado = new Ingreso();
        when(ingresoService.crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador))).thenReturn(ingresoCreado);

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        ResponseEntity<?> response = controller.crearIngreso(mascotaId, ingresoRequest);

        verify(ingresoService, times(1)).crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ingresoCreado, response.getBody());
    }
    

    @Test
    public void testCrearIngreso_MascotaNotFoundException() throws MascotaException, RegistradorNoAutorizadoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        LocalDate fechaAlta = LocalDate.now();
        String dniRegistrador = "12345678A";

        Ingreso ingresoRequest = new Ingreso();
        ingresoRequest.setFechaAlta(fechaAlta);
        ingresoRequest.setDniRegistrador(dniRegistrador);

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        when(ingresoService.crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador))).thenThrow(new MascotaException("Mascota no encontrada"));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        ResponseEntity<?> response = controller.crearIngreso(mascotaId, ingresoRequest);

        verify(ingresoService, times(1)).crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Mascota no encontrada", response.getBody());
    }
    
    @Test
    public void testCrearIngreso_RegistradorNoAutorizadoException() throws MascotaException, RegistradorNoAutorizadoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        LocalDate fechaAlta = LocalDate.now();
        String dniRegistrador = "12345678A";

        Ingreso ingresoRequest = new Ingreso();
        ingresoRequest.setFechaAlta(fechaAlta);
        ingresoRequest.setDniRegistrador(dniRegistrador);

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        when(ingresoService.crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador))).thenThrow(new RegistradorNoAutorizadoException("El registrador no está autorizado para registrar esta mascota."));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        ResponseEntity<?> response = controller.crearIngreso(mascotaId, ingresoRequest);

        verify(ingresoService, times(1)).crearIngreso(eq(mascotaId), eq(fechaAlta), eq(dniRegistrador));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("El registrador no está autorizado para registrar esta mascota.", response.getBody());
    }
    
    
    // Test 3. Actualizar ingreso
    
    @Test
    public void testActualizarIngreso_Success() throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        Long ingresoId = 1L;
        Ingreso ingresoDetalles = new Ingreso();
        ingresoDetalles.setFechaAlta(LocalDate.now());
        ingresoDetalles.setDniRegistrador("12345678A");

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        Ingreso ingresoActualizado = new Ingreso();
        when(ingresoService.actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles))).thenReturn(ingresoActualizado);

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        ResponseEntity<Ingreso> response = controller.actualizarIngreso(mascotaId, ingresoId, ingresoDetalles);

        verify(ingresoService, times(1)).actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ingresoActualizado, response.getBody());
    }
    
    @Test
    public void testActualizarIngreso_IngresoNoEncontradoException() throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        Long ingresoId = 1L;
        Ingreso ingresoDetalles = new Ingreso();
        ingresoDetalles.setFechaAlta(LocalDate.now());
        ingresoDetalles.setDniRegistrador("12345678A");

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        when(ingresoService.actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles))).thenThrow(new IngresoNoEncontradoException("Ingreso no encontrado"));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        try {
            controller.actualizarIngreso(mascotaId, ingresoId, ingresoDetalles);
        } catch (IngresoNoEncontradoException e) {
            assertEquals("Ingreso no encontrado", e.getMessage());
        }

        verify(ingresoService, times(1)).actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles));
    }
    
    @Test
    public void testActualizarIngreso_FechaFinalizacionRequeridaException() throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException, NoSuchFieldException, IllegalAccessException {

        Long mascotaId = 1L;
        Long ingresoId = 1L;
        Ingreso ingresoDetalles = new Ingreso();
        ingresoDetalles.setFechaAlta(LocalDate.now());
        ingresoDetalles.setDniRegistrador("12345678A");

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        when(ingresoService.actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles))).thenThrow(new FechaFinalizacionRequeridaException("Fecha de finalización requerida"));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        try {
            controller.actualizarIngreso(mascotaId, ingresoId, ingresoDetalles);
        } catch (FechaFinalizacionRequeridaException e) {
            assertEquals("Fecha de finalización requerida", e.getMessage());
        }

        verify(ingresoService, times(1)).actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles));
    }
    
    @Test
    public void testActualizarIngreso_FechaFormatoInvalidoException() throws IngresoNoEncontradoException, FechaFinalizacionRequeridaException, FechaFormatoInvalidoException, NoSuchFieldException, IllegalAccessException {
        
    	Long mascotaId = 1L;
        Long ingresoId = 1L;
        Ingreso ingresoDetalles = new Ingreso();
        ingresoDetalles.setFechaAlta(LocalDate.now());
        ingresoDetalles.setDniRegistrador("12345678A");

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        when(ingresoService.actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles))).thenThrow(new FechaFormatoInvalidoException("Formato de fecha inválido"));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        try {
            controller.actualizarIngreso(mascotaId, ingresoId, ingresoDetalles);
        } catch (FechaFormatoInvalidoException e) {
            assertEquals("Formato de fecha inválido", e.getMessage());
        }

        verify(ingresoService, times(1)).actualizarIngreso(eq(mascotaId), eq(ingresoId), eq(ingresoDetalles));
    }
    
    
    // Test 4. Anular ingreso
    
    @Test
    public void testAnularIngreso_Success() throws IngresoNoEncontradoException, NoSuchFieldException, IllegalAccessException {

        Long ingresoId = 1L;

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        doNothing().when(ingresoService).anularIngreso(eq(ingresoId));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        ResponseEntity<Void> response = controller.anularIngreso(ingresoId);

        verify(ingresoService, times(1)).anularIngreso(eq(ingresoId));
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }    
    
    
    @Test
    public void testAnularIngreso_IngresoNoEncontradoException() throws IngresoNoEncontradoException, NoSuchFieldException, IllegalAccessException {
        
    	Long ingresoId = 1L;

        IngresoServiceImpl ingresoService = mock(IngresoServiceImpl.class);
        doThrow(new IngresoNoEncontradoException("Ingreso no encontrado")).when(ingresoService).anularIngreso(eq(ingresoId));

        IngresoController controller = new IngresoController();

        Field ingresoServiceField = IngresoController.class.getDeclaredField("ingresoService");
        ingresoServiceField.setAccessible(true);
        ingresoServiceField.set(controller, ingresoService);

        try {
            controller.anularIngreso(ingresoId);
        } catch (IngresoNoEncontradoException e) {
            assertEquals("Ingreso no encontrado", e.getMessage());
        }

        verify(ingresoService, times(1)).anularIngreso(eq(ingresoId));
    }
    
    
    
    
    
    
}

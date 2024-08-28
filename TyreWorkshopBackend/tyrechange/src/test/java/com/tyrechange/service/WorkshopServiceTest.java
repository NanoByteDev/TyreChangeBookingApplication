package com.tyrechange.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.tyrechange.config.WorkshopsConfig;
import com.tyrechange.model.TyreWorkshop;

class WorkshopServiceTest {

    @Mock
    private WorkshopsConfig workshopsConfig;

    @InjectMocks
    private WorkshopService workshopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllWorkshops_ConfigHasWorkshops_ReturnsWorkshopList() {
        // Arrange
        List<TyreWorkshop> mockWorkshops = Arrays.asList(
            new TyreWorkshop(), new TyreWorkshop()
        );
        when(workshopsConfig.getWorkshops()).thenReturn(mockWorkshops);

        // Act
        List<TyreWorkshop> result = workshopService.getAllWorkshops();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(workshopsConfig, times(1)).getWorkshops();
    }

    @Test
    void getAllWorkshops_ConfigReturnsNull_ThrowsRuntimeException() {
        // Arrange
        when(workshopsConfig.getWorkshops()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> workshopService.getAllWorkshops());
        verify(workshopsConfig, times(1)).getWorkshops();
    }

    @Test
    void getAllWorkshops_ConfigReturnsEmptyList_ThrowsRuntimeException() {
        // Arrange
        when(workshopsConfig.getWorkshops()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> workshopService.getAllWorkshops());
        verify(workshopsConfig, times(1)).getWorkshops();
    }

    @Test
    void getWorkshopByName_WorkshopExists_ReturnsWorkshop() {
        // Arrange
        TyreWorkshop mockWorkshop = new TyreWorkshop();
        mockWorkshop.setName("Test Workshop");
        List<TyreWorkshop> mockWorkshops = Arrays.asList(mockWorkshop);
        when(workshopsConfig.getWorkshops()).thenReturn(mockWorkshops);

        // Act
        TyreWorkshop result = workshopService.getWorkshopByName("Test Workshop");

        // Assert
        assertNotNull(result);
        assertEquals("Test Workshop", result.getName());
    }

    @Test
    void getWorkshopByName_WorkshopDoesNotExist_ThrowsIllegalArgumentException() {
        // Arrange
        TyreWorkshop mockWorkshop = new TyreWorkshop();
        mockWorkshop.setName("Existing Workshop");
        List<TyreWorkshop> mockWorkshops = Arrays.asList(mockWorkshop);
        when(workshopsConfig.getWorkshops()).thenReturn(mockWorkshops);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> workshopService.getWorkshopByName("Non-existent Workshop"));
    }
}
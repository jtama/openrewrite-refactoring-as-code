package com.github.jtama.app.hostels;

import com.github.jtama.app.exception.DuplicateEntityException;
import com.github.jtama.app.exception.InvalidNameException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class HostelTest {

    @Test
    public void testGettersAndSetters() {
        Hostel hostel = new Hostel();
        hostel.setName("MyHostel");
        assertEquals("MyHostel", hostel.getName());
    }

    @Test
    public void testFindByName() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            PanacheQuery query = mock(PanacheQuery.class);
            Hostel expected = new Hostel();
            expected.setName("hostel-one");
            when(query.firstResultOptional()).thenReturn(Optional.of(expected));

            mock.when(() -> PanacheEntityBase.find(eq("name"), (Object[]) any())).thenReturn(query);

            Optional<Hostel> result = Hostel.findByName("hostel-one");
            assertTrue(result.isPresent());
            assertEquals(expected, result.get());
        }
    }

    @Test
    public void testPersistIfNotExists_Success() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            mock.when(() -> PanacheEntityBase.count(eq("name"), (Object[]) any())).thenReturn(0L);

            Hostel hostel = spy(new Hostel());
            hostel.setName("hostel-one");
            doNothing().when(hostel).persist();

            Hostel result = Hostel.persistIfNotExists(hostel);
            assertEquals(hostel, result);
            verify(hostel, times(1)).persist();
        }
    }

    @Test(expected = InvalidNameException.class)
    public void testPersistIfNotExists_InvalidName() {
        Hostel hostel = new Hostel();
        hostel.setName("a"); // too short, needs at least 2 chars

        Hostel.persistIfNotExists(hostel);
    }

    @Test(expected = DuplicateEntityException.class)
    public void testPersistIfNotExists_Duplicate() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            mock.when(() -> PanacheEntityBase.count(eq("name"), (Object[]) any())).thenReturn(1L);

            Hostel hostel = new Hostel();
            hostel.setName("hostel-one");

            Hostel.persistIfNotExists(hostel);
        }
    }
}

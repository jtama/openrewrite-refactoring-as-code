package com.github.jtama.app.rocket;

import com.github.jtama.app.exception.DuplicateEntityException;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RocketTest {

    @Test
    public void testGettersAndSetters() {
        Rocket rocket = new Rocket();
        rocket.setName("Falcon 9");
        rocket.setType(RocketType.LUXURY);

        assertEquals("Falcon 9", rocket.getName());
        assertEquals(RocketType.LUXURY, rocket.getType());
    }

    @Test
    public void testFindByName() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            PanacheQuery query = mock(PanacheQuery.class);
            Rocket expected = new Rocket();
            expected.setName("Falcon Heavy");
            when(query.firstResultOptional()).thenReturn(Optional.of(expected));

            mock.when(() -> PanacheEntityBase.find(eq("name"), (Object[]) any())).thenReturn(query);

            Optional<Rocket> result = Rocket.findByName("Falcon Heavy");
            assertTrue(result.isPresent());
            assertEquals(expected, result.get());
        }
    }

    @Test
    public void testPersistIfNotExists_Success() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            PanacheQuery query = mock(PanacheQuery.class);
            when(query.count()).thenReturn(0L);

            mock.when(() -> PanacheEntityBase.find(eq("name"), (Object[]) any())).thenReturn(query);

            Rocket rocket = spy(new Rocket());
            rocket.setName("Starship");
            rocket.setType(RocketType.LUXURY);
            doNothing().when(rocket).persist();

            Rocket result = Rocket.persistIfNotExists(rocket);
            assertEquals(rocket, result);
            verify(rocket, times(1)).persist();
        }
    }

    @Test(expected = DuplicateEntityException.class)
    public void testPersistIfNotExists_Duplicate() {
        try (MockedStatic<PanacheEntityBase> mock = mockStatic(PanacheEntityBase.class)) {
            PanacheQuery query = mock(PanacheQuery.class);
            when(query.count()).thenReturn(1L);

            mock.when(() -> PanacheEntityBase.find(eq("name"), (Object[]) any())).thenReturn(query);

            Rocket rocket = new Rocket();
            rocket.setName("Starship");
            rocket.setType(RocketType.LUXURY);

            Rocket.persistIfNotExists(rocket);
        }
    }
}

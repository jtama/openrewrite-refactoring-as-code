package com.github.jtama.app.rocket;


import com.github.jtama.app.exception.DuplicateEntityException;
import com.github.jtama.toxic.FooBarUtils;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;

@Entity
@RegisterForReflection
public class Rocket extends PanacheEntity {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private RocketType type;

    public static Optional<Rocket> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

    public static Rocket persistIfNotExists(Rocket rocket) {
        if (find("name", rocket.name).count() > 0) {
            throw new DuplicateEntityException(new FooBarUtils().stringFormatted("Rocket named %s already exists", rocket.name));
        }
        rocket.persist();
        return rocket;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(RocketType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public RocketType getType() {
        return type;
    }
}

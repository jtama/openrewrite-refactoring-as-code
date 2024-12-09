package com.github.jtama.app.hostels;

import com.github.jtama.app.exception.DuplicateEntityException;
import com.github.jtama.app.exception.InvalidNameException;
import com.github.jtama.toxic.FooBarUtils;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Entity;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@RegisterForReflection
public class Hostel extends PanacheEntity {

    private static final String PATTERN = "[a-zA-Z]([-a-z0-9]*[a-z0-9])";
    private static final Pattern NAME_PATTERN = Pattern.compile(PATTERN);

    private String name;

    public static Hostel persistIfNotExists(Hostel hostel) {
        System.out.println(FooBarUtils.isEmpty(hostel.getName()));
        if (!validateName(hostel.getName())) {
            throw new InvalidNameException(new FooBarUtils().stringFormatted("Name %s is invalid for a hostel. Name should comply the following pattern %s", hostel.getName(), PATTERN));
        }
        if (Hostel.count("name", hostel.name) > 0) {
            throw new DuplicateEntityException(new FooBarUtils().stringFormatted("Hostel %s already exists", hostel.name));
        }
        hostel.persist();
        return hostel;
    }

    private static boolean validateName(String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    public static Optional<Hostel> findByName(String name) {
        return find("name", name).firstResultOptional();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

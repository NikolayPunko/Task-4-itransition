package ru.punko.springcourse.FirstSecurityApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.punko.springcourse.FirstSecurityApp.models.Person;
import ru.punko.springcourse.FirstSecurityApp.repositories.PeopleRepository;

import java.util.Date;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Person person){
        person.setRole("ROLE_USER");
        person.setRegDate(new Date());
        person.setLogDate(new Date());
        person.setStatus("Unblock");
        String encodedPassword = passwordEncoder.encode(person.getPassword()); //шифруем
        person.setPassword(encodedPassword);
        peopleRepository.save(person);
    }
}

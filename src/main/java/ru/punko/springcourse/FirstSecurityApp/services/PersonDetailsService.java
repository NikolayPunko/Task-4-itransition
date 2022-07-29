package ru.punko.springcourse.FirstSecurityApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.punko.springcourse.FirstSecurityApp.models.Person;
import ru.punko.springcourse.FirstSecurityApp.repositories.PeopleRepository;
import ru.punko.springcourse.FirstSecurityApp.security.PersonDetails;

import java.util.Date;
import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepositor) {
        this.peopleRepository = peopleRepositor;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(username);

        if(person.get().getStatus().equals("Block")){
            throw new LockedException("User is blocked");
        }

        if (person.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        updateLogDate(person.get());

        return new PersonDetails(person.get());
    }

    @Transactional
    public void updateLogDate(Person person){
        person.setLogDate(new Date());
        peopleRepository.save(person);
    }
}

package ru.punko.springcourse.FirstSecurityApp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.punko.springcourse.FirstSecurityApp.models.Person;
import ru.punko.springcourse.FirstSecurityApp.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if (peopleService.findByUsername(person.getUsername()).isPresent())
            errors.rejectValue("username", "", "Пользователь с таким логином уже существует");

        if (peopleService.findByEmail(person.getEmail()).isPresent())
            errors.rejectValue("email", "", "Пользователь с таким почтовым адресом уже существует");
    }
}

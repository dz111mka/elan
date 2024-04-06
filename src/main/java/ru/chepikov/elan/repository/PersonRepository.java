package ru.chepikov.elan.repository;

import org.springframework.data.repository.CrudRepository;
import ru.chepikov.elan.entity.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

}

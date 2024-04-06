package ru.chepikov.elan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.chepikov.elan.entity.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

}

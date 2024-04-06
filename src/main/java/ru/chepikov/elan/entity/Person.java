
package ru.chepikov.elan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstname;

    String lastname;

    String username;

    String phone;

    Location location;

    @Override
    public String toString() {
        return this.getId() + ") " + this.getFirstname() + " " + this.getLastname() + " находится " + at(this.getLocation()) + "\n";
    }

    private String at(Location location) {
        return switch (location) {
            case HOME -> " дома";
            case ILL -> "на больничном";
            case DINNER -> "на обеде";
            case OFFICE -> "в офисе";
            case BLANK_WORKSHOP -> "в заготовке";
            case AUTO_WORKSHOP -> "на автоматном участке";
            case VACUUM_WORKSHOP -> "на вакуумном участке";
            case SHPAT_WORKSHOP -> "на шпате";
            case QUALITY_CONTROL_DEPARTMENT -> "в ОТК";
            case FLUSHING -> "в промывочной";
            case LABORATORY -> "в лаборатории";
            case LEO_KHUDYAKOV -> "у Лени Худякова";
            case TIMONIN -> "у Тимонина";
            case VACATION -> "в отпуске";
        };
    }
}


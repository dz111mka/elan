
package ru.chepikov.elan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;


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
    @Column(unique = true)
    String username;

    String phone;

    Location location;

    LocalDateTime birthday;

    @Override
    public String toString() {
        return this.getId() + ") "
                + this.getFirstname()
                + " "
                + this.getLastname()
                + " находится "
                + at(this.getLocation())
                + " телефон для связи "
                + this.getPhone()
                +  "\n";
    }

    private String at(Location location) {
        return switch (location) {
            case HOME -> " дома \uD83C\uDFE0";
            case ILL -> " на больничном \uD83E\uDD12";
            case DINNER -> "на обеде \uD83C\uDF7D\uFE0F";
            case OFFICE -> "в офисе \uD83D\uDCBC";
            case BLANK_WORKSHOP -> "в заготовке \uD83D\uDEE0\uFE0F";
            case AUTO_WORKSHOP -> "на автоматном участке \uD83D\uDEE0\uFE0F";
            case VACUUM_WORKSHOP -> "на вакуумном участке \uD83D\uDEE0\uFE0F";
            case SHPAT_WORKSHOP -> "на шпате \uD83D\uDEE0\uFE0F";
            case QUALITY_CONTROL_DEPARTMENT -> "в ОТК \uD83D\uDC67";
            case FLUSHING -> "в промывочной \uD83D\uDC67";
            case LABORATORY -> "в лаборатории \uD83E\uDD7C";
            case LEO_KHUDYAKOV -> "у Лени Худякова \uD83E\uDD7C";
            case TIMONIN -> "у Тимонина \uD83D\uDEAC";
            case VACATION -> "в отпуске ✈\uFE0F";
        };
    }
}


package ru.revuelArvida.telegrambot.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "anekdots")
public class AnekdotEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anek")
    private String anek;

    public AnekdotEntity(String anek){
        this.anek = anek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnekdotEntity that = (AnekdotEntity) o;
        return id.equals(that.id) && anek.equals(that.anek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, anek);
    }
}

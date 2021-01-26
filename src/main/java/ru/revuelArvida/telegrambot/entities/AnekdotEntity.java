package ru.revuelArvida.telegrambot.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

}

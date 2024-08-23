package com.MapView.BackEnd.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Set;

@Table(name = "responsible")
@Entity(name = "responsible")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_responsible")

public class Responsible {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_responsible;

    private String responsible_name;
    private String edv;
    @OneToOne
    @JoinColumn(name = "id_classes")
    private Classes id_classes;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private Users id_user;
    private boolean operative;

    @OneToMany(mappedBy = "id_responsible")
    private Set<EquipmentResponsible> equipmentResponsibles;



    public  Responsible(String responsible_name, String edv,Classes classes,Users users){
        this.responsible_name = responsible_name;
        this.edv = edv;
        this.id_classes = classes;
        this.id_user = users;
        this.operative = true;

    }

    public void activeResposible(){
        this.setOperative(true);
    }
    public void inactivateEnviroment(){
        this.setOperative(false);
    }
}

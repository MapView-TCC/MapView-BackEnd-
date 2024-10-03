package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.Responsible.ResponsibleCrateDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Classes classes;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private Users user;
    private boolean operative;

    @OneToMany(mappedBy = "id_responsible")
    @JsonManagedReference
    private Set<EquipmentResponsible> equipmentResponsibles;


    public  Responsible(String responsible_name, String edv,Classes classes,Users users){
        this.responsible_name = responsible_name;
        this.edv = edv;
        this.classes = classes;
        this.user = users;
        this.operative = true;

    }

    public  Responsible(ResponsibleCrateDTO data, Classes classes, Users users){
        this.responsible_name = data.responsible_name();
        this.edv = data.edv();
        this.classes = classes;
        this.user = users;
        this.operative = true;

    }



    public boolean status_check(){
        return this.operative;
    }

}

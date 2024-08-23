package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Table(name = "equipment")
@Entity(name = "equipment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_equipment")
public class Equipment {

    @Id
    private String id_equipment;
    private String rfid;
    private String type;
    private String model;
    private String validity; // periodo de utilização, quando tem que ser devolvido
    private String admin_rights; // codigo que eles tem para fazer as requisições
    private String observation;
    @OneToOne
    @JoinColumn(name = "id_location")
    private Location id_location;
    @OneToOne
    @JoinColumn(name = "id_owner")
    private MainOwner id_owner;
    private boolean operative;

    @OneToMany(mappedBy = "id_equipment")
    private Set<EquipmentResponsible> equipmentResponsibles;


    public Equipment(EquipmentCreateDTO data, Location location, MainOwner id_owner) {
        this.id_equipment = data.id_equipment();
        this.rfid = data.rfid();
        this.type = data.type();
        this.model = data.model();
        this.validity = data.validity();
        this.admin_rights = data.admin_rights();
        this.observation = data.observation();
        this.id_location = location;
        this.id_owner = id_owner;
        this.operative = true;
    }

    public boolean status_check(){
        return this.operative;
    }
}

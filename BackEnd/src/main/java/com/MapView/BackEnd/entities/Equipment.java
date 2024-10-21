package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.Equipment.EquipmentCreateDTO;
import com.MapView.BackEnd.enums.EnumModelEquipment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cod_equipment")
    private String codigo;

    // novo campo
    private String name_equipment;

    private Long rfid;
    private String type;
    @Enumerated(EnumType.STRING)
    private EnumModelEquipment model;
    private LocalDate validity; // periodo de utilização, quando tem que ser devolvido
    private String admin_rights; // codigo que eles tem para fazer as requisições
    private String observation;

    //@ManyToOne
    @OneToOne
    @JoinColumn(name = "id_location")
    //@JsonBackReference
    private Location location;


    //@ManyToOne
    @OneToOne
    @JoinColumn(name = "id_owner")
    //@JsonBackReference // it will be omitted from serialization.
    private MainOwner owner;

    @ManyToOne
    @JoinColumn(name = "id_image")
    private Image id_image;
    private boolean operative;

    @OneToMany(mappedBy = "idEquipment")
    @JsonManagedReference
    private Set<EquipmentResponsible> equipmentResponsibles;


    public Equipment(EquipmentCreateDTO data, LocalDate date, Location location, MainOwner id_owner) {
        this.codigo = data.id_equipment();
        this.name_equipment = data.name_equipment();
        this.rfid = data.rfid();
        this.type = data.type();
        this.model = data.model();
        this.validity = date;
        this.admin_rights = data.admin_rights();
        this.observation = data.observation();
        this.location = location;
        this.owner = id_owner;
        this.operative = true;
    }
    public Equipment(String id_equipment, Long rfid) {
        this.codigo = id_equipment;
        this.rfid = rfid;
        this.type = null;
        this.model = null;
        this.validity = null;
        this.admin_rights = null;
        this.observation = null;
        this.location = null;
        this.owner = null;
        this.operative = true;
    }
    public Equipment(Long rfid) {
        this.codigo = null;
        this.rfid = rfid;
        this.type = null;
        this.model = null;
        this.validity = null;
        this.admin_rights = null;
        this.observation = null;
        this.location = null;
        this.owner = null;
        this.operative = true;
    }


}

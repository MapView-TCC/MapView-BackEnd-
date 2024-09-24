package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.MainOwner.MainOwnerCreateDTO;
import com.MapView.BackEnd.dtos.MainOwner.MainOwnerDetailsDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "main_owner")
@Entity(name = "main_owner")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id_owner")
public class MainOwner {
    @Id
    private String id_owner;
    private String owner_name;
    @OneToOne
    @JoinColumn(name = "id_cost_center")
    private CostCenter costCenter;
    private boolean operative;

//    @OneToMany(mappedBy = "owner") // Mapeia a relação inversa no Equipment
//    @JsonManagedReference
//    private Set<Equipment> equipments = new HashSet<>();


    public MainOwner(MainOwnerCreateDTO dados, CostCenter costCenter) {
        this.id_owner = dados.id_owner();
        this.owner_name =  dados.owner_name();
        this.costCenter = costCenter;
        this.operative = true;
    }

}

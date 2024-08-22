package com.MapView.BackEnd.entities;

import com.MapView.BackEnd.dtos.Area.AreaCreateDTO;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "area")
@Entity(name = "area")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id_area")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_area;
    private String area_code;
    private String area_name;

    private boolean operative;

    public Area(AreaCreateDTO dados) {
        this.area_code = dados.area_code();
        this.area_name = dados.area_name();
        this.operative = true;
    }

    public boolean check_status(){
        return this.operative;
    }
}
gi
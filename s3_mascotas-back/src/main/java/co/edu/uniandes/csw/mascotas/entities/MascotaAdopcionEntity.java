/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.mascotas.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

/**
 *
 * @author Sebastian Mujica
 */
@Entity
public class MascotaAdopcionEntity extends BaseEntity implements Serializable{

    private String historia;
    
    
    @PodamExclude
    @OneToOne(cascade = CascadeType.PERSIST)
    private MascotaEntity mascota;

    public MascotaAdopcionEntity() {
    }
    


    /**
     * @return the historia
     */
    public String getHistoria() {
        return historia;
    }

    /**
     * @param historia the historia to set
     */
    public void setHistoria(String historia) {
        this.historia = historia;
    }

    /**
     * @return the mascotaEntity
     */
    public MascotaEntity getMascota() {
        return mascota;
    }

    /**
     * @param mascotaEntity the mascotaEntity to set
     */
    public void setMascota(MascotaEntity mascota) {
        this.mascota = mascota;
    }
}

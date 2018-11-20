/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.mascotas.test.logic;

import co.edu.uniandes.csw.mascotas.ejb.MascotaLogic;
import co.edu.uniandes.csw.mascotas.ejb.RazaLogic;
import co.edu.uniandes.csw.mascotas.ejb.RazaMascotaLogic;
import co.edu.uniandes.csw.mascotas.entities.MascotaEntity;
import co.edu.uniandes.csw.mascotas.entities.RazaEntity;
import co.edu.uniandes.csw.mascotas.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.mascotas.persistence.MascotaPersistence;
import co.edu.uniandes.csw.mascotas.persistence.RazaPersistence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.validation.constraints.AssertFalse;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 *
 * @author lemus
 */
@RunWith(Arquillian.class)
public class RazaMascotaLogicTest {
    
    private PodamFactory factory = new PodamFactoryImpl();
    
    @Inject
    private RazaMascotaLogic razaMascotaLogic;
    
    @Inject
    private RazaLogic razaLogic;
    
    @Inject
    private MascotaLogic mascotaLogic;
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private UserTransaction ut;
    
    private List<RazaEntity> listaRazas = new ArrayList<>();
    
    private List<MascotaEntity> listaMascotas = new ArrayList<>();
    
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(RazaEntity.class.getPackage())
                .addPackage(MascotaEntity.class.getPackage())
                .addPackage(RazaMascotaLogic.class.getPackage())
                .addPackage(RazaPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }  
    
    @Before
    public void configTest(){
        try{
            ut.begin();
            em.joinTransaction();
            //clearData();
            insertData();
            ut.commit();
        }
        catch(Exception e){
            e.printStackTrace();
            try{
                ut.rollback();
            }
            catch(Exception e1){
                e1.printStackTrace();
            }
        }
    }

    private void clearData(){
        em.createQuery("delete from RazaEntity").executeUpdate();
        em.createQuery("delete from MascotaEntity").executeUpdate();
    }


    private void insertData(){
        
        for(int i= 0; i<10;i++){
            MascotaEntity nueva = factory.manufacturePojo(MascotaEntity.class);
            nueva.setDeleted(Boolean.FALSE);
            em.persist(nueva);
            listaMascotas.add(nueva);
        }
        
        for(int i= 0; i<10;i++){
            RazaEntity nueva = factory.manufacturePojo(RazaEntity.class);
            nueva.setDeleted(Boolean.FALSE);
            em.persist(nueva);
            listaRazas.add(nueva);
        }
    }  
    
    @Test
    public void addMascotaTest() throws BusinessLogicException{
        RazaEntity r = listaRazas.get(0);
        MascotaEntity m1 = listaMascotas.get(0);
        MascotaEntity m2 = listaMascotas.get(1);
        MascotaEntity m3 = listaMascotas.get(2);
        
        razaMascotaLogic.addMascota(r.getId(), m1);
        razaMascotaLogic.addMascota(r.getId(), m2);
        razaMascotaLogic.addMascota(r.getId(), m3);
        
        RazaEntity modR = razaLogic.getRaza(r.getId());
        MascotaEntity modM1 = mascotaLogic.getMascota(m1.getId());
        MascotaEntity modM2 = mascotaLogic.getMascota(m2.getId());
        MascotaEntity modM3 = mascotaLogic.getMascota(m3.getId());
        
        Assert.assertEquals(modM1.getRaza().getNombre(), modR.getNombre());
        Assert.assertEquals(modM2.getRaza().getNombre(), modR.getNombre());
        Assert.assertEquals(modM3.getRaza().getNombre(), modR.getNombre());
        
        List<MascotaEntity> list = modR.getMascotas();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void getMascotas() throws BusinessLogicException{
        RazaEntity r = listaRazas.get(0);
        MascotaEntity m1 = listaMascotas.get(0);
        MascotaEntity m2 = listaMascotas.get(1);
        MascotaEntity m3 = listaMascotas.get(2);

        razaMascotaLogic.addMascota(r.getId(), m1);
        razaMascotaLogic.addMascota(r.getId(), m2);
        razaMascotaLogic.addMascota(r.getId(), m3);

        RazaEntity modR = razaLogic.getRaza(r.getId());
        MascotaEntity modM1 = mascotaLogic.getMascota(m1.getId());
        MascotaEntity modM2 = mascotaLogic.getMascota(m2.getId());
        MascotaEntity modM3 = mascotaLogic.getMascota(m3.getId());

        List<String> nombres = new ArrayList<>();
        List<String> nombresEsperados = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            nombresEsperados.add(listaMascotas.get(i).getNombre());
        }
        
        for(MascotaEntity m : modR.getMascotas()){
            nombres.add(m.getNombre());
        }
        
        Collections.sort(nombres);
        Collections.sort(nombresEsperados);
        
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(nombres.get(i), nombresEsperados.get(i));
        }
    }
    
    @Test
    public void removeMascota() throws BusinessLogicException{
        RazaEntity r = listaRazas.get(0);
        MascotaEntity m1 = listaMascotas.get(0);
        MascotaEntity m2 = listaMascotas.get(1);
        MascotaEntity m3 = listaMascotas.get(2);
        MascotaEntity m4 = listaMascotas.get(3);
        MascotaEntity m5 = listaMascotas.get(4);
        MascotaEntity m6 = listaMascotas.get(5);

        razaMascotaLogic.addMascota(r.getId(), m1);
        razaMascotaLogic.addMascota(r.getId(), m2);
        razaMascotaLogic.addMascota(r.getId(), m3);
        razaMascotaLogic.addMascota(r.getId(), m4);
        razaMascotaLogic.addMascota(r.getId(), m5);
        razaMascotaLogic.addMascota(r.getId(), m6);
        
        razaMascotaLogic.removeMascota(r.getId(), m2.getId());

        RazaEntity modR = razaLogic.getRaza(r.getId());
        List<MascotaEntity> mascotas = modR.getMascotas();
        Assert.assertEquals(5, mascotas.size());
        
        for(MascotaEntity m : mascotas){
            Assert.assertFalse(m.getId().equals(m2.getId()));
        }

    }
}

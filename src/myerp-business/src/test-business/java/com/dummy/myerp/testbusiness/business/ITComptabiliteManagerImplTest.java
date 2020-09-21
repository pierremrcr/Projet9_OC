package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.sql.Date.valueOf;

@ContextConfiguration(locations = "/com/dummy/myerp/business/applicationContext.xml")

public class ITComptabiliteManagerImplTest extends BusinessTestCase {

    private ComptabiliteManager manager = getBusinessProxy().getComptabiliteManager();

    @Test
    public void getListCompteComptableTest(){
       List<CompteComptable> compteComptableList = this.manager.getListCompteComptable();
        Assert.assertTrue(compteComptableList.size()==7);
    }

    @Test
    public void getListJournalComptableTest(){
        List<JournalComptable> journalComptableList = this.manager.getListJournalComptable();
        Assert.assertEquals(4, journalComptableList.size());
    }

    @Test
    public void getListEcritureComptabletest(){
        List<EcritureComptable> ecritureComptableList = this.manager.getListEcritureComptable();
        Assert.assertEquals(5, ecritureComptableList.size());
    }

    @Test
    public void insertUpdateDeleteEcritureComptableTest() throws FunctionalException {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        LocalDate localDate = LocalDate.of(2016, 12, 28);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), null, new BigDecimal(123), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606), null, null, new BigDecimal(123)));

        manager.addReference(ecritureComptable);

        // INSERT
        manager.insertEcritureComptable(ecritureComptable);
        Assert.assertEquals("BQ-2016/00053", ecritureComptable.getReference());
        Assert.assertEquals(6, manager.getListEcritureComptable().size());

        // UPDATE
        ecritureComptable.setLibelle("Libelle mis à jour");
        manager.updateEcritureComptable(ecritureComptable);
        Assert.assertEquals("Libelle mis à jour", ecritureComptable.getLibelle());

        // DELETE
        manager.deleteEcritureComptable(ecritureComptable.getId());
        Assert.assertEquals(5,manager.getListEcritureComptable().size());


    }



}

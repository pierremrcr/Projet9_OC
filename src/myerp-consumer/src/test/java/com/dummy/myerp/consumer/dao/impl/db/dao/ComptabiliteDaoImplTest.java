package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testconsumer.consumer.ConsumerTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static java.sql.Date.valueOf;

public class ComptabiliteDaoImplTest extends ConsumerTestCase {

    private ComptabiliteDao dao = getDaoProxy().getComptabiliteDao();

    @Test
    public void getListCompteComptableTest() {
        List<CompteComptable> compteComptableList = dao.getListCompteComptable();
        Assert.assertTrue(compteComptableList.size() > 1);
    }

    @Test
    public void getListJournalComptableTest() {
        List<JournalComptable> journalComptableList = dao.getListJournalComptable();
        Assert.assertTrue(journalComptableList.size() > 1);
    }


    @Test
    public void getListSequenceEcritureComptableTest() {
        List<SequenceEcritureComptable> sequenceEcritureComptableList = dao.getListSequenceEcritureComptable();
        Assert.assertTrue(sequenceEcritureComptableList.size() > 1);
    }

    @Test
    public void loadListLigneEcritureTest(){
        EcritureComptable ecritureComptable = new EcritureComptable();
        Date currentDate = new Date();
        Integer currentYear = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
        ecritureComptable.setJournal(new JournalComptable("AC", "Matières Premières"));
        ecritureComptable.setReference("AC-" + currentYear + "/00006");
        ecritureComptable.setDate(currentDate);
        ecritureComptable.setLibelle("Matières Premières Test");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606), "Cartouches imprimantes", new BigDecimal(23), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), "Facture Test", new BigDecimal(350), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(512), "Virement bancaire", null, new BigDecimal(373)));

        dao.insertEcritureComptable(ecritureComptable);

        List<LigneEcritureComptable> ligneEcritureComptableList = dao.loadListLigneEcriture(ecritureComptable);

        Assert.assertEquals(3,ligneEcritureComptableList.size());

        dao.deleteEcritureComptable(ecritureComptable.getId());

    }

    // ==================== EcritureComptable - GET ====================

    @Test
    public void getListEcritureComptableTest() {
        List<EcritureComptable> ecritureComptableList = dao.getListEcritureComptable();
        Assert.assertTrue(ecritureComptableList.size() > 1);
    }

    @Test
    public void getEcritureComptableByIdTest() throws NotFoundException {
        EcritureComptable ecritureComptable = dao.getEcritureComptable(-3);
        Assert.assertEquals("BQ-2016/00003", ecritureComptable.getReference());

    }

    @Test
    public void getEcritureComptableByRefTest() throws NotFoundException {
        EcritureComptable ecritureComptable = dao.getEcritureComptableByRef("BQ-2016/00003");
        Assert.assertEquals("BQ", ecritureComptable.getJournal().getCode());
        Assert.assertEquals(-3, ecritureComptable.getId().intValue());
    }

    // ==================== EcritureComptable - INSERT ====================

    @Test
    public void insertEcritureComptableTest() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        Date currentDate = new Date();
        Integer currentYear = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
        ecritureComptable.setJournal(new JournalComptable("AC", "Matières Premières"));
        ecritureComptable.setReference("AC-" + currentYear + "/00006");
        ecritureComptable.setDate(currentDate);
        ecritureComptable.setLibelle("Matières Premières Test");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606), "Cartouches imprimantes", new BigDecimal(23), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), "Facture Test", new BigDecimal(350), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(512), "Virement bancaire", null, new BigDecimal(373)));

        dao.insertEcritureComptable(ecritureComptable);
        EcritureComptable ecritureComptable1 = dao.getEcritureComptableByRef("AC-" + currentYear + "/00006");

        Assert.assertTrue(ecritureComptable.getReference().equals(ecritureComptable1.getReference()));
        Assert.assertTrue(ecritureComptable.getLibelle().equals(ecritureComptable1.getLibelle()));

        dao.deleteEcritureComptable(ecritureComptable1.getId());


    }

    // ==================== EcritureComptable - UPDATE ====================

    @Test
    public void updateEcritureComptableTest() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        Date currentDate = new Date();
        Integer currentYear = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
        ecritureComptable.setJournal(new JournalComptable("AC", "Matières Premières"));
        ecritureComptable.setReference("AC-" + currentYear + "/00006");
        ecritureComptable.setDate(currentDate);
        ecritureComptable.setLibelle("Matières Premières Test");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606), "Cartouches imprimantes", new BigDecimal(23), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401), "Facture Test", new BigDecimal(350), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(512), "Virement bancaire", null, new BigDecimal(373)));

        dao.insertEcritureComptable(ecritureComptable);
        EcritureComptable ecritureComptable1 = dao.getEcritureComptableByRef("AC-" + currentYear + "/00006");

        ecritureComptable1.setLibelle("Matières Premières Update");

        dao.updateEcritureComptable(ecritureComptable1);

        Assert.assertTrue(dao.getEcritureComptableByRef("AC-" + currentYear + "/00006").getLibelle().equals("Matières Premières Update"));
        dao.deleteEcritureComptable(ecritureComptable1.getId());

    }

    // ==================== EcritureComptable - DELETE ====================

    @Test(expected = NotFoundException.class)
    public void deleteEcritureComptableTest() throws NotFoundException{
        dao.deleteEcritureComptable(-6);
        dao.getEcritureComptable(-6);

    }

    // ==================== SequenceEcritureComptable - INSERT - UPDATE - DELETE ====================

    @Test
    public void insertUpdateDeleteSequenceEcritureComptableTest() throws NotFoundException {

        dao.insertSequenceEcritureComptable(2020, "AC");
        List<SequenceEcritureComptable> vList = dao.getListSequenceEcritureComptable();
        SequenceEcritureComptable sequenceBis = new SequenceEcritureComptable();
        for (SequenceEcritureComptable seq :vList){
            if(seq.getCodeJournal().equals("AC") && seq.getAnnee()==2020){
                sequenceBis=seq;
            }
        }

        Assert.assertEquals("AC", sequenceBis.getCodeJournal());
        Assert.assertTrue(sequenceBis.getAnnee()==2020);
        Assert.assertTrue(sequenceBis.getDerniereValeur()==1);

        SequenceEcritureComptable sequenceAfterUpdate = dao.updateSequenceEcritureComptable(sequenceBis);

        Assert.assertEquals("AC", sequenceAfterUpdate.getCodeJournal());
        Assert.assertEquals(2, sequenceAfterUpdate.getDerniereValeur().intValue());

        dao.deleteSequenceEcritureComptable(sequenceAfterUpdate);

        List<SequenceEcritureComptable> sequenceEcritureComptableList = dao.getListSequenceEcritureComptable();

        Assert.assertEquals(4, sequenceEcritureComptableList.size());

    }

    @Test
    public void getSequenceJournalTest() throws NotFoundException {
        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("Libellé Test");

        LocalDate localDate = LocalDate.of(2016, 9, 15);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);

        // Journal existant
        ecritureComptable.setJournal(new JournalComptable("BQ", "Banque"));
        dao.insertEcritureComptable(ecritureComptable);
        dao.getSequenceJournal(ecritureComptable);
        Assert.assertEquals(51, dao.getSequenceJournal(ecritureComptable).getDerniereValeur().intValue());
        Assert.assertEquals("BQ", dao.getSequenceJournal(ecritureComptable).getCodeJournal());
        dao.deleteEcritureComptable(ecritureComptable.getId());
    }



    @Test
    public void isCodeJournalValidTest(){

        Assert.assertTrue(dao.isCodeJournalValid("BQ"));
        Assert.assertFalse(dao.isCodeJournalValid("RZ"));

    }

}

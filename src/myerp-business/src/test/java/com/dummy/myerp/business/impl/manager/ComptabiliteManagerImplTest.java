package com.dummy.myerp.business.impl.manager;


import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.sql.Date.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComptabiliteManagerImplTest extends AbstractBusinessManager {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    @Mock
    private static DaoProxy daoProxyMock;

    @Mock
    private static TransactionManager transactionManager;

    @Mock
    private ComptabiliteDao comptabiliteDaoMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(this.daoProxyMock.getComptabiliteDao()).thenReturn(this.comptabiliteDaoMock);
        AbstractBusinessManager.configure(null, daoProxyMock, transactionManager);
    }


    @Test
    public void checkEcritureComptableUnit() throws Exception {
        EcritureComptable vEcritureComptable;

        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        manager.checkEcritureComptableUnit(vEcritureComptable);


    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG1() {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(100), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(100)));

        assertEquals("Le solde est négatif", manager.RG_Compta_1(vEcritureComptable));

        EcritureComptable vEcritureComptable2;
        vEcritureComptable2 = new EcritureComptable();
        vEcritureComptable2.setJournal(new JournalComptable("BF", "Vente"));
        vEcritureComptable2.setDate(new Date());
        vEcritureComptable2.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(123)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, null, new BigDecimal(100)));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(50), null));

        assertEquals("Le solde est positif", manager.RG_Compta_1(vEcritureComptable));

    }



    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(1234)));
        manager.checkIfEcritureIsEquilibree(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        manager.checkIfEcritureContainsAtLeastTwoLines(vEcritureComptable);

    }


    @Test
    public void checkEcritureComptableUnitRG4()  {
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("-35.40"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "libellé", null, new BigDecimal("-35.40")));
        Assertions.assertEquals(new BigDecimal("-35.40"), manager.RG_Compta_4(vEcritureComptable), "1 débit / 1 crédit, négatif");
        }



    @Test
    public void checkEcritureComptableUnitRG5() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");

        manager.checkIfRefIsToTheRightFormat(vEcritureComptable);
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG6() throws Exception {

        // Nouvelle écriture pas encore persistée et donc sans ID
        EcritureComptable ecriture1 = new EcritureComptable();
        ecriture1.setJournal(new JournalComptable("AC", "Achat"));
        LocalDate localDate1 = LocalDate.of(2020, 07, 20);
        Date date1 = valueOf(localDate1);
        ecriture1.setDate(date1);
        ecriture1.setReference("AC-2020/00987");

        // Ecriture en BDD
        EcritureComptable ecriture2 = new EcritureComptable();
        ecriture2.setId(100);
        ecriture2.setJournal(new JournalComptable("AC", "Achat"));
        LocalDate localDate2 = LocalDate.of(2020, 07, 25);
        Date date2 = valueOf(localDate2);
        ecriture2.setDate(date2);
        ecriture2.setReference("AC-2020/00987");

        // Une écriture non persistée à la même référence qu'une autre écriture en BDD
        when(getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(Mockito.anyString())).thenReturn(ecriture2);
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableContext(ecriture1));
        assertEquals("La BDD contient une autre écriture comptable avec la même référence.", thrown.getMessage());

        // La recherche d'une écriture persistée ne retourne qu'elle même : pas d'exception
        when(getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(Mockito.anyString())).thenReturn(ecriture2);
        manager.checkEcritureComptableContext(ecriture2);

        // La recherche d'une écriture non persistée  ne retourne rien : pas d'exception
        when(getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(Mockito.anyString())).thenThrow(new NotFoundException());
        manager.checkEcritureComptableContext(ecriture1);


    }

    @Test
    public void checkEcritureComptableUnitRG7() throws Exception {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal("123.987"),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal("123.987")));

        FunctionalException thrown3 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_Contraintes(ecritureComptable));


        assertTrue(thrown3.getMessage().contains("L'écriture comptable ne respecte pas les contraintes de validation"));
        assertTrue(thrown3.getMessage().contains("Valeur numérique hors limite"));

    }


    @Test
    public void getListEcritureComptableTest() {
        List<EcritureComptable> ecritureComptableListMock =
                new ArrayList<>(Arrays.asList(new EcritureComptable(),
                        new EcritureComptable(),
                        new EcritureComptable()));

        when(getDaoProxy().getComptabiliteDao().getListEcritureComptable()).thenReturn(ecritureComptableListMock);

        List<EcritureComptable> ecritureComptableList = manager.getListEcritureComptable();
        assertEquals(3, ecritureComptableList.size());

    }

    @Test
    public void getListCompteComptableTest() {
        List<CompteComptable> compteComptableListMock =
                new ArrayList<>(Arrays.asList(new CompteComptable(12, "libellé"),
                        new CompteComptable(13, "libellé"),
                        new CompteComptable(14, "libellé")));
        when(getDaoProxy().getComptabiliteDao().getListCompteComptable()).thenReturn(compteComptableListMock);

        List<CompteComptable> compteComptableList = manager.getListCompteComptable();
        assertEquals(3, compteComptableList.size());


    }

    @Test
    public void getListJournalComptableTest() {
        List<JournalComptable> journalComptableListMock =
                new ArrayList<>(Arrays.asList(new JournalComptable("GT", "Journal Test 1"),
                        new JournalComptable("TY", "Journal Test 2")));
        when(getDaoProxy().getComptabiliteDao().getListJournalComptable()).thenReturn(journalComptableListMock);

        List<JournalComptable> journalComptableList = manager.getListJournalComptable();
        assertEquals(2, journalComptableList.size());
        assertEquals("Journal Test 1", journalComptableList.get(0).getLibelle());
    }

    @Test
    @DisplayName("addReference")
    public void addReferenceTest() throws FunctionalException, NotFoundException {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setJournal(new JournalComptable("TR", "Achat"));

        LocalDate localDate = LocalDate.of(2020, 07, 20);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);

        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));


        // Code journal inexistant en BDD
        when(getDaoProxy().getComptabiliteDao().isCodeJournalValid(Mockito.anyString())).thenReturn(false);
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> manager.addReference(ecritureComptable));
        assertEquals("Le code journal n'existe pas en base de donnée.", thrown.getMessage());

        // Code journal et année existants en BDD
        when(getDaoProxy().getComptabiliteDao().isCodeJournalValid(Mockito.anyString())).thenReturn(true);

        SequenceEcritureComptable sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setCodeJournal("TR");
        sequenceEcritureComptable.setAnnee(2020);
        sequenceEcritureComptable.setDerniereValeur(156);

        when(getDaoProxy().getComptabiliteDao().getSequenceJournal(any(EcritureComptable.class))).thenReturn(sequenceEcritureComptable);

        assertEquals(null, ecritureComptable.getReference());
        manager.addReference(ecritureComptable);
        assertEquals("TR-2020/00157", ecritureComptable.getReference());


        // Code journal et année existants en BDD mais lastReference = 99999
        sequenceEcritureComptable.setDerniereValeur(99999);
        when(getDaoProxy().getComptabiliteDao().getSequenceJournal(any(EcritureComptable.class))).thenReturn(sequenceEcritureComptable);
        FunctionalException thrown2 = assertThrows(FunctionalException.class, () -> manager.addReference(ecritureComptable));
        assertEquals("Nombre maximal d'écritures atteint. Veuillez choisir un nouveau journal.", thrown2.getMessage());
    }



}





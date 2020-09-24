package com.dummy.myerp.business.impl.manager;


import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testbusiness.business.BusinessTestCase;
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
import java.util.*;

import static java.sql.Date.valueOf;
import static org.junit.Assert.assertEquals;
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
    public void checkEcritureComptableUnitTest() throws FunctionalException {

        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        LocalDate localDate = LocalDate.of(2020, 12, 28);
        Date date = valueOf(localDate);
        vEcritureComptable.setDate(date);

        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00002");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        manager.checkEcritureComptableUnit_Contraintes(vEcritureComptable);

        // JournalComptable  null
        vEcritureComptable.setJournal(null);
        vEcritureComptable.setDate(date);

        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        FunctionalException thrown1 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_Contraintes(vEcritureComptable));
        Assert.assertEquals("L'écriture comptable ne respecte pas les contraintes de validation. Le journal comptable ne doit pas être null.", thrown1.getMessage());

        // Taille du libellé = 0
        vEcritureComptable.setJournal(new JournalComptable("AB", "journal fournisseurs"));
        vEcritureComptable.setLibelle("");
        FunctionalException thrown2 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_Contraintes(vEcritureComptable));
        Assert.assertEquals("L'écriture comptable ne respecte pas les contraintes de validation. Le libellé doit être compris entre 1 et 200 caractères.", thrown2.getMessage());

        // Format des montants non respecté
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal("123.987"),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal("123.987")));

        FunctionalException thrown3 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_Contraintes(vEcritureComptable));
        Assert.assertEquals("L'écriture comptable ne respecte pas les contraintes de validation. " +
                        "Le format du montant comptable est invalide: max 13 chiffres et 2 décimaux. " +
                        "Le format du montant comptable est invalide: max 13 chiffres et 2 décimaux.",
                thrown3.getMessage());

    }


    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolationTest() throws Exception {
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    @Test
    public void checkEcritureComptableUnitRG1Test() {
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

        assertEquals("Le solde est négatif", manager.checkEcritureComptableUnit_RG1(vEcritureComptable));

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

        assertEquals("Le solde est positif", manager.checkEcritureComptableUnit_RG1(vEcritureComptable));

    }



    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2Test() throws Exception {
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
        manager.checkEcritureComptableUnit_RG2(vEcritureComptable);

        // Ecriture équilibrée avec montants décimaux et non décimaux
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal("123.20"),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, new BigDecimal("123"),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(3),
                null, null,
                new BigDecimal("246.20")));
        manager.checkEcritureComptableUnit_RG2(vEcritureComptable);

        // Ecriture non-équilibrée avec montants entiers
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(156),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(1244)));

        FunctionalException thrown1 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG2(vEcritureComptable));
        assertEquals("L'écriture comptable n'est pas équilibrée.", thrown1.getMessage());

        // Ecriture non-équilibrée avec montants décimaux
        vEcritureComptable.getListLigneEcriture().clear();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal("122.90"),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal("17.89")));

        FunctionalException thrown2 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG2(vEcritureComptable));
        assertEquals("L'écriture comptable n'est pas équilibrée.", thrown2.getMessage());
    }

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Test() throws Exception {

        // 1 ligne d'écriture au crédit et une au débit
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
        manager.checkEcritureComptableUnit_RG3(vEcritureComptable);

        // 0 ligne d'écriture au crédit
        EcritureComptable ecritureComptable = new EcritureComptable();

        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        LocalDate localDate = LocalDate.of(2020, 12, 28);
        Date date = valueOf(localDate);
        ecritureComptable.setDate(date);

        ecritureComptable.setLibelle("Libelle");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, new BigDecimal(300), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(3), null, new BigDecimal(300), null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4), null, new BigDecimal(222), null));

        FunctionalException thrown1 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG3(ecritureComptable));
        assertEquals("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.", thrown1.getMessage());

        // 0 ligne d'écriture au débit
        ecritureComptable.getListLigneEcriture().clear();

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal(123)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal(300)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(3), null, null, new BigDecimal(300)));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4), null, null, new BigDecimal(222)));

        FunctionalException thrown2 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG3(ecritureComptable));
        assertEquals("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.", thrown2.getMessage());

        // 1 seule ligne d'écriture comptable avec montant au débit ET au crédit

        ecritureComptable.getListLigneEcriture().clear();
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal(123), new BigDecimal(123)));

        FunctionalException thrown3 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG3(ecritureComptable));
        assertEquals("L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.", thrown3.getMessage());

    }


    @Test
    public void checkEcritureComptableUnitRG4Test()  {

        //1 ligne d'écriture au crédit négative et 1 ligne au débit négative
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("-35.40"), null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(), "libellé", null, new BigDecimal("-35.40")));
        Assertions.assertEquals(new BigDecimal("-35.40"), manager.checkEcritureComptableUnit_RG4(vEcritureComptable), "1 débit / 1 crédit, négatif");
        }



    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG5Test() throws Exception {

        //Correct
        EcritureComptable vEcritureComptable;
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");

        manager.checkEcritureComptableUnit_RG5(vEcritureComptable);

        // Sans référence
        EcritureComptable vEcritureComptable2;
        vEcritureComptable2 = new EcritureComptable();
        vEcritureComptable2.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable2.setDate(new Date());
        vEcritureComptable2.setLibelle("Libelle");

        FunctionalException thrown5 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG5(vEcritureComptable2));
        Assert.assertEquals("La référence ne peut pas être null.", thrown5.getMessage());

        // Code journal incorrect
        vEcritureComptable.setReference("AAC-2020/00003");
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG5(vEcritureComptable));
        assertEquals("Format de la référence incorrect : le code journal ne correspond pas au journal de l'écriture.", thrown.getMessage());

        // Année incorrecte
        vEcritureComptable.setReference("AC-2019/00003");
        FunctionalException thrown1 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG5(vEcritureComptable));
        assertEquals("Format de la référence incorrect : l'année indiquée dans la référence ne correspond pas à la date de l'écriture.", thrown1.getMessage());

        // Séquence incorrecte
        vEcritureComptable.setReference("AC-2020/3");
        FunctionalException thrown2 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG5(vEcritureComptable));
        assertEquals("Format de la référence incorrect : le numéro de séquence doit être représenté avec 5 chiffres.", thrown2.getMessage());

        // Séparateurs incorrects
        vEcritureComptable.setReference("AC/2020/00003");
        FunctionalException thrown4 = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG5(vEcritureComptable));
        assertEquals("Format de la référence incorrect : le code journal est suivi d'un - et l'année d'un /", thrown4.getMessage());


    }

    @Test
    public void checkEcritureComptableUnitRG6Test() throws Exception {

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
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> manager.checkEcritureComptableUnit_RG6(ecriture1));
        assertEquals("La BDD contient une autre écriture comptable avec la même référence.", thrown.getMessage());

        // La recherche d'une écriture persistée ne retourne qu'elle même : pas d'exception
        when(getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(Mockito.anyString())).thenReturn(ecriture2);
        manager.checkEcritureComptableUnit_RG6(ecriture2);

        // La recherche d'une écriture non persistée  ne retourne rien : pas d'exception
        when(getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(Mockito.anyString())).thenThrow(new NotFoundException());
        manager.checkEcritureComptableUnit_RG6(ecriture1);


    }

    /**
     * Verifie qu'une exception est lancee si une ligne d'ecriture a plus de 2 chiffres apres la virgule
     * @throws Exception
     */

    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG7Test() throws Exception {

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setLibelle("Libelle");
        ecritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2018,1,1);
        ecritureComptable.setDate(calendar.getTime());
        ecritureComptable.setReference("AC-2018/00123");
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal("123.987"),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal("123.987")));


        manager.checkEcritureComptableUnit(ecritureComptable);

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





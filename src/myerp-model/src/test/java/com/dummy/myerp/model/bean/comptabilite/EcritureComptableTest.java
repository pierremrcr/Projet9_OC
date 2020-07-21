package com.dummy.myerp.model.bean.comptabilite;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;

import static java.sql.Date.valueOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class EcritureComptableTest {

    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
        return vRetour;
    }

    LigneEcritureComptable debit_positif_200_50 = new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("200.50"), null);
    LigneEcritureComptable debit_positif_49_50 = new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("49.50"), null);
    LigneEcritureComptable debit_positif_250 = new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("250.00"), null);
    LigneEcritureComptable debit_negatif_35_40 = new LigneEcritureComptable(new CompteComptable(), "libellé", new BigDecimal("-35.40"), null);

    LigneEcritureComptable credit_positif_250 = new LigneEcritureComptable(new CompteComptable(), "libellé",null,  new BigDecimal("250"));
    LigneEcritureComptable credit_positif_100 = new LigneEcritureComptable(new CompteComptable(), "libellé",null,  new BigDecimal("100.00"));
    LigneEcritureComptable credit_positif_150 = new LigneEcritureComptable(new CompteComptable(), "libellé",null,  new BigDecimal("150.00"));
    LigneEcritureComptable credit_negatif_35_40 = new LigneEcritureComptable(new CompteComptable(), "libellé", null, new BigDecimal("-35.40"));


    @Test
    public void getTotalDebitTest() {

        EcritureComptable ecritureComptable1  = new EcritureComptable();
        ecritureComptable1.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable1.getListLigneEcriture().add(credit_positif_250);
        assertEquals(new BigDecimal("250.00"), ecritureComptable1.getTotalDebit(), "1 débit / 1 crédit, positif");

        EcritureComptable ecritureComptable2  = new EcritureComptable();
        ecritureComptable2.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable2.getListLigneEcriture().add(credit_positif_100);
        ecritureComptable2.getListLigneEcriture().add(credit_positif_150);
        assertEquals(new BigDecimal("250.00"), ecritureComptable2.getTotalDebit(), "1 débit / 2 crédit, positif");

        EcritureComptable ecritureComptable3  = new EcritureComptable();
        ecritureComptable3.getListLigneEcriture().add(debit_positif_200_50);
        ecritureComptable3.getListLigneEcriture().add(debit_positif_49_50);
        ecritureComptable3.getListLigneEcriture().add(credit_positif_250);
        assertEquals(new BigDecimal("250.00"), ecritureComptable3.getTotalDebit(), "2 débit / 1 crédit, positif");

        EcritureComptable ecritureComptable4  = new EcritureComptable();
        ecritureComptable4.getListLigneEcriture().add(debit_negatif_35_40);
        ecritureComptable4.getListLigneEcriture().add(credit_negatif_35_40);
        assertEquals(new BigDecimal("-35.40"), ecritureComptable4.getTotalDebit(), "1 débit / 1 crédit, négatif");

        EcritureComptable ecritureComptable5  = new EcritureComptable();
        ecritureComptable5.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable5.getListLigneEcriture().add(credit_positif_150);
        assertEquals(new BigDecimal("250.00"), ecritureComptable5.getTotalDebit(), "1 débit / 1 crédit, positif, non equilibré");


    }


    @Test
    public void getTotalCreditTest() {

        EcritureComptable ecritureComptable1  = new EcritureComptable();
        ecritureComptable1.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable1.getListLigneEcriture().add(credit_positif_250);
        assertEquals(new BigDecimal("250.00"), ecritureComptable1.getTotalDebit(), "1 débit / 1 crédit, positif");

        EcritureComptable ecritureComptable2  = new EcritureComptable();
        ecritureComptable2.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable2.getListLigneEcriture().add(credit_positif_100);
        ecritureComptable2.getListLigneEcriture().add(credit_positif_150);
        assertEquals(new BigDecimal("250.00"), ecritureComptable2.getTotalDebit(), "1 débit / 2 crédit, positif");

        EcritureComptable ecritureComptable3  = new EcritureComptable();
        ecritureComptable3.getListLigneEcriture().add(debit_positif_200_50);
        ecritureComptable3.getListLigneEcriture().add(debit_positif_49_50);
        ecritureComptable3.getListLigneEcriture().add(credit_positif_250);
        assertEquals(new BigDecimal("250.00"), ecritureComptable3.getTotalDebit(), "2 débit / 1 crédit, positif");

        //Méthode validant la règle de gestion RG_Compta_4
        EcritureComptable ecritureComptable4  = new EcritureComptable();
        ecritureComptable4.getListLigneEcriture().add(debit_negatif_35_40);
        ecritureComptable4.getListLigneEcriture().add(credit_negatif_35_40);
        assertEquals(new BigDecimal("-35.40"), ecritureComptable4.getTotalDebit(), "1 débit / 1 crédit, négatif");

        EcritureComptable ecritureComptable5  = new EcritureComptable();
        ecritureComptable5.getListLigneEcriture().add(debit_positif_250);
        ecritureComptable5.getListLigneEcriture().add(credit_positif_150);
        assertEquals(new BigDecimal("250.00"), ecritureComptable5.getTotalDebit(), "1 débit / 1 crédit, positif, non equilibré");

    }

    @Test
    public void isEquilibree() {

        EcritureComptable vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        Assert.assertTrue(vEcriture.toString(), vEcriture.isEquilibree());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        Assert.assertFalse(vEcriture.toString(), vEcriture.isEquilibree());
    }



    @Test
    public void testToString(){


        LocalDate localDate = LocalDate.of(2020, 07, 15);
        Date date = valueOf(localDate);

        JournalComptable journalComptable = new JournalComptable();
        journalComptable.setCode("AX");
        journalComptable.setLibelle("Journal de banque");

        EcritureComptable ecritureComptable = new EcritureComptable();
        ecritureComptable.setReference("AX-2020/00076");
        ecritureComptable.setDate(date);
        ecritureComptable.setId(23);
        ecritureComptable.setJournal(journalComptable);
        ecritureComptable.setLibelle("Libellé");

        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                null, new BigDecimal(123),
                null));
        ecritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));



        assertEquals("EcritureComptable{id=23, journal=JournalComptable{code='AX', libelle='Journal de banque'}, reference='AX-2020/00076', date=2020-12-28, libelle='Libellé', totalDebit=123.00, totalCredit=123.00, listLigneEcriture=[\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=401, libelle='null'}, libelle='null', debit=123, credit=null}\n" +
                "LigneEcritureComptable{compteComptable=CompteComptable{numero=411, libelle='null'}, libelle='null', debit=null, credit=123}\n" +
                "]}", ecritureComptable.toString());

    }


}




package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class LigneEcritureComptableTest {

    @Test
    public void toStringTest(){
        LigneEcritureComptable ligneEcritureComptable = new LigneEcritureComptable();
        ligneEcritureComptable.setCompteComptable(null);
        ligneEcritureComptable.setLibelle("libelle");
        ligneEcritureComptable.setCredit(BigDecimal.valueOf(10));
        ligneEcritureComptable.setDebit(null);

        Assert.assertEquals(ligneEcritureComptable.toString(),"LigneEcritureComptable{compteComptable=null, libelle='libelle', debit=null, credit=10}");
    }
}

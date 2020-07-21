package com.dummy.myerp.model.bean.comptabilite;


import org.junit.Assert;
import org.junit.Test;

public class SequenceEcritureComptableTest {

        @Test
        public void toStringTest(){
            SequenceEcritureComptable sequence = new SequenceEcritureComptable();
            sequence.setCodeJournal("AC");
            sequence.setAnnee(2018);
            sequence.setDerniereValeur(1);

            Assert.assertEquals(sequence.toString(),"SequenceEcritureComptable{codeJournal='AC', annee=2018, derniereValeur=1}");
        }
    }


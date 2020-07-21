package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class JournalComptableTest {

    @Test
    public void getJournalComptableByCodeTest(){

        List<JournalComptable> journalComptableList = new ArrayList<>();

        for (int i = 1; i<4; i++){
            JournalComptable journal = Mockito.mock(JournalComptable.class);
            Mockito.when(journal.getCode()).thenReturn(i+"0");
            Mockito.when(journal.getLibelle()).thenReturn("Libelle" + i);
            journalComptableList.add(journal);
        }

        for(int i=1; i<4; i++){
            Assert.assertEquals(JournalComptable.getByCode(journalComptableList, i+"0").getLibelle(), "Libelle"+i);
        }

    }
}

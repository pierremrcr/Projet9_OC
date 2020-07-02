package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CompteComptableTest {

    @Test
    public void getCompteComptableByCodeTest(){

        List<CompteComptable> listeComptes = new ArrayList<>();

        for(int i=0; i<4; i++){
            CompteComptable compte = Mockito.mock(CompteComptable.class);
            Mockito.when(compte.getNumero()).thenReturn(i);
            Mockito.when(compte.getLibelle()).thenReturn("Libelle " + i);
            listeComptes.add(compte);
        }

        for(int i=0; i<4; i++){
            Assert.assertEquals(CompteComptable.getByNumero(listeComptes, i).getLibelle(), "Libelle " + i);
        }

    }
}

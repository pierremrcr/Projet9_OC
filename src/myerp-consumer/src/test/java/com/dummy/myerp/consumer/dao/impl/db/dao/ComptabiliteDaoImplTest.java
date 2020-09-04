package com.dummy.myerp.consumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.testconsumer.consumer.ConsumerTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ComptabiliteDaoImplTest extends ConsumerTestCase {

    private ComptabiliteDao dao = getDaoProxy().getComptabiliteDao();

    @Test
    public void getListCompteComptableTest(){
        List<CompteComptable> compteComptableList = dao.getListCompteComptable();
        Assert.assertTrue(compteComptableList.size()>1);
    }

    @Test
    public void getListJournalComptableTest(){
        List<JournalComptable> journalComptableList = dao.getListJournalComptable();
        Assert.assertTrue(journalComptableList.size()>1);
    }

    @Test
    public void getListEcritureComptableTest(){
        List<EcritureComptable> ecritureComptableList = dao.getListEcritureComptable();
        Assert.assertTrue(ecritureComptableList.size()>1);
    }

    @Test
    public void getListSequenceEcritureComptableTest(){
        List<SequenceEcritureComptable> sequenceEcritureComptableList = dao.getListSequenceEcritureComptable();
        Assert.assertTrue(sequenceEcritureComptableList.size()>1);
    }



}

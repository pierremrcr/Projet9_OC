package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

import static com.dummy.myerp.technical.util.ConvertDate.convertDateToCalendar;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }


    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException {

        //On récupère l'année de l'écriture que l'on convertit au format calendar
        int yearEcritureComptable = convertDateToCalendar(pEcritureComptable.getDate()).get(Calendar.YEAR);

        String reference = pEcritureComptable.getJournal().getCode() + "-" + yearEcritureComptable + "/";


        if (!getDaoProxy().getComptabiliteDao().isCodeJournalValid(pEcritureComptable.getJournal().getCode())) {
            throw new FunctionalException("Le code journal n'existe pas en base de donnée.");

        } else {

            try {
            //On récupère la séquence correspondante à l'écriture afin de connaitre la dernière valeur
            SequenceEcritureComptable sequenceEcritureComptable = getDaoProxy().getComptabiliteDao().getSequenceJournal(pEcritureComptable);

            if (sequenceEcritureComptable.getDerniereValeur() == 99999) {
                throw new FunctionalException("Nombre maximal d'écritures atteint. Veuillez choisir un nouveau journal.");
            } else {
                //On ajoute à la référence la dernière valeur que l'on incrémente de 1
                reference += (StringUtils.leftPad(String.valueOf(sequenceEcritureComptable.getDerniereValeur() + 1), 5, "0"));
                //Update de la séquence avec la nouvelle valeur
                getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(sequenceEcritureComptable);
            }
            //Dans le cas ou l'écriture n'est rattachée à aucune séquence, création d'une nouvelle référence et d'une nouvelle séquence correspondant à l'écriture
            } catch (NotFoundException notFoundException) {
                reference += "00001";
                getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(yearEcritureComptable, pEcritureComptable.getJournal().getCode());
            }
            pEcritureComptable.setReference(reference);
        }
    }


    /**
     * {@inheritDoc}
     */
    // TODO à tester
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableUnit_RG6(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */

    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        checkEcritureComptableUnit_Contraintes(pEcritureComptable);

        checkEcritureComptableUnit_RG1(pEcritureComptable);

        checkEcritureComptableUnit_RG2(pEcritureComptable);

        checkEcritureComptableUnit_RG3(pEcritureComptable);

        checkEcritureComptableUnit_RG4(pEcritureComptable);

        checkEcritureComptableUnit_RG5(pEcritureComptable);

        checkEcritureComptableUnit_RG6(pEcritureComptable);


    }

    public void checkEcritureComptableUnit_Contraintes(EcritureComptable pEcritureComptable) throws FunctionalException {
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            Iterator<ConstraintViolation<EcritureComptable>> it = vViolations.iterator();
            String customMessage = "L'écriture comptable ne respecte pas les contraintes de validation.";
            while (it.hasNext()) {
                customMessage = customMessage + " " + it.next().getMessage();
            }
            throw new FunctionalException(customMessage);
        }
    }

    public void checkEcritureComptableUnit_RG5(EcritureComptable pEcritureComptable) throws FunctionalException {

        Integer currentYear = convertDateToCalendar(pEcritureComptable.getDate()).get(Calendar.YEAR);

        String[] referenceSplit = pEcritureComptable.getReference().split("-", 2);
        if(!referenceSplit[0].equals(pEcritureComptable.getJournal().getCode())){
            throw new FunctionalException("Le code journal ne correspond pas au code journal de la reférence.");
        }

        // Vérifie que le code du journal dans la référence correspond au journal de l'écriture
        String codeJournalInReference = pEcritureComptable.getReference().substring(0, 2);
        if (!codeJournalInReference.equals(pEcritureComptable.getJournal().getCode())) {
            throw new FunctionalException("Format de la référence incorrect : le code journal ne correspond pas au journal de l'écriture.");
        }

        String[] referenceSplitDate = referenceSplit[1].split("/", 5);
        if (Integer.parseInt(referenceSplitDate[0]) != currentYear ) {
            throw new FunctionalException("L'année dans la référence ne correspond pas à la date de l'écriture.");
        }

        else {
            throw new FunctionalException("La référence ne peut pas être null.");
        }
    }

    public void checkEcritureComptableUnit_RG3(EcritureComptable pEcritureComptable) throws FunctionalException {
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        // avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
                || vNbrCredit < 1
                || vNbrDebit < 1) {
            throw new FunctionalException(
                    "L'écriture comptable doit avoir au moins ;deux lignes : une ligne au débit et une ligne au crédit.");
        }
    }

    public void checkEcritureComptableUnit_RG2(EcritureComptable pEcritureComptable) throws FunctionalException {
        if (!pEcritureComptable.isEquilibree()) {
               throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }
    }



    public BigDecimal checkEcritureComptableUnit_RG4(EcritureComptable pEcritureComptable)  {

        return pEcritureComptable.getTotalCredit();
    }

    @Override
    public String checkEcritureComptableUnit_RG1(EcritureComptable ecritureComptable)  {
        return ecritureComptable.getSolde();

    }


    public void checkEcritureComptableUnit_RG6(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("La BDD contient une autre écriture comptable avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
                // Si l'écriture à vérifier est une mise à jour (id != null)
                if(pEcritureComptable.getId() != null){
                    throw new FunctionalException("Aucune écriture comptable n'est enregistrée pour cette référence");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionManager transactionManager = getTransactionManager();
        TransactionStatus vTS = transactionManager.beginTransactionMyERP();

        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            try {
                getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}

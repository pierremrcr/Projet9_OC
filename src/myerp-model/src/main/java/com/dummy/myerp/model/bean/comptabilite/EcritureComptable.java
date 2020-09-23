package com.dummy.myerp.model.bean.comptabilite;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;


/**
 * Bean représentant une Écriture Comptable
 */
public class EcritureComptable {

    // ==================== Attributs ====================
    /** The Id. */
    private Integer id;
    /** Journal comptable */
    @NotNull (message = "Le journal comptable ne doit pas être null.")
    private JournalComptable journal;
    /** The Reference. */
    @Pattern(message = "Le format de la référence n'est pas correct.", regexp = "\\w{2}-\\d{4}/\\d{5}")
    private String reference;
    /** The Date. */
    @NotNull (message = "La date ne doit pas être null.")
    private Date date;

    /** The Libelle. */
    @NotNull (message = "Le libellé ne doit pas être null.")
    @Size(message = "Le libellé doit être compris entre 1 et 200 caractères.", min = 1, max = 200)
    private String libelle;

    /** La liste des lignes d'écriture comptable. */
    @Valid
    @Size(message = "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.",min = 2)
    private final List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();


    // ==================== Getters/Setters ====================
    public Integer getId() {
        return id;
    }

    public void setId(Integer pId) {
        id = pId;
    }

    public JournalComptable getJournal() {
        return journal;
    }

    public void setJournal(JournalComptable pJournal) {
        journal = pJournal;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String pReference) {
        reference = pReference;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String pLibelle) {
        libelle = pLibelle;
    }

    public List<LigneEcritureComptable> getListLigneEcriture() {
        return listLigneEcriture;
    }

    /**
     * Calcul et renvoie le total des montants au débit des lignes d'écriture
     *
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au débit
     */

    public BigDecimal getTotalDebit() {
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (LigneEcritureComptable vLigneEcritureComptable : listLigneEcriture) {
            if (vLigneEcritureComptable.getDebit() != null) {
                totalDebit = totalDebit.add(vLigneEcritureComptable.getDebit());
            }
        }
        return totalDebit.setScale(2,BigDecimal.ROUND_UP);
    }

    /**
     * Calcul et renvoie le total des montants au crédit des lignes d'écriture
     *
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au crédit
     */
    public BigDecimal getTotalCredit() {
        BigDecimal totalCredit = BigDecimal.ZERO; //Format(0,00)
        for (LigneEcritureComptable vLigneEcritureComptable : listLigneEcriture) {
            if (vLigneEcritureComptable.getCredit()!= null) { //Correction credit au lieu de débit
                totalCredit = totalCredit.add(vLigneEcritureComptable.getCredit());
            }
        }
        return totalCredit.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Renvoie si l'écriture est équilibrée (TotalDebit = TotalCrédit)
     * @return boolean
     */
    public boolean isEquilibree() {
        boolean vRetour = this.getTotalDebit().equals(getTotalCredit());
        return vRetour;
    }

    public String getSolde() {

        String message;

        if (this.getTotalCredit().compareTo(this.getTotalDebit()) > 0) {
            message ="Le solde est positif";
        } else {
            message = "Le solde est négatif";

        }

        return message;

    }


    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
            .append("id=").append(id)
            .append(vSEP).append("journal=").append(journal)
            .append(vSEP).append("reference='").append(reference).append('\'')
            .append(vSEP).append("date=").append(date)
            .append(vSEP).append("libelle='").append(libelle).append('\'')
            .append(vSEP).append("totalDebit=").append(this.getTotalDebit().toPlainString())
            .append(vSEP).append("totalCredit=").append(this.getTotalCredit().toPlainString())
            .append(vSEP).append("listLigneEcriture=[\n")
            .append(StringUtils.join(listLigneEcriture, "\n")).append("\n]")
            .append("}");
        return vStB.toString();
    }
}

package com.dummy.myerp.testbusiness.business;


import com.dummy.myerp.business.contrat.BusinessProxy;
import com.dummy.myerp.business.impl.TransactionManager;


    /**
     * Classe mère des classes de test d'intégration de la couche Consumer
     */
    public abstract class ConsumerTestCase {

        static {
            SpringRegistry.init();
        }

        /** {@link DaoProxy} */
        private static final DaoProxy DAO_PROXY = SpringRegistry.getDaoProxy();


        // ==================== Constructeurs ====================
        /**
         * Constructeur.
         */
        public ConsumerTestCase() {
        }


        // ==================== Getters/Setters ====================
        public static DaoProxy getDaoProxy() {
            return DAO_PROXY;
        }
    }

package Models;

import View.ViewFactory;

public class Model {
    private static Model model;
    private final ViewFactory viewfactory;
    private final DataBaseConnection dataBaseConnection;
    private TransactionsProcessing transactionsProcessing;

    // No-arg constructor
    private Model() {
        this.viewfactory = new ViewFactory();
        this.dataBaseConnection = new DataBaseConnection();
    }

    public static synchronized Model getInstance(){
        if(model == null){
            model = new Model();
        }
        return model;
    }

    //Getter method ViewFactory
    public ViewFactory getViewfactory() {return viewfactory;}
    //Getter method DatabaseConnection
    public DataBaseConnection getDataBaseConnection() {return dataBaseConnection;}
    //Getter method for TransactionProcessing
    public synchronized TransactionsProcessing getTransactionsProcessing() {if (transactionsProcessing == null) {
        transactionsProcessing = new TransactionsProcessing();}return transactionsProcessing;}
}

package com.getcollate;

import com.getcollate.trip.*;
import com.getcollate.trip.accounts.CATEGORY;
import com.getcollate.trip.accounts.SHARETYPE;
import com.getcollate.trip.accounts.settler.factory.SettlerFactory;
import com.getcollate.trip.accounts.Transaction;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.getcollate.trip.accounts.settler.SettlementMode.*;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) {
        System.out.println( "Hello World! New Changes inComing from the Main class!" );
        List<Transaction> listOfTransactions = Arrays.asList(new Transaction(100.13f, new Participant("Pranav"),
                CATEGORY.FOOD, SHARETYPE.EQUAL, new Date(),Arrays.asList(new Participant("Pranav"))));

//        Trip trip = new Trip("Trip to Bali", Arrays.asList("John", "Jane", "Jack"), SimplifiedBalanceSheet.class);
        Trip trip = new Trip("Trip to Bali", Arrays.asList(new Participant("John"), new Participant("Jane"), new Participant("Jack")));
        trip.addParticipants(Arrays.asList(new Participant("Pranav"), new Participant("Raj")));
        trip.removeParticipants(Arrays.asList("John", "Jane", "Jack"));
        trip.addTransactions(listOfTransactions); // returns transaction ids as keys and the transaction details as the value
        trip.settle(SettlerFactory.create(SIMPLIFIED));
    }
}

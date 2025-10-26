/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/* ATTENTION: TO_DO -> This file will contain all the logic to perform the data transformation */

package com.cs4347.main;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author nguyenp
 */
public class Normalization {
    
    public static void main(String[] args){
        System.out.println("Normalization process is working....");
        
        try{
            processBorrowers("borrower.csv");
        } catch(IOException | CsvException e){
            e.printStackTrace();
        }
    }
    
    /*
        Column Mapping
        ID0000id ->                     Card_id
        ssn ->                          Ssn
        first_name + last_name ->       Bname
        address + city + state ->       Address
        phone ->                        Phone
    
    TO_DO: Case Standardization. All names must use the same case convention
    */
    private static void processBorrowers(String inputFile) throws IOException, CsvException {
    System.out.println("Processing " + inputFile + " -> ");
    
    InputStream inputStream = Normalization.class.getResourceAsStream("/" + inputFile);
    if (inputStream == null) {
        System.err.println("File not found in resources: " + inputFile);
        return;
    }
    
    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
    List<String[]> allRecords = reader.readAll();
    
    // print only first 3 rows to test the reader (IT WORKS !)
    /*
        Normalization process is working....
        Processing borrower.csv -> 
        [ID0000id, ssn, first_name, last_name, email, address, city, state, phone]
        [ID000001, 850-47-3740, Mark, Morgan, mmorgan0@g.co, 5677 Coolidge Street, Plano, TX, (469) 904-1438]
        [ID000002, 256-95-4382, Eric, Warren, ewarren1@ed.gov, 9062 Schurz Drive, Dallas, TX, (214) 701-8127]
    */
    
//    for (int i = 0; i < 3; i++) {
//        String[] record = allRecords.get(i);
//        System.out.println(Arrays.toString(record));
//    }


}

}

    
}

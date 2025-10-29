/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/* ATTENTION: TO_DO -> This file will contain all the logic to perform the data transformation */

package com.cs4347.main;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

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
            processBorrowers("borrowers.csv", "borrower.csv");
            processBooks("books.csv", "book.csv", "authors.csv", "book_authors.csv");
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
    
    TO-DO: Read all rows and normalized(finished)
    TO-DO: Write to borrower.csv(finished)
    TO-DO: Case Standardization. All names must use the same case convention (finished)
    */
    private static void processBorrowers(String inputFile, String outputFile) throws IOException, CsvException {
        System.out.println("Processing " + inputFile);
        InputStream inputStream = Normalization.class.getResourceAsStream("/" + inputFile);
        if (inputStream == null) {
            System.err.println("File not found in resources: " + inputFile);
            return;
        }
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile));

        try{
            //Read all records at once (1001 rows, including the header row)
            List<String[]> allRecords = reader.readAll();
            //System.out.println("Rows read: " + allRecords.size());


            // print only first 3 rows to test the reader (IT WORKS !)
            /*
                Normalization process is working....
                Processing borrower.csv -> 
               THis is header --> [ID0000id, ssn, first_name, last_name, email, address, city, state, phone]
                [ID000001, 850-47-3740, Mark, Morgan, mmorgan0@g.co, 5677 Coolidge Street, Plano, TX, (469) 904-1438]
                [ID000002, 256-95-4382, Eric, Warren, ewarren1@ed.gov, 9062 Schurz Drive, Dallas, TX, (214) 701-8127]
            */

        //    for (int i = 0; i < 3; i++) {
        //        String[] record = allRecords.get(i);
        //        System.out.println(Arrays.toString(record));
        //    }
            String[] header = allRecords.remove(0);
            //System.out.println("Printing header: \n"+ Arrays.toString(header));


            //Create a map to find column index by name
            Map<String, Integer> headerMap = new HashMap<>();
            for(int i = 0; i < header.length; i++){
                headerMap.put(header[i], i);
                //System.out.println(headerMap);
            }

            //Write the new header for the output file
            writer.writeNext(new String[] {"Card_id", "Ssn", "Bname", "Address", "Phone"} );

           //Process each record
           for(String[] borrowerRows : allRecords){
               String cardID = borrowerRows[headerMap.get("ID0000id")];
               
               String ssn = borrowerRows[headerMap.get("ssn")];
               
               String bname = borrowerRows[headerMap.get("first_name")] 
                       + " "+ borrowerRows[headerMap.get("last_name")];
               
               String address = borrowerRows[headerMap.get("address")] 
                       + ", " + borrowerRows[headerMap.get("city")]
                       + ", " + borrowerRows[headerMap.get("state")];
               
               String phone = borrowerRows[headerMap.get("phone")];
               //System.out.println(bname);
               
               writer.writeNext(new String[]{cardID, ssn, bname,address,phone});

           }
           writer.flush();

        } catch (IOException e){
            e.printStackTrace();
        }

    }
    
    //---PART 2------
    
    /*
        BOOK table ( has to be unique )
        Isbn Title
    
        BOOK_AUTHORS table
        Author_id   Isbn
    
        AUTHORS table ( also has to be unique )
        Author_id   Name
        
    */
    
    private static void processBooks(String inputFile, String bookFile, String authorFile, String bookAuthorFile) throws IOException, CsvException{
        System.out.println("Started processing" + inputFile);
        
        //Book table (unique)
        Set<List<String>> uniqueBooks = new LinkedHashSet<>();
        //Authors table (unique)
        Map<String, Integer> authorsMap = new LinkedHashMap<>();
        //Book_Authors table
        Set<List<Object>> bookAuthors = new LinkedHashSet<>();
        
        int nextAuthorId = 1;
        
        InputStream inputStream = Normalization.class.getResourceAsStream("/" + inputFile);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator('\t')
                .build();

        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                .withCSVParser(parser)
                .build();
        
        CSVWriter writer = new CSVWriter(new FileWriter(bookFile));
        CSVWriter writer2 = new CSVWriter(new FileWriter(authorFile));
        CSVWriter writer3 = new CSVWriter(new FileWriter(bookAuthorFile));

        
        try{
            List<String[]> allRecords = reader.readAll();
            String[] header = allRecords.remove(0);

            Map<String, Integer> headerMap = new HashMap<>();
            
            for (int i = 0; i < header.length; i++) {
                headerMap.put(header[i], i);
            }
            
            //{Pages=6, Cover=4, ISBN10=0, Title=2, Author=3, ISBN13=1, Publisher=5}
            System.out.println(headerMap);
            
            for(String[] bookRows: allRecords){
                //Book table(unique)
               String isbn = bookRows[headerMap.get("ISBN10")];
               String title = bookRows[headerMap.get("Title")];
               uniqueBooks.add(Arrays.asList(isbn, title));
               
               //Author table(unique)
               String authorCell = bookRows[headerMap.get("Author")];
               /*if(authorName == null || authorName.trim().isEmpty()){
                   continue;
               }
               */
               String[] authors = authorCell.split(",");
               for(String authorName: authors){
                   authorName = authorName.trim();
                   
                   if(!authorsMap.containsKey(authorName)){
                       authorsMap.put(authorName, nextAuthorId++);
                   }
                   
                   Integer authorId = authorsMap.get(authorName);
                   //Create the link
                   //[4338, 155874424X], [596, 0451197275], [1989, 083175006]
                   bookAuthors.add(Arrays.asList(authorId, isbn));
                   //System.out.println(bookAuthors);
               }
                             
               
            }
            //--Write the data (Book table)---
            writer.writeNext(new String[]{"Isbn", "Title"});
            uniqueBooks.forEach(book -> writer.writeNext(new String[]{book.get(0), book.get(1)}));
            //System.out.println("Successfully wrote " + uniqueBooks.size() + " records to " + bookFile);
            
            //--Write the data (Authors table)
            writer2.writeNext(new String[]{"Author_id", "Name"});
            authorsMap.forEach((name,id) -> writer2.writeNext(new String[]{String.valueOf(id), name}));
            writer2.flush();
            //System.out.println("Successfully wrote " + authorsMap.size() + " records to " + authorFile);
            
            //--Write the data(Book_Authors table)
            writer3.writeNext(new String[]{"Author_id", "Isbn"});
            bookAuthors.forEach(bookAuthorLinks -> writer3.writeNext(new String[]{String.valueOf(bookAuthorLinks.get(0)), String.valueOf(bookAuthorLinks.get(1))}));
                
        }catch(IOException e){
            e.printStackTrace();
        }
    }
//    private static String caseStandardize(String input){
//        if(input == null || input.isEmpty()){
//            return "";
//        }
//        
//        StringBuilder titleCase = new StringBuilder();
//        boolean nextTitleCase = true;
//        
//        for(char c : input.toLowerCase().toCharArray()){
//            if(Character.isSpaceChar(c)){
//                nextTitleCase = true;
//            }
//            else if(nextTitleCase == false){
//                c = Character.toTitleCase(c);
//                nextTitleCase = false;
//            }
//            titleCase.append(c);
//        }
//        return titleCase.toString();
//    }
    

    
}

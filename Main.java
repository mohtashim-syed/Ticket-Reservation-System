/* Mohtashim Syed */

import java.util.*;
import java.io.*;
import java.lang.Math;

public class Main
{
    	public static void main(String[] args) throws IOException{
		Scanner scnr = new Scanner(System.in); //regular scanenr
		
        System.out.println("Please enter filename: ");
		String filename = scnr.next();
		
		Scanner sc = new Scanner(new File(filename));   //scanner for file
	    int row = 0;
	    int col = 0;

	    int aT = 0; //adult ticket count
	    int cT = 0; //child ticket count
	    int sT = 0; //senior ticket count
	    
	    String outfile = "A1.txt";
	   
	    //reading how many columns and rows are in the file in order to get the right parameters for creating array.
	    while(sc.hasNextLine()){
	        String temp = sc.nextLine();
	        col = temp.length();
	        row++;
	    }
	    
	    if(row > 10 || col > 26){
	        //values must be within these parameters
	        return;
	    }
	    
		char[][] auditorium = createAuditorium(filename, row, col);

		
		boolean exit = false;
	
	do {
	    int total = 0;
	    System.out.println("\nMain Menu:");
        System.out.println("1. Reserve Seats");
        System.out.println("2. Exit");
        System.out.print("Select an option: ");
            
            int option = scnr.nextInt();
            
            switch (option) {
                case 1:
                    displayAuditorium(auditorium, row, col);
                    
                    System.out.println("Which row would you like to reserve the seat?");
                    int firstRow = scnr.nextInt();
                    
                    if(firstRow > row){                         //input Validation
                        System.out.println("row out of bounds");
                        firstRow = scnr.nextInt();
                    }
                    System.out.println("Which seat would you like to reserve in the row?");
                    char firstSeat = scnr.next().charAt(0);
                    int n1Seat = firstSeat-'A';
                    
                    if(n1Seat > col){                           //input Validation
                       System.out.println("column out of counds");
                        firstSeat = scnr.next().charAt(0);
                        n1Seat = firstSeat-'A';
                    }
                    
                    System.out.println("How many adults?");
                        aT = scnr.nextInt();
                       if(aT < 0){ //input validation
                       System.out.println("no seats available");
                       }
                       total += aT;
                       
                    System.out.println("How many children?");
                        cT = scnr.nextInt();
                       if(cT < 0){ //input validation
                       System.out.println("no seats available");
                       }
                       total += cT;
                       
                    System.out.println("How many seniors?");
                       sT = scnr.nextInt();
                       if(sT < 0){ //input validation
                       System.out.println("no seats available");
                       }
                       total += sT;
                    
                    boolean available = checkAvailability(auditorium, row, col, firstRow, n1Seat, total);
                    
                    if(available){
                        reserveSeats(auditorium, row, col, firstRow, n1Seat, aT, cT, sT, total); 
                        printFile(auditorium, outfile);
                    } 
                    else if(!available){ //finding the best available
                        int bestSeat = bestAvailable(auditorium, row, total, firstRow);
                        char bestCSeat = (char)('A' + bestSeat); //turns best seat (int) into a char.
                        
                            
                            if(bestSeat == -1){ //if no best seat available
                                System.out.println("no seats available");
                                 
                                displayReport(auditorium, row, col);
                                printFile(auditorium, outfile);
                                
                                break;
                             }
                            else if(bestSeat != -1){ //prints out best available w the option to select to reserve
                                System.out.println("Best available: " + firstRow+ bestCSeat + " - " + firstRow + (char)(bestCSeat + total - 1));
                                System.out.println("Reserve? Y/N");
                                char input = scnr.next().charAt(0);
                                
                                if(input == 'Y'){
                                reserveSeats(auditorium, row, col, firstRow, bestSeat, aT, cT, sT, total);
                                printFile(auditorium, outfile);
                                }
                             }
                             else{
                                 System.out.println("no seats available");
                                 displayReport(auditorium, row, col);
                                 printFile(auditorium, outfile);
                                 break;
                             }
                             
                    }
                    
                    displayReport(auditorium, row, col);
                    break;
                
                case 2:
                    exit = true; //"EXIT"
                    displayReport(auditorium, row, col);
                    printFile(auditorium, outfile);
                    break;
                
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
	} while (!exit);
	}
	
	public static char[][] createAuditorium(String filename, int row, int col) throws IOException{
	    //using the parameters from above (row,col), this function creates a 2D array
	    //by going through each line in the file and assembling an array using the characters in that string.
	    //returns auditorium at the end of the file.
	    
	    	char[][] auditorium = new char[row][col];
	    	Scanner nr = new Scanner(new File(filename)); //reads file

	        for(int i = 0; i < row; i++){
	            String row1 = nr.nextLine();
	            for(int j = 0; j < col; j++){
	            auditorium[i][j] = row1.charAt(j);
	            }
	        }
	    
	    return auditorium;
	}
	
    public static void displayAuditorium(char[][] auditorium, int row, int col) {
       //writes each letter according to the number of columns in the array.
       //at the beginning of each row, it displays the row number.
       // prints out all the array values in terms of # or . depending on the availability.
       
        System.out.print("\n  ");
        for (char letter = 'A'; letter <= col+'A'-1; letter++) {
            System.out.print(letter);
        }
        System.out.println();
        for (int i = 0; i < row; i++){
            System.out.print(i+1+ " ");
            for (int j = 0; j < col; j++){
                if (auditorium[i][j] != '.') {
                    System.out.print("#");
                }else{
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
    
    public static void displayReport(char[][] auditorium, int row, int col){
       //this function recounts the array in case there has been updates to the seats.
       //basic calculations are made.
       //calculations are printed.
        int aC = 0;
        int cC = 0;
        int sC = 0;
        double totalSales = 0;
        int totalSeats = 0;
        int totalSeatsSold = 0;
        
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if(auditorium[i][j] == 'A'){
	                aC++;
	            }else if(auditorium[i][j] == 'C'){
	                cC++;
	            } else if(auditorium[i][j] == 'S'){
	                sC++;
	            }
            }
        }
        totalSeats = row*col;
        totalSeatsSold = aC+cC+sC;
        totalSales = ((aC*10) + (cC*5) + ((double)sC*7.5));
        
        System.out.println("Total Seats: " + totalSeats);
        System.out.println("Total Tickets: " + totalSeatsSold);
        System.out.println("Adult Tickets: " + aC);
        System.out.println("Child Tickets: " + cC);
        System.out.println("Senior Tickets: " + sC);
        System.out.printf("Total Sales: $%.2f", totalSales);
    }
    
    public static boolean checkAvailability(char[][] auditorium, int row, int col, int firstRow, int n1Seat, int total) {
    // Check if the selected starting seat is valid
    if (firstRow <= 0 || firstRow > row || n1Seat < 0 || n1Seat >= col || n1Seat+total > col) {
        return false; // Invalid starting seat
    }

    // Check if the selected seats are available
        for (int c = n1Seat; c < n1Seat + total; c++) {
            if ( auditorium[firstRow-1][c] != '.') {
                return false; // Seats not available or out of bounds
            }
        }
    

    return true; // All seats are available
}
    
    public static void reserveSeats(char[][] auditorium, int row, int col, int firstRow, int n1Seat, int aT, int cT, int sT, int total) {
       //if the availability is good, seats will be reserved. otherwise it will display invalid
        if (checkAvailability(auditorium, row, col, firstRow, n1Seat, total)) {
            for (int c = n1Seat; c < n1Seat + total; c++) {
                if (aT > 0) {
                    auditorium[firstRow - 1][c] = 'A'; // Reserve adult seat
                    aT--;
                } else if (cT > 0) {
                    auditorium[firstRow - 1][c] = 'C'; // Reserve child seat
                    cT--;
                } else if (sT > 0) {
                    auditorium[firstRow - 1][c] = 'S'; // Reserve senior seat
                    sT--;
                }
            }
            System.out.println("Seats reserved successfully.");
        } else {
            System.out.println("Seats are not available or invalid seat selection.");
        }
    }
    
    public static int bestAvailable(char[][] auditorium, int r, int total, int firstRow) {
       //gets the columns again. creates the enter point of the columns
       //offset gets the middle of the user's number of total seats.
       // then it goes through each element of the array and checks the availability based on the number of seats picked.
       //if the minimum dist needs to updated, it does so. and then bestseat is set to i.
       //returns the best seat in int form.
        int numSeats = auditorium[0].length;
        double center = 0;
        if(numSeats % 2 == 0){
            center = numSeats/2 - 1;
        } else {
            center = numSeats/2;
        }
    
        int bestSeat = -1;
        double minDistance = 100;
        
        int offset = 0;
        if(total % 2 == 0){
            offset = total/2 - 1;
        } else {
            offset = total/2;
        }
    
        for (int i = 0; i < numSeats - total + 1; i++) {
                
                if(checkAvailability(auditorium, r, numSeats, firstRow, i, total)){
                    double distance = Math.abs(center - (i+offset));
                    
                    if(distance <= minDistance){
                        minDistance = distance;
                        bestSeat = i;
                    
                    }
                }
            
        }
    
        return bestSeat;
    }

    public static void printFile(char[][] auditorium, String outfile) throws IOException{
       //using bufferedwriter to print into the output file.
       //goes through each element using nexted for loop and writes into the file.
        BufferedWriter outWrite = null;
        outWrite = new BufferedWriter(new FileWriter(outfile));
        int rowLength = auditorium.length;
        int colLength = auditorium[0].length;
        
            for(int i = 0; i < rowLength; i++){
                for(int j = 0; j < colLength; j++){
                    outWrite.write("" + auditorium[i][j]);
                }
                if(i != rowLength-1){
                outWrite.write("\n");
                }
            }
            
            outWrite.flush();
            outWrite.close();
    }
    
}

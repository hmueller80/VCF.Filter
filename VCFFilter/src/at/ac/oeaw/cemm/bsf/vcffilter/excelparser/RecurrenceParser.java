/*
*     This file is part of the VCF.Filter project (https://biomedical-sequencing.at/VCFFilter/).
*     VCF.Filter permits graphical filtering of VCF files on cutom annotations and standard VCF fields, pedigree analysis, and cohort searches.
* %%
*     Copyright © 2016, 2017  Heiko Müller (hmueller@cemm.oeaw.ac.at)
* %%
* 
*     VCF.Filter is free software: you can redistribute it and/or modify
*     it under the terms of the GNU General Public License as published by
*     the Free Software Foundation, either version 3 of the License, or
*     (at your option) any later version.
* 
*     This program is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*     GNU General Public License for more details.
* 
*     You should have received a copy of the GNU General Public License
*     along with VCF.Filter.  If not, see <http://www.gnu.org/licenses/>.
* 
*     VCF.Filter  Copyright © 2016, 2017  Heiko Müller (hmueller@cemm.oeaw.ac.at)
*     This program comes with ABSOLUTELY NO WARRANTY;
*     This is free software, and you are welcome to redistribute it
*     under certain conditions; 
*     For details interrogate the About section in the File menu.
*/
package at.ac.oeaw.cemm.bsf.vcffilter.excelparser;

import at.ac.oeaw.cemm.bsf.vcffilter.VCFFilter;
import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 
 * Parser for recurrence files. In its original version, it parsed xlsx formatted recurrence files.
 * But there where issues with number precision of variant positions. 
 * Therefore, parsing is done from a tsv file, which also means that the 
 * ExcelParser functionalities are not needed right now.
 * This may change in the future.
 * RecurrenceParser.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class RecurrenceParser extends ExcelParser{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The final result of the parsing process is stored here.
     */
    ArrayList<VariantRecurrence> recurrence;   
 
    /**
    * Creates new instance of RecurrenceParser.
    * 
    * @param filename input file
    * @author Heiko Müller
    * @since 1.0
    */
    public RecurrenceParser(String filename) {
        //super(filename);
        parse();
    }
    
    /**
    * Creates new instance of RecurrenceParser.
    * 
    * @param filename input file
    * @param tsv indicates whether input file is in tab separated values format
    * @author Heiko Müller
    * @since 1.0
    */
    public RecurrenceParser(String filename, boolean tsv) {
        super();
        parseRecurrenceFromTSV(filename);
    }
    
    /**
    * Creates new instance of RecurrenceParser.
    * 
    * @param filename input file
    * @param tsv indicates whether input file is in tab separated values format
    * @param gui VCFFilter instance
    * @author Heiko Müller
    * @since 1.0
    */
    public RecurrenceParser(String filename, boolean tsv, VCFFilter gui) {
        super();
        parseRecurrenceFromTSV(filename, gui);
    }

    /**
    * Implementation of super class method parse(). Redirects actual parsing to parseRecurrence();
    * Parses .xlsx formatted files.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    protected void parse() {        
        recurrence = parseRecurrence();           
    }
    
    /**
    * Parses recurrence file in .xlsx format.
    *
    * @return ArrayList<VariantRecurrence>
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<VariantRecurrence> parseRecurrence() {
        recurrence = new ArrayList<VariantRecurrence>();
        ArrayList<Integer> headers = findHeaderRowIndices(sheet1);        
        if(headers.size() == 1){
            int headerrowindex = headers.get(0);
            for(int i = headerrowindex + 1; i < sheet1.size(); i++){
                String row = sheet1.get(i);
                VariantRecurrence ss = new VariantRecurrence(row);
                recurrence.add(ss);
            }            
        }else{
            System.out.println("More than one header row found in worksheet. Make sure to have a unique header row starting with CHROM\tPOS");
        }
        return recurrence;
    }
    
    /**
    * Parses recurrence file in .tsv format.
    *
    * @param file the input recurrence file name
    * @return ArrayList<VariantRecurrence>
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<VariantRecurrence> parseRecurrenceFromTSV(String file) {
        recurrence = new ArrayList<VariantRecurrence>();
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while( (line = br.readLine()) != null){
                VariantRecurrence ss = new VariantRecurrence(line);
                recurrence.add(ss);
            }
        }catch(Exception e){
            System.out.println("There were problems parsing file " + file);
            e.printStackTrace();
        }           
        return recurrence;
    }
    
    /**
    * Parses recurrence file in .tsv format.
    *
    * @param file the input recurrence file name
    * @param gui VCFFilter instance
    * @return ArrayList<VariantRecurrence>
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<VariantRecurrence> parseRecurrenceFromTSV(String file, VCFFilter gui) {
        recurrence = new ArrayList<VariantRecurrence>();
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while( (line = br.readLine()) != null){
                VariantRecurrence ss = new VariantRecurrence(line, gui);
                recurrence.add(ss);
            }
        }catch(Exception e){
            System.out.println("There were problems parsing file " + file);
            e.printStackTrace();
            new Warning(gui, "There were problems parsing recurrence file. Exiting");
            System.exit(0);
        }           
        return recurrence;
    }
    
    /**
    * Finds the index of the column header row.
    *
    * @param columheaders 
    * @param key
    * @return int the column header row index, -1 if no header row is found.
    * @author Heiko Müller
    * @since 1.0
    
    private int findIndexForColumnHeader(String[] columheaders, String key){
        for(int i = 0; i < columheaders.length; i++){
            if(columheaders[i].startsWith(key)){
                return i;
            }
        }
        return -1;
    }
    * */
    
    /**
    * Finds the index of the column header row based on presence of either key or key2.
    *
    * @param columheaders 
    * @param key startsWith key
    * @param key2 equals key
    * @return int the column header row index, -1 if no header row is found.
    * @author Heiko Müller
    * @since 1.0
    
    private int findIndexForColumnHeader(String[] columheaders, String key, String key2){
        for(int i = 0; i < columheaders.length; i++){
            if(columheaders[i].startsWith(key) || columheaders[i].equals(key2)){
                return i;
            }
        }
        return -1;
    }
    * */
    
    /**
    * Finds the index of the column header row based on presence of either key or key2 or key3.
    *
    * @param columheaders 
    * @param key startsWith key
    * @param key2 equals key
    * @param key3 equals key
    * @return int the column header row index, -1 if no header row is found.
    * @author Heiko Müller
    * @since 1.0
    
    private int findIndexForColumnHeader(String[] columheaders, String key, String key2, String key3){
        for(int i = 0; i < columheaders.length; i++){
            if(columheaders[i].startsWith(key) || columheaders[i].equals(key2) || columheaders[i].equals(key3)){
                return i;
            }
        }
        return -1;
    }
    * */
    
    /**
    * Finds the indices of header rows starting with CHROM\tPOS
    *
    * @param rows 
    * @return ArrayList<Integer> list of indices, usually exactly one value
    * @author Heiko Müller
    * @since 1.0
    */
    private ArrayList<Integer> findHeaderRowIndices(ArrayList<String> rows){
        ArrayList<Integer> headers = new ArrayList<Integer>();
        for(int i = 0; i < rows.size(); i++){
            if(rows.get(i).startsWith("CHROM\tPOS")){
                headers.add(i);
            }
        }
        return headers;
    }
    
    /**
    * Tests for presence of header row that starts with header
    *
    * @param header the value header row starts with
    * @return boolean true if header row is found, false otherwise
    * @author Heiko Müller
    * @since 1.0
    
    private boolean headerPresent(String header) {
        for (String row : rows) {
            if (row.startsWith(header)) {//use startsWith to handle trailung null values (cells that appear empty but have had some value in it)
                return true;
            }
        }
        return false;
    }
    * */

    /**
    * Finds the index of the row starting with s
    *
    * @param s the value row starts with
    * @return int the index of the first matching row. -1 if no row matches.
    * @author Heiko Müller
    * @since 1.0
    
    private int findRowIndex(String s) {
        for(int i = 0; i < rows.size(); i++){
            if(rows.get(i).startsWith(s)){                
                return i;
            }
        }
        return -1;
    }
    * */

    /**
    * Getter for recurrence
    *
    * @return ArrayList&#60;VariantRecurrence&#62; the parsing end result
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<VariantRecurrence> getVariantRecurrence(){
        return recurrence;
    }
    
    protected void readFile(){}

}

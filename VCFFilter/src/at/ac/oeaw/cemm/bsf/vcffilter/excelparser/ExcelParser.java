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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
//import org.apache.poi.hssf.extractor.ExcelExtractor;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Abstract class for reading .xls and .xlsx files.
 * ExcelParser.java 04 OCT 2016
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public abstract class ExcelParser extends Parser{
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /**
     * The rows of text found in this Excel sheet
     */
    protected ArrayList<String> rows;
    
    /**
     * Holds the data of Sheet1
     */
    protected ArrayList<String> sheet1;
    
    /**
    * Creates new ExcelParser.
    * 
    * @param filename input file
    * @author Heiko Müller
    * @since 1.0
    */
    public ExcelParser(String filename){
        super(filename);
        //parseRows();
        //initSheet1();
    }
    
    /**
     * Initializes ExcelParser
     */
    public ExcelParser(){
        super();    
        
    }
    
    /**
    * Redirects reading input files depending on the extension .xlsx or .xls
    *
    * @author Heiko Müller
    * @since 1.0
    */
    /*
    protected void readFile(){
        if(fileName.endsWith(".xlsx")){
            readXLSXFile();
        }else if(fileName.endsWith(".xls")){
            readXLSFile();
        }
    }
    */
    
    /**
    * Reads input file in .xlsx format
    *
    * @author Heiko Müller
    * @since 1.0
    */
    /*
    private void readXLSXFile(){
        rawFileContent = getText(fileName);            
    }
    */
    
    /**
    * Reads input file in .xls format
    *
    * @author Heiko Müller
    * @since 1.0
    */
    /*
    private void readXLSFile(){
       try{
            FileInputStream fis = new FileInputStream(fileName);            
            POIFSFileSystem fileSystem = new POIFSFileSystem(fis);        
            ExcelExtractor excelExtractor = new ExcelExtractor(fileSystem);
            excelExtractor.setIncludeBlankCells(true);            
            rawFileContent = excelExtractor.getText();                  
        }
       catch(IOException ioe){
           Logger.getLogger(this.getClass().getName()).severe("FAILED IOException caught");
           ioe.printStackTrace();
       }          
    }
    */
    
    /**
    * Reads input files depending on the extension .xlsx or .xls
    *
    * @param filename the input file name
    * @return String the file content as text
    * @author Heiko Müller
    * @since 1.0
    */
    /*
    private static String getText(String filename) {
        try {
            String s = "";
            if (filename.endsWith(".xls")) {
                FileInputStream fis = new FileInputStream(filename);
                POIFSFileSystem fileSystem = new POIFSFileSystem(fis);
                ExcelExtractor excelExtractor = new ExcelExtractor(fileSystem);
                excelExtractor.setIncludeBlankCells(true);
                s = excelExtractor.getText();
                return s;
            } else if (filename.endsWith(".xlsx")) {
                XSSFWorkbook wb = new XSSFWorkbook(filename);
                int sheets = wb.getNumberOfSheets();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < sheets; i++) {
                    String name = wb.getSheetName(i);
                    XSSFSheet sheet = wb.getSheet(name);
                    sb.append(name + "\n");
                    for (Row row : sheet) {
                        if(!isRowEmpty(row)){
                            for (int j = 0; j < row.getLastCellNum(); j++) {
                                Cell c = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
                                String content = "";
                                try{
                                    content = c.getStringCellValue();
                                }catch(IllegalStateException is){
                                    if(c.getCellType() == 0){
                                        Double d = c.getNumericCellValue();
                                        
                                        content = "" + d.floatValue();
                                    }else{
                                        content = c.toString();
                                    }
                                }                              
                                if(j == 0 && content.equals("")){
                                    content = content + "1";
                                }
                                content = removeWhiteSpace(content);
                                sb.append(content.trim() + "\t");
                            }
                            sb.append("\n");
                        }
                    }
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    */
    
    /**
    * Removes all line feed characters form a String
    *
    * @param s the input String
    * @return String without \n characters
    * @author Heiko Müller
    * @since 1.0
    */
    private static String removeWhiteSpace(String s){
        char[] ca = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < ca.length; i++){
            if((int)ca[i] != 10){
                sb.append(Character.toString(ca[i]));
            }
        }
        return sb.toString();
    }
    
    /**
    * Tests if a row is empty. Returns true if row is empty. False otherwise.
    *
    * @param r the input row
    * @return boolean
    * @author Heiko Müller
    * @since 1.0
    */
    /*
    private static boolean isRowEmpty(Row r) {
        for (int j = 0; j < r.getLastCellNum(); j++) {
            Cell c = r.getCell(j, Row.CREATE_NULL_AS_BLANK);
            String s = c.toString();
            if(s.trim().length() > 0){
                return false;
            }
        }
        return true;
    }
    */
    
    /**
    * Parses text into rows based on the new line character at the end of each row.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void parseRows(){
        if(rawFileContent != null){
            String[] r = rawFileContent.split("\n");
            rows = new ArrayList<String>();
            for(int i = 0; i < r.length; i++){
                if(!isAllNullRow(r[i])){
                    rows.add(r[i].trim());
                }
            }
        }
    }

    /**
    * Tests if a row is empty or contains only null values.
    *
    * @return boolean true if row is empty, false otherwise
    * @author Heiko Müller
    * @since 1.0
    */
    private boolean isAllNullRow(String row){
        boolean result = true;
        String[] r = row.split("\t");
        for(int i = 0; i < r.length; i++){
            if(!(r[i].equals("null") || r[i].equals(""))){
                result = false;
            }
        }
        return result;
    }
    
    /**
    * Initializes sheet1 ArrayList<String>.   
    * Reads through the file content until it finds Sheet 1 related text and
    * puts the row into the sheet1 ArrayList<String>.
    *
    * @author Heiko Müller
    * @since 1.0
    */
    private void initSheet1(){
        sheet1 = new ArrayList<String>();
        boolean start = false;
        for(String s : rows){
            if(s.equals("Sheet1")){
                start = true;
            }            
            
            if(start){
                if(!s.startsWith("null")){
                    sheet1.add(s);
                }
            }
            
            if(s.equals("Sheet2")){
                start = false;
            } 
        }
    }

    /**
    * Returns sheet1 ArrayList&#60;String&#62;.  
    *
    * @return ArrayList&#60;String&#62;
    * @author Heiko Müller
    * @since 1.0
    */
    public ArrayList<String> getSheet1() {
        return sheet1;
    }
}

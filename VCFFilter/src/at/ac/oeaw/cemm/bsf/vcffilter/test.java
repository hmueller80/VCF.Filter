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
package at.ac.oeaw.cemm.bsf.vcffilter;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.File;
import java.util.Iterator;

/**
 * Test class for writing VCF files.
 * test.java 04 OCT 2016 
 *
 * @author Heiko Müller
 * @version 1.0
 * @since 1.0
 */
public class test {
    
    /**
     * 
     * @param inputtextvcf sample VCF file to read header
     * @return VariantContextWriter to write variants to VCF file
     */    
    public static VariantContextWriter getVariantContextWriter(File inputtextvcf){
        //VCFFileReader reader = new VCFFileReader(inputtextvcf, false);
        
        SAMSequenceDictionary refDict = new SAMSequenceDictionary();        
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
            .setReferenceDictionary(refDict)
            .setOption(Options.INDEX_ON_THE_FLY)       
            .setBuffer(8192);
        
        VariantContextWriter sample_writer = builder
            .setOutputFile(inputtextvcf.getAbsolutePath() + "_indexed.vcf")             
            .build();   
        builder.setOutputFileType(VariantContextWriterBuilder.OutputType.VCF);
        sample_writer = builder.setOutputFile(inputtextvcf.getAbsolutePath() + "_indexed.vcf").build();
        return sample_writer;
    }
    
    /**
     * 
     * @param a input parameters
     */
    public static void main(String[] a){
        VCFFileReader reader = new VCFFileReader(new File("C:\\Temp\\Case1.vcf"), false);
        System.out.println(reader.getFileHeader().toString());
        
        SAMSequenceDictionary refDict = new SAMSequenceDictionary();        
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
       .setReferenceDictionary(refDict)
       .setOption(Options.INDEX_ON_THE_FLY)       
       .setBuffer(8192);
        
        
 
   VariantContextWriter sample1_writer = builder
       .setOutputFile("sample1.vcf")             
       .build();   
       builder.setOutputFileType(VariantContextWriterBuilder.OutputType.VCF);
       sample1_writer = builder
       .setOutputFile("sample1.vcf")             
       .build();
   
   sample1_writer.writeHeader(reader.getFileHeader());
   Iterator<VariantContext> it = reader.iterator();
   while(it.hasNext()){
       sample1_writer.add(it.next());
   }
   
   sample1_writer.close();
   
   
   
   
       
        
    }
    
    
   
    
    
}

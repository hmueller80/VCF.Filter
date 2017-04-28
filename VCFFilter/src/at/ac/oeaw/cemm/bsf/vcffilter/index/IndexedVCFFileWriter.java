/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.oeaw.cemm.bsf.vcffilter.index;

import static at.ac.oeaw.cemm.bsf.vcffilter.index.test.getVariantContextWriterVCF;
import static at.ac.oeaw.cemm.bsf.vcffilter.index.test.getVariantContextWriterVCFGZ;
import static at.ac.oeaw.cemm.bsf.vcffilter.index.test.indexVCFGZ;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import java.io.File;
import java.util.Iterator;

/**
 *
 * @author hmueller
 */
public class IndexedVCFFileWriter {
    
    /**
     * 
     * @param inputVCFFile input VCF file
     * @param overwriteInputFile true or false, currently no overwriting is implemented
     * @return boolean true if index file is present
     */
    public static boolean index(File inputVCFFile, boolean overwriteInputFile){
        
        if(inputVCFFile.getName().endsWith("vcf")){
            indexVCF(inputVCFFile, overwriteInputFile);
            return true;
        }else if(inputVCFFile.getName().endsWith("vcf.gz")){
            indexVCFGZ(inputVCFFile, overwriteInputFile);
            return true;
        }else{
            //System.out.println("Input file must be VCF text (.vcf) or compressed VCF text (.vcf.gz)"); 
            return false;
        }        
    }
    
    /**
     * 
     * @param inputTextVCF sample VCF file for reading header information
     * @param overwriteInputFile true or false, currently, no overwriting is implemented
     * @return VariantContextWriter to write variants to VCF file
     */
    private static VariantContextWriter getVariantContextWriterVCF(File inputTextVCF, boolean overwriteInputFile) {
        VCFFileReader reader = new VCFFileReader(inputTextVCF, false);
        VCFHeader header = reader.getFileHeader();
        SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary(); 
        if(sequenceDictionary == null){
            sequenceDictionary = new SAMSequenceDictionary();
        }

        //SAMSequenceDictionary refDict = new SAMSequenceDictionary();
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setReferenceDictionary(sequenceDictionary)
                .setOption(Options.INDEX_ON_THE_FLY)
                .setBuffer(8192);

        VariantContextWriter sample_writer = builder
                .setOutputFile(inputTextVCF.getAbsolutePath() + "_indexed.vcf")
                .build();
        builder.setOutputFileType(VariantContextWriterBuilder.OutputType.VCF);
        sample_writer = builder.setOutputFile(inputTextVCF.getAbsolutePath() + "_indexed.vcf").build();
        reader.close();
        return sample_writer;
    }
    
    /**
     * 
     * @param inputCompressedTextVCF input VCF file for reading header lines
     * @param overwriteInputFile true or false, currently, no overwriting is implemented
     * @return VariantContextWriter to write variants to VCF file
     */
    private static VariantContextWriter getVariantContextWriterVCFGZ(File inputCompressedTextVCF, boolean overwriteInputFile) {
        VCFFileReader reader = new VCFFileReader(inputCompressedTextVCF, false);
        VCFHeader header = reader.getFileHeader();
        //SAMSequenceDictionary refDict = new SAMSequenceDictionary();
        SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary(); 
        if(sequenceDictionary == null){
            sequenceDictionary = new SAMSequenceDictionary();
        }
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setReferenceDictionary(sequenceDictionary)
                .setOption(Options.INDEX_ON_THE_FLY)
                .setBuffer(8192);

        VariantContextWriter sample_writer = builder
                .setOutputFile(inputCompressedTextVCF.getAbsolutePath() + "_indexed.vcf.gz")
                .build();
        builder.setOutputFileType(VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF);        
        sample_writer = builder.setOutputFile(inputCompressedTextVCF.getAbsolutePath() + "_indexed.vcf.gz").build();
        reader.close();
        //final VariantContextWriter out = variantContextWriterBuilder.setOutputFile(outputVCF).build(); 
        //variantContextWriterBuilder.setOutputFileType(VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF);
        
        return sample_writer;
    }
    
    /**
     * 
     * @param f File to be indexed
     * @param overwriteInputFile true or false, currently, no overwriting is implemented
     */
    private static void indexVCF(File f, boolean overwriteInputFile) {
        if (f.getName().endsWith(".vcf")) {
            VariantContextWriter writer = null;
            try {
                writer = getVariantContextWriterVCF(f, overwriteInputFile);
                VCFFileReader reader = new VCFFileReader(f, false);
                writer.writeHeader(reader.getFileHeader());                
                Iterator<VariantContext> it = reader.iterator();
                while (it.hasNext()) {
                    writer.add(it.next());
                }                
                writer.close();
                reader.close();
            } catch (Exception e) {
                if(writer != null){
                    writer.close();
                }
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        if(overwriteInputFile){
            File x = new File(f.getAbsolutePath() + "_indexed.vcf.gz");
            if(f.exists()){                
                if(f.canWrite()){
                    //System.out.println("deleting");                    
                    //System.out.println("deleted " + f.delete());  
                    //System.out.println("deleted " + f.renameTo(new File(f.getAbsolutePath() + "_bak.vcf")));  
                    //x.renameTo(f);
                }else{
                    //System.out.println("input file not writable");
                }
            }
        }
    }
    
    /**
     * 
     * @param f File to be indexed
     * @param overwriteInputFile true or false, currently, no overwriting is implemented
     */
    private static void indexVCFGZ(File f, boolean overwriteInputFile) {        

        if (f.getName().endsWith(".vcf.gz")) {
            VariantContextWriter writer = null;
            try {
                writer = getVariantContextWriterVCFGZ(f, overwriteInputFile);
                VCFFileReader reader = new VCFFileReader(f, false);
                writer.writeHeader(reader.getFileHeader());
                Iterator<VariantContext> it = reader.iterator();
                while (it.hasNext()) {
                    writer.add(it.next());
                }
                writer.close();
                reader.close();
            } catch (Exception e) {                
                writer.close();
                System.out.println(e.getMessage());
            }
        }
        if(overwriteInputFile){            
            File x = new File(f.getAbsolutePath() + "_indexed.vcf.gz");
            if(f.exists()){                
                if(f.canWrite()){
                    //System.out.println("deleting");
                    //System.out.println("deleted " + f.delete());
                    
                    //x.renameTo(f);
                }else{
                    //System.out.println("input file not writable");
                }
            }
        }
    }
    
    /**
     * 
     * @param a input parameters
     */
    public static void main(String[] a) {
        //IndexedVCFFileWriter.index(new File("C:\\Temp\\index\\1148.vcf.gz"), false);
        //IndexedVCFFileWriter.index(new File("C:\\Temp\\index\\testvcf.vcf"), false);
    }
    
}

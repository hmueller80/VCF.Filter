/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.oeaw.cemm.bsf.vcffilter.index;

import at.ac.oeaw.cemm.bsf.vcffilter.Warning;
import at.ac.oeaw.cemm.bsf.vcffilter.filter.Filter;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFileChooser;
import picard.PicardException;

/**
 *
 * @author hmueller
 */
public class test {

    /**
     * 
     * @param inputtextvcf
     * @return VariantContextWriter
     */
    public static VariantContextWriter getVariantContextWriterVCF(File inputtextvcf) {
        VCFFileReader reader = new VCFFileReader(inputtextvcf, false);
        VCFHeader header = reader.getFileHeader();
        SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary(); 

        //SAMSequenceDictionary refDict = new SAMSequenceDictionary();
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setReferenceDictionary(sequenceDictionary)
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
     * @param inputtextvcf
     * @return VariantContextWriter
     */
    public static VariantContextWriter getVariantContextWriterVCFGZ(File inputtextvcf) {
        VCFFileReader reader = new VCFFileReader(inputtextvcf, false);
        VCFHeader header = reader.getFileHeader();
        //SAMSequenceDictionary refDict = new SAMSequenceDictionary();
        SAMSequenceDictionary sequenceDictionary = header.getSequenceDictionary(); 
        VariantContextWriterBuilder builder = new VariantContextWriterBuilder()
                .setReferenceDictionary(sequenceDictionary)
                .setOption(Options.INDEX_ON_THE_FLY)
                .setBuffer(8192);

        VariantContextWriter sample_writer = builder
                .setOutputFile(inputtextvcf.getAbsolutePath() + "_indexed.vcf.gz")
                .build();
        builder.setOutputFileType(VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF);
        sample_writer = builder.setOutputFile(inputtextvcf.getAbsolutePath() + "_indexed.vcf.gz").build();
        
        //final VariantContextWriter out = variantContextWriterBuilder.setOutputFile(outputVCF).build(); 
        //variantContextWriterBuilder.setOutputFileType(VariantContextWriterBuilder.OutputType.BLOCK_COMPRESSED_VCF);
      
        return sample_writer;
    }

    /**
     * index VCF file
     */
    public static void indexVCF() {
        File f = new File("C:\\Temp\\index\\testvcf.vcf");

        if (f.getName().endsWith(".vcf")) {
            VariantContextWriter writer = null;
            try {
                writer = getVariantContextWriterVCF(f);
                VCFFileReader reader = new VCFFileReader(f, false);
                writer.writeHeader(reader.getFileHeader());
                Iterator<VariantContext> it = reader.iterator();
                while (it.hasNext()) {
                    writer.add(it.next());
                }
                writer.close();
            } catch (Exception e) {
                writer.close();
                System.out.println(e.getMessage());
            }
        }
    }
    
    /**
     * index VCF file
     */
    public static void indexVCFGZ() {
        File f = new File("C:\\Temp\\index\\1148.vcf.gz");

        if (f.getName().endsWith(".vcf.gz")) {
            VariantContextWriter writer = null;
            try {
                writer = getVariantContextWriterVCFGZ(f);
                VCFFileReader reader = new VCFFileReader(f, false);
                writer.writeHeader(reader.getFileHeader());
                Iterator<VariantContext> it = reader.iterator();
                while (it.hasNext()) {
                    writer.add(it.next());
                }
                writer.close();
            } catch (Exception e) {
                writer.close();
                System.out.println(e.getMessage());
            }
        }
    }    

    /**
     * 
     * @param a input parameters
     */
    public static void main(String[] a) {
        indexVCFGZ();
    }

}

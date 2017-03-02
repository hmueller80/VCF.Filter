/******************************************************************************
 *  Compilation:  javac Hilbert.java
 *  Execution:    java Hilbert n
 *  Dependencies: StdDraw.java
 *  
 *  Plot an order n Hilbert curve using two mutually recursive functions.
 *
 *  % java Hilbert 5
 *
 *
 ******************************************************************************/

package at.ac.oeaw.cemm.bsf.vcffilter.vcftoimage;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;

/** 
 * Hilbert.java
  * @author Robert Sedgewick
  * @author Kevin Wayne
  * @author Heiko Müller (modified the original Hilbert.java from Robert Sedgewick and Kevin Wayne)
  */
public class Hilbert {
    
    /**
     * The version number of this class.
     */
    static final long serialVersionUID = 1L;
    
    /** Turtle */
    private Turtle turtle;
    
    /** Genome, should be read from the sample VCF... Current implementation works only for hg19 */
    static int[] genome = {249250621,243199373,198022430,191154276,180915260,171115067,159138663,146364022,141213431,135534747,135006516,133851895,115169878,107349540,102531392,90354753,81195210,78077248,59128983,63025520,48129895,51304566,155270560,59373566};
    static int X = 23;
    static int Y = 24;
    
    /**
    * Creates new Hilbert.
    * 
    * @param n order of the curve
    * @since 1.0
    */
    public Hilbert(int n) {
        turtle = new Turtle(0.5, 0.5, 0.0);
        double max = Math.pow(2, n);
        turtle.setXscale(0, max);
        turtle.setYscale(0, max);
        hilbert(n);
    }
    
    /**
    * Creates new Hilbert.
    * 
    * @param n order of the curve
    * @param variants list of variants to be plotted
    * @author Heiko Müller
    * @since 1.0
    */
    public Hilbert(int n, ArrayList<VariantContext> variants) {
        turtle = new Turtle(0.5, 0.5, 0.0);
        double max = Math.pow(2, n);
        int length = (int)max;
        boolean[] interval = new boolean[length * length];
        boolean[] c1 = new boolean[length * length];
        boolean[] c2 = new boolean[length * length];
        for(int i = 0; i < interval.length; i++){
            interval[i] = false;
            c1[i] = false;
            c2[i] = false;
        }
        long bin = 3000000000L/interval.length;
        for(VariantContext v : variants){
            long i = getPixelIndex(v, bin);
            String alt = v.getAlternateAllele(0).getBaseString();
            if(i >= 0 && i < interval.length){
                interval[(int)i] = true;
                if(alt.startsWith("A")){
                    c1[(int)i] = true;
                    c2[(int)i] = true;
                }else if(alt.startsWith("C")){
                    c1[(int)i] = true;
                    c2[(int)i] = false;
                }else if(alt.startsWith("G")){
                    c1[(int)i] = false;
                    c2[(int)i] = true;
                }else if(alt.startsWith("T")){
                    c1[(int)i] = false;
                    c2[(int)i] = false;
                }
            }else{
                //System.out.println("missed variant");
            }
        }
        turtle.setInterval(interval);
        turtle.setColor1(c1);
        turtle.setColor2(c2);
        turtle.setXscale(0, max);
        turtle.setYscale(0, max);
        hilbert(n);
    }


    /**
    * Draws Hilbert curve.
    * 
    * @param n order of the curve
    * @since 1.0
    */
    private void hilbert(int n) {
        if (n == 0) return;
        turtle.turnLeft(90);
        treblih(n-1);
        turtle.goForward(1.0);
        turtle.turnLeft(-90);
        hilbert(n-1);
        turtle.goForward(1.0);
        hilbert(n-1);
        turtle.turnLeft(-90);
        turtle.goForward(1.0);
        treblih(n-1);
        turtle.turnLeft(90);
    }

    /**
    * Draws Hilbert curve.
    * 
    * @param n order of the curve
    * @since 1.0
    */
    public void treblih(int n) {
        if (n == 0) return;
        turtle.turnLeft(-90);
        hilbert(n-1);
        turtle.goForward(1.0);
        turtle.turnLeft(90);
        treblih(n-1);
        turtle.goForward(1.0);
        treblih(n-1);
        turtle.turnLeft(90);
        turtle.goForward(1.0);
        hilbert(n-1);
        turtle.turnLeft(-90);
    }
    
    /**
    * Shows the drawn curve.
    * 
    * @author Heiko Müller
    * @since 1.0
    */
    public void show(){
        StdDraw.setVisible(true);
        StdDraw.show();
    }
    
    /**
    * Returns the position index of this variant along the curve depending on Hilbert curve order.
    * 
    * @param variant variant
    * @param binsize depends on Hilbert curve order
    * @return long
    * @author Heiko Müller
    * @since 1.0
    */
    public static long getPixelIndex(VariantContext variant, long binsize){
        long position = getGenomePosition(variant);        
        return (long)position/binsize;      
    }
    
    /**
    * Returns the position of this variant along the curve depending on Hilbert curve order.
    * 
    * @param variant variant
    * @return long variant position on Hilbert curve
    * @author Heiko Müller
    * @since 1.0
    */
    public static long getGenomePosition(VariantContext variant){
        long result = 0;
        String contig = variant.getContig();
        int c = 0;
        try{
            c = Integer.parseInt(contig);
        }catch(NumberFormatException ioe){
            if(contig.toUpperCase().equals("X")){
                c = X;
            }else if(contig.toUpperCase().equals("Y")){
                c = Y;
            }else{
                c = 0;
            }
        }
        if(c > 0){
            for(int i = 0; i < genome.length && i < c - 1; i++){
                result = result + genome[i];
            }
        }
        result = result + variant.getStart();
        //System.out.println(result);
        return result;
    }
    
    /**
    * Sets the contig sizes for a given genome assembly.
    * 
    * @param contigSizes chromosome sizes
    * @author Heiko Müller
    * @since 1.0
    */
    public static void setContigSizes(int[] contigSizes){
        genome = contigSizes;
    }
    
    /**
    * Sets the X-chromosome contig index.
    * 
    * @param XcontigIndex chrX contig index
    * @author Heiko Müller
    * @since 1.0
    */
    public static void setX(int XcontigIndex){
        X = XcontigIndex;
    }
    
    /**
    * Sets the Y-chromosome contig index.
    * 
    * @param YcontigIndex chrY contig index
    * @author Heiko Müller
    * @since 1.0
    */
    public static void setY(int YcontigIndex){
        Y = YcontigIndex;
    }

    
    // plot a Hilber curve of order n
    //public static void main(String[] args) {
        //int n = Integer.parseInt(args[0]);
    //    int n = 7;
    //    new Hilbert(n);
    //}
}


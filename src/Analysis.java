import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class Analysis {

	public static void main(String[] args) throws IOException {
		
		// Setup the root and the main directory
		// Change this for your own files location
		// Setup the minimal lateral roots length, below this value we do not consider the lateral or
		// secondary root
		
		String root = "C:";
		String workDir = "W_2012_03_08";
		Double minLateralRootLength = 0.1;
		
		// Setup the working directories inside the main directory
		// 1_Input : directory to place the files to be analyzed
		// 2_Cleanup : directory where the cleaned up files are stored
		// 3_Output : for each input file a CSV file will be created and placed in this directory
		// 4_Final : directory with all the final files
		
		String inputDir = root+"\\"+workDir+"\\1_Input\\";
		String cleanupDir = root+"\\"+workDir+"\\2_Cleanup\\";
		String outputDir = root+"\\"+workDir+"\\3_Output\\";
		String finalDir = root+"\\"+workDir+"\\4_Final\\";
		
		File dir = new File(inputDir);	
		String[] children = dir.list();
	    List<Accession> accessionsList = new ArrayList<Accession>();
		
		if (children == null) {
		} else {
		    for (int i=0; i<children.length; i++) { // Loop in the directory for the files to be treated

		    	// Get the accession name out of the file name
		    	int pointIndex = children[i].indexOf(".");
			    String accession = children[i].substring(0, pointIndex);
			    //System.out.println(accession);
			    
			    // Build the different file names
			    String inputFileName = inputDir+accession+".bmp.txt";
			    //String inputFileName = inputDir+accession+".txt";
				String cleanupFileName = cleanupDir+accession+".txt";
				String outputFileName = outputDir+accession+".csv";
				
				// Clean up the input file and store it
				File inFile = new File(inputFileName);
				cleanup(inFile,cleanupFileName);
				
				// Parse the file we have cleaned up to extract the required data
				// Build a .csv file for each accession containing the extracted data
				inFile = new File(cleanupFileName);
				Accession myAccession = new Accession();
				myAccession = parse(inFile,outputFileName,minLateralRootLength);
				//System.out.println(myAccession.getAccessionName());
			    accessionsList.add(myAccession);
			    
		    }		    
			
		    // Write final file #1 
		    String outFileName = finalDir+"Accessions_01.csv";
		    
		    // Compute from all the data parsed the global means
		    // globalMeans[0] is for the Main Root Length
		    // globalMeans[1] is for the Number of Lateral Roots
		    // globalMeans[2] is for the Sum of Lateral Roots Length
		    // globalMeans[3] is for the roots Density
		    
//		    Double[] globalMeans = new Double[4];
//		    globalMeans = writeFile2(outFileName,accessionsList);		    	    
			
		}				
	}

	public static void cleanup(File infile,String cleanupfilename){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    FileWriter f0 = new FileWriter(cleanupfilename);
		    
		    while (dis.available() != 0) {	    	
		    	String line = dis.readLine();
		    	//System.out.println(line);
		    	String tmpLine1 = line.replace("\t", ";");
		    	//System.out.println(tmpLine1);
		    	String tmpLine2 = tmpLine1.replace(": ", ";");
		    	//System.out.println(tmpLine2);
		    	String tmpLine3 = tmpLine2+"\r\n";
		    	//System.out.println(tmpLine3);
		    	f0.write(tmpLine3);		    	
		    }
		    
			f0.close();			
		    fis.close();
		    bis.close();
		    dis.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Accession parse(File infile,String outputfilename, Double minlateralrootlength){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the accessions data
		String fileName;
		String userName; // should be empty
		String experimentName; // should be empty
		String boxName; // should contain a letter for the box name
		String genotype; // used to store the accession name
		String media; // used to store the concentration
		int nbOfPlants; // self explanatory

	    Accession currentAccession = new Accession();
	    Accession parsedAccession = new Accession();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    while (dis.available() != 0) {
		    	
		    	String line = dis.readLine();
		    	fileName = getStringLineItem(line,1,";");
		    	
			    line = dis.readLine();
			    userName = getStringLineItem(line,1,";");
			    
			    line = dis.readLine();
			    experimentName = getStringLineItem(line,1,";");
		    			    				    
			    line = dis.readLine();
			    boxName = getStringLineItem(line,1,";");
			    currentAccession.setBox(boxName);
			     
			    line = dis.readLine();
			    genotype = getStringLineItem(line,1,";");
			    currentAccession.setName(genotype);

			    line = dis.readLine();
			    media = getStringLineItem(line,1,";");
			    currentAccession.setConcentration(media);
			    
			    // skip lines with Age of Plants
			    dis.readLine();
			    
			    // Get the accession number of plants
			    line = dis.readLine();
			    nbOfPlants = getIntegerLineItem(line,1,";");
			    currentAccession.setNbOfPlants(nbOfPlants);
			    //System.out.println(nbOfPlants);
			    
			    // skip line with scale and 3 blank lines
			    dis.readLine();
			    dis.readLine();
			    dis.readLine();
			    dis.readLine();

			    // We need to extract for each plant in the accession:
			    // - the main root length
			    // - the number of lateral roots
			    // - the sum of all the lateral and their secondary roots length
			    // - the roots density
			    //
			    Double[] mainRootLength = new Double[nbOfPlants];
				int[] nbOfLateralRoots = new int[nbOfPlants];
				Double[] sumOfLatRootsLength = new Double[nbOfPlants];
				Double[] rootsDensity = new Double[nbOfPlants];
				Double rootDeltaLength = 1.0;
			    
			    for (int i = 0; i < nbOfPlants; i++) {
			    	//System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			    	// Skip line with the root identification (Root i)
			    	dis.readLine();
			    	
			    	// Get the Main root length
			    	line = dis.readLine();
			    	mainRootLength[i] = getDoubleLineItem(line,1,";");
				    currentAccession.setMRL(mainRootLength[i],i);
				    //System.out.println(roundDouble(rootLength[i]));
				    
			    	// Skip lines with Main root vector, Main root angle
				    dis.readLine();
				    dis.readLine();
				    
				    // Get the Number lateral root(s)
				    line = dis.readLine();
				    nbOfLateralRoots[i] = getIntegerLineItem(line,1,";");
				    currentAccession.setNLR(nbOfLateralRoots[i],i);
				    
				    // We will now get the length of each lateral root and of its secondary roots
				    // we will sum those lengths only for roots having a length greater than a 
				    // specified limit.
				    //
				    // We will compute the roots density based on the following algorithm:
				    // - Find the position of the first lateral root that is longer than the minimal value
				    // - Find the position of the last lateral root that is longer than the minimal value
				    // - Compute the delta between these two
				    // - Get the final number of lateral roots
				    // - Compute the density by dividing the number of lateral roots by the delta length
				    
				    Double lateralRootsLenghSum = 0.00;
				    Double tempValue = 0.00;
				    int maxNbOfRoots = nbOfLateralRoots[i];
				    Double rootStartPosition = 0.00;
				    
				    // The number of lines to read is a function of the number of the lateral roots
				    for (int j = 0; j < maxNbOfRoots; j++) {
				    	
				    	// read the line with the lateral root data
				    	// and extract all the fields of that line
				    	line = dis.readLine();
				    	String[] lineFields = getFields(line,";");
				    	int nbOfFields = lineFields.length;
				    	
				    	// check if we are looking at a lateral root
				    	// if yes and the length is below the minimal root length limit
				    	// then we need to decrease the number of lateral roots
				    	
				    	if (nbOfFields == 12){
				    		
					    	// Get the length of the lateral root
					    	for (int k = 0; k < nbOfFields; k++ ){
					    		if (lineFields[k].contains("Length")){
					    			Double rootLength = Double.valueOf(lineFields[k+1].replace(",", "."));
					    			//System.out.println(rootLength);
					    			if (rootLength <= minlateralrootlength){
					    				nbOfLateralRoots[i] = nbOfLateralRoots[i] -1;
					    				currentAccession.setNLR(nbOfLateralRoots[i],i);
					    			}
					    			//System.out.println(nbOfLateralRoots[i]);
					    			if (rootLength > minlateralrootlength){
					    				Double rootPosition = Double.valueOf(lineFields[k+3].replace(",", "."));
					    				//System.out.println("--------------------------------------------------");
					    				//System.out.println(rootPosition);
					    				if (rootStartPosition == 0.00) {
					    					rootStartPosition = rootPosition;
					    				}
					    				rootDeltaLength = rootPosition - rootStartPosition;
					    				//System.out.println(rootDeltaLength);
					    			}					    			
					    		}
					    	}
				    	}
				    	
				    	// Get the Number secondary root(s)
				    	int nbOfSecondaryRoots = Integer.parseInt(lineFields[nbOfFields-1]);
				    	//System.out.println(nbOfSecondaryRoots);
				    	
				    	// if the value of the number of secondary roots is not 0 then 
				    	// we need to loop some extra lines more, one line per secondary root
				    	// so we increase the loop counter limit by the number of secondary roots
				    	
				    	maxNbOfRoots = maxNbOfRoots + nbOfSecondaryRoots;
				    	
				    	// Get the length of the lateral or secondary root
				    	for (int k = 0; k < nbOfFields; k++ ){
				    		if (lineFields[k].contains("Length")){
				    			Double rootLength = Double.valueOf(lineFields[k+1].replace(",", "."));
				    			//System.out.println(rootLength);
				    			if (rootLength > minlateralrootlength){
				    				tempValue = rootLength;
				    			} else {
				    				tempValue = 0.00;
				    			}
				    		}
				    	}
				    	lateralRootsLenghSum = lateralRootsLenghSum + tempValue;
				    	//System.out.println(lateralRootsLenghSum);
				    }
				    
				    // Save the value for the current root
				    sumOfLatRootsLength[i]=lateralRootsLenghSum;
				    currentAccession.setSLRL(sumOfLatRootsLength[i],i);
				    //System.out.println(roundDouble(sumOfLatRootsLength[i]));

				    // Save the roots density
				    //System.out.println(nbOfLateralRoots[i]);
				    if (nbOfLateralRoots[i] == 0) {
				    	rootsDensity[i] = 0.00; // There are no lateral roots
				    	currentAccession.setRD(rootsDensity[i],i);
				    } else if (nbOfLateralRoots[i] == 1) {
				    	rootsDensity[i] = 1/mainRootLength[i]; // Only one lateral root
				    	currentAccession.setRD(rootsDensity[i],i);
				    } else {
				    	rootsDensity[i] = nbOfLateralRoots[i]/rootDeltaLength;
				    	currentAccession.setRD(rootsDensity[i],i);
				    }
				    //System.out.println(rootsDensity[i]);
				    
				    // Skip 3 blank lines before the next root
				    dis.readLine();
				    dis.readLine();
				    dis.readLine();
		        }
			    
			    // write the output file		    
			    parsedAccession = writeFile(currentAccession,
			    							outputfilename,
			    		  				    boxName,
			    		  				    genotype,
			    		  				    media,
			    		  				    nbOfPlants,
			    		  				    mainRootLength,
			    		  				    nbOfLateralRoots,
			    		  				    sumOfLatRootsLength,
			    		  				    rootsDensity);

		    }
		    
		    fis.close();
		    bis.close();
		    dis.close();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parsedAccession;
	}
	
	private static Accession writeFile(Accession parsedaccession,
            String outputfilename,
	 	  	   String boxname,
	 	  	   String genotype,
	 	  	   String media,
	 	  	   int nbofplants,
	 	  	   Double[] mainrootlength,
	 	  	   int[] nboflateralroots,
	 	  	   Double[] sumoflatrootslength,
	 	  	   Double[] rootsdensity) throws IOException{

		FileWriter f1 = new FileWriter(outputfilename);

		// Write first line with the columns titles
		String source = "Accession Name"+";"+
					    "Box Name"+";"+
					    "Concentration"+";"+
					    "Nb of Plants"+";"+
					    "Main Root Length"+";"+";"+
					    "Nb of Lateral Roots"+";"+";"+
					    "Sum of Lateral Roots Length"+";"+";"+
					    "Roots Density"+"\r\n";	    
		f1.write(source);

		// Write the second line, for this line we write the accession name, the box name, 
		// the concentration and the number of plants. This will not be repeated for the next plants.
		source = genotype+";"+
				 boxname+";"+
				 media+";"+
				 nbofplants+";"+
				 roundDouble(mainrootlength[0],"#.##")+";"+";"+
				 nboflateralroots[0]+";"+";"+
				 roundDouble(sumoflatrootslength[0],"#.##")+";"+";"+
				 roundDouble(rootsdensity[0],"#.##")+"\r\n";

		// Just to make sure the numbers are OK for Excel
		String newSource = source.replace(".", ",");			    
		f1.write(newSource);

		// we will now write the following lines based on the number of plants
		for (int l = 1; l < nbofplants; l++ ){

			source = ";"+";"+";"+";"+
					roundDouble(mainrootlength[l],"#.##")+";"+";"+
					nboflateralroots[l]+";"+";"+
					roundDouble(sumoflatrootslength[l],"#.##")+";"+";"+
					roundDouble(rootsdensity[l],"#.##")+"\r\n";

			newSource = source.replace(".", ",");    	
			f1.write(newSource);	    	
		}

		// Calculate the means
		Double mainRootLengthMean = meanDouble(mainrootlength);
		parsedaccession.setMRLmean(mainRootLengthMean);
		Double nbOfLateralRootsMean = meanInt(nboflateralroots);
		parsedaccession.setNLRmean(nbOfLateralRootsMean);
		Double sumOfLatRootsLengthMean = meanDouble(sumoflatrootslength);
		parsedaccession.setSLRLmean(sumOfLatRootsLengthMean);
		Double rootsDensityMean = meanDouble(rootsdensity);
		parsedaccession.setRDmean(rootsDensityMean);

		// Calculate the standard deviations
		Double mainRootLengthSD = sdDouble(mainrootlength);
		//System.out.println(mainRootLengthSD);
		parsedaccession.setMRLsd(mainRootLengthSD);
		Double nbOfLateralRootsSD = sdInt(nboflateralroots);
		parsedaccession.setNLRsd(nbOfLateralRootsSD);
		Double sumOfLatRootsLengthSD = sdDouble(sumoflatrootslength);
		parsedaccession.setSLRLsd(sumOfLatRootsLengthSD);
		Double rootsDensitySD = sdDouble(rootsdensity);
		parsedaccession.setRDsd(rootsDensitySD);	    

		// Calculate the standard errors
		Double mainRootLengthSE = mainRootLengthSD/Math.sqrt(nbofplants-1);
		parsedaccession.setMRLse(mainRootLengthSE);
		Double nbOfLateralRootsSE = nbOfLateralRootsSD/Math.sqrt(nbofplants-1);
		parsedaccession.setNLRse(nbOfLateralRootsSE);
		Double sumOfLatRootsLengthSE = sumOfLatRootsLengthSD/Math.sqrt(nbofplants-1);
		parsedaccession.setSLRLse(sumOfLatRootsLengthSE);
		Double rootsDensitySE = rootsDensitySD/Math.sqrt(nbofplants-1);
		parsedaccession.setRDse(rootsDensitySE);

		// Write the line with the different mean values
		source = ";"+";"+";"+"Mean;"+
				roundDouble(mainRootLengthMean,"#.##")+";"+";"+
				roundDouble(nbOfLateralRootsMean,"#.##")+";"+";"+
				roundDouble(sumOfLatRootsLengthMean,"#.##")+";"+";"+
				roundDouble(rootsDensityMean,"#.##")+"\r\n";

		newSource = source.replace(".", ",");
		f1.write(newSource);

		// Write the line with the different standard deviations
		source = ";"+";"+";"+"SD;"+
				roundDouble(mainRootLengthSD,"#.##")+";"+";"+
				roundDouble(nbOfLateralRootsSD,"#.##")+";"+";"+
				roundDouble(sumOfLatRootsLengthSD,"#.##")+";"+";"+
				roundDouble(rootsDensitySD,"#.##")+"\r\n";

		newSource = source.replace(".", ",");
		f1.write(newSource);

		// Write the line with the different standard errors
		source = ";"+";"+";"+"SE;"+
				roundDouble(mainRootLengthSE,"#.##")+";"+";"+
				roundDouble(nbOfLateralRootsSE,"#.##")+";"+";"+
				roundDouble(sumOfLatRootsLengthSE,"#.##")+";"+";"+
				roundDouble(rootsDensitySE,"#.##")+"\r\n";

		newSource = source.replace(".", ",");
		f1.write(newSource);

		f1.close();

		return parsedaccession;
	}

//	private static Double[] writeFile2(String outputfilename,
//	 	  	                  List<Accession> accessionlist) throws IOException{
//		
//		FileWriter f1 = new FileWriter(outputfilename);
//		
//		// Write first line with the columns titles
//		String source = "Experiment Name"+";"+
//						"Accession"+";"+
//						"Concentration"+";"+
//						"Box Name"+";"+
//						""+";"+
//						"Main Root Length"+";"+
//						"Nb of Lateral Roots"+";"+
//						"Sum of Lateral Roots Length"+";"+
//						"Roots Density"+"\r\n";
//		
//		f1.write(source);
//
//		// Write the file lines
//		for (int i=0; i<accessionlist.size(); i++) {
//			
//			source = accessionlist.get(i).getExperimentName()+";"+
//					 accessionlist.get(i).getAccessionName()+";"+
//					 accessionlist.get(i).getConcentration()+";"+
//					 accessionlist.get(i).getBox()+";"+
//					 ""+";"+
//					 roundDouble(accessionlist.get(i).getMRLmean(),"#.##")+";"+
//					 roundDouble(accessionlist.get(i).getNLRmean(),"#.##")+";"+
//					 roundDouble(accessionlist.get(i).getSLRLmean(),"#.##")+";"+
//			 		 roundDouble(accessionlist.get(i).getRDmean(),"#.##")+"\r\n";
//
////			 accessionlist.get(i).getNbOfPlants()+";"+
//
//			
//			// Just to make sure the numbers are OK for Excel
//			String newSource = source.replace(".", ",");			    
//			f1.write(newSource);
//		}
//
//		Double[] globalMeans = new Double[4];			   
//		globalMeans = calculateGlobalMeans(accessionlist);
//		
//		// Write a blank line
//		source = ""+";"+""+";"+""+";"+""+";"+""+";"+""+";"+""+";"+""+"\r\n";
//		f1.write(source);
//		
//		// Write the line with the global means
//		source = ""+";"+
//				 ""+";"+
//				 ""+";"+
//				 ""+";"+
//				 "Mean"+";"+
//				 roundDouble(globalMeans[0],"#.##")+";"+
//				 roundDouble(globalMeans[1],"#.##")+";"+
//				 roundDouble(globalMeans[2],"#.##")+";"+
//		 		 roundDouble(globalMeans[3],"#.##")+"\r\n";
//		
//		// Just to make sure the numbers are OK for Excel
//		String newSource = source.replace(".", ",");
//		f1.write(newSource);
//		
//		f1.close();
//		return globalMeans;
//	}
	
    private static String getStringLineItem(String line, int index, String patternstr) {
    	
    	// This routine takes a string line as input and returns a string based on the index value 	
    	
    	String fieldStr;
    	String[] fields = line.split(patternstr);
    	if (fields.length==1) {
    		fieldStr = "";
    	} else {
    		fieldStr = fields[index];
    	}
    	return fieldStr;
    }
    
    private static int getIntegerLineItem(String line, int index, String patternstr) {
 
    	// This routine takes a string line as input and returns an integer based on the index value
   	
    	String[] fields = line.split(patternstr);
    	return Integer.parseInt(fields[index]);
    }
    
    private static Double getDoubleLineItem(String line, int index, String patternstr) {

    	// This routine takes a string line as input and returns a double based on the index value

    	String[] fields = line.split(patternstr);
    	Double value = Double.valueOf(fields[index].replace(",", "."));
    	return value;
    }
    
    private static String[] getFields(String line, String patternstr) {
    	
    	// This routine takes a string line as input and returns an array of string based on the split pattern
    	String[] fields = line.split(patternstr);
    	return fields;
    }
    
    static Double roundDouble(Double d, String decimalformat) {
    	
    	// This routine takes a double as input an returns a rounded double based on the format
    	//System.out.println(d);
    	
    	DecimalFormat twoDForm = new DecimalFormat(decimalformat);
	return Double.valueOf(twoDForm.format(d).replace(",", "."));
    }
    
    static Double[] calculateGlobalMeans(List<Accession> list) {
    	
    	// This routines calculates the global means
    	// It retrieves for each accession the MRLmean, NRLmean, SLRLmean value
    	// It then calculate for each of them the global mean value and return them as an array
    	
    	Double[] calculatedMeans = new Double[4];
    	Double[] MRLmeans = new Double[list.size()];
    	Double[] NLRmeans = new Double[list.size()];
    	Double[] SLRLmeans = new Double[list.size()];
    	Double[] RDmeans = new Double[list.size()];
    	
    	for (int i=0; i<list.size(); i++) {
    		MRLmeans[i] = roundDouble(list.get(i).getMRLmean(),"#.##");
    		NLRmeans[i] = roundDouble(list.get(i).getNLRmean(),"#.##");
    		SLRLmeans[i] = roundDouble(list.get(i).getSLRLmean(),"#.##");
    		RDmeans[i] = roundDouble(list.get(i).getRDmean(),"#.##");
        }
    	
    	calculatedMeans[0] = roundDouble(meanDouble(MRLmeans),"#.##");
    	calculatedMeans[1] = roundDouble(meanDouble(NLRmeans),"#.##");
    	calculatedMeans[2] = roundDouble(meanDouble(SLRLmeans),"#.##"); 
    	calculatedMeans[3] = roundDouble(meanDouble(RDmeans),"#.##");
    	
        return calculatedMeans;
    }
    
    static Double meanDouble(Double[] p) {

    	// This routine returns the mean for doubles

    	Double sum = 0.00;  // sum of all the elements
        for (int i=0; i<p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }
    
    static Double meanInt(int[] p) {
    	
    	// This routine returns the mean for integers
    	
        Double sum = 0.00;  // sum of all the elements
        for (int i=0; i<p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }
    
    public static Double sdDouble ( Double[] data )
    {
    // This routine returns the standard deviation for doubles
    // sd is sqrt of sum of (values-mean) squared divided by n - 1
    	
    // Calculate the mean
    Double mean = 0.00;
    final int n = data.length;
    if ( n < 2 )
       {
       return Double.NaN;
       }
    for ( int i=0; i<n; i++ )
       {
       mean += data[i];
       }
    mean /= n;

    // calculate the sum of squares
    Double sum = 0.00;
    for ( int i=0; i<n; i++ )
       {
       final Double v = data[i] - mean;
       sum += v * v;
       }

    // Change to ( n - 1 ) to n if you have complete data instead of a sample.
    return Math.sqrt( sum / ( n - 1 ) );
    }

    public static Double sdInt ( int[] data )
    {
    // This routine returns the standard deviation for integers	
    // sd is sqrt of sum of (values-mean) squared divided by n - 1
    	
    // Calculate the mean
    Double mean = 0.00;
    final int n = data.length;
    if ( n < 2 )
       {
       return Double.NaN;
       }
    for ( int i=0; i<n; i++ )
       {
       mean += data[i];
       }
    mean /= n;
    
    // calculate the sum of squares
    Double sum = 0.00;
    for ( int i=0; i<n; i++ )
       {
       final Double v = data[i] - mean;
       sum += v * v;
       }
    
    // Change to ( n - 1 ) to n if you have complete data instead of a sample.
    return Math.sqrt( sum / ( n - 1 ) );
    }

//    static List<String> getUniqueAccessionsNames(List<Accession> list, String concentration) {
//    	
//    	// This routines returns a list with unique accession names per concentration
//    	
//    	List<String> uniqueNames = new ArrayList<String>();
//       	
//    	for (int i=0; i<list.size(); i++) {
//    		String currentName = list.get(i).getAccessionName();
//    		String currentConcentration = list.get(i).getConcentration();    		
//    		if (currentConcentration.equals(concentration)) {
//    			if (!uniqueNames.contains(currentName)){
//    			uniqueNames.add(currentName);
//    			//System.out.println(currentName);
//    			}
//    		}
//    	}	    	    	
//        return uniqueNames;
//    }    
    
    static Double[] moveToArray(Double value1, Double value2, Double value3, Double value4) {
    	
    	// Moves the 4 Doubles received as input into one array
    	// If a value is equal to zero then it is not added in the array
    	// This means the routine can return an array of size 0 !
    	
    	List<Double> myList = new ArrayList<Double>();
    	
    	if (value1 != 0) {
    		myList.add(value1);
    	}
    	if (value2 != 0) {
    		myList.add(value2);
    	}
    	if (value3 != 0) {
    		myList.add(value3);
    	}
    	if (value4 != 0) {
    		myList.add(value4);
    	}

    	Double [] myArray = new Double[myList.size()];
    	
    	for (int i=0;i<myList.size();i++){
    		myArray[i]=myList.get(i);
    	}
    	
        return myArray;
    }
    
}


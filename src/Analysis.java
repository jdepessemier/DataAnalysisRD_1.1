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
		String workDir = "W_2012_03_22-08";
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
		    writeFile1(outFileName,accessionsList);	
		    
		    // Write final file #2
		    File inFile1 = new File(finalDir+"Accessions_01.csv");
		    List<AccessionSummary> mainAccessionSummaryList = new ArrayList<AccessionSummary>();
		    mainAccessionSummaryList = getAccessionSummaryList(inFile1);	    
		    outFileName = finalDir+"Accessions_02.csv";
		    writeFile2(outFileName,mainAccessionSummaryList);		    
		    
		    // Write final file #3
		    File inFile2 = new File(finalDir+"Accessions_02.csv");
		    List<AccessionMeans> accessionMeansListPerBox = new ArrayList<AccessionMeans>();	    
		    accessionMeansListPerBox = getAccessionMeansListPerBox(inFile2);    
		    outFileName = finalDir+"Accessions_High_Low.csv";
		    writeFile3(outFileName,accessionMeansListPerBox);	  			
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
			    if (media.equals("10uM")) {
			    	media = "10然";
			    }
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

	private static void writeFile1(String outputfilename,
	 	  	                  List<Accession> accessionlist) throws IOException{	
		
		FileWriter f1 = new FileWriter(outputfilename);
		String source="";
		
		for (int j = 0; j < accessionlist.size(); j++ ){
			
			String name= "";
			String concentration="";
			String box="";
			int nbOfPlants=0;
			Double MRL=0.0;
			Double NLR=0.0;
			Double SLRL=0.0;
			Double RD=0.0;
			
			for (int l = 0; l < accessionlist.get(j).getNbOfPlants(); l++ ){
				name = accessionlist.get(j).getName();
				concentration = accessionlist.get(j).getConcentration();
				box = accessionlist.get(j).getBox();
				nbOfPlants = accessionlist.get(j).getNbOfPlants();
				MRL = MRL + accessionlist.get(j).getMRL(l);
				NLR = NLR + accessionlist.get(j).getNLR(l);
				SLRL = SLRL + accessionlist.get(j).getSLRL(l);
				RD = RD + accessionlist.get(j).getRD(l);
			}
			
			MRL = MRL/accessionlist.get(j).getNbOfPlants();
			NLR = NLR/accessionlist.get(j).getNbOfPlants();
			SLRL = SLRL/accessionlist.get(j).getNbOfPlants();
			RD = RD/accessionlist.get(j).getNbOfPlants();
			
			source = name+";"+
					 concentration+";"+
					 nbOfPlants+";"+
					 box+";"+
				     roundDouble(MRL,"#.##")+";"+
				     roundDouble(NLR,"#.##")+";"+
				     roundDouble(SLRL,"#.##")+";"+
				     roundDouble(RD,"#.##")+"\r\n";
			
			// Just to make sure the numbers are OK for Excel
			String newSource = source.replace(".", ",");			    
			f1.write(newSource);
			
		}
				
		f1.close();
	}
	
	private static void writeFile2(String outputfilename,List<AccessionSummary> mainAccessionSummaryList) throws IOException{	
			
		List<AccessionSummary> accessionSummaryList_A  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_B  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_C  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_D  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_E  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_F  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_G  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_H  = new ArrayList<AccessionSummary>();
		List<AccessionSummary> accessionSummaryList_I  = new ArrayList<AccessionSummary>();
		
		List<AccessionSummary> accessionSummaryListFinal  = new ArrayList<AccessionSummary>();
			
		// Get the list of all unique accessions names ------------------------------------------------------------------
		List<Accession> accessionNamesList = new ArrayList<Accession>();
		String currentAccessionName="";
		String accessionName="";

		for (int j = 0; j < mainAccessionSummaryList.size(); j++ ){
			accessionName = mainAccessionSummaryList.get(j).getName();
			if (!(accessionName.equals(currentAccessionName))) {
				Accession myAccessionName = new Accession();
				myAccessionName.setName(accessionName);
				accessionNamesList.add(myAccessionName);
				currentAccessionName=accessionName;
			}
		}
		
//		// Debug
//	    for (int l = 0; l < accessionNamesList.size(); l++ ){
//	    	System.out.println(accessionNamesList.get(l).getName());
//	    }	
	    
	    // Set the list of concentrations -------------------------------------------------------------------------------
	    List<Concentration> concentrationList = new ArrayList<Concentration>();
		Concentration concentration_10mM = new Concentration("10mM");
		concentrationList.add(concentration_10mM);
		Concentration concentration_10然 = new Concentration("10然");
		concentrationList.add(concentration_10然);
		
//		// Debug
//	    for (int l = 0; l < concentrationList.size(); l++ ){
//	    	System.out.println(concentrationList.get(l).getName());
//	    }
	    
		String name= "";
		String concentration="";
		String box="";
		int nbofplants=0;
		Double MRL=0.0;
		Double NLR=0.0;
		Double SLRL=0.0;
		Double RD=0.0;
			
		// Sort the accessions per boxes - A,B,C,D,E,F,G,H,I -------------------------------------------------------------
		for (int j = 0; j < mainAccessionSummaryList.size(); j++ ){
			
			name = mainAccessionSummaryList.get(j).getName();
			concentration = mainAccessionSummaryList.get(j).getConcentration();
			box = mainAccessionSummaryList.get(j).getBox();
			nbofplants = mainAccessionSummaryList.get(j).getNbOfPlants();
			MRL = mainAccessionSummaryList.get(j).getMRL();
			NLR = mainAccessionSummaryList.get(j).getNLR();
			SLRL = mainAccessionSummaryList.get(j).getSLRL();
			RD = mainAccessionSummaryList.get(j).getRD();
			
			if (box.equals("A")) {
				AccessionSummary myAccessionSummary_A = new AccessionSummary();
				myAccessionSummary_A.setName(name);
				myAccessionSummary_A.setBox(box);
				myAccessionSummary_A.setConcentration(concentration);
				myAccessionSummary_A.setNbOfPlants(nbofplants);
				myAccessionSummary_A.setMRL(MRL);
				myAccessionSummary_A.setNLR(NLR);
				myAccessionSummary_A.setSLRL(SLRL);
				myAccessionSummary_A.setRD(RD);
				accessionSummaryList_A.add(myAccessionSummary_A);
			}
			if (box.equals("B")) {
				AccessionSummary myAccessionSummary_B = new AccessionSummary();
				myAccessionSummary_B.setName(name);
				myAccessionSummary_B.setBox(box);
				myAccessionSummary_B.setConcentration(concentration);
				myAccessionSummary_B.setNbOfPlants(nbofplants);
				myAccessionSummary_B.setMRL(MRL);
				myAccessionSummary_B.setNLR(NLR);
				myAccessionSummary_B.setSLRL(SLRL);
				myAccessionSummary_B.setRD(RD);
				accessionSummaryList_B.add(myAccessionSummary_B);
			}
			if (box.equals("C")) {
				AccessionSummary myAccessionSummary_C = new AccessionSummary();
				myAccessionSummary_C.setName(name);
				myAccessionSummary_C.setBox(box);
				myAccessionSummary_C.setConcentration(concentration);
				myAccessionSummary_C.setNbOfPlants(nbofplants);
				myAccessionSummary_C.setMRL(MRL);
				myAccessionSummary_C.setNLR(NLR);
				myAccessionSummary_C.setSLRL(SLRL);
				myAccessionSummary_C.setRD(RD);
				accessionSummaryList_C.add(myAccessionSummary_C);
			}
			if (box.equals("D")) {
				AccessionSummary myAccessionSummary_D = new AccessionSummary();
				myAccessionSummary_D.setName(name);
				myAccessionSummary_D.setBox(box);
				myAccessionSummary_D.setConcentration(concentration);
				myAccessionSummary_D.setNbOfPlants(nbofplants);
				myAccessionSummary_D.setMRL(MRL);
				myAccessionSummary_D.setNLR(NLR);
				myAccessionSummary_D.setSLRL(SLRL);
				myAccessionSummary_D.setRD(RD);
				accessionSummaryList_D.add(myAccessionSummary_D);
			}
			if (box.equals("E")) {
				AccessionSummary myAccessionSummary_E = new AccessionSummary();
				myAccessionSummary_E.setName(name);
				myAccessionSummary_E.setBox(box);
				myAccessionSummary_E.setConcentration(concentration);
				myAccessionSummary_E.setNbOfPlants(nbofplants);
				myAccessionSummary_E.setMRL(MRL);
				myAccessionSummary_E.setNLR(NLR);
				myAccessionSummary_E.setSLRL(SLRL);
				myAccessionSummary_E.setRD(RD);
				accessionSummaryList_E.add(myAccessionSummary_E);
			}
			if (box.equals("F")) {
				AccessionSummary myAccessionSummary_F = new AccessionSummary();
				myAccessionSummary_F.setName(name);
				myAccessionSummary_F.setBox(box);
				myAccessionSummary_F.setConcentration(concentration);
				myAccessionSummary_F.setNbOfPlants(nbofplants);
				myAccessionSummary_F.setMRL(MRL);
				myAccessionSummary_F.setNLR(NLR);
				myAccessionSummary_F.setSLRL(SLRL);
				myAccessionSummary_F.setRD(RD);
				accessionSummaryList_F.add(myAccessionSummary_F);
			}
			if (box.equals("G")) {
				AccessionSummary myAccessionSummary_G = new AccessionSummary();
				myAccessionSummary_G.setName(name);
				myAccessionSummary_G.setBox(box);
				myAccessionSummary_G.setConcentration(concentration);
				myAccessionSummary_G.setNbOfPlants(nbofplants);
				myAccessionSummary_G.setMRL(MRL);
				myAccessionSummary_G.setNLR(NLR);
				myAccessionSummary_G.setSLRL(SLRL);
				myAccessionSummary_G.setRD(RD);
				accessionSummaryList_G.add(myAccessionSummary_G);
			}
			if (box.equals("H")) {
				AccessionSummary myAccessionSummary_H = new AccessionSummary();
				myAccessionSummary_H.setName(name);
				myAccessionSummary_H.setBox(box);
				myAccessionSummary_H.setConcentration(concentration);
				myAccessionSummary_H.setNbOfPlants(nbofplants);
				myAccessionSummary_H.setMRL(MRL);
				myAccessionSummary_H.setNLR(NLR);
				myAccessionSummary_H.setSLRL(SLRL);
				myAccessionSummary_H.setRD(RD);
				accessionSummaryList_H.add(myAccessionSummary_H);
			}
			if (box.equals("I")) {
				AccessionSummary myAccessionSummary_I = new AccessionSummary();
				myAccessionSummary_I.setName(name);
				myAccessionSummary_I.setBox(box);
				myAccessionSummary_I.setConcentration(concentration);
				myAccessionSummary_I.setNbOfPlants(nbofplants);
				myAccessionSummary_I.setMRL(MRL);
				myAccessionSummary_I.setNLR(NLR);
				myAccessionSummary_I.setSLRL(SLRL);
				myAccessionSummary_I.setRD(RD);
				accessionSummaryList_I.add(myAccessionSummary_I);
			}
		}	
		
		//Debug
		System.out.println(accessionSummaryList_A.size()+" "+
				   accessionSummaryList_B.size()+" "+
				   accessionSummaryList_C.size()+" "+
				   accessionSummaryList_D.size()+" "+
				   accessionSummaryList_E.size()+" "+
				   accessionSummaryList_F.size()+" "+
				   accessionSummaryList_G.size()+" "+
				   accessionSummaryList_H.size()+" "+
				   accessionSummaryList_I.size());
		
		
		// Loop in the unique accessions names, and by concentration combine the data ------------------------------------
			
		for (int i = 0; i < accessionNamesList.size(); i++ ){
		
			name = accessionNamesList.get(i).getName();
			for (int j = 0; j < concentrationList.size(); j++ ){
				
				int totalNbOfPlants=0;
				int nbOfPlantsA=0;
				int nbOfPlantsB=0;
				int nbOfPlantsC=0;
				int nbOfPlantsD=0;				
				int nbOfPlantsE=0;
				int nbOfPlantsF=0;
				int nbOfPlantsG=0;
				int nbOfPlantsH=0;
				int nbOfPlantsI=0;
				Double MRLA=0.0;
				Double MRLB=0.0;
				Double MRLC=0.0;
				Double MRLD=0.0;
				Double MRLE=0.0;
				Double MRLF=0.0;
				Double MRLG=0.0;
				Double MRLH=0.0;
				Double MRLI=0.0;
				Double NLRA=0.0;
				Double NLRB=0.0;
				Double NLRC=0.0;
				Double NLRD=0.0;
				Double NLRE=0.0;
				Double NLRF=0.0;
				Double NLRG=0.0;
				Double NLRH=0.0;
				Double NLRI=0.0;
				Double SLRLA=0.0;
				Double SLRLB=0.0;
				Double SLRLC=0.0;
				Double SLRLD=0.0;
				Double SLRLE=0.0;
				Double SLRLF=0.0;
				Double SLRLG=0.0;
				Double SLRLH=0.0;
				Double SLRLI=0.0;
				Double RDA=0.0;
				Double RDB=0.0;
				Double RDC=0.0;
				Double RDD=0.0;
				Double RDE=0.0;
				Double RDF=0.0;
				Double RDG=0.0;
				Double RDH=0.0;
				Double RDI=0.0;
				
				concentration = concentrationList.get(j).getName();
				for (int k = 0; k < accessionSummaryList_A.size(); k++ ){
					if (accessionSummaryList_A.get(k).getName().equals(name)) {
						if (accessionSummaryList_A.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsA = accessionSummaryList_A.get(k).getNbOfPlants();
							MRLA = MRLA + accessionSummaryList_A.get(k).getMRL()*nbOfPlantsA;
							NLRA = NLRA + accessionSummaryList_A.get(k).getNLR()*nbOfPlantsA;
							SLRLA = SLRLA + accessionSummaryList_A.get(k).getSLRL()*nbOfPlantsA;
							RDA = RDA + accessionSummaryList_A.get(k).getRD()*nbOfPlantsA;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsA+" "+
											   MRLA+" "+
											   NLRA+" "+
											   SLRLA+" "+
											   RDA);
							
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_B.size(); k++ ){
					if (accessionSummaryList_B.get(k).getName().equals(name)) {
						if (accessionSummaryList_B.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsB = accessionSummaryList_B.get(k).getNbOfPlants();
							MRLB = MRLB + accessionSummaryList_B.get(k).getMRL()*nbOfPlantsB;
							NLRB = NLRB + accessionSummaryList_B.get(k).getNLR()*nbOfPlantsB;
							SLRLB = SLRLB + accessionSummaryList_B.get(k).getSLRL()*nbOfPlantsB;
							RDB = RDB + accessionSummaryList_B.get(k).getRD()*nbOfPlantsB;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsB+" "+
											   MRLB+" "+
											   NLRB+" "+
											   SLRLB+" "+
											   RDB);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_C.size(); k++ ){
					if (accessionSummaryList_C.get(k).getName().equals(name)) {
						if (accessionSummaryList_C.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsC = accessionSummaryList_C.get(k).getNbOfPlants();
							MRLC = MRLC + accessionSummaryList_C.get(k).getMRL()*nbOfPlantsC;
							NLRC = NLRC + accessionSummaryList_C.get(k).getNLR()*nbOfPlantsC;
							SLRLC = SLRLC + accessionSummaryList_C.get(k).getSLRL()*nbOfPlantsC;
							RDC = RDC + accessionSummaryList_C.get(k).getRD()*nbOfPlantsC;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsC+" "+
											   MRLC+" "+
											   NLRC+" "+
											   SLRLC+" "+
											   RDC);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_D.size(); k++ ){
					if (accessionSummaryList_D.get(k).getName().equals(name)) {
						if (accessionSummaryList_D.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsD = accessionSummaryList_D.get(k).getNbOfPlants();
							MRLD = MRLD + accessionSummaryList_D.get(k).getMRL()*nbOfPlantsD;
							NLRD = NLRD + accessionSummaryList_D.get(k).getNLR()*nbOfPlantsD;
							SLRLD = SLRLD + accessionSummaryList_D.get(k).getSLRL()*nbOfPlantsD;
							RDD = RDD + accessionSummaryList_D.get(k).getRD()*nbOfPlantsD;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsD+" "+
											   MRLD+" "+
											   NLRD+" "+
											   SLRLD+" "+
											   RDD);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_E.size(); k++ ){
					if (accessionSummaryList_E.get(k).getName().equals(name)) {
						if (accessionSummaryList_E.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsE = accessionSummaryList_E.get(k).getNbOfPlants();
							MRLE = MRLE + accessionSummaryList_E.get(k).getMRL()*nbOfPlantsE;
							NLRE = NLRE + accessionSummaryList_E.get(k).getNLR()*nbOfPlantsE;
							SLRLE = SLRLE + accessionSummaryList_E.get(k).getSLRL()*nbOfPlantsE;
							RDE = RDE + accessionSummaryList_E.get(k).getRD()*nbOfPlantsE;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsE+" "+
											   MRLE+" "+
											   NLRE+" "+
											   SLRLE+" "+
											   RDE);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_F.size(); k++ ){
					if (accessionSummaryList_F.get(k).getName().equals(name)) {
						if (accessionSummaryList_F.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsF = accessionSummaryList_F.get(k).getNbOfPlants();
							MRLF = MRLF + accessionSummaryList_F.get(k).getMRL()*nbOfPlantsF;
							NLRF = NLRF + accessionSummaryList_F.get(k).getNLR()*nbOfPlantsF;
							SLRLF = SLRLF + accessionSummaryList_F.get(k).getSLRL()*nbOfPlantsF;
							RDF = RDF + accessionSummaryList_F.get(k).getRD()*nbOfPlantsF;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsF+" "+
											   MRLF+" "+
											   NLRF+" "+
											   SLRLF+" "+
											   RDF);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_G.size(); k++ ){
					if (accessionSummaryList_G.get(k).getName().equals(name)) {
						if (accessionSummaryList_G.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsG = accessionSummaryList_G.get(k).getNbOfPlants();
							MRLG = MRLG + accessionSummaryList_G.get(k).getMRL()*nbOfPlantsG;
							NLRG = NLRG + accessionSummaryList_G.get(k).getNLR()*nbOfPlantsG;
							SLRLG = SLRLG + accessionSummaryList_G.get(k).getSLRL()*nbOfPlantsG;
							RDG = RDG + accessionSummaryList_G.get(k).getRD()*nbOfPlantsG;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsG+" "+
											   MRLG+" "+
											   NLRG+" "+
											   SLRLG+" "+
											   RDG);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_H.size(); k++ ){
					if (accessionSummaryList_H.get(k).getName().equals(name)) {
						if (accessionSummaryList_H.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsH = accessionSummaryList_H.get(k).getNbOfPlants();
							MRLH = MRLH + accessionSummaryList_H.get(k).getMRL()*nbOfPlantsH;
							NLRH = NLRH + accessionSummaryList_H.get(k).getNLR()*nbOfPlantsH;
							SLRLH = SLRLH + accessionSummaryList_H.get(k).getSLRL()*nbOfPlantsH;
							RDH = RDH + accessionSummaryList_H.get(k).getRD()*nbOfPlantsH;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsH+" "+
											   MRLH+" "+
											   NLRH+" "+
											   SLRLH+" "+
											   RDH);
						}
					}
				}
				for (int k = 0; k < accessionSummaryList_I.size(); k++ ){
					if (accessionSummaryList_I.get(k).getName().equals(name)) {
						if (accessionSummaryList_I.get(k).getConcentration().equals(concentration)) {
							nbOfPlantsI = accessionSummaryList_I.get(k).getNbOfPlants();
							MRLI = MRLI + accessionSummaryList_I.get(k).getMRL()*nbOfPlantsI;
							NLRI= NLRI + accessionSummaryList_I.get(k).getNLR()*nbOfPlantsI;
							SLRLI = SLRLI + accessionSummaryList_I.get(k).getSLRL()*nbOfPlantsI;
							RDI = RDI + accessionSummaryList_I.get(k).getRD()*nbOfPlantsI;
							
							//Debug
							System.out.println(name+" "+
											   concentration+" "+
											   nbOfPlantsI+" "+
											   MRLI+" "+
											   NLRI+" "+
											   SLRLI+" "+
											   RDI);
						}
					}
				}

				
				totalNbOfPlants = nbOfPlantsA + 
								  nbOfPlantsB + 
								  nbOfPlantsC +
								  nbOfPlantsD +
								  nbOfPlantsE +
								  nbOfPlantsF +
								  nbOfPlantsG +
								  nbOfPlantsH +
								  nbOfPlantsI;
				
				if (!(totalNbOfPlants==0)) {
					MRL = (MRLA+MRLB+MRLC+MRLD+MRLE+MRLF+MRLG+MRLH+MRLI)/ totalNbOfPlants;
					NLR = (NLRA+NLRB+NLRC+NLRD+NLRE+NLRF+NLRG+NLRH+NLRI)/ totalNbOfPlants;
					SLRL = (SLRLA+SLRLB+SLRLC+SLRLD+SLRLE+SLRLF+SLRLG+SLRLH+SLRLI)/ totalNbOfPlants;
					RD = (RDA+RDB+RDC+RDD+RDE+RDF+RDG+RDH+RDI)/ totalNbOfPlants;
					
					//Debug
					System.out.println(name+" "+
									   concentration+" "+
									   totalNbOfPlants+" "+
									   MRL+" "+
									   NLR+" "+
									   SLRL+" "+
									   RD);
					
					AccessionSummary accessionSummaryFinal = new AccessionSummary();
					accessionSummaryFinal.setName(name);
					accessionSummaryFinal.setConcentration(concentration);
					accessionSummaryFinal.setMRL(MRL);
					accessionSummaryFinal.setNLR(NLR);
					accessionSummaryFinal.setSLRL(SLRL);
					accessionSummaryFinal.setRD(RD);
					accessionSummaryListFinal.add(accessionSummaryFinal);				
				}
			}
		}
		
		// Debug
		System.out.println(accessionSummaryListFinal.size());
	    for (int l = 0; l < accessionSummaryListFinal.size(); l++ ){
	    	System.out.println(accessionSummaryListFinal.get(l).getName()+" "+
	    			           accessionSummaryListFinal.get(l).getConcentration());
	    }
	    
	    // Write the file
	    FileWriter f1 = new FileWriter(outputfilename);
		String source="";
		String currentName="";
		String currentConcentration="";
		Double currentMRL=0.0;
		Double currentNLR=0.0;
		Double currentSLRL=0.0;
		Double currentRD=0.0;
		
	    for (int l = 0; l < accessionSummaryListFinal.size(); l++ ){
	    	currentName = accessionSummaryListFinal.get(l).getName();
	    	currentConcentration = accessionSummaryListFinal.get(l).getConcentration();
	    	currentMRL = accessionSummaryListFinal.get(l).getMRL();
	    	currentNLR = accessionSummaryListFinal.get(l).getNLR();
	    	currentSLRL = accessionSummaryListFinal.get(l).getSLRL();
	    	currentRD = accessionSummaryListFinal.get(l).getRD();
	    	
	    	source = currentName+";"+
	    			 currentConcentration+";"+
					 roundDouble(currentMRL,"#.##")+";"+
					 roundDouble(currentNLR,"#.##")+";"+
					 roundDouble(currentSLRL,"#.##")+";"+
					 roundDouble(currentRD,"#.##")+"\r\n";

			// Debug
		    System.out.println(currentName+";"+
	    			 currentConcentration+";"+
					 roundDouble(currentMRL,"#.##")+";"+
					 roundDouble(currentNLR,"#.##")+";"+
					 roundDouble(currentSLRL,"#.##")+";"+
					 roundDouble(currentRD,"#.##"));
			
			String newSource = source.replace(".", ",");
			f1.write(newSource);
	    }

//		//Debug
//    	System.out.println(accessionSummaryList_A.size());
//    	System.out.println(accessionSummaryList_B.size());
//    	System.out.println(accessionSummaryList_C.size());
    	
		f1.close();
	}	

	private static List<AccessionMeans> getAccessionMeansListPerBox(File infile){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the data
		String accession;
		String concentration;
		Double MRL=0.0;
		Double NLR=0.0;
		Double SLRL=0.0;
		Double RD=0.0;
		
		List<AccessionMeans> myAccessionMeansList = new ArrayList<AccessionMeans>();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    
		    
		    while (dis.available() != 0) {
		    	   	
		    	String line = dis.readLine();
		    	
		    	accession = getStringLineItem(line,0,";");
		    	concentration = getStringLineItem(line,1,";");
		    	MRL = getDoubleLineItem(line,2,";");
		    	NLR = getDoubleLineItem(line,3,";");
		    	SLRL = getDoubleLineItem(line,4,";");
		    	RD = getDoubleLineItem(line,5,";");
		    	
		    	AccessionMeans myAccessionMeans = new AccessionMeans();
		    	
		    	myAccessionMeans.setName(accession);
		    	myAccessionMeans.setConcentration(concentration);
		    	myAccessionMeans.setMRL(MRL);
		    	myAccessionMeans.setNLR(NLR);
		    	myAccessionMeans.setSLRL(SLRL);
		    	myAccessionMeans.setRD(RD);
		    	
		    	myAccessionMeansList.add(myAccessionMeans);	 
		    		    	
		    }
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return myAccessionMeansList;
	}	
	
	private static List<AccessionSummary> getAccessionSummaryList(File infile){
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		// variables to store the data
		String accession;
		String concentration;
		String box;
		int nbofplants;
		Double MRL=0.0;
		Double NLR=0.0;
		Double SLRL=0.0;
		Double RD=0.0;
		
		List<AccessionSummary> myAccessionSummaryList = new ArrayList<AccessionSummary>();
		
		try {
			fis = new FileInputStream(infile);
		    bis = new BufferedInputStream(fis);
		    dis = new DataInputStream(bis);
		    
		    while (dis.available() != 0) {
		    	   	
		    	String line = dis.readLine();
		    	
		    	accession = getStringLineItem(line,0,";");
		    	concentration = getStringLineItem(line,1,";");
		    	nbofplants = getIntegerLineItem(line,2,";");
		    	box = getStringLineItem(line,3,";");
		    	MRL = getDoubleLineItem(line,4,";");
		    	NLR = getDoubleLineItem(line,5,";");
		    	SLRL = getDoubleLineItem(line,6,";");
		    	RD = getDoubleLineItem(line,7,";");
		    	
		    	AccessionSummary myAccessionSummary = new AccessionSummary();
		    	
		    	myAccessionSummary.setName(accession);
		    	myAccessionSummary.setConcentration(concentration);
		    	myAccessionSummary.setBox(box);
		    	myAccessionSummary.setNbOfPlants(nbofplants);
		    	myAccessionSummary.setMRL(MRL);
		    	myAccessionSummary.setNLR(NLR);
		    	myAccessionSummary.setSLRL(SLRL);
		    	myAccessionSummary.setRD(RD);
		    	
		    	myAccessionSummaryList.add(myAccessionSummary);	 
		    		    	
		    }
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	

		return myAccessionSummaryList;
	}	
	
	private static void writeFile3(String outputfilename,
               List<AccessionMeans> accessionMeansList) throws IOException{
		
				List<Accession> myAccessionNamesList = new ArrayList<Accession>();
		String currentName="";
		String name="";
		
		for (int j = 0; j < accessionMeansList.size(); j++ ){
			name = accessionMeansList.get(j).getName();
			if (!(name.equals(currentName))) {
				Accession myAccessionNames = new Accession();
				myAccessionNames.setName(name);
				myAccessionNamesList.add(myAccessionNames);
				currentName=name;
			}
		}
			
//		for (int j = 0; j < myAccessionNamesList.size(); j++ ){
//			System.out.println(myAccessionNamesList.get(j).getName());
//		}
			
		FileWriter f1 = new FileWriter(outputfilename);
		String source="";
		String LOW = "10然";
		String HIGH = "10mM";
		

		List<AccessionMeans> toSaveAccessionMeansList = new ArrayList<AccessionMeans>();
			
		for (int j = 0; j < myAccessionNamesList.size(); j++ ){
			String currentAccessionName = myAccessionNamesList.get(j).getName();
			AccessionMeans myAccessionMeans = new AccessionMeans();
			myAccessionMeans.setName(currentAccessionName);
			for (int k = 0; k < accessionMeansList.size(); k++ ){
				String accessionMeansName = accessionMeansList.get(k).getName();
				String concentration = accessionMeansList.get(k).getConcentration();
				if (currentAccessionName.equals(accessionMeansName)){
					if (concentration.equals(LOW)) {
						myAccessionMeans.setMRLlow(accessionMeansList.get(k).getMRL());
						myAccessionMeans.setNLRlow(accessionMeansList.get(k).getNLR());
						myAccessionMeans.setSLRLlow(accessionMeansList.get(k).getSLRL());
						myAccessionMeans.setRDlow(accessionMeansList.get(k).getRD());					
					} else {
						myAccessionMeans.setMRLhigh(accessionMeansList.get(k).getMRL());
						myAccessionMeans.setNLRhigh(accessionMeansList.get(k).getNLR());
						myAccessionMeans.setSLRLhigh(accessionMeansList.get(k).getSLRL());
						myAccessionMeans.setRDhigh(accessionMeansList.get(k).getRD());
					}
				}
			}
			toSaveAccessionMeansList.add(myAccessionMeans);
		}
		
		// Write first line with the columns titles
		source = "Accession"+";"+
				 "MRL (low)"+";"+
				 "NLR (low)"+";"+
				 "LRL (low)"+";"+
				 "R Density (low)"+";"+
				 "MRL (High)"+";"+
				 "NLR (High)"+";"+
				 "LRL (High)"+";"+
				 "R Density (High)"+"\r\n";	
		
		f1.write(source);
						
		for (int j = 0; j < toSaveAccessionMeansList.size(); j++ ){
			
			source = toSaveAccessionMeansList.get(j).getName()+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getMRLlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getNLRlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getSLRLlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getRDlow(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getMRLhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getNLRhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getSLRLhigh(),"#.##")+";"+
					 roundDouble(toSaveAccessionMeansList.get(j).getRDhigh(),"#.##")+"\r\n";

			// Just to make sure the numbers are OK for Excel
			String newSource = source.replace(".", ",");			    
			f1.write(newSource);
		}

		f1.close();
	}	
	
	
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


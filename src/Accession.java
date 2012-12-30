public class Accession {
	
	private String name;
	private String concentration;
	private String box;
	private int nbOfPlants;
	private Double[] MRL = new Double[5];
	private int[] NLR = new int[5];
	private Double[] SLRL = new Double[5];
	private Double[] RD = new Double[5];
	
	// Main Root Length (MRL) mean, standard deviation, and standard error
	private Double MRLmean;
	private Double MRLsd;
	private Double MRLse;
	
	// Number of Lateral Roots (NLR) mean, standard deviation, and standard error 
	private Double NLRmean;
	private Double NLRsd;
	private Double NLRse;
	
	// Sum of Lateral Roots Length (SLRL) mean, standard deviation, and standard error
	private Double SLRLmean;
	private Double SLRLsd;
	private Double SLRLse;
	
	// Roots Density mean, standard deviation, and standard error
	private Double RDmean;
	private Double RDsd;
	private Double RDse;
	
	public Accession() {
		name = "";
		concentration = "";
		box = "";
		nbOfPlants = 0;
		
		for (int i=0; i<4; i++) {
			MRL[i] = 0.00;
			NLR[i] = 0;
			SLRL[i] = 0.00;
			RD[i] = 0.00;
		}
		
		MRLmean = 0.00;
		MRLsd = 0.00;
		MRLse = 0.00;
		NLRmean = 0.00;
		NLRsd = 0.00;
		NLRse = 0.00;
		SLRLmean = 0.00;
		SLRLsd = 0.00;
		SLRLse = 0.00;		
		RDmean = 0.00;
		RDsd = 0.00;
		RDse = 0.00;		
	}
	
	public Accession(String name,
					 String concentration,
					 String box,
					 int nbofplants,
					 Double[] mrl,
					 int[] nlr,
					 Double[] slrl,
					 Double[] rd,
					 Double mainrootlengthmean,
					 Double mainrootlengthsd,
					 Double mainrootlengthse,
					 Double nboflateralrootsmean,
					 Double nboflateralrootssd,
					 Double nboflateralrootsse,
					 Double sumoflateralrootslengthmean,
					 Double sumoflateralrootslengthsd,
					 Double sumoflateralrootslengthse,
					 Double rootsdensitymean,
					 Double rootsdensitysd,
					 Double rootsdensityse) {
		this.name = name;
		this.concentration = concentration;
		this.box = box;
		this.nbOfPlants = nbofplants;
		this.MRL = mrl;
		this.NLR = nlr;
		this.SLRL = slrl;
		this.RD = rd;		
		this.MRLmean = mainrootlengthmean;
		this.MRLsd = mainrootlengthsd;
		this.MRLse = mainrootlengthse;
		this.NLRmean = nboflateralrootsmean;
		this.NLRsd = nboflateralrootssd;
		this.NLRse = nboflateralrootsse;
		this.SLRLmean = sumoflateralrootslengthmean;
		this.SLRLsd = sumoflateralrootslengthsd;
		this.SLRLse = sumoflateralrootslengthse;
		this.RDmean = rootsdensitymean;
		this.RDsd = rootsdensitysd;
		this.RDse = rootsdensityse;
	}

	// Accession Name
	
	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	// Concentration
	
	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String value) {
		concentration = value;
	}

	// Box
	
	public String getBox() {
		return box;
	}

	public void setBox(String value) {
		box = value;
	}

	// Number of Plants
	
	public int getNbOfPlants() {
		return nbOfPlants;
	}

	public void setNbOfPlants(int value) {
		nbOfPlants = value;
	}
	
	// MRL
	
	public Double getMRL(int idx) {
		return MRL[idx];
	}

	public void setMRL(double value, int idx) {
		MRL[idx] = value;
	}
	
	// NLR
	
	public int getNLR(int idx) {
		return NLR[idx];
	}

	public void setNLR(int value, int idx) {
		NLR[idx] = value;
	}
	
	// SLRL
	
	public Double getSLRL(int idx) {
		return SLRL[idx];
	}

	public void setSLRL(double value, int idx) {
		SLRL[idx] = value;
	}

	// RD
	
	public Double getRD(int idx) {
		return RD[idx];
	}

	public void setRD(double value, int idx) {
		RD[idx] = value;
	}

	// Main Root Length
	
	public Double getMRLmean() {
		return MRLmean;
	}

	public void setMRLmean(Double value) {
		MRLmean = value;
	}

	public Double getMRLsd() {
		return MRLsd;
	}

	public void setMRLsd(Double value) {
		MRLsd = value;
	}

	public Double getMRLse() {
		return MRLse;
	}

	public void setMRLse(Double value) {
		MRLse = value;
	}

	// Number of Lateral Roots
	
	public Double getNLRmean() {
		return NLRmean;
	}

	public void setNLRmean(Double value) {
		NLRmean = value;
	}

	public Double getNLRsd() {
		return NLRsd;
	}

	public void setNLRsd(Double value) {
		NLRsd = value;
	}

	public Double getNLRse() {
		return NLRse;
	}

	public void setNLRse(Double value) {
		NLRse = value;
	}

	// Sum of Lateral Roots Length
	
	public Double getSLRLmean() {
		return SLRLmean;
	}

	public void setSLRLmean(Double value) {
		SLRLmean = value;
	}

	public Double getSLRLsd() {
		return SLRLsd;
	}

	public void setSLRLsd(Double value) {
		SLRLsd = value;
	}

	public Double getSLRLse() {
		return SLRLse;
	}

	public void setSLRLse(Double value) {
		SLRLse = value;
	}

	// Roots Density
	
	public Double getRDmean() {
		return RDmean;
	}

	public void setRDmean(Double value) {
		RDmean = value;
	}

	public Double getRDsd() {
		return RDsd;
	}

	public void setRDsd(Double value) {
		RDsd = value;
	}

	public Double getRDse() {
		return RDse;
	}

	public void setRDse(Double value) {
		RDse = value;
	}
	
}
	

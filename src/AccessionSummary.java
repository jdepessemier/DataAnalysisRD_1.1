public class AccessionSummary {
	
	private String name;
	private String concentration;
	private String box;
	private int nbOfPlants;
	private Double MRL;
	private Double NLR;
	private Double SLRL;
	private Double RD;
	
	public AccessionSummary() {
		name = "";
		concentration = "";
		box = "";
		nbOfPlants = 0;
		MRL = 0.00;
		NLR = 0.00;
		SLRL = 0.00;
		RD = 0.00;
	}
	
	public AccessionSummary(String name,
						   	String concentration,
						   	int nbOfPlants,
						   	String box,
						   	Double MRL,
						   	Double NLR,
						   	Double SLRL,
						   	Double RD) {
		this.name = name;
		this.concentration = concentration;
		this.nbOfPlants = nbOfPlants;
		this.box = box;
		this.MRL = MRL;
		this.NLR = NLR;
		this.SLRL = SLRL;
		this.RD = RD;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	public String getConcentration(){
		return concentration;
	}

	public void setConcentration(String value) {
		concentration = value;
	}
	
	public String getBox(){
		return box;
	}

	public void setBox(String value) {
		box = value;
	}
	
	public int getNbOfPlants() {
		return nbOfPlants;
	}

	public void setNbOfPlants(int value) {
		nbOfPlants = value;
	}

	public Double getMRL() {
		return MRL;
	}

	public void setMRL(Double value) {
		MRL = value;
	}
	
	public Double getNLR() {
		return NLR;
	}

	public void setNLR(Double value) {
		NLR = value;
	}
	
	public Double getSLRL() {
		return SLRL;
	}

	public void setSLRL(Double value) {
		SLRL = value;
	}
	
	public Double getRD() {
		return RD;
	}

	public void setRD(Double value) {
		RD = value;
	}

}
	

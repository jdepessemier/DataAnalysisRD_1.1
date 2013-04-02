public class AccessionMeans {
	
	private String name;
	private String concentration;
	private Double MRL;
	private Double NLR;
	private Double SLRL;
	private Double RD;
	private Double MRLlow;
	private Double NLRlow;
	private Double SLRLlow;
	private Double RDlow;
	private Double MRLhigh;
	private Double NLRhigh;
	private Double SLRLhigh;
	private Double RDhigh;
	
	public AccessionMeans() {
		name = "";
		concentration = "";
		MRL = 0.00;
		NLR = 0.00;
		SLRL = 0.00;
		RD = 0.00;
		MRLlow = 0.00;
		NLRlow = 0.00;
		SLRLlow = 0.00;
		RDlow = 0.00;
		MRLhigh = 0.00;
		NLRhigh = 0.00;
		SLRLhigh = 0.00;
		RDhigh = 0.00;
	}
	
	public AccessionMeans(String name,
						  String concentration,
						  Double MRL,
						  Double NLR,
						  Double SLRL,
						  Double RD,
						  Double MRLlow,
						  Double NLRlow,
						  Double SLRLlow,
						  Double RDlow,
						  Double MRLhigh,
						  Double NLRhigh,
						  Double SLRLhigh,
						  Double RDhigh) {
		this.name = name;
		this.concentration = concentration;
		this.MRL = MRL;
		this.NLR = NLR;
		this.SLRL = SLRL;
		this.RD = RD;
		this.MRLlow = MRLlow;
		this.NLRlow = NLRlow;
		this.SLRLlow = SLRLlow;
		this.RDlow = RDlow;
		this.MRLhigh = MRLhigh;
		this.NLRhigh = NLRhigh;
		this.SLRLhigh = SLRLhigh;		
		this.RDhigh = RDhigh;
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
	
	public Double getMRLlow() {
		return MRLlow;
	}

	public void setMRLlow(Double value) {
		MRLlow = value;
	}
	
	public Double getNLRlow() {
		return NLRlow;
	}

	public void setNLRlow(Double value) {
		NLRlow = value;
	}
	
	public Double getSLRLlow() {
		return SLRLlow;
	}

	public void setSLRLlow(Double value) {
		SLRLlow = value;
	}
	
	public Double getRDlow() {
		return RDlow;
	}

	public void setRDlow(Double value) {
		RDlow = value;
	}

	public Double getMRLhigh() {
		return MRLhigh;
	}

	public void setMRLhigh(Double value) {
		MRLhigh = value;
	}
	
	public Double getNLRhigh() {
		return NLRhigh;
	}

	public void setNLRhigh(Double value) {
		NLRhigh = value;
	}
	
	public Double getSLRLhigh() {
		return SLRLhigh;
	}

	public void setSLRLhigh(Double value) {
		SLRLhigh = value;
	}
	
	public Double getRDhigh() {
		return RDhigh;
	}

	public void setRDhigh(Double value) {
		RDhigh = value;
	}

}
	

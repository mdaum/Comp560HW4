package extraCredit;

public class Decision {
	String filepath;
	String classDecision;
	double[] clutchProbs;
	double clutchResult;
	double[] hoboProbs;
	double hoboResult;
	double[] flatsProbs;
	double flatsResult;
	double[] pumpsProbs;
	double pumpsResult;
	boolean interesting;
	String originalClass;

	public Decision(double[] clutchProbs, double clutchResult,
			double[] hoboProbs, double hoboResult, double[] flatsProbs,
			double flatsResult, double[] pumpsProbs, double pumpsResult,
			String filepath) {
		this.clutchProbs=clutchProbs;
		this.clutchResult=clutchResult;
		this.hoboProbs=hoboProbs;
		this.hoboResult=hoboResult;
		this.flatsProbs=flatsProbs;
		this.flatsResult=flatsResult;
		this.pumpsProbs=pumpsProbs;
		this.pumpsResult=pumpsResult;
		this.filepath=filepath;
		this.interesting=false;
	}
	//Decides which class is most likely to match the test image, using the four class models
	public void makeDecision(){
		double probClutch = clutchProbs[1];
		double probHobo = hoboProbs[1];
		double probFlats = flatsProbs[1];
		double probPumps = pumpsProbs[1];
		
		double max1 = Math.max(probClutch,probHobo);
		classDecision = (max1 == probClutch) ? "clutch" : "hobo";

		double max2=Math.max(probPumps, probFlats);
		String contender = (max2 == probFlats) ? "flats" : "pumps";
		
		if(max2>max1)classDecision=contender;
		
		if(!(filepath.contains(classDecision)))interesting=true;
		if(filepath.contains("clutch"))originalClass="clutch";
		if(filepath.contains("hobo"))originalClass="hobo";
		if(filepath.contains("flats"))originalClass="flats";
		if(filepath.contains("pumps"))originalClass="pumps";
	}
	public String printDecision(){
		String toprint="";
		toprint+="FILENAME: "+filepath+"\n";
		toprint+="ClutchProbs: ["+clutchProbs[0]+","+clutchProbs[1]+"]\n";
		toprint+="ClutchResult: "+clutchResult+"\n";
		toprint+="HoboProbs: ["+hoboProbs[0]+","+hoboProbs[1]+"]\n";
		toprint+="HoboResult: "+hoboResult+"\n";
		toprint+="FlatsProbs: ["+flatsProbs[0]+","+flatsProbs[1]+"]\n";
		toprint+="FlatsResult: "+flatsResult+"\n";
		toprint+="PumpsProbs: ["+pumpsProbs[0]+","+pumpsProbs[1]+"]\n";
		toprint+="PumpsResult: "+pumpsResult+"\n";
		toprint+="DECISION: "+classDecision+"\n";
		toprint+="isInteresting: "+interesting+"\n";
		return toprint;
		
	}
}

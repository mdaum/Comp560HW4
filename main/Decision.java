package main;

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
		double probClutch = (clutchResult == 1) ? Math.max(clutchProbs[0], clutchProbs[1]) : Math.min(clutchProbs[0], clutchProbs[1]);
		double probHobo = (hoboResult == 1) ? Math.max(hoboProbs[0], hoboProbs[1]) : Math.min(hoboProbs[0], hoboProbs[1]);
		double probFlats = (flatsResult == 1) ? Math.max(flatsProbs[0], flatsProbs[1]) : Math.min(flatsProbs[0], flatsProbs[1]);
		double probPumps = (pumpsResult == 1) ? Math.max(pumpsProbs[0], pumpsProbs[1]) : Math.min(pumpsProbs[0], pumpsProbs[1]);
		
		double max1 = Math.max(probClutch,probHobo);
		classDecision = (max1 == probClutch) ? "clutch" : "hobo";

		double max2=Math.max(probPumps, probFlats);
		String contender = (max2 == probFlats) ? "flats" : "pumps";
		
		if(max2>max1)classDecision=contender;
		
		if(!(filepath.contains(classDecision)))interesting=true;
	}
}

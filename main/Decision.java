package main;

public class Decision {
	String filepath;
	String classDecision;
	double[] clutchProbs;
	double clutchResult;
	double[] hoboProbs;
	double hoboResult;
	double[] FlatsProbs;
	double FlatsResult;
	double[] PumpsProbs;
	double PumpsResult;
	boolean interesting;

	public Decision(double[] clutchProbs, double clutchResult,
			double[] hoboProbs, double hoboResult, double[] FlatsProbs,
			double FlatsResult, double[] PumpsProbs, double PumpsResult,
			String filepath) {
		this.clutchProbs=clutchProbs;
		this.clutchResult=clutchResult;
		this.hoboProbs=hoboProbs;
		this.hoboResult=hoboResult;
		this.FlatsProbs=FlatsProbs;
		this.FlatsResult=FlatsResult;
		this.PumpsProbs=PumpsProbs;
		this.PumpsResult=PumpsResult;
		this.filepath=filepath;
		this.interesting=false;
	}
	public void makeDecision(){
		double probClutch;
		if(clutchResult==1)probClutch=Math.max(clutchProbs[0], clutchProbs[1]);
		else probClutch=Math.min(clutchProbs[0], clutchProbs[1]);
		double probHobo;
		if(hoboResult==1)probHobo=Math.max(hoboProbs[0], hoboProbs[1]);
		else probHobo=Math.min(hoboProbs[0], hoboProbs[1]);
		double probFlats;
		if(FlatsResult==1)probFlats=Math.max(FlatsProbs[0], FlatsProbs[1]);
		else probFlats=Math.min(FlatsProbs[0], FlatsProbs[1]);
		double probPumps;
		if(PumpsResult==1)probPumps=Math.max(PumpsProbs[0], PumpsProbs[1]);
		else probPumps=Math.min(PumpsProbs[0], PumpsProbs[1]);
		double max1=Math.max(probClutch,probHobo);
		if(max1==probClutch)classDecision="clutch";
		else classDecision="hobo";
		double max2=Math.max(probPumps, probFlats);
		String contender;
		if(max2==probFlats)contender="flats";
		else contender="pumps";
		if(max2>max1)classDecision=contender;
		if(!(filepath.contains(classDecision)))interesting=true;
	}
}

package testingResults;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;


/*
 * Optimal Parameters....
 * Linear Both: C=10000
 * RBF TinyImage: C=2^13, gamma=2^-25
 * RBF Histogram: C=2, gamma=2^7
 * */
public class placeholder {   
	
	static int labelcount=0; //Number of actual labels 
	static int numInteresting=0;
	
	public static void main(String[] args){
		boolean isHistogram = (Integer.parseInt(args[0])==1)? true:false; //boolean representing whether we are using a histogram or not
		boolean isLinear = (Integer.parseInt(args[1])==1)? true:false; //boolean representing whether we are using a linear kernel
		
		int numTrainingItems = 2759; //number of training images, old is 2759
		int vectorLength;
		if(!isHistogram)
		{
			vectorLength = 3072; //length of vector representation of image
		}
		else
		{
			vectorLength = 512; //length of histogram representation of images (8x8x8?)
		}
		
		
		//Initialization of SVM problems for each class, for the vector representation
		System.out.println("Constructing svm problem(s)");
		svm_problem Clutch = constructProblem(numTrainingItems, vectorLength);
		svm_problem Hobo = constructProblem(numTrainingItems, vectorLength);
		svm_problem Flats = constructProblem(numTrainingItems, vectorLength);
		svm_problem Pumps = constructProblem(numTrainingItems, vectorLength);


		//populate each of the svm_problem variables with data
		//1000,782,996,745
		//346
		int oldLabels = 0;
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "bags_clutch", 305, 1000, isHistogram);
		System.out.println(labelcount - oldLabels);
		oldLabels = labelcount;
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "bags_hobo", 85, 782, isHistogram);
		System.out.println(labelcount - oldLabels);
		oldLabels = labelcount;
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "womens_flats", 299, 996, isHistogram);
		System.out.println(labelcount - oldLabels);
		oldLabels = labelcount;
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "womens_pumps", 48, 745, isHistogram);	
		System.out.println(labelcount - oldLabels);
		oldLabels = labelcount;
		System.out.println("Done. Teaching svm(s)");
		
		
			//Build the svm_paramater object
			svm_parameter param = constructParameter(isHistogram,isLinear);
			
			
			//Train the svm_problems and produce svm_models for each class
			svm_model trainedClutch=svm.svm_train(Clutch,param);
			System.out.println("Trained Clutch");
			svm_model trainedHobo=svm.svm_train(Hobo, param);
			System.out.println("Trained Hobo");
			svm_model trainedFlats=svm.svm_train(Flats, param);
			System.out.println("Trained Flats");
			svm_model trainedPumps=svm.svm_train(Pumps,param);
			System.out.println("Trained Pumps");
			
			//Test the model for each class
			ArrayList<Decision>decisions=new ArrayList<Decision>(); 
			//1000,782,996,745
			evaluateClass(trainedClutch, trainedHobo, trainedFlats, trainedPumps, "bags_clutch", "Testing", 2, 1000, decisions, isHistogram,args);
			evaluateClass(trainedClutch, trainedHobo, trainedFlats, trainedPumps, "bags_hobo", "Testing", 2, 1000, decisions, isHistogram,args); //will just have lots of continues here
			evaluateClass(trainedClutch, trainedHobo, trainedFlats, trainedPumps, "womens_flats", "Testing", 2, 1000, decisions, isHistogram,args); 
			evaluateClass(trainedClutch, trainedHobo, trainedFlats, trainedPumps, "womens_pumps", "Testing", 2, 1000, decisions, isHistogram,args);
			System.out.println("Done with Testing: num interesting="+numInteresting);
			int clutchWrong=0;
			int hoboWrong=0;
			int flatsWrong=0;
			int pumpsWrong=0;
			int[][]confusionMatrix=new int[4][4];
			for (Decision decision : decisions) {
				if(decision.interesting){
					System.out.println(""+decision.filepath+": classified as "+decision.classDecision);
					if(decision.originalClass.equals("clutch")){
						clutchWrong++;
						if(decision.classDecision.equals("hobo"))confusionMatrix[0][1]++;
						else if(decision.classDecision.equals("flats"))confusionMatrix[0][2]++;
						else confusionMatrix[0][3]++;
					}
					if(decision.originalClass.equals("hobo")){
						hoboWrong++;
						if(decision.classDecision.equals("clutch"))confusionMatrix[1][0]++;
						else if(decision.classDecision.equals("flats"))confusionMatrix[1][2]++;
						else confusionMatrix[1][3]++;
					}
					if(decision.originalClass.equals("flats")){
						flatsWrong++;
						if(decision.classDecision.equals("clutch"))confusionMatrix[2][0]++;
						else if(decision.classDecision.equals("hobo"))confusionMatrix[2][1]++;
						else confusionMatrix[2][3]++;
					}
					if(decision.originalClass.equals("pumps")){
						pumpsWrong++;
						if(decision.classDecision.equals("clutch"))confusionMatrix[3][0]++;
						else if(decision.classDecision.equals("hobo"))confusionMatrix[3][1]++;
						else confusionMatrix[3][2]++;
					}
				}
				else if(decision.classDecision.equals("clutch"))confusionMatrix[0][0]++;
				else if(decision.classDecision.equals("hobo"))confusionMatrix[1][1]++;
				else if(decision.classDecision.equals("flats"))confusionMatrix[2][2]++;
				else confusionMatrix[3][3]++;	
			}
			double clutchAccuracy=1-((double)clutchWrong)/(double)(300);
			double hoboAccuracy=1-((double)hoboWrong)/(double)(299);
			double flatsAccuracy=1-((double)flatsWrong)/(double)(299);
			double pumpsAccuracy=1-((double)pumpsWrong)/(double)(299);
			double overallAccuracy = 1-((double)numInteresting)/(double)1197; //Denominator is number of "testing" documents
			System.out.println("Testing docs looked at: "+decisions.size());
			System.out.println("# Clutch looked at: 300");
			System.out.println("# Hobo looked at: 299");
			System.out.println("# Flats looked at: 299");
			System.out.println("# Pumps looked at: 299");
			System.out.println("clutchWrong: "+clutchWrong);
			System.out.println("hoboWrong: "+hoboWrong);
			System.out.println("flatsWrong: "+flatsWrong);
			System.out.println("pumpsWrong: "+pumpsWrong);
			System.out.println("Clutch accuracy: "+clutchAccuracy);
			System.out.println("Hobo accuracy: "+hoboAccuracy);
			System.out.println("Flats accuracy: "+flatsAccuracy);
			System.out.println("Pumps accuracy: "+pumpsAccuracy);
			System.out.println("Overall accuracy: "+overallAccuracy);
			printMatrix(confusionMatrix);
			System.out.println("done");
			
			
		}
	private static void printMatrix(int[][] confusionMatrix){
		System.out.println("Confusion Matrix");
		System.out.println();
		System.out.println("  C   H   F   P");
		String cLine="C";
			for (int i : confusionMatrix[0]) {
				cLine+=" "+i;
			}
		System.out.println(cLine);
		String hLine="H";
		for (int i : confusionMatrix[1]) {
			hLine+=" "+i;
		}
		System.out.println(hLine);
		String fLine="F";
		for (int i : confusionMatrix[2]) {
			fLine+=" "+i;
		}
		System.out.println(fLine);
		String pLine="P";
		for (int i : confusionMatrix[3]) {
			pLine+=" "+i;
		}
	System.out.println(pLine);
	}
	
	//returns a 8x8x8 triple-array histogram
	private static double[][][] histogram(String imageName)throws Exception{
		double[][][] histogram = new double[8][8][8];
		//try{
			BufferedImage image = ImageIO.read(new File(imageName));
			double imageSize=image.getHeight()*image.getWidth();
			for(int i=0;i<image.getWidth();i++){
				for(int j=0;j<image.getHeight();j++){
					Color c = new Color(image.getRGB(i, j));
					//since these int values range from 0:255, dividing by 32 makes them range from 0:7
					histogram[c.getRed()/32][c.getGreen()/32][c.getBlue()/32]++;
				}
			}
			for (double[][] ds : histogram) {
				for (double[] ds2 : ds) {
					for(int i=0;i<8;i++){
						ds2[i]=ds2[i]/imageSize;
					}
					
				}
				
			}
		//}catch(IOException e){}
		return histogram;
	}
	
	private static double[] linearizeHistogram(double[][][] histogram)
	{
		double[] result = new double[histogram.length * histogram[0].length * histogram[0][0].length];
		int index = 0;
		for(int i = 0;i < histogram.length;i++)
		{
			for(int j = 0; j < histogram[0].length;j++)
			{
				for(int k = 0;k < histogram[0][0].length;k++)
				{
					result[index] = histogram[i][j][k];
					index++;
				}
			}
		}
		return result;
	}
	//this is how we turn those 32x32 jpegs into vectors
	private static double[] rgbVector(String imageName)throws Exception{
		BufferedImage small = shrinkImage(imageName);
		double[] smallRGBVector = new double[32*32*3];
		for(int i=0;i<32;i++){
			for(int j=0;j<32;j++){
				Color c = new Color(small.getRGB(i,j));
				smallRGBVector[i*32+j]=c.getRed();
				smallRGBVector[i*32+j+32*32]=c.getGreen();
				smallRGBVector[i*32+j+32*32*2]=c.getBlue();
			}
		}
		return smallRGBVector;
	}
	//this is how we reduce the jpegs into 32x32 jpegs
	private static BufferedImage shrinkImage(String imageName){
		try{
			BufferedImage originalImage = ImageIO.read(new File(imageName));
			BufferedImage resizedImage = new BufferedImage(32, 32,originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType());
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, 32, 32, null);
			g.dispose();
			return resizedImage;
		}
		catch(IOException e)
		{
			return null;
		}
    }
	
	//Constructs an svm_problem with the given arguments
	private static svm_problem constructProblem(int numTrainingItems, int lengthOfItems)
	{
		svm_problem problem=new svm_problem();
		problem.l = numTrainingItems;
		problem.y = new double[numTrainingItems];
		problem.x = new svm_node[numTrainingItems][lengthOfItems];
		return problem;
	}
	
	//Constructs an svm_parameter with the given arguments
	private static svm_parameter constructParameter(boolean isHistogram, boolean isLinear)
	{
		svm_parameter param = new svm_parameter();
		param.probability = 1;
	    param.gamma = (isHistogram)? Math.pow(2, 7):Math.pow(2, -25);  //right now just default gamma if commented
	    param.nu = 0.5;
	    if(isLinear){
	    	param.C=10000;
	    }
	    else if(isHistogram){
	    	param.C=2;
	    }
	    else param.C=Math.pow(2, 13);
	    param.svm_type = svm_parameter.C_SVC;
	    param.kernel_type = (isLinear) ? svm_parameter.LINEAR : svm_parameter.RBF;     
	    param.cache_size = 20000;
	    param.eps = 0.001;
	    
	    return param;
	}
			
			                                        
			                              
	//fills the arrays of the svm_problem objects to prep them for training
	private static void fillTrainingArrays(svm_problem Clutch, svm_problem Hobo, svm_problem Flats, svm_problem Pumps, String filePrefix,
										  int lowerBound, int upperBound, boolean isHistogram)
	{
		//some images are missing, so we just skip over not found images, we know the image numbers of start and end though
		for(int i=lowerBound;i<upperBound;i++){ 
			try{
			 double[] v;
			 
			 if(!isHistogram)
			 {
				 v=rgbVector("Training/img_" + filePrefix +"_"+i+".jpg");
			 }
			 else
			 {
				 v=linearizeHistogram(histogram("Training/img_" + filePrefix +"_"+i+".jpg"));
			 }
			 Clutch.y[labelcount] = filePrefix.contains("clutch") ? 1 : 0;
			 Hobo.y[labelcount] = filePrefix.contains("hobo") ? 1 : 0;
			 Flats.y[labelcount] = filePrefix.contains("flats") ? 1 : 0;
			 Pumps.y[labelcount] = filePrefix.contains("pumps") ? 1 : 0;
			 
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch.x[labelcount][j]=toput;
				 Hobo.x[labelcount][j]=toput;
				 Flats.x[labelcount][j]=toput;
				 Pumps.x[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
		}
	}
	
	//trains for a particular class
	private static void evaluateClass(svm_model trainedClutch, svm_model trainedHobo, svm_model trainedFlats, svm_model trainedPumps, String filePrefix, String folder,
											  int lowerBound, int upperBound, ArrayList<Decision> decisions, boolean isHistogram,String[]args)
	{
		for(int j=lowerBound;j<upperBound;j++){ //looking at testing/clutch stuff first
			svm_node[] testingVector;
			try {
				double[] testImage;
				if(!isHistogram)
				{
					testImage = rgbVector(folder+"/img_" + filePrefix + "_"+j+".jpg");
					testingVector=new svm_node[3072];
				}
				else
				{
					testImage = linearizeHistogram(histogram(folder + "/img_" + filePrefix + "_"+j+".jpg"));
					testingVector=new svm_node[512];
				}
				
				for(int i=0;i<testImage.length;i++){
					svm_node toput=new svm_node();
					toput.index=i;
					toput.value=testImage[i];
					testingVector[i]=toput;
				}
				double[] clutchProbs=new double[2];
				double clutchResult=svm.svm_predict_probability(trainedClutch,testingVector,clutchProbs);
				double[] hoboProbs=new double[2];
				double hoboResult=svm.svm_predict_probability(trainedHobo,testingVector,hoboProbs);
				double[] FlatsProbs=new double[2];
				double FlatsResult=svm.svm_predict_probability(trainedFlats,testingVector,FlatsProbs);
				double[] PumpsProbs=new double[2];
				double PumpsResult=svm.svm_predict_probability(trainedPumps,testingVector,PumpsProbs);
				Decision d = new Decision(clutchProbs,clutchResult,hoboProbs,hoboResult,FlatsProbs,FlatsResult,PumpsProbs,PumpsResult,folder+"/img_" + filePrefix + "_"+j+".jpg");
				d.makeDecision();
				decisions.add(d);
				if(d.interesting)numInteresting++;
			 }catch (Exception e) {
				continue;
			}
			}
		if(Integer.parseInt(args[2])==1){
		//if you want to record entire run uncomment this....
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(args[3], true)))) {
		    out.println("Start of New Class Evaluation");
		    for (Decision decision : decisions) {
				out.println(decision.printDecision());
			}
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		}
	}
	
}

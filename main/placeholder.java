package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class placeholder {   
	
	static int labelcount=0; //Number of actual labels
	
	public static void main(String[] args){
		
		int numTrainingItems = 2759; //number of training images
		int vectorLength = 3072; //length of vector representation of image
		int histogramLength = 512; //length of histogram representation of images (8x8x8?)
		
		//Parameters for SVM
		int probability = 1;
	    double gamma = 0.5;
	    double nu = 0.5;
	    double C = 1;
	    int svm_type = svm_parameter.C_SVC;
	    int kernel_type = svm_parameter.LINEAR;       
	    double cache_size = 20000;
	    double eps = 0.001;
		
		
		//Initialization of SVM problems for each class, for the vector representation
		System.out.println("Constructing svm problem(s)");
		svm_problem Clutch = constructProblem(numTrainingItems, vectorLength);
		svm_problem Hobo = constructProblem(numTrainingItems, vectorLength);
		svm_problem Flats = constructProblem(numTrainingItems, vectorLength);
		svm_problem Pumps = constructProblem(numTrainingItems, vectorLength);


		//populate each of the svm_problem variables with data
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "bags_clutch", 305, 1000);
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "bags_hobo", 85, 782);
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "womens_flats", 299, 996);
		fillTrainingArrays(Clutch, Hobo, Flats, Pumps, "womens_pumps", 48, 745);		 
		System.out.println("Done. Teaching svm(s)");
		
		//Build the svm_paramater object
		svm_parameter param = constructParameter(probability, gamma, nu, C, svm_type, kernel_type, cache_size, eps);
		
		
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
		int numInteresting=0;
		for(int j=2;j<305;j++){ //looking at testing/clutch stuff first
		try {
			int[]testImageClutch=rgbVector("Testing/img_bags_clutch_"+j+".jpg");
			svm_node[] goo=new svm_node[3072];
			for(int i=0;i<testImageClutch.length;i++){
				svm_node toput=new svm_node();
				toput.index=i;
				toput.value=testImageClutch[i];
				goo[i]=toput;
			}
			double[] clutchProbs=new double[2];
			double clutchResult=svm.svm_predict_probability(trainedClutch,goo,clutchProbs);
			double[] hoboProbs=new double[2];
			double hoboResult=svm.svm_predict_probability(trainedHobo,goo,hoboProbs);
			double[] FlatsProbs=new double[2];
			double FlatsResult=svm.svm_predict_probability(trainedFlats,goo,FlatsProbs);
			double[] PumpsProbs=new double[2];
			double PumpsResult=svm.svm_predict_probability(trainedPumps,goo,PumpsProbs);
			Decision d = new Decision(clutchProbs,clutchResult,hoboProbs,hoboResult,FlatsProbs,FlatsResult,PumpsProbs,PumpsResult,"Testing/img_bags_clutch_"+j+".jpg");
			d.makeDecision();
			decisions.add(d);
			if(d.interesting)numInteresting++;
		 }catch (Exception e) {
			continue;
		}
		}
		System.out.println("Done with Clutch: num interesting="+numInteresting);
		for (Decision decision : decisions) {
			if(decision.interesting)System.out.println(""+decision.filepath+": classified as "+decision.classDecision);
		}
		
		
	}
	//returns a 8x8x8 triple-array histogram, which is NOT NORMALIZED
	private static double[][][] histogram(String imageName){
		double[][][] histogram = new double[8][8][8];
		try{
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
		}catch(IOException e){}
		return histogram;
	}
	//this is how we turn those 32x32 jpegs into vectors
	private static int[] rgbVector(String imageName)throws Exception{
		BufferedImage small = shrinkImage(imageName);
		int[] smallRGBVector = new int[32*32*3];
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
	//I'm just using this to test methods. Currently testing only the following:
	//shrinkImage() creates a 32x32 version of an image
	//rgbVector() is invertible
	private static void test() throws Exception{
		BufferedImage original = shrinkImage("images/img_bags_clutch_"+101+".jpg");
		int[] v = rgbVector("images/img_bags_clutch_"+101+".jpg");
		BufferedImage recreation = new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<32;i++){
			for(int j=0;j<32;j++){
				Color c = new Color(v[i*32+j],v[i*32+j+32*32],v[i*32+j+32*32*2]);
				recreation.setRGB(i, j, c.getRGB());
			}
		}
		for(int i=0;i<32;i++){
			for(int j=0;j<32;j++){
				if(original.getRGB(i,j)!=recreation.getRGB(i,j))
					System.out.println("well would you look at that");
			}
		}
		try{
			ImageIO.write(original,"jpg",new File("a1.jpg"));
			ImageIO.write(recreation,"jpg",new File("a2.jpg"));
		}catch(IOException e){}
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
	private static svm_parameter constructParameter(int probability, double gamma, double nu, double C, int svm_type, int kernel_type, 
													double cache_size, double eps)
	{
		svm_parameter param = new svm_parameter();
		param.probability = probability;
	    param.gamma = gamma;
	    param.nu = nu;
	    param.C = C;
	    param.svm_type = svm_type;
	    param.kernel_type = kernel_type;       
	    param.cache_size = cache_size;
	    param.eps = eps;
	    
	    return param;
	}
			
			                                        
			                              
	//fills the arrays of the svm_problem objects to prep them for training
	private static void fillTrainingArrays(svm_problem Clutch, svm_problem Hobo, svm_problem Flats, svm_problem Pumps, String filePrefix,
										  int lowerBound, int upperBound)
	{
		//some images are missing, so we just skip over not found images, we know the image numbers of start and end though
		for(int i=lowerBound;i<upperBound;i++){ 
			try{
			 int [] v=rgbVector("Training/img_" + filePrefix +"_"+i+".jpg");
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
	
}

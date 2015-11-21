package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class placeholder {

	public static void main(String[] args){
		System.out.println("Constructing svm problem(s)");
		svm_problem Clutch=new svm_problem();
		Clutch.l=2759;//actual number of images
		double[] Clutch_y=new double[2759]; //image label
		svm_node[][] Clutch_z=new svm_node[2759][3072];
		svm_problem Hobo=new svm_problem();
		Hobo.l=2759;
		double[] Hobo_y=new double[2759];
		svm_node[][] Hobo_z=new svm_node[2759][3072];
		svm_problem Flats=new svm_problem();
		Flats.l=2759;
		double[] Flats_y=new double[2759];
		svm_node[][]Flats_z=new svm_node[2759][3072];
		svm_problem Pumps=new svm_problem();
		Pumps.l=2759;
		double[] Pumps_y=new double[2759];
		svm_node[][]Pumps_z=new svm_node[2759][3072];
		int labelcount=0;
		for(int i=305;i<1000;i++){ //some images are missing, so we just skip over not found images, we know the image numbers of start and end though
			try{
			 int [] v=rgbVector("Training/img_bags_clutch_"+i+".jpg");
			 Clutch_y[labelcount]=1;//yes it is
			 Hobo_y[labelcount]=0;
			 Flats_y[labelcount]=0;
			 Pumps_y[labelcount]=0;
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch_z[labelcount][j]=toput;
				 Hobo_z[labelcount][j]=toput;
				 Flats_z[labelcount][j]=toput;
				 Pumps_z[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
		}
		for(int i=85;i<782;i++){ //some images are missing, so we just skip over not found images, we know the image numbers of start and end though
			try{
			 int [] v=rgbVector("Training/img_bags_hobo_"+i+".jpg");
			 Clutch_y[labelcount]=0;
			 Hobo_y[labelcount]=1;//yes it is
			 Flats_y[labelcount]=0;
			 Pumps_y[labelcount]=0;
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch_z[labelcount][j]=toput;
				 Hobo_z[labelcount][j]=toput;
				 Flats_z[labelcount][j]=toput;
				 Pumps_z[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
		}
		for(int i=299;i<996;i++){ //some images are missing, so we just skip over not found images, we know the image numbers of start and end though
			try{
			 int [] v=rgbVector("Training/img_womens_flats_"+i+".jpg");
			 Clutch_y[labelcount]=0;
			 Hobo_y[labelcount]=0;
			 Flats_y[labelcount]=1;//yes it is
			 Pumps_y[labelcount]=0;
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch_z[labelcount][j]=toput;
				 Hobo_z[labelcount][j]=toput;
				 Flats_z[labelcount][j]=toput;
				 Pumps_z[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
		}
		for(int i=48;i<745;i++){ //some images are missing, so we just skip over not found images, we know the image numbers of start and end though
			try{
			 int [] v=rgbVector("Training/img_womens_pumps_"+i+".jpg");
			 Clutch_y[labelcount]=0;
			 Hobo_y[labelcount]=0;
			 Flats_y[labelcount]=0;
			 Pumps_y[labelcount]=1;//yes it is
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch_z[labelcount][j]=toput;
				 Hobo_z[labelcount][j]=toput;
				 Flats_z[labelcount][j]=toput;
				 Pumps_z[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
		}
		Clutch.y=Clutch_y;
		Clutch.x=Clutch_z; //oops
		Hobo.y=Hobo_y;
		Hobo.x=Hobo_z;
		Flats.y=Flats_y;
		Flats.x=Flats_z;
		Pumps.y=Pumps_y;
		Pumps.x=Pumps_z;
		System.out.println("Done. Teaching svm(s)");
		svm_parameter param=new svm_parameter();
		param.probability = 1;
	    param.gamma = 0.5;
	    param.nu = 0.5;
	    param.C = 1;
	    param.svm_type = svm_parameter.C_SVC;
	    param.kernel_type = svm_parameter.LINEAR;       
	    param.cache_size = 20000;
	    param.eps = 0.001;   
		svm_model trainedClutch=svm.svm_train(Clutch,param);
		System.out.println("Trained Clutch");
		svm_model trainedHobo=svm.svm_train(Hobo, param);
		System.out.println("Trained Hobo");
		svm_model trainedFlats=svm.svm_train(Flats, param);
		System.out.println("Trained Flats");
		svm_model trainedPumps=svm.svm_train(Pumps,param);
		System.out.println("Trained Pumps");
		
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
			// TODO Auto-generated catch block
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
}

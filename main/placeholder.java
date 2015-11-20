package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class placeholder {

	public static void main(String[] args){
		svm_problem Clutch=new svm_problem();
		Clutch.l=2759;//actual number of images
		double[] Clutch_y=new double[2759]; //image label
		svm_node[][] Clutch_z=new svm_node[2759][3072];
		int labelcount=0;
		for(int i=305;i<1000;i++){ //some images are missing, so we just skip over not found images, we know the image numbers of start and end though
			try{
			 int [] v=rgbVector("Training/img_bags_clutch_"+i+".jpg");
			 Clutch_y[labelcount]=1;//yes it is...
			 for (int j=0;j<v.length;j++){
				 svm_node toput=new svm_node();
				 toput.index=j;
				 toput.value=v[j];
				 Clutch_z[labelcount][j]=toput;
			 }
			 labelcount++;//increment at end!
			 
			}
			catch(Exception e){
				continue;
			}
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
	private static void test(){
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

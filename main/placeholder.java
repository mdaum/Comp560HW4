package main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class placeholder {

	public static void main(String[] args){
		/*System.out.println("hey");
		ImageTest.ResizeAll();
		System.out.println("done");*/
		
		/*String imageName="";
		for(int i=1;i<1000;i++){//the images range from 1-999
			imageName="images/img_bags_clutch_"+i+".jpg";
		}*/
		
	}
	//this is how we turn those 32x32 jpegs into vectors
	private static int[] rgbVector(String imageName){
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
	//both methods do indeed work; shrinkImage creates a 32x32 version of an image,
	//and rgbVector is invertible
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

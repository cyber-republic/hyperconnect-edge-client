package com.hyper.connect.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

import java.util.Hashtable;


public class QRCodeUtil{
	
	public static Image getQRCodeImage(String content, int width, int height){
		BufferedImage bufferedImage=null;
		try{
			Hashtable<EncodeHintType, String> hints=new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.ERROR_CORRECTION, "H");
			hints.put(EncodeHintType.MARGIN, "2");
			
			BitMatrix bitMatrix=new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			bufferedImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			bufferedImage.createGraphics();

			Graphics2D graphics=(Graphics2D)bufferedImage.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, width, height);
			graphics.setColor(Color.BLACK);
			
			for(int i=0;i<height;i++){
				for(int j=0;j<width;j++){
					if(bitMatrix.get(i, j)){
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
		}
		catch(WriterException ex){}
		Image qrView=SwingFXUtils.toFXImage(bufferedImage, null);
		return qrView;
	}
	
}
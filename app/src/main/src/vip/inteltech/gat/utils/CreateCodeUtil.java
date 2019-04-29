package vip.inteltech.gat.utils;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class CreateCodeUtil {
	private static final int IMAGE_HALFWIDTH = 40;//宽度值，影响中间图片大小
	/**
	 * 生成二维码
	 * @param string 二维码中包含的文本信息
	 * @param mBitmap logo图片
	 * @param format  编码格式
	 * @return Bitmap 位图
	 * @throws WriterException
	 */
	public static Bitmap createCode(String string,Bitmap mBitmap, BarcodeFormat format)
			throws WriterException {
		Matrix m = new Matrix();
		float sx = (float) 2 * IMAGE_HALFWIDTH / mBitmap.getWidth();
		float sy = (float) 2 * IMAGE_HALFWIDTH
				/ mBitmap.getHeight();
		m.setScale(sx, sy);//设置缩放信息
		//将logo图片按martix设置的信息缩放
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight(), m, false);
		
		MultiFormatWriter writer = new MultiFormatWriter();//
		Hashtable<EncodeHintType, String> hst = new Hashtable<EncodeHintType, String>();
		hst.put(EncodeHintType.CHARACTER_SET, "UTF-8");//设置字符编码
		BitMatrix matrix = writer.encode(string, format, 700, 700, hst);//生成二维码矩阵信息
		int width = matrix.getWidth();//矩阵高度
		int height = matrix.getHeight();//矩阵宽度
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];//定义数组长度为矩阵高度*矩阵宽度，用于记录矩阵中像素信息
		for (int y = 0; y < height; y++) {//从行开始迭代矩阵
			for (int x = 0; x < width; x++) {//迭代列
				if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
						&& y > halfH - IMAGE_HALFWIDTH
						&& y < halfH + IMAGE_HALFWIDTH) {//次处位置用于存放图片信息
					pixels[y * width + x] = mBitmap.getPixel(x - halfW
							+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);//记录图片每个像素信息
				} else {
					if (matrix.get(x, y)) {//如果有黑块点，记录信息
						pixels[y * width + x] = 0xff000000;//记录黑块信息
					}
				}

			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}

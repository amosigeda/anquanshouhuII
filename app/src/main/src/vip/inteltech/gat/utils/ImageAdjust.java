package vip.inteltech.gat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

public class ImageAdjust {
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return degree;
		}
		return degree;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		if (degrees == 0 || null == bitmap) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		if (null != bitmap) {
			bitmap.recycle();
		}
		return bmp;
	}

	private static final int DEFAULT_REQUIRED_SIZE = 70;

	public static Bitmap decodeFile(File f, int size) {
		try {
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, option);
			stream1.close();
			final int REQUIRED_SIZE = size > 0 ? size : DEFAULT_REQUIRED_SIZE;
			int width_tmp = option.outWidth, height_tmp = option.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			if (scale >= 2) {
				scale /= 2;
			}
			BitmapFactory.Options option2 = new BitmapFactory.Options();
			option2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, option2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static File rotateFile;

	private static void compressHeadPhoto(final Bitmap bm, String photoName) {
		rotateFile = new File(Environment.getExternalStorageDirectory(), photoName);
		try {
			bm.compress(Bitmap.CompressFormat.PNG, 50, new FileOutputStream(rotateFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void Work(String photoName){
		String path = Environment.getExternalStorageDirectory() + "/" + photoName;
		compressHeadPhoto(rotateBitmap(decodeFile(new File(Environment.getExternalStorageDirectory(), photoName), 100), readPictureDegree(path)), photoName);
	}
}

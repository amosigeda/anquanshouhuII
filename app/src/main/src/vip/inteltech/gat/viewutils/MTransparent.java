package vip.inteltech.gat.viewutils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MTransparent extends FrameLayout {

	 

    private int width = -1;

    private int height = -1;

    private Bitmap bitmap;

 

    public MTransparent(Context context) {

        super( context);

    }

 

    public MTransparent(Context context, AttributeSet attrs, int defStyle) {

        super( context, attrs, defStyle);

    }

 

    public MTransparent(Context context, AttributeSet attrs) {

        super( context, attrs);

    }

 
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        if(action != MotionEvent.ACTION_DOWN) {

            return super.onTouchEvent( event);

        }

        int x = (int)event.getX();

        int y = (int)event.getY();

        if(width == -1 || height == -1) {
        	
        	width = getWidth();
        	
        	height = getHeight();
            //Drawable drawable = ((StateListDrawable)getBackground()).getCurrent();
        	Drawable drawable = getBackground();
            //bitmap = ((BitmapDrawable)drawable).getBitmap();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888: Bitmap.Config.RGB_565;
            try{
            	bitmap = Bitmap.createBitmap(width,height,config);
                //System.out.println(bytesToHexString(Bitmap2Bytes(bitmap)));
                Canvas canvas = new Canvas(bitmap);   
                drawable.setBounds(0, 0, width, height);   
                drawable.draw(canvas);
                //System.out.println(bytesToHexString(Bitmap2Bytes(bitmap)));
            }catch(OutOfMemoryError e){
            	e.printStackTrace();
            }

        }

        if(null == bitmap || x < 0 || y < 0 || x >= width || y >= height) {

            return false;

        }

        int pixel = bitmap.getPixel( x, y);

        if(Color.TRANSPARENT != pixel) {
        	//System.out.println("非透明区域");
            return true;

        }

        return super.onTouchEvent( event);

    }
    /**
	 * 初始化AMap对象
	 */
    public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
		return baos.toByteArray();
	}
    public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

}
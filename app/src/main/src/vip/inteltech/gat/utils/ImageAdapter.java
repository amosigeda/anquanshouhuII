package vip.inteltech.gat.utils;

import java.util.List;

import vip.inteltech.coolbaby.R;
import vip.inteltech.gat.model.AlbumModel;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAdapter extends BaseAdapter
{
    private Context mContext;
    
    private List<AlbumModel> mAlbumList;
    
    public ImageAdapter(Context mContext, List<AlbumModel> mAlbumList)
    {
        super();
        this.mContext = mContext;
        this.mAlbumList = mAlbumList;
    }
    
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mAlbumList.size();
    }
    
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
    
    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
    	LayoutInflater mInflater = LayoutInflater.from(mContext);
    	convertView = mInflater.inflate(
				R.layout.album_item, null);
    	ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
    	ImageLoader.getInstance().displayImage(
        		Contents.IMAGEVIEW_URL + mAlbumList.get(position).getPath(), iv,
				new AnimateFirstDisplayListener());
       /* ImageView iv = new ImageView(mContext);
        ImageLoader.getInstance().displayImage(
        		Contents.IMAGEVIEW_URL + mAlbumList.get(position).getPath(), iv,
				new AnimateFirstDisplayListener());
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        Gallery.LayoutParams layoutParam = new Gallery.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParam.setMargins(5, 5, 5, 5);
        iv.setLayoutParams(layoutParam);*/
        
        return convertView;
    }
    
}
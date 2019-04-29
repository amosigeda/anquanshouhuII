package vip.inteltech.gat.chatutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//版本更新
public class DownloadVoice {

	private static URL url=null;
	/**
	 * 
	 * 可下载任意形式的文件
	 * 该函数返回int类型
	 * 返回-1：下载出错
	 * 返回0：下载成功
	 * 返回1：代表文件已存在
	 * 参数：第一个为网络地址链接，第二个为存放SD卡目录(/xx)，第三个为存放的文件名称
	 */

	public static int downloadmp(String urlStr,String path,String fileName)
	{
		InputStream inputstream=null;
		try 
		{
			if(FileUtils.isFileExist(path+fileName))
			{
				return -2;
			}
			else
			{
				inputstream = getInputStreamFromUrl(urlStr);
				File resultFile=FileUtils.write2SDFromInput(path, fileName, inputstream);
				if(resultFile==null)
					return -1;

				//关闭InputStream
				inputstream.close();
			}			
		} 
		catch (IOException e) 
			{
				e.printStackTrace();
				return -1;
			}
/*		finally
		{
			try 
			{
				inputstream.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}*/
		return 0;		
	}
	
	//根据URL获得输入流
	public static InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException,IOException
	{
		url=new URL(urlStr);
		HttpURLConnection urlconn=(HttpURLConnection) url.openConnection();
		//urlconn.connect();
		InputStream inputstream=urlconn.getInputStream();
		return inputstream;
	}
}

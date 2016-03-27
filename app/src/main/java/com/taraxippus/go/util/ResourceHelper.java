package com.taraxippus.go.util;
import android.graphics.*;
import com.taraxippus.go.*;
import java.io.*;

public class ResourceHelper
{
	Main main;
	
	public ResourceHelper(Main main)
	{
		this.main = main;
	}
	
	public String getString(int resourceId)
	{
		InputStream is = main.getResources().openRawResource(resourceId);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String readLine = null;

		StringBuilder file = new StringBuilder();
		
		try 
		{
			while ((readLine = br.readLine()) != null) 
			{
				file.append(readLine);
				file.append("\n");
			}
			
			is.close();
			br.close();

			return file.toString();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Bitmap getBitmap(int res)
	{
		return BitmapFactory.decodeResource(main.getResources(), res);
	}
}

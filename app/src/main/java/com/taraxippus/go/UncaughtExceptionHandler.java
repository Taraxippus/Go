package com.taraxippus.go;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Environment;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler
{
	public final String path;
	private final java.lang.Thread.UncaughtExceptionHandler defaultUEH;
	
	public UncaughtExceptionHandler(String path)
	{
		this.path = Environment.getExternalStorageDirectory() + path;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	public void uncaughtException(Thread t, Throwable e) 
	{
        String timestamp = "crash-" + new SimpleDateFormat("yyyy-MM-dd--hh:mm:ss").format(new Date()).toString();
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = timestamp + ".stacktrace";

        writeToFile(stacktrace, filename);
     
        defaultUEH.uncaughtException(t, e);
    }
	
	private void writeToFile(String stacktrace, String filename) 
	{
        try
		{
			new File(path).mkdirs();
            BufferedWriter bos = new BufferedWriter(new FileWriter(path + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        }
		catch (Exception e)
		{
            e.printStackTrace();
        }
    }
}

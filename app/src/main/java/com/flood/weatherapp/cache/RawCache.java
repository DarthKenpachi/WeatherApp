package com.flood.weatherapp.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.flood.weatherapp.utils.Utils;

import android.content.Context;

public class RawCache {
	
	public static final String PREFIX = "WA_TSCOTT";
	public static final String SEPARATOR = "_";
	public static final long RETENTION_TIME = 1000 * 60 * 5; //5 min
	
	public static File getRoot(Context ctx){
		return ctx.getFilesDir();
	}
	
	public static String generateKey(String type){
		return PREFIX + SEPARATOR + type;
	}
	
	public static void cache(Context ctx, String type, String data){
		if (ctx != null && data != null && !"".equals(data)){
			File file = null;
			FileWriter writer = null;
			
			try {
				file = new File(getRoot(ctx), generateKey(type));
				writer = new FileWriter(file);
				writer.write(data);
			} catch(Exception e){
				
			} finally{
				if (writer != null){
					try {
						writer.close();
					} catch(Exception e) {
						
					}
				}
			}
		}
	}
	
	public static String fromCache(Context ctx, String type){
		String data = null;
		
		if (ctx != null){
			File file = null;
			FileReader reader = null;
			BufferedReader buf = null;
			
			try {
				file = new File(getRoot(ctx), generateKey(type));
				reader = new FileReader(file);
				buf = new BufferedReader(reader);
				data = buf.readLine();
				
			} catch (Exception e){
				if(reader != null){
					try{
						reader.close();
					} catch(Exception ex){
						
					}
				}
				
				if(buf != null){
					try{
						buf.close();
					} catch(Exception ex){
						
					}
					
				}
				
			}
		}
		
		return data;
	}
	
	public static boolean isInCache(Context ctx, String type){
		boolean isInCache = false;
		
		if(ctx != null){
			File file = new File(getRoot(ctx), generateKey(type));
			
			if (file != null && file.exists()){
				if (!Utils.isConnected(ctx) || System.currentTimeMillis() - file.lastModified() < RETENTION_TIME){
					isInCache = true;
				} else {
					//delete cache
					try{
						file.delete();
					} catch(Exception e){
						
					}
				}
			}
		}
		
		return isInCache;
	}
	
	

}

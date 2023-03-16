package com.spice.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.spice.conf.Redisconfig;
import com.spice.model.Item;


@Service
public class ServiceClass {
	
	
	@Value("${gt.pool.refresh.time.minutes}")
	int gt_pool_refresh;
	
	
	@Value("${gt.key.name}")
	String gtkeyname;
	@Value("${msc.key.name}")
	String msckeyname;
	
	@Value("${gt.file.path}")
	String filepath;
	@Value("${gt.file.name}")
	String filename;
	
	@Autowired
	Redisconfig redisconfig;
	//@Autowired
	//Item item;
	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	//@Autowired
	// private RedisTemplate1<String,Object> redisTemplateOB;
	DateFormat dateFormat;Calendar cal;Date date;
	
	 private HashOperations hashOperations;
	 private HashOperations<String, String, Object> hashOperations1;
	
	@PostConstruct
	public void init() {
		hashOperations = redisTemplate.opsForHash();
		hashOperations1 = redisTemplate.opsForHash();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath+filename));
			String line = reader.readLine();
			while (line != null) {
				
				if(hashOperations.get(gtkeyname,line) != null)
				{
					logger.debug("key already exist:: "+hashOperations.get(gtkeyname,line));
				}
				else
				{
					hashOperations.put(gtkeyname,line, "free");
					logger.debug("key Added: "+hashOperations.get(gtkeyname,line));
				}
				
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	Logger logger = Logger.getLogger(ServiceClass.class);
	/*
	 * ===========================================================
	 * MO-------->prov_number:aparty whitelist_number:bparty
	 * MT-------->prov_number:bparty whitelist_number:aparty 
	 * ===========================================================
	 * */
	public String processStatusRequest(Map<String, String> params) {
		//RequestData rd = null;
		String returnStatus = "ERROR";
		Item notification=null;
		
		if(hashOperations1.get(msckeyname,params.get("MSISDN")) !=null)
		{
			 notification=(Item)hashOperations1.get(msckeyname,params.get("MSISDN"));
			
			cal = Calendar.getInstance();
			//System.out.println("Current Date Time : " + dateFormat.format(cal.getTime()));
			cal.add(Calendar.MINUTE,gt_pool_refresh);
			//System.out.println("Add one minute to current date : " + dateFormat.format(cal.getTime()));
			
			notification.setDate(cal.getTime());
			hashOperations1.put(msckeyname,params.get("MSISDN"), notification);
			returnStatus=notification.getId();
			//notification=(Item)hashOperations1.get("incoming",params.get("MSISDN"));
			
			//System.out.println(notification.getId()+":::::::::::::"+notification.getDate());
			/*hashOperations1.entries("incoming").entrySet().stream().forEach((e) -> {
            
        	Item notification1=(Item)e.getValue();
        	System.out.println(notification1.getDate()+":"+notification1.getId());
      
        	}); */
		}
		else
		{
			
			try {
			hashOperations1.entries(gtkeyname).entrySet().stream().forEach((e) -> {
				
	        	if(e.getValue().equals("free") )
	        	{
	        			cal = Calendar.getInstance();
	        			//System.out.println("Current Date Time : " + dateFormat.format(cal.getTime()));
	        			cal.add(Calendar.MINUTE, gt_pool_refresh);
	        			//System.out.println("Add one minute to current date : " + dateFormat.format(cal.getTime()));
	        			  
	 			 
	        		 Item notification1 =new Item(e.getKey(),cal.getTime()); 
	        		hashOperations1.put(msckeyname,params.get("MSISDN"), notification1);
	        		hashOperations.put(gtkeyname, e.getKey(), "busy");
	       
	        		int a=1/0;
	        	}
	        	}); 
			}
			catch(Exception e)
			{
				
				logger.error(e.toString());
			}
			
			try {
			 notification=(Item)hashOperations1.get(msckeyname,params.get("MSISDN"));
			
			returnStatus=notification.getId();
			}
			catch(Exception e)
			{
				logger.error(e.toString());
				hashOperations1.entries(msckeyname).entrySet().stream().forEach((e1) -> {
					
		        		Item notification2=(Item)e1.getValue();
		        		long curr = new Date().getTime()/(60*1000);
		    			//ReloadNotification notification=(ReloadNotification)e.getValue();
		    			long old = notification2.getDate().getTime()/(60*1000);
		    			long diff = curr - old;
		    			
		    			//System.out.println( e1.getKey()+":::"+notification.getId()+"::"+notification.getDate()+":::"+notification.getDate().getTime()+"::::::::::"+curr+":::"+old+":::"+diff);
		        		if(diff >gt_pool_refresh)
		        		{
		        			
		        			hashOperations1.delete(msckeyname, e1.getKey());
		        			hashOperations.put(gtkeyname, notification2.getId(), "free");
		        		}
		        		
		        	});
				try {
					hashOperations1.entries(gtkeyname).entrySet().stream().forEach((e2) -> {
						
			        	if(e2.getValue().equals("free") )
			        	{
			        		 Date date = new Date();  
			        		 Item notification4 =new Item(e2.getKey(),date); 
			        		hashOperations1.put(msckeyname,params.get("MSISDN"), notification4);
			        		hashOperations.put(gtkeyname, e2.getKey(), "busy");
			        		int a=1/0;
			        	}
			        	}); 
					}
					catch(Exception ex)
					{
						logger.error(ex.toString());
					}
				try {
					Item notification5=(Item)hashOperations1.get(msckeyname,params.get("MSISDN"));
					
					returnStatus=notification5.getId();
					}
					catch(Exception exx)
					{
						logger.error(exx.toString());
						returnStatus="ERROR";
					}
			}
			//System.out.println(returnStatus);
					     
		}
		
		try {		
			synchronized(this){
				//rd = new RequestData(Msisdn);
				//reqRepo.save(rd);
			}
			
			
		}catch(Exception e) {
			logger.error("Exception Occured while Inserting URL Params: "+e);
		}
		return returnStatus;
	}
	
	}

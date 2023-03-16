package com.spice.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.spice.service.ServiceClass;

@RestController
public class AcceptController {
	
	@Autowired
	ServiceClass service;
	
	Logger logger = Logger.getLogger(AcceptController.class);
	
	//@GetMapping(path = "/M2M_SRI-1.0/FetchStatus/{MSISDN}")
	@GetMapping(path = "/FetchStatus/{key}/{MSISDN}")
	public String getResponse(@PathVariable LinkedHashMap<String, String> Params) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String reqDate = dateFormat.format(new Date());
		
		Set<String> keys = Params.keySet();
		String req = "";
        for(String k:keys){
        	req += "/"+Params.get(k);
        	//System.out.println(k + " " + Params.get(k)); 
        }
        
        String returnStatus = "";		
      
	    String returnObject = service.processStatusRequest(Params);
	    String responseDate = dateFormat.format(new Date());
	    
	   	returnStatus = returnObject;
	    
	    logger.info(reqDate+" "+req+"::"+responseDate+"/"+returnObject+"/"+returnStatus);
	    return returnStatus;
	}
}

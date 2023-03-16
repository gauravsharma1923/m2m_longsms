package com.spice.model;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable  {
    private String id;
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private Date date;
    
    public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Item() {
    }

    public Item(String id, Date date) {
        this.id = id;
        this.date = date;
        
    }

    
    
    
}
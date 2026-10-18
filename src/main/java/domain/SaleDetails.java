package domain;

import java.io.File;
import java.util.Date;

public class SaleDetails {
    private String title;
    private String desc;
    private int status;
    private float price;
    private Date date;
    private File file;

    public SaleDetails(String title, String desc, int status, float price, Date date, File file) {
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.price = price;
        this.date = date;
        this.file = file;
    }

    public String getTitle() { 
    	return title; 
    }
    
    public String getDesc() { 
    	return desc; 
    }
    
    public int getStatus() { 
    	return status; 
    }
    
    public float getPrice() { 
    	return price; 
    }
    
    public Date getDate() { 
    	return date; 
    }
    
    public File getFile() { 
    	return file; 
    }
    
}
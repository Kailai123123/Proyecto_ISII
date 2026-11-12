package gui;

import java.awt.Color;
import java.net.URL;
import java.util.Locale;

import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import configuration.ConfigXML;
import dataAccess.DataAccess;
import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import businessLogic.BLFactory; // Import añadido

public class ApplicationLauncher { 
	
	public static void main(String[] args) {
		
		try { 
	        UIManager.put("Button.arc", 5); 
	        
	        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
	        
	    } catch( Exception ex ) {
	        System.err.println( "Failed to initialize LaF" );
	    }

		ConfigXML c=ConfigXML.getInstance();		
		Locale.setDefault(new Locale(c.getLocale()));
		
		MainGUI a=new MainGUI();
		a.setVisible(true);


		try {
			
			BLFacade appFacadeInterface;
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
			BLFactory factory = new BLFactory();
			appFacadeInterface = factory.getBusinessLogicFactory(c.isBusinessLogicLocal());
			
			MainGUI.setBussinessLogic(appFacadeInterface);
			
		}catch (Exception e) {
			a.jLabelSelectOption.setText("Error: "+e.toString());
			a.jLabelSelectOption.setForeground(Color.RED);	
			
			System.out.println("Error in ApplicationLauncher: "+e.toString());
		}

	}

}
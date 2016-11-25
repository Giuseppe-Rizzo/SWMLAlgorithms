package samplers;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Properties;
import java.util.Set;

public class DatasetAcquisition {

	public Set<String> readFromFile(){
		Set<String>dataset = null; 
		Properties props= new Properties();
		try
		{
			
			FileInputStream fis = new FileInputStream(props.getProperty("urlOwlFile")+".dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			dataset = (Set<String>) ois.readObject(); 
			ois.close();
		}
		catch(Exception e)
		{
			System.out.println("Eccezione:"  + e.toString());
		}
		return dataset;

	}



}

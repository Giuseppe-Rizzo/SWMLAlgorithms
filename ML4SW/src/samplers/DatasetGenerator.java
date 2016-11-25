package samplers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owl.model.OWLIndividual;

import evaluation.Parameters;

import utils.Generator;

import knowledgeBasesHandler.KnowledgeBase;

public class DatasetGenerator {

	KnowledgeBase  kb;

	public DatasetGenerator (KnowledgeBase kb){

		this.kb= kb;

	}

	public Set<String> sampleIndividuals(int sampleSize){
		final OWLIndividual[] individuals = kb.getIndividuals();
		Random generator = Generator.generator;
		int nExamples=0;
		Set<String> sample= new HashSet<String>(); //
		boolean finished= false;
		while (!finished){
			final int nextElement = generator.nextInt(sampleSize);
			OWLIndividual owlIndividual = individuals[nextElement];
			if (!(sample.contains(owlIndividual.toString()))){
				sample.add(owlIndividual. toString());
				nExamples++; 			
				System.out.println(nExamples+ "-"+owlIndividual.toString());
			}
			if (nExamples>= sampleSize)
				finished=true;
		}
		System.out.println("Writing files");
		writeFile(sample);


		return sample;

	}

	public Set<OWLIndividual> sampleIndividuals(OWLIndividual [] inds, int sampleSize){
		Random generator = Generator.generator;
		int nExamples=0;
		Set<OWLIndividual> sample= new HashSet<OWLIndividual>(); //
		boolean finished= false;
		while (!finished){
			final int nextElement = generator.nextInt(sampleSize);
			OWLIndividual owlIndividual = inds[nextElement];
			if (!(sample.contains(owlIndividual))){
				sample.add(owlIndividual);
				nExamples++; 			
//				System.out.println(nExamples+ "-"+owlIndividual.toString());
			}
			if (nExamples>= sampleSize)
				finished=true;
		}
//		System.out.println("Writing files");
//		writeFile(sample);


		return sample;

	}

	
	
	
	public void writeFile(Set<String> sample) {
		try
		{ 
			Properties props= new Properties();
			FileOutputStream fileOut =
					new FileOutputStream(props.getProperty("urlOwlFile")+".dat");
			System.out.println("--------------");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(sample);
			System.out.println("--------------");
			out.close();
			fileOut.close();
			System.out.printf("Dataset Extracted and saved in");
		}catch(IOException i)
		{
			i.printStackTrace();
		}
	}


}

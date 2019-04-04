package com.iiitb.SemWeb_NoSQL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model model = FileManager.get().loadModel("/home/sarika1/eclipse-workspace/SemWeb-NoSQL/src/main/resources/data.rdf");
		model.write(System.out,"RDF/JSON");
	}

}

package com.iiitb.SemWeb_NoSQL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

public class Main {
	
	

	public static void main(String[] args) throws FileNotFoundException {	
		
		FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model model = FileManager.get().loadModel("/home/sarika1/Downloads/university.owl");
		
//		DBConnection.establishConnection();
//		addClass(model); 
//		addDataTypeProperty(model); 
//		addObjectProperty(model);
//		addIndividual(model);
//		
//		addDetails(model,"Class");
//		addDetails(model,"ObjectProperty");
//		addDetails(model,"DatatypeProperty");
//		addDetails(model,"NamedIndividual");
//		DBConnection.addTypestoClasses();
//		
		DBConnection.addIDstoSuperClasses();
	}
	

	static void addDetails(Model model,String collection) {
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		ArrayList<String> individuals = DBConnection.getDetails(collection);
		for(String s : individuals) {
			
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("title",s);
			
			String queryString = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
					"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
					"prefix ab: <http://www.semanticweb.org/sarika1/ontologies/2019/3/untitled-ontology-20#>\n" + 
					"\n" + 
					"SELECT ?predicate ?object\n" + 
					"WHERE {\n" + 
					"   ab:" + s +  "?predicate ?object\n" + 
					"}";
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			try {
				ResultSet results = qexec.execSelect();
				while(results.hasNext()) {
					QuerySolution soln = results.nextSolution();
					RDFNode ynode = soln.get("object");
					String predicate = null;
					String object = null;
			        if (ynode.isResource()) {
			             predicate = soln.getResource("predicate").getURI();
			             object = soln.getResource("object").getURI();
			        } else {
			        	 predicate = soln.getResource("predicate").getURI();
			        	 object = soln.getLiteral("object").getString();
			        }
			        //System.out.println(predicate+" "+object);
			        String[] words = predicate.split("#");
			        String[] objects;
			        if(object.contains("#")) {
			        	objects = object.split("#");
			        	if(!objects[1].equals(collection))
			        		map.put(words[1],objects[1]);
			        }
			        else {
			        	map.put(words[1],object);
			        }
				}
			}
			finally {
				qexec.close();
			}
			list.add(map);
			
			
		}
		
		  for(Map<String,String> map : list){ 
			  for(Map.Entry<String,String> e : map.entrySet()) { 
				  System.out.println(e.getKey() + " " + e.getValue());
			  }
		  }
		 
		  DBConnection.addInstanceDetails(list,collection);
	}


	static void addClass(Model model) {
		
		  String queryString = "prefix owl: <http://www.w3.org/2002/07/owl#>\n" +
		  "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "\n" +
		  "SELECT ?class WHERE { ?class a owl:Class }";
		 
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int i = 0;
		ArrayList<String> classes = new ArrayList<>();
		try {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String name = soln.getResource("class").getURI();
				String[] words = name.split("#");
				classes.add(words[1]);
			}
		}
		finally {
			qexec.close();
		}
		DBConnection.addClasses(classes);
	}
	
	static void addDataTypeProperty(Model model) {
		
		String queryString = "prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?class \n" + 
				"WHERE {\n" + 
				"  ?class a owl:DatatypeProperty.\n" + 
				"}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int i = 0;
		ArrayList<String> dproperties = new ArrayList<>();
		try {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String name = soln.getResource("class").getURI();
				String[] words = name.split("#");
				dproperties.add(words[1]);
			}
		}
		finally {
			qexec.close();
		}
		DBConnection.addDataProperties(dproperties);
	
	}

	static void addObjectProperty(Model model) {
		
		String queryString = "prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?class \n" + 
				"WHERE {\n" + 
				"  ?class a owl:ObjectProperty.\n" + 
				"}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int i = 0;
		ArrayList<String> oproperties = new ArrayList<>();
		try {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String name = soln.getResource("class").getURI();
				String[] words = name.split("#");
				oproperties.add(words[1]);
			}
		}
		finally {
			qexec.close();
		}
		DBConnection.addObjectProperties(oproperties);

	}

	static void addIndividual(Model model) {
		
		String queryString = "prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?class\n" + 
				"WHERE {\n" + 
				"  ?class a owl:NamedIndividual.\n" + 
				"}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		int i = 0;
		ArrayList<String> individuals = new ArrayList<>();
		try {
			ResultSet results = qexec.execSelect();
			while(results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				String name = soln.getResource("class").getURI();
				String[] words = name.split("#");
				individuals.add(words[1]);
			}
		}
		finally {
			qexec.close();
		}
		DBConnection.addIndividuals(individuals);

	}

}

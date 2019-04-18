package com.iiitb.SemWeb_NoSQL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Updates;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.Mongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

public class DBConnection {
	
	static MongoClient mongoClient = null;
	static MongoDatabase db = null;
	
	public static void establishConnection() {
		try {

			mongoClient = new MongoClient("localhost", 27017);
			System.out.println("Connection build successfully");
			
		    db = mongoClient.getDatabase("university");
		}
		catch(MongoException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void addClasses(ArrayList<String> classes) {

		try
		{
			MongoCollection<Document> coll = db.getCollection("Class");
			
			Document doc = null;
			
			for(String s : classes) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		
	}

	public static void addDataProperties(ArrayList<String> dproperties) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("DataTypeProperty");
			
			Document doc = null;
			
			for(String s : dproperties) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
	}

	public static void addObjectProperties(ArrayList<String> oproperties) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("ObjectProperty");
			
			Document doc = null;
			
			for(String s : oproperties) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
	}

	public static void addIndividuals(ArrayList<String> individuals) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("NamedIndividual");
			
			Document doc = null;
			
			for(String s : individuals) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getDetails() {
		
		ArrayList<String> individuals = new ArrayList<>();
		
		try
		{
			MongoCollection<Document> coll = db.getCollection("NamedIndividual");
			
			FindIterable<Document> docs = coll.find();
			
			for(Document doc : docs) {
				individuals.add(doc.getString("title"));
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		return individuals;
	}

	public static void addInstanceDetails(List<HashMap<String, String>> list) {
		
		try
		{
			MongoCollection<Document> coll = db.getCollection("NamedIndividual");
			
			//FindIterable<Document> docs = coll.find();
			
			for(HashMap<String, String> map : list) {
				
				String temp = map.get("title");
				FindIterable<Document> docs = coll.find();
				Bson filter = new Document("title", temp);
				for(Map.Entry<String,String> e : map.entrySet()) {
					coll.updateOne(filter, {$set: {e.getKey():e.getValue()}});
				}
				
				System.out.println("Here");
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
	}
}

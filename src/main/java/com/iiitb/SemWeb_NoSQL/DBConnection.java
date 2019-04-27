package com.iiitb.SemWeb_NoSQL;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;import java.util.Map;
import java.util.Set;

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
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;

public class DBConnection {
	
	static MongoClient mongoClient = null;
	static MongoDatabase db = null;
	
	public static void establishConnection() {
		try {

			mongoClient = new MongoClient("localhost", 27017);
			System.out.println("Connection build successfully");
			
		    db = mongoClient.getDatabase("university1");
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
			MongoCollection<Document> coll = db.getCollection("DatatypeProperty");
			
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

	public static ArrayList<String> getDetails(String collection) {
		
		ArrayList<String> individuals = new ArrayList<>();
		
		try
		{
			MongoCollection<Document> coll = db.getCollection(collection);
			
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

	public static void addInstanceDetails(List<HashMap<String, String>> list,String collection) {
		
		try
		{
			MongoCollection<Document> coll = db.getCollection(collection);
						
			for(HashMap<String, String> map : list) {
				
				String temp = map.get("title");
				BasicDBObject doc = new BasicDBObject();
				BasicDBObject q = new BasicDBObject("title",temp);
				  for(Map.Entry<String,String> e : map.entrySet()) { 
					  doc.put(e.getKey(), e.getValue());
				  } 				
				coll.updateMany(q,new BasicDBObject("$set",doc));
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
	}

	public static void addTypestoClasses() {
		try {
			MongoCollection<Document> coll = db.getCollection("NamedIndividual");
			
			FindIterable<Document> docs = coll.find();
			
			for(Document doc : docs) {
				
				String type = doc.getString("type");
				Object ID = doc.get("_id");
				
				MongoCollection<Document> coll2 = db.getCollection("Class");
				
				BasicDBObject q = new BasicDBObject("title",type);
				
				//FindIterable<Document> docs1 = coll2.find();
				
				BasicDBObject d = new BasicDBObject();
				d.put("data",ID);
				
				coll2.updateMany(q,new BasicDBObject("$push",d));
				
				
			}
		}
		catch(MongoException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static void addIDstoSuperClasses() {
		try {
			
			DB db = null;
			//MongoCollection<Document> coll = db.getCollection("Class");
			try {

				mongoClient = new MongoClient("localhost", 27017);
				System.out.println("Connection build successfully");
				
				db = mongoClient.getDB("university1");
			}
			catch(MongoException e) {
				
				e.printStackTrace();
			}
			
			DBCollection coll = db.getCollection("Class");
			
			BasicDBObject sco = new BasicDBObject("subClassOf",new BasicDBObject("$exists",false));
			
			DBCursor scos = coll.find(sco);
			
			for(DBObject scose : scos) {
				BasicDBObject r = new BasicDBObject();
				r.put("subClassOf","nosuperclass");
				coll.update(sco,new BasicDBObject("$set",r));
			}
			
			
			
			BasicDBObject q = new BasicDBObject("data",new BasicDBObject("$exists",true));
			
			DBCursor docs = coll.find(q);
			
			DBObject documents;
			
			for(DBObject docx : docs) {
				
				DBObject doc = docx;
				String setorpush = "$set";
				do {
					String superClass = doc.get("subClassOf").toString();
							//((Document) doc).getString("subClassOf");
					String currClass = doc.get("title").toString();
					System.out.println(superClass+" "+ currClass);
					
					DBObject dbObject = (DBObject) doc.get("data");
					
					BasicDBObject d = new BasicDBObject("title",superClass);
					
//					BasicDBObject r = null;
//					for(Object obj : arr) {
//						r = new BasicDBObject();
//						r.put("data",obj);
					Set<Object> list = new HashSet<Object>();
//					}
					for (Object key : dbObject.keySet()) {
					    list.add(dbObject.get((String) key));
					    System.out.println(dbObject.get((String) key));
					}
//					BasicDBObject r = new BasicDBObject();
//					r.put("data",list);
//					System.out.println(r);
					
					//DBObject ex = coll.findOne(new BasicDBObject("data",new BasicDBObject("$exists",true)));
					
					BasicDBObject andQuery = new BasicDBObject();
				    Set<BasicDBObject> obj = new HashSet<BasicDBObject>();
				    obj.add(d);
				    obj.add(new BasicDBObject("data",new BasicDBObject("$exists",true)));
				    andQuery.put("$and", obj);
				  
				    DBCursor cursor = coll.find(andQuery);
					
					if(cursor.hasNext()) {
						System.out.println("herep");
						DBObject dbo = cursor.next();
						dbo = (DBObject) dbo.get("data");
						//List<Object> list1 = new ArrayList<Object>();
//						}
						for (Object key : dbo.keySet()) {
						    list.add(dbo.get((String) key));
						    System.out.println(dbo.get((String) key));
						}
						BasicDBObject r = new BasicDBObject();
						r.put("data",list);
						System.out.println(r);

						coll.update(d,new BasicDBObject("$set",r));
					}
					else {
						System.out.println("heres");
						BasicDBObject r = new BasicDBObject();
						r.put("data",list);
						System.out.println(r);

						coll.update(d, new BasicDBObject("$set",r));
					}
					
//				
					BasicDBObject dr = new BasicDBObject("subClassOf",new BasicDBObject("$exists",true));
//					
//					//System.out.println(d.get("subClassOf").toString());
//					
					//documents = coll.findOne(d);
					doc = coll.findOne(d);
//					
					
				}while(!doc.get("subClassOf").toString().equals("nosuperclass"));
			}
			
		}
		catch(MongoException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getAll() {
		try
		{
			MongoCollection<Document> coll = db.getCollection("NamedIndividual");
			
			FindIterable<Document> docs = coll.find();
			
			List<String> list = new ArrayList<String>();
			for(Document doc : docs) {
				list.add(doc.toJson());
				System.out.println(doc.toJson());
			}
			System.out.println(list);
			return list;
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void addSymmetricProperties(ArrayList<String> sproperties) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("SymmetricProperty");
			
			Document doc = null;
			
			for(String s : sproperties) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void addTransitiveProperties(ArrayList<String> tproperties) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("TransitiveProperty");
			
			Document doc = null;
			
			for(String s : tproperties) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void addInverseProperties(ArrayList<String> iproperties) {
		try
		{
			MongoCollection<Document> coll = db.getCollection("InverseProperty");
			
			Document doc = null;
			
			for(String s : iproperties) {
				doc = new Document("title",s);
				coll.insertOne(doc);
			}
			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		
	}

	public static void closeConnection() {
		mongoClient.close();
	}
	
}

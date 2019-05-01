package com.iiitb.SemWeb_NoSQL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
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

	public static void addInstanceDetails(List<HashMap<String, ArrayList<String>>> list,String collection) {
		try
		{
			MongoCollection<Document> coll = db.getCollection(collection);
			for(HashMap<String, ArrayList<String>> map : list) {
				ArrayList<String> list1 = new ArrayList<String>();
				String temp = map.get("title").get(0);
				BasicDBObject doc = new BasicDBObject();
				BasicDBObject q = new BasicDBObject("title",temp);
				  for(Map.Entry<String,ArrayList<String>> e : map.entrySet()) {
					  list1 = e.getValue();
					  if(list1.size()==1) {
						  doc.put(e.getKey(), list1.get(0));
					  }
					  else {
						  doc.put(e.getKey(), e.getValue());
					  }
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
					String currClass = doc.get("title").toString();
					System.out.println(superClass+" "+ currClass);
					DBObject dbObject = (DBObject) doc.get("data");
					BasicDBObject d = new BasicDBObject("title",superClass);
					Set<Object> list = new HashSet<Object>();
					for (Object key : dbObject.keySet()) {
					    list.add(dbObject.get((String) key));
					    System.out.println(dbObject.get((String) key));
					}
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
					BasicDBObject dr = new BasicDBObject("subClassOf",new BasicDBObject("$exists",true));
					doc = coll.findOne(d);
				}while(!doc.get("subClassOf").toString().equals("nosuperclass"));
			}
			
		}
		catch(MongoException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		mongoClient.close();
	}
}

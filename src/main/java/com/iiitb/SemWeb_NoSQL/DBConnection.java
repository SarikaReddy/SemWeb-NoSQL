package com.iiitb.SemWeb_NoSQL;

import java.util.Date;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.Mongo;
import com.mongodb.DB;

public class DBConnection {
	public static void main(String[] args) {

		try
		{
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			System.out.println("Connection bulied successfully");
			
			MongoDatabase db=mongoClient.getDatabase("mongoDbDemo");
			
			MongoCollection<Document> table = db.getCollection("employee");  
			//---------- Creating Document ---------------------------//    
			Document doc = new Document("name", "Peter John");  
			doc.append("id",12);  
			Document doc2 =new Document("name","Parul Parikh");
			doc2.append("id", 24);
			//----------- Inserting Data ------------------------------//  
			table.insertOne(doc);
			table.insertOne(doc2);
			
			

			
		}
		catch(MongoException e)
		{
			e.printStackTrace();
		}
		
		}
}

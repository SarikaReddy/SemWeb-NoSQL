package com.iiitb.SemWeb_NoSQL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/Test")
public class Test {

	@Path("/execQuery")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//@Produces(MediaType.APPLICATION_JSON)
    public static Response execQuery(@FormParam("query") String query) {
    	
        Runtime rt = Runtime.getRuntime();
        try {

            // the query you want to run in mongo, you can get it 
            // from a file using a FileReader
            //String query = "db.NamedIndividual.find({'title':'student1'}).pretty()";

            // the database name you need to use
            String db = "university1";

            // run a command from terminal. this line is equivalent to 
            // mongo database --eval "db.col.find()" 
            // it calls the mongo binary and execute the javascript you 
            // passed in eval parameter. It should work for both unix and 
            // windows 
            Process pr = rt.exec(new String[]{"mongo", db, "--eval", query});
            // read the output of the command
            InputStream in = pr.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String out = "";
            String line;
            while ((line = reader.readLine()) != null) {
                //out.append(line+"\n");
                //System.out.println(line);
            	out += line;
            	out += "\n";
            }
            // print the command and close outputstream reader
            System.out.println("Output" + out);   
            reader.close();
            return Response.status(201).entity(out).build();
        } 
        catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
		return null;
    }
}
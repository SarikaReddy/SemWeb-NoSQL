package com.iiitb.SemWeb_NoSQL;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Get")
public class GetController {
	
	@Path("/individuals")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAll() {
		
		DBConnection.establishConnection();
		
		String list;
		
		list = DBConnection.getAll().toString();
		
		return list;
	}
}

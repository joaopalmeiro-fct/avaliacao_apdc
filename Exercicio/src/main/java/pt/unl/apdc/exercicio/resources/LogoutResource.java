package pt.unl.apdc.exercicio.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.apdc.exercicio.util.LogoutData;

@Path("/logout")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	private static final Logger LOG = Logger.getLogger (LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LogoutResource () { };	//Nothing to be done
	
	@POST
	@Path ("/user")
	@Consumes (MediaType.APPLICATION_JSON)
	public Response logout (LogoutData data) {
		LOG.fine("Attempt to lougout from account: " + data.username);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		
		String tokenID = DigestUtils.sha256Hex(data.tokenID);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenID);
		
		Transaction txn = datastore.newTransaction();
		
		try {
			Entity user = txn.get(userKey);
			
			if (user == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Logout failed (No such username in the system).").build();
			}
			
			Entity tokenDB = txn.get(tokenKey);
			
			if (tokenDB == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Logout failed (No such tokenID in the system).").build();
			}
			
			if (tokenDB.getLong("user_expiration_time") < System.currentTimeMillis()){
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Logout failed (Token alredy expired).").build();
			}

			
			if (! user.getString("username").equals(tokenDB.getString("username")) ) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Logout failed (You cannot logout another user).").build();
			}
				
			tokenDB = Entity.newBuilder(datastore.get(tokenKey))
					.set("user_expiration_time", System.currentTimeMillis())
					.set("username", tokenDB.getString("username"))
					.set("role", tokenDB.getString("role"))
					.set("user_creation_time", tokenDB.getLong("user_creation_time"))
					.set("tokenID", tokenDB.getString("tokenID"))
					.build();
			txn.update(tokenDB);
			txn.commit();
			return Response.status(Status.OK).entity("Logout sucessfull").build();
			
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}

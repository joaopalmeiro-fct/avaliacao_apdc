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

import pt.unl.apdc.exercicio.util.RemoveData;

@Path("/remove")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveUser {

	private static final Logger LOG = Logger.getLogger (LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RemoveUser () { };	//Nothing to be done

	@POST
	@Path("/user")
	@Consumes (MediaType.APPLICATION_JSON)
	public Response removeUSER (RemoveData data) {
		LOG.fine ("Attempt to remove user " + data.username);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);

		String tokenID = DigestUtils.sha256Hex(data.tokenID);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenID);

		Transaction txn = datastore.newTransaction();

		try {
			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Remove failed (No such username in the system).").build();
			}

			Entity tokenDB = txn.get(tokenKey);

			if (tokenDB == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Remove failed (No such tokenID in the system).").build();
			}

			if (tokenDB.getLong("user_expiration_time") < System.currentTimeMillis()){
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Remove failed (Token alredy expired).").build();
			}
			
			if (! user.getString("username").equals(tokenDB.getString("username")) ) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Remove failed (You cannot remove another user).").build();
			}
			
			tokenDB = Entity.newBuilder(datastore.get(tokenKey))
					.set("user_expiration_time", System.currentTimeMillis())
					.set("username", tokenDB.getString("username"))
					.set("role", tokenDB.getString("role"))
					.set("user_creation_time", tokenDB.getLong("user_creation_time"))
					.set("tokenID", tokenDB.getString("tokenID"))
					.build();
			
			user = Entity.newBuilder(datastore.get(userKey))
					.set("username", user.getString("username"))
					.set("user_pwd", user.getString("user_pwd"))
					.set("user_creation_time", user.getTimestamp("user_creation_time"))
					.set("user_email", user.getString("user_email"))
					.set("role", user.getString("role"))
					.set("state", "DISABLED")
					.set("profile", user.getString("profile"))
					.set("address", user.getString("address"))
					.set("landline", user.getString("landline"))
					.set("phoneNumber", user.getString("phoneNumber"))
					.set("complementarAddress", user.getString("complementarAddress"))
					.set("locality", user.getString("locality"))
					.build();
			
			txn.update(tokenDB);
			txn.update(user);
			txn.commit();
			
			return Response.ok("User removed correctly").build();
		}
		finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
	
}

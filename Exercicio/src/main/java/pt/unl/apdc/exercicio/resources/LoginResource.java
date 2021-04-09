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
import com.google.gson.Gson;

import pt.unl.apdc.exercicio.util.AuthToken;
import pt.unl.apdc.exercicio.util.LoginData;

@Path("/login")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	private static final Logger LOG = Logger.getLogger (LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();

	public LoginResource () { };	//Nothing to be done

	@POST
	@Path ("/user")
	@Consumes (MediaType.APPLICATION_JSON)
	@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLoginV1 (LoginData data) {
		LOG.fine("Attempt to login");

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		if (user == null) 
			return Response.status(Status.FORBIDDEN).entity("Login failed (No such username in the system).").build();
		else {
			
			if (user.getString("state").equals("DISABLED")) 
				return Response.status(Status.FORBIDDEN).entity("Login failed (Account has been removed).").build();
			
			String hashedPWD = user.getString("user_pwd");
			if (hashedPWD.equals(DigestUtils.sha256Hex(data.password))) {
				AuthToken token = new AuthToken(data.username);
				LOG.info("User " + data.username + " logged in successfuly.");
				this.saveToken(token);
				return Response.ok(g.toJson(token)).build();
			}
			else {
				LOG.warning("Incorrect password for username: " + data.username + " " + DigestUtils.sha256Hex(data.password));
				return Response.status(Status.FORBIDDEN).build(); 
			}
		}
	}
	
	private void saveToken (AuthToken token) {
		LOG.fine("Attempt to save token on data base");
		
		String tokenID = DigestUtils.sha256Hex(token.tokenID);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenID);
		
		Transaction txn = datastore.newTransaction();

		try {
			Entity tokenDB  = txn.get(tokenKey);
			if (tokenDB == null) {
				tokenDB = Entity.newBuilder(tokenKey)
						.set("username", token.username)
						.set("tokenID", token.tokenID)
						.set("user_creation_time", token.creationData)
						.set("user_expiration_time", token.expirationData)
						.set("role", token.role)
						.build();
				txn.add(tokenDB);
				LOG.info("Boostrap user registered");
				txn.commit();
			} else {
				txn.rollback();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}

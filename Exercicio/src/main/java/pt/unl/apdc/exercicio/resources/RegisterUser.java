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
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
//import com.google.gson.Gson;

import pt.unl.apdc.exercicio.util.AccountData;

@Path("/register")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterUser {

	private static final Logger LOG = Logger.getLogger (LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterUser () { };	//Nothing to be done
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegisterV3 (AccountData data) {
		LOG.fine("Register attempt from: " + data.username);

		if (!data.checkConfirmation())
			return Response.status(Status.BAD_REQUEST).entity("Password and confirmation don't match.").build();
		
		if (!data.checkPassword() )
			return Response.status(Status.BAD_REQUEST).entity("Password does not match the expected restrictions.").build();
		
		Transaction txn = datastore.newTransaction();

		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if (user == null) {
				user = Entity.newBuilder(userKey)
						.set("username", data.username)
						.set("user_pwd", DigestUtils.sha256Hex(data.password))
						.set("user_creation_time", Timestamp.now())
						.set("user_email", data.email)
						.set("role", "USER")
						.set("state", "ENABLED")
						.set("profile", "")
						.set("address", "")
						.set("landline", "")
						.set("phoneNumber", "")
						.set("complementarAddress", "")
						.set("locality", "")
						.build();
				txn.add(user);
				LOG.info("User registered " + data.username);
				txn.commit();
				return Response.ok("User Registered").build();
			} else {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("The username is already registered.").build();
			}
		} finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}

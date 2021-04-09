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

import pt.unl.apdc.exercicio.util.StateResources;

@Path("/update")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateState {

	private static final Logger LOG = Logger.getLogger (LoginResource.class.getName());

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public UpdateState () { };	//Nothing to be done

	@POST
	@Path("/state")
	@Consumes (MediaType.APPLICATION_JSON)
	public Response changeRoles (StateResources data) {
		LOG.fine("Attempt to change state");
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
		Key otherUserKey = datastore.newKeyFactory().setKind("User").newKey(data.getOtherUser());

		String tokenID = DigestUtils.sha256Hex(data.getToken());
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenID);

		Transaction txn = datastore.newTransaction();

		try {
			Entity user = txn.get(userKey);

			if (user == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update roles failed (No such username in the system).").build();
			}
			
			Entity otherUser = txn.get(otherUserKey);
			
			if (otherUser == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update roles failed (Other person does not exist).").build();
			}

			Entity tokenDB = txn.get(tokenKey);

			if (tokenDB == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update roles failed (No such tokenID in the system).").build();
			}

			if (tokenDB.getLong("user_expiration_time") < System.currentTimeMillis()){
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update failed (Token alredy expired).").build();
			}
			
			if (!user.getString("username").equals(tokenDB.getString("username"))) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update failed (Your token is not correct).").build();
			}
			
			if ( data.checkUser(user.getString("role")) || data.checkGBO(user.getString("role")) )  {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update failed (You can't change roles of another user).").build();
			}
			
			if ( !(data.checkGA(user.getString("role"))
					|| data.checkSU(user.getString("role")) )
					&& data.checkGBO(otherUser.getString("role"))) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update failed (You can't change to GBO role).").build();
			}
			
			if ( !data.checkSU(user.getString("role")) && data.checkGA(otherUser.getString("role"))) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity("Update failed (You can't change to GA role).").build();
			}
				
			otherUser = Entity.newBuilder(datastore.get(userKey))
					.set("username", otherUser.getString("username"))
					.set("user_pwd", otherUser.getString("user_pwd"))
					.set("user_creation_time", otherUser.getTimestamp("user_creation_time"))
					.set("user_email", otherUser.getString("user_email"))
					.set("role", otherUser.getString("role"))
					.set("state", data.getNewState())
					.set("profile", otherUser.getString("profile"))
					.set("address", otherUser.getString("address"))
					.set("landline", otherUser.getString("landline"))
					.set("phoneNumber", otherUser.getString("phoneNumber"))
					.set("complementarAddress", otherUser.getString("complementarAddress"))
					.set("locality", otherUser.getString("locality"))
					.build();
			
			txn.update(otherUser);
			txn.commit();
			
			return Response.ok("User updated correctly").build();
		}
		finally {
			if (txn.isActive())
				txn.rollback();
		}
	}
}

package com.blazingcontacts.BlazingContacts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple wrapper to abstract communication with BlazingContacts web services
 * @author Sam Pottinger
 */
public class WebWrapper {
	
	public enum Mode {CREATED_GROUP, JOINED_GROUP};
	public enum HttpMethod {GET, POST, PUT, DELETE};
	private static final String DEFAULT_HOST = "blazingcontacts.appspot.com";
	private static final String DEFAULT_SCHEME = "https";
	private static final String GROUP_NAME_PARAMETER = "name";
	private static final String PASSWORD_PARAMETER = "password";
	private static final String EXPIRATION_PARAMETER = "expiration";
	private static final String DATA_PARAMETER = "data";
	private static final String GROUP_MAX_MEMBERS_PARAMETER = "max_members";
	private static final String GROUP_RESOURCE = "/group";
	private static final String GROUP_DOWNLOAD_RESOURCE = "/group/download";
	private static final String CONTACT_RESOURCE = "/contact";
	private static final String JSON_DATE_ATTRIBUTE = "expiration";
	private static final String JSON_GROUP_MAX_ATTRIBUTE = "max_members";
	private static final String JSON_MEMBER_COUNT_ATTRIBUTE = "member_count";
	private static final String JSON_CONTACTS_ATTRIBUTE = "contacts";
	private static final String JSON_ERROR_NUM_ATTRIBUTE = "error";
	private static final String JSON_ERROR_MESSAGE_ATTRIBUTE = "error_message";
	private static final String JSON_RESULT = "result";
	
	public static final int NO_GROUP_MAX = -1;
	private static final int NO_ERROR = 0;
	
	private String groupName;
	private String password;
	private Mode mode;
	
	/**
	 * Joins and interfaces with an existing group
	 * 
	 * @param newGroupName The name of the group to join
	 * @param newPassword The group password to use
	 * @param newContact The local user's self-selected contact information
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 * @throws ServerException 
	 */
	public WebWrapper(String newGroupName, String newPassword, Contact newContact) throws ClientProtocolException, URISyntaxException, IOException, JSONException, ServerException
	{
		// Save simple attributes
		mode = Mode.JOINED_GROUP;
		groupName = newGroupName;
		password = newPassword;
		
		// Upload contact information
		addContactInfo(newContact);
	}
	
	/**
	 * Creates and interfaces with a new group
	 * 
	 * @param newGroupName The name of the group to join
	 * @param newPassword The password to use
	 * @param newContact The local user's self-selected contact information
	 * @param expiration The Date after which the group will be deleted (set in user timezone)
	 * @param groupMax The maximum number of people allowed in the group
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 * @throws ServerException 
	 */
	public WebWrapper(String newGroupName, String newPassword, Contact newContact, Date expiration, int groupMax) throws ClientProtocolException, URISyntaxException, IOException, JSONException, ServerException
	{
		ArrayList<NameValuePair> parameters;
		
		// Save attributes
		mode = Mode.CREATED_GROUP;
		groupName = newGroupName;
		password = newPassword;
		
		// Create list of parameters
		// TODO: ArrayList may not be the most effective data structure here
		parameters = new ArrayList<NameValuePair>(4);
		parameters.add(new BasicNameValuePair(EXPIRATION_PARAMETER, dateToISOString(expiration)));
		parameters.add(new BasicNameValuePair(GROUP_MAX_MEMBERS_PARAMETER, new Integer(groupMax).toString()));
		
		// Create group on server
		executeRequest(GROUP_RESOURCE, HttpMethod.PUT, parameters);
		
		// Send contact information to server
		addContactInfo(newContact);
	}
	
	/**
	 * Requests the status of the current group with dates encoded for the user's timezone
	 * @return A GroupStatus corresponding to this WebWrapper's group
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 * @throws ParseException 
	 * @throws ServerException 
	 */
	public GroupStatus getStatus() throws ClientProtocolException, URISyntaxException, IOException, JSONException, ParseException, ServerException
	{
		String isoExpirationDate;
		Date expiration;
		Date current_date;
		Date difference;
		int groupMax;
		int memberCount;
		
		// Get the JSON encoded information from the server
		JSONObject result = executeRequest(GROUP_RESOURCE, HttpMethod.GET, new ArrayList<NameValuePair>(2));
		
		// Determine remaining time
		isoExpirationDate = result.getString(JSON_DATE_ATTRIBUTE);
		expiration = isoStringToDate(isoExpirationDate);
		current_date = new Date();
		difference = new Date(expiration.getTime() - current_date.getTime());
		
		// Parse other data
		groupMax = result.getInt(JSON_GROUP_MAX_ATTRIBUTE);
		memberCount = result.getInt(JSON_MEMBER_COUNT_ATTRIBUTE);
		
		return new GroupStatus(difference, groupMax, memberCount);
	}
	
	/**
	 * Determines the name of the group this WebWrapper is
	 * responsible for
	 * @return The string group name
	 */
	public String getGroupName()
	{
		return groupName;
	}
	
	/**
	 * Requests and decodes all the group contact information
	 * @return An array of Contact objects
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 * @throws ServerException 
	 */
	public Contact[] downloadContactInfo() throws ClientProtocolException, URISyntaxException, IOException, JSONException, ServerException
	{
		JSONObject result;
		JSONArray jsonContactList;
		ArrayList<Contact> javaContactList;
		
		// Execute request
		result = executeRequest(GROUP_DOWNLOAD_RESOURCE, HttpMethod.GET, new ArrayList<NameValuePair>(2));
		
		// Create contact objects (javaContactList) from results (jsonContactList)
		jsonContactList = result.getJSONArray(JSON_CONTACTS_ATTRIBUTE);
		javaContactList = new ArrayList<Contact>(jsonContactList.length());
		for(int i = 0; i<jsonContactList.length(); i++)
			javaContactList.set(i, Contact.fromJSON((String) jsonContactList.get(i)) ); // TODO: a bit kludgy
		
		// TODO: The cast to array is a bit kludgy
		return (Contact []) javaContactList.toArray();
	}
	
	/**
	 * Publishes the given contact info to the web service
	 * @param info The contact information to publish
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 * @throws ServerException 
	 */
	private void addContactInfo(Contact info) throws ClientProtocolException, URISyntaxException, IOException, JSONException, ServerException
	{
		// Create parameters
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>(3);
		parameters.add(new BasicNameValuePair( DATA_PARAMETER, info.toJSON() ));
		
		// Execute the request
		executeRequest(CONTACT_RESOURCE, HttpMethod.PUT, parameters);
	}
	
	/**
	 * Executes an HTTPRequest on the server, attaching a password and group name
	 * 
	 * @param resource The URL to execute this method on (eg /group or /contact)
	 * @param method The HTTP method to use (GET, POST, PUT, DELETE)
	 * @param parameters List of NameValuePairs to use as form encoded parameters
	 * @return Decoded JSON result from the server
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ServerException 
	 */
	private JSONObject executeRequest(String resource, HttpMethod method, List<NameValuePair> parameters) throws URISyntaxException, ClientProtocolException, IOException, JSONException, ServerException
	{
		HttpRequestBase request;
		HttpResponse response;
		String jsonContent;
		JSONObject result;
		HttpClient httpclient = new DefaultHttpClient();
		
		// Add group name and password
		parameters.add(new BasicNameValuePair(GROUP_NAME_PARAMETER, groupName));
		parameters.add(new BasicNameValuePair(PASSWORD_PARAMETER, password));
		
		// Create resource path and request
		URI uri = URIUtils.createURI(DEFAULT_SCHEME, DEFAULT_HOST, -1, resource, 
			    URLEncodedUtils.format(parameters, "UTF-8"), null);
		request = createRequest(method, uri);
		
		// Execute request
		response = httpclient.execute(request);
		
		// Parse results and create JSON object
		jsonContent = readResults(response);
		result = new JSONObject(jsonContent);
		
		// Check for errors
		if (result.getInt(JSON_ERROR_NUM_ATTRIBUTE) != NO_ERROR)
			throw new ServerException(result.getString(JSON_ERROR_MESSAGE_ATTRIBUTE));
		
		return result.getJSONObject(JSON_RESULT);
	}
	
	/**
	 * Simple factory to create an appropriate request object
	 * @param method The method that will be executed
	 * @param uri The URI on which this method will be executed
	 * @return The appropriate request that corresponds to the provided method
	 */
	private HttpRequestBase createRequest(HttpMethod method, URI uri)
	{
		switch (method)
		{
		case GET:
			return new HttpGet(uri);
		case PUT:
			return new HttpPut(uri);
		case POST:
			return new HttpPost(uri);
		case DELETE:
			return new HttpDelete(uri);
		default:
			return null; // Unreachable code
		}
	}
	
	/**
	 * Returns the raw string body of an HttpResponse
	 * @param response the response to read
	 * @return Body of the provided response
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private String readResults(HttpResponse response) throws IllegalStateException, IOException
	{
		// Get the buffered reader from an InputStream and InputStreamReader
		InputStream inputStream = response.getEntity().getContent();
		InputStreamReader reader = new InputStreamReader(inputStream);
		BufferedReader r = new BufferedReader(reader);
		
		// Create a string builder to 
		StringBuilder total = new StringBuilder();
		
		// Read body from reader
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		
		return total.toString();
	}
	
	/**
	 * Converts the given date to an ISO compatible string
	 * @param target The date to convert
	 * @return A string ISO representation of the target
	 */
	private String dateToISOString(Date target)
	{
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
		return formatter.format(target);
	}
	
	/**
	 * Returns a Date representation of this ISO encoded string datetime
	 * @param target The string representation of this datetime
	 * @return The Date object representation of target
	 * @throws ParseException 
	 */
	private Date isoStringToDate(String target) throws ParseException
	{
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formatter.parse(target);
	}
	
	/**
	 * Determines the Date that is the number of given milliseconds from now
	 * @param milliseconds The number of milliseconds to offset by
	 * @return A new Date object that represents the time a given number of milliseconds from now
	 */
	public static Date getDateFromNow(long milliseconds)
	{
		Date now = new Date();
		long totalMilliSec = now.getTime() + milliseconds;
		return new Date(totalMilliSec);
	}
}

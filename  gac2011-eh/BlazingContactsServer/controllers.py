""" Controllers for the web application layer for the BlazingContacts project

Contains code that responds to requests for the BlazingContacts Android contact
sharing system """

import cgi
import logging
from google.appengine.ext import webapp
from google.appengine.ext.webapp import util
import simplejson as json
import models

class JSONController(webapp.RequestHandler):
	
	NO_ERROR_INT = 0
	NO_ERROR_STRING = ""
	BAD_FORM_ERROR_INT = 1
	BAD_FORM_ERROR_STRING = "Invalid form data"
	GROUP_ALREADY_EXISTS_ERROR_INT = 2
	GROUP_ALREADY_EXISTS_ERROR_STRING = "Group by that name already exists"
	NO_SUCH_GROUP_ERROR_INT = 3
	NO_SUCH_GROUP_ERROR_STRING = "Group by that name does not exist"
	NO_SUCH_CONTACT_ERROR_INT = 4
	NO_SUCH_CONTACT_ERROR_STRING = "No contact by id and group combination"
	BAD_PASSWORD_ERROR_INT = 5
	BAD_PASSWORD_ERROR_STRING = "Invalid group password"
	
	def render_json(self, response, error = NO_ERROR_INT, message = NO_ERROR_STRING, require_serialization = False):
		""" Renders a JSONSerializable object with appropriate headers
		
		@type response: JSONSerializable
		@param response: The object to be rendered to the client
		@type error, integer
		@param error, the error number to report
		@type require_serialization bool
		@param require_serialziation If True, the as_dict method will be called on the result
		@type message, string
		@param message, error message to report to user"""
		
		self.response.headers['Content-Type'] = 'application/json'
		
		# Convert response
		if response != None and require_serialization:
			response = response.as_dict()
		
		returnDict = {"result":response, "error":error, "error_message":message}
		
		self.response.out.write(json.dumps(returnDict))

class GroupDownloadController(JSONController):
	""" Responds to a download request for a group """
	
	def get(self):
		
		# Get the name and group
		try:
			password = self.request.get("password")
			name = self.request.get("name")
			name = cgi.escape(name)
			group = models.Group.get_by_name(name)
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
			
		# Update download count
		group.times_downloaded = group.times_downloaded + 1
		group.put()
		
		# Get all group members and render
		response = map(models.Contact.as_dict, group.get_contacts())
		self.render_json({"contacts" : response}, require_serialization = False)
		
		# Check to see if the group has expired
		if group.expired():
			group.delete()

class GroupController(JSONController):
	""" Responds to queries regarding a group """
	def get(self):
		
		# Get the name and group
		try:
			name = self.request.get("name")
			name = cgi.escape(name)
			group = models.Group.get_by_name(name)
			password = self.request.get("password")
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
			return
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
		
		# Check group exists
		if group == None:
			self.render_json(None, JSONController.NO_SUCH_GROUP_ERROR_INT, JSONController.NO_SUCH_GROUP_ERROR_STRING)
			return
		
		# Render JSON representation
		self.render_json(group)		
	
	def put(self):
		
		# Extract escaped values
		# TODO: This kludge is a result of a bug in Google's App Engine
		#       see http://code.google.com/p/googleappengine/issues/detail?id=719
		try:
			params = cgi.parse_qs(self.request.body)
			name = cgi.escape(params["name"][0])
			expiration = cgi.escape(params["expiration"][0])
			max_members = int(params["max_members"][0])
			password = params["password"][0]
			
			# Convert expiration to a real datetime
			expiration = models.Group.parse_expiration_time(expiration)
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
			return
			
		# Check group uniqueness
		if models.Group.get_by_name(name) != None:
			self.render_json(None, JSONController.GROUP_ALREADY_EXISTS_ERROR_INT, JSONController.GROUP_ALREADY_EXISTS_ERROR_STRING)
			return
		
		# Create and save model instance
		new_group = models.Group(name=name, 
				  expiration=expiration, 
				  max_members=max_members)
		new_group.put()	
		
		# Save password
		new_group.set_password(password)
		new_group.put()
		
		self.render_json(new_group)
	
	def post(self):
		
		# Get the name and group
		name = self.request.get("name")
		name = cgi.escape(name)
		group = models.Group.get_by_name(name)
		password = self.request.get("password")
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
		
		# Update the expiration
		expiration = self.request.get("expiration", None)
		if expiration != None:
			expiration = cgi.escape(expiration)
			expiration = models.Group.parse_expiration_time(expiration)
			group.expiration = expiration
		
		# Update the max members
		max_members = self.request.get("max_members", None)
		if max_members != None:
			max_members = int(max_members)
			group.max_members = max_members
		
		group.put()
		self.render_json(group)
	
	#def delete(self):
		
		# Get the name and group
	#	name = self.request.get("name")
	#	name = cgi.escape(name)
	#	group = models.Group.get_by_name(name)
		
	#	group.delete()

class ContactController(JSONController):
	""" Responds to queries regarding a contact """
	
	def get(self):
		
		# Get the name and group
		try:
			contact_id = int(self.request.get("contact_id"))
			group_name = cgi.escape(self.request.get("group_name"))
			password = self.request.get("password")
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
			return
		
		# Get group
		try:
			group_id = models.Group.get_by_name(group_name).key().id()
		except:
			self.render_json(None,JSONController.NO_SUCH_GROUP_ERROR_INT, JSONController.NO_SUCH_GROUP_ERROR_STRING)
			return
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
		
		# Get contact
		contact = models.Contact.get_by_id_and_group(contact_id, group_id)
		if contact == None:
			self.render_json(None, JSONController.NO_SUCH_CONTACT_ERROR_INT, JSONController.NO_SUCH_CONTACT_ERROR_STRING)
			return
		
		# Render JSON representation
		self.render_json(contact)	
	
	def put(self):
		
		# Extract escaped values
		# TODO: This kludge is a result of a bug in Google's App Engine
		#       see http://code.google.com/p/googleappengine/issues/detail?id=719
		try:
			params = cgi.parse_qs(self.request.body)
			data = cgi.escape(params["data"][0])
			group_name = cgi.escape(params["group_name"][0])
			password = params["password"][0]
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
			return
		
		# Get group
		group = models.Group.get_by_name(group_name)
		if group == None:
			self.render_json(None, JSONController.NO_SUCH_GROUP_ERROR_INT, JSONController.NO_SUCH_GROUP_ERROR_STRING)
			return
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
		
		# Create and save model instance
		new_contact = models.Contact(data=data, group=group)
		new_contact.put()	
		
		# Update group member count
		group.current_users = group.current_users + 1
		group.put()
		
		# Render result
		self.render_json(new_contact)
	
	def post(self):
		try:
			contact_id = int(self.request.get("contact_id"))
			group_name = cgi.escape(self.request.get("group_name"))
			data = cgi.escape(self.request.get("data"))
			password = self.request.get("password")
		except:
			self.render_json(None, JSONController.BAD_FORM_ERROR_INT, JSONController.BAD_FORM_ERROR_STRING)
			return
		
		# Get group
		try:
			group_id = models.Group.get_by_name(group_name).key().id()
		except:
			self.render_json(None,JSONController.NO_SUCH_GROUP_ERROR_INT, JSONController.NO_SUCH_GROUP_ERROR_STRING)
			return
		
		# Check authentication
		if not group.check_password(password):
			self.render_json(None, JSONController.BAD_PASSWORD_ERROR_INT, JSONController.BAD_PASSWORD_ERROR_STRING)
			return
		
		# Get contact
		contact = models.Contact.get_by_id_and_group(contact_id, group_id)
		if contact == None:
			self.render_json(None, JSONController.NO_SUCH_CONTACT_ERROR_INT, JSONController.NO_SUCH_CONTACT_ERROR_STRING)
			return
		
		# Update contact data
		contact.data = data
		contact.put()
		
		# Render result
		self.render_json(contact)
	
	#def delete(self):
	#	pass
	
 # TODO: Cron job
class CleanUpController(webapp.RequestHandler):
	""" Removes all expired groups """
	
	def get(self):
		for group in models.Group.all():
			if group.expired(): group.delete()
		self.response.out.write("Database cleaned")
	
def main():
	application = webapp.WSGIApplication([
		('/group/download', GroupDownloadController),
		('/group', GroupController),
		('/clean', CleanUpController),
		('/contact', ContactController)], debug=True)
	util.run_wsgi_app(application)


if __name__ == '__main__':
	main()

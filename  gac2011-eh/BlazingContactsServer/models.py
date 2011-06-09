"""The server-side database models for the BlazingContacts contact sharing project"""

import datetime
import hashlib
from google.appengine.ext import db
import simplejson as json

class JSONSerializable(db.Model):
	""" Class that can be seralized to JSON """
	
	def as_json(self):
		""" Returns a JSON representation of this group 
		
		@rtype: String
		@return: The json serialization of this model instance"""
		return json.dumps(returnDict)
	
	def as_dict(self):
		""" Stub for serializing this object as a dictionary to be implemented by subclass """
		raise NotImplementedError("Needs to be implemented by subclass")

class Group(JSONSerializable):
	""" A short lifetime BlazingContacts group that groups together users """
	
	NO_GROUP_MAX = -1
	DEFAULT_STARTING_MEMBER_COUNT = 0
	DEFAULT_STARTING_DOWNLOADS_COUNT = 0
	
	creation = db.DateTimeProperty(auto_now=True) # Datetime for creation
	name = db.StringProperty()
	expiration = db.DateTimeProperty() # Time limit for this group
	max_members = db.IntegerProperty() # Max users in this group
	current_users = db.IntegerProperty(default=DEFAULT_STARTING_MEMBER_COUNT) # The number of users that have joined this group
	times_downloaded = db.IntegerProperty(default=DEFAULT_STARTING_DOWNLOADS_COUNT) # The number of users that have downloaded this information successfully
	crypted_password = db.BlobProperty()
	salt = db.BlobProperty()
	
	@classmethod
	def parse_expiration_time(self, str_date):
		""" Converts the given string date to a python DateTime object
		
		@type str_data: String
		@param str_data: The date string to convert
		@rtype DateTime instance
		@return The DateTime form of the string date provided """
		
		return datetime.datetime.strptime( "2007-03-04T21:08:12", "%Y-%m-%dT%H:%M:%S" )
	
	@classmethod
	def get_by_name(self, name):
		""" Gets the group with the given name
		
		@type name: String
		@param name: The name of the group to lookup
		@rtype Group instance
		@return The group with the given name """
		
		query = db.Query(Group)
		query.filter('name = ', name)
		return query.get()
	
	def delete(self):
		""" Deletes this group and all related contacts """
		
		# Clear out contacts
		for contact in self.get_contacts():
			contact.delete()
		
		# Delete group
		Group.delete(self)
	
	def as_dict(self):
		""" Returns a dictionary representation of this group
		
		@rtype Dictionary
		@return The dictionary serialization of this model instance """
		return {"name:" : self.name, 
		        "expiration" : self.expiration.isoformat(),
		        "max_members" : self.max_members,
		        "current_users" : self.current_users}
	
	def get_contacts(self):
		""" Gets all of the contacts associated with this group
		
		@rtype A list of Contact instances
		@return A list of user information assocated with this group"""
		query = db.Query(Contact)
		query.filter('group = ', self)
		return query.fetch(self.max_members)
	
	# Thanks to http://groups.google.com/group/google-appengine-python/browse_thread/thread/e872b4e426431fbb
	def __encrypt(self, plaintext, salt=""): 
		""" Returns the SHA256 hexdigest of a plaintext and salt""" 
		phrase = hashlib.sha256() 
		phrase.update("%s--%s" % (plaintext, salt)) 
		return phrase.hexdigest() 
	
	def set_password(self, new_password): 
		""" Sets the user's crypted_password""" 
		self.salt = self.__encrypt(str(datetime.datetime.now())) 
		self.crypted_password = self.__encrypt(new_password, self.salt)
		
	def check_password(self, plaintext): 
		""" Returns true if the password is correct """
		return self.__encrypt(plaintext, self.salt) == self.crypted_password 
	
	def expired(self):
		""" Returns true if this group information is no longer needed
		
		Returns true if the number of downloads equals the number of people in the group and the maximum group member count or if the current time execeeds that of the expiration time"""
		return (self.max_members == self.current_users and self.max_members == self.current_users) or self.expiration < datetime.datetime.now()

class Contact(JSONSerializable):
	""" Model to contain information about a BlazingContacts client """
	creation = db.DateTimeProperty(auto_now=True) # Datetime for creation
	data = db.TextProperty() # Generic JSON-encoded data about this user
	group = db.ReferenceProperty(Group) # The group this user is part of
	
	@classmethod
	def get_by_id_and_group(self, target_id, group_id):
		""" Gets the contact with the given id / group combination
		
		@type target_id: integer
		@param target_id: The user with the given id will be targeted
		@type group_id: integer
		@param group_id: The group with the given id will be targetd
		@rtype: Contact instance
		@return: The contact with the given combination or None if none matched """
		
		# Attempt to load contact
		try:
			contact = Contact.get_by_id(target_id)
		except:
			contact = None
		
		# Confirm that the group is correct
		if contact != None and contact.group.key().id() == group_id:
			return contact
		else:
			return None
	
	def as_dict(self):
		""" Returns a dictionary representation of this users
		
		@rtype: Dictionary
		@return: The dictionary serialization of this model instance """
		return {"group" : self.group.key().id(),
			"data" : self.data}
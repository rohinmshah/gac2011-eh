Things to think about for the app:

1.  Account type - at present, just uses a default value instead of finding it from the contacts.
2.  final String TAG in MainActivity - why is this there?
3.  Start and Join Activities - we are guaranteed to have a name (otherwise the buttons are invisible).  Also, if there is no phone number / email, uncheck the box.
4.  Better code - instead of having 2 SharedPreferences for start and join, there should only be 1.
5.  If 4. is not done, at least data from the join activity should be put into data for the start activity.
6.  Password - Does the web server check that the password matches?  If so, can be implemented quite easily.  Just need to know what happens if the password doesn't match.
7.  ContactDownloadService creates a notification - it needs an icon (right now it uses one of the default icons).
8.  ContactDownloadAsyncTask - should ask for a GroupStatus every minute or so, but update the notification every 5 seconds.
9.  Sleep mode - When it goes into sleep mode, the service freezes in place.  We need to use a WakeLock and a WifiLock to continue the service, but this will drain the battery.  So, we need to make this an optional thing.
10. Help buttons - Put a help button next to each View explaining the purpose of that view.
11. Options - Create OptionsActivity where the user can set options.
11a.	Option 1: Suppose there is already a contact with the same name as the contact which is about to be added.  Should that contact be updated, or should a new contact be inserted?
12. AsyncTask - Fix publishProgress so that the "Finished!" notification has the number of added contacts and the number of updated contacts.
13. WebWrapper - There are a bunch of warnings and to dos.

Classes that Rohin has checked:

1. ContactsProviderWrapper (account type problem)
2. MainActivity (TAG problem)
3. Contact (no problems)
4. GroupStatus (no problems now, removed the unused finished private field)
5. StartActivity (changed maximum time to 1hr, see 4)
6. ServerException
7. JoinActivity (see 4, 5)
8. GroupActivity
9. ContactDownloadService (removed text variable, see 7)
10. ContactDownloadAsyncTask

WebTestWrapper doesn't need checking.  I didn't check WebWrapper because I barely understand it.
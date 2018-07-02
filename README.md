# Instagram_clone

 App with all basic features of Instagram.
 
## Included features 
1.	Account settings - Sign up, Email verification, Sign in with verified email. 
2.	Main feed to display all photos posted by user, and from people followed by user.
   Can like and comment on the photo. Can take photo using Camera and post it.
   Chat with people in contact list.
3.	Search feed to search any registered users, using auto completed user name. 
   Can choose to follow or un-follow the selected user.
4.	Share feed where user can find photos to share from their device memory. 
   Or can take photo and then share the selected photo with caption.
5.	Profile feed to keep track of user activity in app. 
   Here user can change profile photo, authenticated user name, personal info or can opts to Log out.  
 <br>
 
### 1. Account settings
<p float="left">
  <img src="images/account_settings/sign_in_screen.png" width="300" />
  <img src="images/account_settings/sign_up_screen.png" width="300" /> 
</p>
 
When user first sign up, he has to provide his email to register. Using FireBase authentication we can verify for exiting email, if the enterd email is not already registered in our authenticated user list. We will send the confirmation email to the entered email address. User can sign-in only if he already verfied the email. 
<br><br>

### 2. Main feed
<p float="left">
  <img src="images/main_ feed/shredphoto_display_screen.png" width="280" />
  <img src="images/main_ feed/sharedphoto_display_screen_two.png" width="280" /> 
  <img src="images/main_ feed/comment_screen.png" width="280" />
</p>

Main feed displays all the photos posted by current user, and by all the users followed by current users.
<br>Top label of user name and profile photo, displays image posted user info.
<br>User can like the photo by clicking on heart icon. or can comment on photo by clicking on comment bubble icon. 
<br>

<p float="left">
  <img src="images/main_ feed/camera_sreen.png" width="300" /> 
  <img src="images/main_ feed/chat_screen.png" width="300" />
</p>

User can also take photo and share it with others.
<br>User can chat with mutually(user1 follows user2, user2 also follow user1) followed users.
<br><br>

### 3. Search feed
<p float="left">
  <img src="images/search/search_screen.png" width="280" /> 
  <img src="images/search/follow_user_screen.png" width="280" />
  <img src="images/search/unfollow_user_screen.png" width="280" />
</p>

User can searh for any other registered users, by typing registerd user's user name. 
<br>Current user can opt to follow/un-follow the search result user.
<br><br>

### 4. Share feed
<p float="left">
  <img src="images/share_feed/share_main_screen.png" width="300" /> 
  <img src="images/share_feed/photo_upload_screen.png" width="300" />
</p>

User can select photos from device memory(from DCIM/Camera folders), or can take new photo by camera.
And then share it by writing some caption.
<br><br>

### 5. Profile feed
<p float="left">
  <img src="images/profile_feed/main_profile_screen.png" width="280" /> 
  <img src="images/profile_feed/edit_profile_screen.png" width="280" />
  <img src="images/profile_feed/account_settings_optios_screen.png" width="280" />
</p>

Profile feed keep track of all the user activities, like number of photos shared, number of followers, following users.
<br>User can change their profile photo. Or can edit their personal information here.
<br>Here user can sign-out from the app.





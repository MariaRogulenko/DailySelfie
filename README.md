# DailySelfie

This is my second android application that was written as a part of assignment for Mobile Cloud Conputing for Android (Coursera.org)

If the user clicks on the camera icon on the ActionBar, the app will open up a picture-taking app already installed on the device. If the user now snaps a picture and accepts it, the picture is returned to the DailySelfie app and then displayed to the user along with other selfies the user may have already taken.

If the user clicks on the small view, then a large view will open up, showing the selfie in a larger format.

The image data is stored in some permanent way. In particular, if the user exits the app and then reopens it, they have access to all the selfies saved on their device.

Because the user wants to take selfies periodically over a long period of time, the app should create and set an Alarm that fires roughly once every two minutes. In a real app, this would most likely be set to a longer period, such as once per day. Pulling down on the notification drawer exposes a notification view. Clicking on this notification view brings the user back to the application.

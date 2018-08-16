# Goals

**Goals** is an Android social media application that allows users to set, maintain, and achieve personal goals through the support of their friends. 

## Features
### Sign Up/Login
* **Sign Up** - Users can create accounts by setting their username, password, and profile picture.
* **Login** - Users can login to preexisting accounts.

### Goal Attributes
* **Title** - The name of the goal.
* **Duration** - The expected duration of the goal, in days.
* **Frequency** - How often the user expects to maintain their goal.
* **Friends** - Other users who wish to pursue the same goal.
* **Story** - A collection of photos and videos where the user and their friends can view their progress. Users must upload photos/videos to each goal's story based on the selected frequency, as a means of maintaining their goal. Each post within a story displays the user who posted it and the day it was posted, relative to when the goal began.
* **Reacts** - Users may interact with and show their support for a goal by "reacting" to any individual post within a goal's story, choosing from a selection of six different reacts. Users may view the reacts for each post or the total sum of reacts for a goal as a whole.
* **Streak** - The number of consecutive days/weeks/months (depending on the selected frequency for the goal) that the users in a shared goal post to the goal's story. If any user in a shared goal forgets to post on time, the streak is lost, thereby motivating all the users to keep up with their goal.

### Home Screen
* **Create Goals** - Users can create any number of goals for themselves.
* **View Goals** - Users can see all their goals, as well as view a goal's story, view its remaining duration, view its streak, view whether the streak is about to run out, view its reacts, add reacts, view its friends, add friends, and remove the goal entirely.

### Camera
* **Camera** - Users can take photos, record videos, or upload media from their gallery.
* **Customization** - Users can add captions to their post.
* **Post To Goal** - Users can add their post to any number of goals.

### Profile
* **Notifications** - Users can view notifications related to friend requests, goal requests, and lost streaks. 
* **Logout** - Users can logout from their accounts.

### Feed
* **Add Friends** - Users can add friends by searching their username.
* **Stories** - Users can view and interact with the stories for friends' goals, sorted according to the most recently updated and which the user has not yet viewed. Stories not yet viewed by the user are indicated by an orange dot in the top left corner.
* **Friend Profiles** - Users can view the profile for a friend, which displays all of the friend's goals. Users can also remove their friend and directly message their friends in a private chatroom.

### Other
* **Swipe Navigation** - Users must swipe between screens. Users land on the home screen, swipe right for the camera, swipe left for the feed, and swipe down for their profile.
* **Local Push Notifications** - Users will receive local push notification reminders for maintaining their goal based on its selected frequency.
* **Parse** - All accounts and backend data are stored in Parse.

## Walkthrough
[Video Walkthrough](https://drive.google.com/file/d/1ROYQaiLdi8tun4zL7pw0rk7eLhe6yMoP/view?usp=sharing)

## License

    Copyright 2018 Demar Edwards, Cassandra Kane, Carol Wang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

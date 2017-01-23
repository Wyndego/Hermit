# Hermit

Welcome to Hermit...the Twitter client for people that don't like people. The following guide will aid in understanding the workflow.

## Activities

There are just two Activities in Hermit. The first one allows a user to login and once successful, the user will be presented with an Activity providing interactions with their twitter wall.
The following are a list of assumptions made for each Activity:

1. Sign In Activity
  * there is no Autherization service connected to this client. As such, the only user thtat can login is represented with hardcoded values
  ```
  username: gooduser@success.com
  password: success
  ```
  * there is a helper function that checks for network connectivity. 10% of the time, the connection will fail. As such, some login attempts may fail due to a 'network error' but this is just a fake situation.
  * there is rudimentary username and password validation but true user authentication would be the responsibility of another system entirely.
  * there is no 'User Profile' retrieved (as there is no authorization service), as such, hardcoded values are used for this user's credentials. This means when you add a post, it will always be for:
  ```
  handle: @gooduser
  display name: Some Dood
  icon: the same icon used for the app icon...this user is the only one to use that icon.
  ```
  
2. Twitter Wall Activity
  * to fetch new tweets, drag down (just like any other twitter client) - Using a SwipeRefreshLayout
  * the very first time you install the app, there are no tweets stored for the user, and it is possible that the first fetch for the data may hit the condition where there is a 'network error', or it is possible the random generator will roll a value to not generate any tweets. If this happens, the wall will be blank. Just swipe to the top to generate some data.
  * although there is data for things like 'likes', 'retweets', 'replies', there is no functionality attached to them. This is true of any widgets on the Tweet Wall...it is all just fake data with no additional functionality.
  * the icons in the ActionBar will allow the user to post a new tweet, or logout respectively.
  
## Other Assumptions

1. The server communication is faked using async tasks, which means that features like providing a 'Toast-like' pop-up when there are new tweets for the user are not implemented .To tackle this I would have expected a real server and some GCM functionality.
2. Maybe this is re-iterating a previous comment, but there is only 1 user possible with this client. If I provided a different set of credentials, that user would retrieve the same generated tweets. The data model in the DB can easily be expanded to cover this scenario, but I did not believe it to be part of the goal.

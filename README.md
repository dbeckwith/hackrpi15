# Activity Buddy
##### Find the perfect exercise buddy!

## Contributors
 * [Daniel Beckwith](https://github.com/dbeckwith)
 * [Larisa Motova](https://github.com/limotova)
 * [Aditya Nivarthi](https://github.com/sizmw)

## What Does it Do?
Activity Buddy was created behind the idea that exercise is always more productive and more fun when you have a partner or a group of people to exercise with. But sometimes finding people who have the same schedule as you to run with is difficult, and even more difficult is finding people who run at the same pace as you. Activity Buddy uses data from Microsoft Band to detect your running pace and intensity as well as your schedule in order to find someone who can comfortably run alongside you.

Activity Buddy records distance traveled, time taken, and calories burned during your running routine. It uses this information to infer when and how hard you run and records this in a central database. Then users can use the app to find people with similar routines to them and get them in contact with each other so that they can start exercising together!

## How is it Built?
The Android app was made using the Java Android library, [Butterknife](http://jakewharton.github.io/butterknife/), and Microsoft's [Android Band SDK](https://developer.microsoftband.com/bandSDK). The app communicates with a Node.js server built on [Express.js](http://expressjs.com/) and [MongoDB](https://www.mongodb.com/). The server is hosted in Microsoft's [Azure Cloud Services](https://azure.microsoft.com/en-us/).

To run the project yourself, clone the repository and navigate to the `/apiserver` directory. Then install the dependencies with `npm`:
```shell
$ npm install express mongodb lodash body-parser
```
and run the main API server:
```shell
$ node server.js
```
To use the Android app, open the `/MobileApp` directory in [Android Studio](http://developer.android.com/develop/index.html), connect a debug-enabled Android phone, and hit the Run button.

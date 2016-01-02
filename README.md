## WomenWhoCode
CodePath group project. Bring the WWCode experience mobile. 

**Problem/Opportunity:** Currently, our reach happens through expanding networks 
and our weekly Code Review. We want to be able to reach an engineer even if we 
don’t have a network in their area, more directly, and make it so they feel like 
they are apart of WWCode regardless of where they are in the world. Our goal is 
to reach 1 million engineers.  

**Product Overview:** This experience will leverage existing internal data: 
WWCode events, event streaming, resources, internal blogs, job posts, inbound 
conference data. We will also provide the ability to connect with other like 
minded engineers through in app direct messaging and event driven groups. 

We are going to find out what interests our members based on a personalization 
flow in order to feed them proper data and connect them with like minded members 
in their area and around the world. This is creating a more immediate, and 
direct way to connect regardless of where you are located.

## Setup for Developers
1. Make sure you have downloaded the latest version of [Android Studio](https://developer.android.com/sdk/index.html). It works on Linux, Windows and Mac. Download the correct version for your OS
1. Go to [the project repo](https://github.com/WomenWhoCode/WomenWhoCode.git/) and fork it by clicking "Fork" 
1. If you are working on Windows, download [Git Bash for Windows](https://git-for-windows.github.io/) to get a full Unix bash with Git functionality
1. Clone the repo to your desktop `git clone git@github.com:your_name/WomenWhoCode.git`
1. Open the project with Android Studio 

## Configure remotes
When a repository is cloned, it has a default remote called `origin` that points to your fork on GitHub, not the original repository it was forked from. To keep track of the original repository, you should add another remote named `upstream`:

1. Open terminal or git bash in your local repository and type:

   `git remote add upstream https://github.com/WomenWhoCode/WomenWhoCode.git`
  
1. Run `git remote -v` to check the status, you should see something like the following:

  > origin    https://github.com/YOUR_USERNAME/WomenWhoCode.git (fetch)
  
  > origin    https://github.com/YOUR_USERNAME/WomenWhoCode.git (push)
  
  > upstream  https://github.com/WomenWhoCode/WomenWhoCode.git (fetch)
  
  > upstream  https://github.com/WomenWhoCode/WomenWhoCode.git (push)

1. To update your local copy with remote changes, run the following:

   `git fetch upstream`

   `git merge upstream/master`

   This will give you an exact copy of the current remote, make sure you don't have any local changes.

## Contributing and developing a feature
1. Make sure you are in the master branch `git checkout master`
1. Sync your copy `git pull`
1. Create a new branch with a meaningful name `git checkout -b branch_name`
1. Develop your feature on Android Studio and run it using the emulator or connecting your own Android device
1. Clean your project from Android Studio `Build/Clean project`
1. Add the files you changed `git add file_name` (avoid using `git add .`)
1. Commit your changes `git commit -m "Message briefly explaining the feature"`
1. We have small Pull Requests, try to keep one commit per feature. If you forgot to add changes, you can edit the previous commit `git commit --amend`
1. Push to your repo `git push origin branch-name`
1. Go into [the Github repo](https://github.com/WomenWhoCode/WomenWhoCode.git/) and create a pull request explaining your changes
1. You will need to add a message on the pull request notifying your changes to your reviewer

## Current App

[![current app](http://img.youtube.com/vi/Tt4ATUSORzk/0.jpg)](http://www.youtube.com/watch?v=Tt4ATUSORzk)

https://www.youtube.com/watch?v=Tt4ATUSORzk

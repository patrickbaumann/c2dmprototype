=============
PushPrototype
=============

About
-----
This repository is a collection of applications that was put together with the sole purpose of demonstrating the basic functionality of Google's Cloud to Device Messaging (c2dm). For more on the service, refer to http://code.google.com/android/c2dm/index.html

There are two applications, an Android app and a Django app. The django app was built with Django 1.2 on top of python 2.7.1 and the Android app using version 2.2 of the API. Refer to http://developer.android.com/sdk/index.html and http://docs.djangoproject.com/en/dev/intro/install/ for more on setting up those environments.

Setup
-----
*Aside from the exceptions below, these apps are pretty vanilla Android and Django configurations.*

* If you haven't already, you'll need to register your gmail or google apps address here: http://code.google.com/android/c2dm/signup.html

* Before running the Django application, rename pushprototypedjango/push/authentication.default.py to pushprototypedjango/push/authentication.py::
   
      mv pushprototypedjango/push/authentication.default.py pushprototypedjango/push/authentication.py
   
* Modify the newly moved file and follow the directions. You'll need to run the following from the console in order to retrieve your authentication token (curl with ssl libraries required: http://curl.haxx.se/)::

      curl https://www.google.com/accounts/ClientLogin -k --data-urlencode Email=youraccount@gmail.com --data-urlencode Passwd=some_password -d accountType=GOOGLE -d source=com.patrickbaumann.pushprototype -d service=ac2dm
  
* The response will contain an SID, AUTH, and LSID::

      SID=alsdjfa;ljsdf;lajsdlfj...
      AUTH=alsdjkfa;lskjdfl;asjd...
      LSID=asl;dfjalskdjflasjdfl...
   
* Paste the AUTH line after the '=' into authentication.py between the quotation marks.

* In order to build the android application, you'll need to download and include the jars that are released as part of the Apache HttpClient libraries in your build path. You can download the latest libraries here: http://hc.apache.org/downloads.cgi
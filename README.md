# URL Shortener API

Simple URL Shortener API, written in JAVA, for Apache Tomcat 9 server.

### Setup

Just copy the folders to: $CATALINA_BASE/webapps, and run the server.

### How to use

To get the shortened url do a POST request to: localhost[:portNumber]/shorten;


Request should contain JSON body in following format: {"url": "[actual url that you want shortened]"}.
 
 *example: {"url":"<https://www.google.com>"}*
  
  
By visiting the shortened link in the response, you will be redirected to the appropriate website.

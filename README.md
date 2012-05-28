![JResponder](/jresponder/jresponder/raw/master/jresponder-web/WebContent/assets/images/jresponder-logo.png)

## An email autoresponder written in Java

# COMING SOON! - This thing is still work in progress, big time.  You have been warned ;)

# Introduction

JResponder is a relatively simple autoresponder email system.  Meaning you hook it up to your website so people subscribe to your mailing list and they are automatically sent a series of emails at particularly chosen intervals.

Samples showing how to rapidly integrate JResponder into your website (using JQuery to call simple JSON web services), a robust (or at least getting more robust as we go along here) sending engine, and a very simple database schema with support for a zero-configuration embedded database or MySQL (or other DBMS supported by Hibernate) provide a workable email autoresponder system that you can use as-is or customize as you see fit.

A standalone download bundles this project with Apache Tomcat, to make a version that runs "out of the box".  JResponder is also easy to customize if you're a Java developer.

# Features

* Standalone version makes it easy to get up and running
* Standard .war deploy inside Java web containers
* JSON-RPC/JSONP interface makes web site integration fast and simple (sample code included)
* Simple file-based HTML+Velocity message format makes authoring messages fast and painless
* Messages are fully dynamic - text and HTML content and subject line can be dynamicized easily
* Comes with embedded database for zero config to get started testing.  Works with MySQL out of the box as well.  Setup for other databases (as supported by Hibernate) is minimal.
* Based on modern Java frameworks and specs: Spring 3.1, JPA, Hibernate, JUnit 4 for testing

# Getting Started

If you just want to download JResponder to try it out, I suggest jresponder-standalone.tar.gz - which you can get from here: http://code.google.com/p/jresponder/downloads/list

Unpack that and have a look at the README file.

TODO: Make sure there is a list somewhere - either here or in that README file that lists out 1, 2, 3, etc. the tour of the key things that one needs to configure and then test to get it so test messages actually send.  Strive for making this as simple as possible.

# Project Status

JResponder is very young.  Aside from "in-development", the overall status of this project is definitely EXPERIMENTAL.

The first JResponder builds and source tree were recently pushed to github, work is in progress on stabling it so we can have an official 0.1 release.

# Requirements

* Java 6/JDK 1.6
* For web app deployment, a Java web container - tested with Tomcat 7.

# Overview

JResponder is pretty simple.  But here are the key concepts you should track with:

## TODO

## Running and Data Storage

(Explain standalone mode and default H2)

# Subscribing, Unsubscribing and Site Integration

TODO

# Configuration

TODO

# Building and Hacking

1. Get the source from github... (git clone)
2. Go to jresonder-builds and run "ant" - this will download and build everything.  Note: this might take a while, as quite a few jars and a version of Tomcat get downloaded in the process.  Rough estimate: 15 min.
3. While developing, it's faster to run "ant" from jresponder-standalone (where as jresponder-builds makes .tar.gz file and javadoc, which just slow you down for regular dev)
3. The main source is in jresponder/src; the webapp is in jresponder-web/WebContent
4. To run your jresponder build standalone, you can just do "jresponder-standalone/build/tomcat/bin/catalina.sh run" - that will run the standalone bundled tomcat instance with your build webapp in it.
5. You can see the standalone webapp at: http://localhost:6680/

## Developing with Eclipse

Most of the JResponder code was written and initially tested in Eclipse. Here's how to get set up:

* Follow the first two steps above - this is important as it downloads the jar files you need.  Otherwise Eclipse ain't gonna be able to do nothi'n.
* Import each of these three Eclipse projects: jresponder-deps, jresponder, jresponder-web
* You might need to right click -> refresh (or F5) on each of those for it show all files changes.
* Now you can build and run the webapp using the normal Eclipse Tomcat deployment steps.  (You know - show servers, create a server if not done already, add/remove the web project to it, and press the little play button)

# Roadmap

Some of the key next things to get done on JResponder are:

* Complete the sample pages for subscription/unsubscription, make it work cross domain with JSONP
* Unit/regression tests
* Test message sending
* Better/any administrative interface
* DKIM support
* Embedded mail server - for bounces and "re: unsubscribe" handling
# Review the sending engine and see how we can make it more efficient (probably just having a configurable number of threads would work as immediate boost)
* Mail server configuration/integration docs
* Open and click tracking
* Review the idea of adding general email sending as a main feature.  Not sure yet if this is wise or foolhardy

# License

JResponder is licensed under the Apache 2.0 license.


![JResponder](/jresponder/jresponder/raw/master/jresponder-web/WebContent/assets/images/jresponder-logo.png)

## An email autoresponder written in Java

# Introduction

JResponder is a relatively simple autoresponder email system.  Meaning you hook it up to your website so people subscribe to your mailing list and they are automatically sent a series of emails at particularly chosen intervals.

Samples showing how to rapidly integrate JResponder into your website (using JQuery to call simple JSON web services), a robust (or at least getting more robust as we go along here) sending engine, and a very simple database schema with support for a zero-configuration embedded database or MySQL (or other DBMS supported by Hibernate) provide a workable email autoresponder system that you can use as-is or customize as you see fit.

A standalone download bundles this project with Apache Tomcat, to make a version that runs "out of the box".  JResponder is also easy to customize if you're a Java developer.

# Getting Started

If you just want to download JResponder to try it out, I suggest jresponder-standalone.tar.gz - which you can get from here: http://github.com/jresponder/jresponder-downloads

Unpack that and have a look at the README file.

# Project Status

JResponder is very young.  Aside from "in-development", the overall status of this project is definitely EXPERIMENTAL.

The first JResponder builds and source tree were recently pushed to github, work is in progress on stabling it so we can have an official 0.1 release.

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
2. Go to jresonder-builds and run "ant" - this will download and build everything
3. Most of the JResponder code was written and initially test in Eclipse.  Once you have run the build above (which downloads the jar dependencies you'll need),  you can open the jresponder and jresponder-web projects in Eclipse and build/run from there as well.

# Roadmap

Some of the key next things to get done on JResponder are:

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


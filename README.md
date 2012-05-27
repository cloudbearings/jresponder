![JResponder](/jresponder/jresponder/raw/master/jresponder-web/WebContent/assets/images/jresponder-logo.png)

## An email autoresponder written in Java

# Introduction

JResponder is a relatively simple autoresponder email system.  Meaning you hook
it up to your website so people subscribe to your mailing list and they are
automatically sent a series of emails at particularly chosen intervals.

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
* Embedded mail server
* Mail server configuration/integration docs
* Open and click tracking
* Review the idea of adding general email sending as a main feature.  Not sure yet if this is wise or foolhardy



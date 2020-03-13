================================================================================
Remote Smart House Protocol
Drexel CS544, Spring 2013
Group 12:
- Ryan Corcoran
- Amber Heilman
- Michael Mersic
- Ariel Stolerman
================================================================================

Executables:
------------

All executables can be found on TUX in /home/ams573/rshc/
In addition, the submission ZIP archive contains the directory rshc/ which
contains all the files listed next.
Files:
- DES_STORE
  RSHC server user/password pairs. Clients are authenticated against the
  credentials in this file.

- rshc.jar
  Executable JAR file to start server / clients. Ran via the server.csh and
  client.csh executables.

- client.csh
  Executable to run the client. Expected arguments:
	[-host <host>] [-port <port>] -login <user>:<pass>
	E.g.: -host 127.0.0.1 -port 7070 -login myname:mypassword
	*  Default host: 127.0.0.1
	*  Default port: 7070
	Or to run in test mode (send raw messages to the server):
	-test
  To start a client, the credentials john:smith123 can be used, e.g.:   
  > client.csh -login john:smith123
  To start a client in test mode:
  > client.csh -test
  To run the client from the attached directory locally, Java 7 should be
  installed on the machine, and the following command can invoked via console:
  > java -cp rshc.jar client.Client -login john:smith123
  
- server.csh
  Executable to run the server (no arguments are expected).
  The server is hardcoded to use port 7070. To start the server:
  > server.csh
  Like for the client, the server can be ran from the attached directory
  locally by invoking the command:
  > java -cp rshc.jar server.Server

Robustness:
-----------
We have tested our implementation thoroughly, and believe it is fairly robust
against fuzzing. We tested our implementation as follows:
- Correctness:
  We tested all functionality of the different components of the code, including
  house, devices, actions, crypto-functions etc.
- Protocol correctness:
  In addition we ran sanity tests to check how the protocol components behave in
  correct settings.
- Robustness:
  We tested both the client and the server in settings where unexpected
  messages are sent by the other end, at different states of the protocol, i.e.
  a thorough fuzzing test check. At every possible state of the protocol on the
  server and the client side, we "threw" at the protocol different messages:
  - completely invalid messages
  - valid messages at other states
  - valid messages at current state, but with invalid functionality (e.g.
    illegal action to be applied on the house)
  Specifically for testing the server (which in real-world scenarios is more
  susceptible to fuzzing attacks than clients), we used the client tester (the
  class ClientCommTester) that allows sending raw byte streams to the server for
  testing purposes.
- Concurrency:
  We tested how the server handles multiple clients, in different scenarios. We
  checked that the state of the house maintained by all connected clients is
  valid and identical at all times. We checked different flows of multiple
  actions, confirmations, denials and updates.
- Environment:
  The server-client framework was tested in different environements - both on
  our local machines and on TUX (therefore tested on Windows, Mac and Linux).


Extra-Credit:
-------------
We have not implemented the extra-credit.

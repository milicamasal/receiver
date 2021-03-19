# Receiver
Receiver is simple java console application for receiving and handling packets from server. All packets have a delay assigned to them. With the expiration of the delay, supposing that the application is in start state, the packet will be sent back to the server. Otherwise, if the application has been stopped in the meantime with stop command and then started again, the packets whose delay hasn't expired will be sent back to the server and if delay has expired, server will be notified about their expiration.  

## How to use

The application will allow the user to input the following commands:

```start ``` - the application will start receiving packets from server

```stop``` - the application will stop receiving packets from server

## Log file

Every state of packets(received, sent, unsent) will be logged in receiver.log file.

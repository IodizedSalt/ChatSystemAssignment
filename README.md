# ChatSystemAssignment
Chat system w Server+Client

Instructions:

    -Launch ChatServer.java and wait for the terminal to output "Server online"
    
    -Launch ChatClient.java
    
    -Enter local IPv4 Address
    
    -Enter a unique username that does not contain [$&+,:;=?@#|'<>.^*()%!]
    
    -Type messages in the textfield (Where it says 'Enter text here')
    
    -If no message  is sent for 1 minute to other useres on the server, your client will time out
    
    -Press disconnect to disconnect from the server

Error Messages:

    -406: Not acceptable input for username- Username may not contain [$&+,:;=?@#|'<>.^*()%!]
    
    -400: Duplicate username- Someone is already using your username, enter a new one


Features Not Working/Implemented

    -IMAV sends heartbeat every 60 seconds even if a message from that user was sent in the last 60 seconds
    -List does not update all users in the current chat program.
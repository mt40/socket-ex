# socket-ex
Simple wrapper of Java Socket class. Created for my homework...

# How to use
For server side:
```java
SocketEx sv = new SocketEx(YOUR_IP, YOUR_PORT);
System.out.println("Listening...");

sv.on("connected", (HostName sender, Packet req) -> {
    System.out.println(req.message);
    // Send message back to this client (a.k.a 'sender' in this context)
    sv.emit("welcome", sender, new Packet("Welcome to the room!"));
    sv.broadcast("announcement", new Packet("A new user has joined!"));
});
```

On client side:
```java
SocketEx sv = new SocketEx(SERVER_IP, SERVER_PORT);
sv.connect(Info.Ip, Info.ServerPort);

sv.on("welcome", (HostName sender, Packet p) -> {
    System.out.println(p.message);
});

sv.on("announcement", (HostName sender, Packet p) -> {
    System.out.println(p.message);
});
```

const express = require('express');
const app = express();
const http = require('http');
const server = http.createServer(app);
const { Server } = require("socket.io");
const fs = require("fs")
const io = new Server(server);

var hostname = "192.168.1.254"
var port = 3000;
server.listen(port, hostname)

var listUser = []
io.sockets.on('connection', function (socket) {
    console.log("Connect...")
    socket.on('user_login', function (user_name) {
        if (listUser.indexOf(user_name) > 0) {
            return;
        }
        listUser.push(user_name)
        socket.user = user_name
        console.log(`xin chao ${user_name}`)
    })
    socket.on('send_message', (message)=>{
        io.sockets.emit('receiver_message', {data: socket.user+": "+message})
        console.log(`${socket.user} sent: ${message}`)
    })

})
console.log(`Server is running at http://${hostname}:${port}`)
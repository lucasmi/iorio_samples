var SerialPort = require('serialport');
///dev/ttyUSB0
var portComunication = new SerialPort('COM5', {
    baudRate: 115200,
    parser: SerialPort.parsers.readline('\n')
});

portComunication.open(function(err) {
    // write errors will be emitted on the port since there is no callback to write 
    if (err) {
        return console.log('Error opening port: ', err.message);
    }


    setTimeout(function() {
        portComunication.write("1;0;255;1;0;255;");
    }, 2000);

    setTimeout(function() {
        portComunication.write("1;1;0;1;1;0;");
    }, 6000);

    setTimeout(function() {
        portComunication.write("0;1;255;0;1;255;");
    }, 4000);


    setTimeout(function() {
        portComunication.write("1;1;0;1;1;0;");
    }, 6000);
});


portComunication.on('data', function(data) {
    console.log('Data: ' + data);
});

portComunication.on('error', function(err) {
    if (err.message != "Port is opening") {
        console.log('Error COM: ', err.message);
    }

})

portComunication.on('disconnect', function(data) {
    //Para motores
    portComunication.write("1;1;0;1;1;0;");
});



/*
var WebSocketServer = require('websocket').server;
var http = require('http');

var server = http.createServer(function(request, response) {
    // process HTTP request. Since we're writing just WebSockets server
    // we don't have to implement anything.
});


var portHTTP=1337;

server.listen(portHTTP, function() { });
console.log("Server:" + portHTTP);

// create the server
wsServer = new WebSocketServer({
    httpServer: server
});

// WebSocket server
wsServer.on('request', function(request) {
    console.log("Chegou CONEXAO");




    var connection = request.accept(null, request.origin);

    // This is the most important callback for us, we'll handle
    // all messages from users here.
    connection.on('message', function(message) {
        var receivedJson = null;

        try {

            if (message.type === 'utf8') {
               // console.log("Chegou:" + JSON.stringify(message));
            }

            //converte de volta
            receivedJson = JSON.parse(message.utf8Data);
            console.log("CHEGOU:" + receivedJson.message);

        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message.data);
            return;
        }


        //responde
        var sendJson = new Object();
        sendJson.message = "MENSAGEM RECEBIDA";
        connection.send(JSON.stringify(sendJson));
        console.log("ENVIOU:" + sendJson.message );
    });

    connection.on('close', function(connection) {
        // close user connection

        console.log("Fechou conexao");
    });
});*/

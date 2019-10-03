var connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection;
var type = connection.effectiveType;

function updateConnectionStatus() {
    console.log("Connection type changed from " + type + " to " + connection.effectiveType);
    type = connection.effectiveType;
}

connection.addEventListener('change', updateConnectionStatus);
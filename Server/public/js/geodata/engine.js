// fecha-hora // lat // lon // aceleracion // luz // ruido 

// Geolocation
var lat;
var lon;
// Light sensor
var exposure;
// Microphone
var noise;
// Datetime
var date;
// Accelerometer
var accelerometer = {
    x: 0, 
    y: 0, 
    z: 0
}

function ubicacion() {
    if (navigator.geolocation) {
        watchID = navigator.geolocation.watchPosition(function (position) {
            pos = {
                lat: position.coords.latitude,
                lon: position.coords.longitude,
            };
            window.lat = pos.lat
            window.lon = pos.lon 
        });
    } else {
        console.log('Browser doesnt support Geolocation');
    }
}

function light() {
    try {
        navigator.permissions.query({ name: 'ambient-light-sensor' }).then(result => {
            if (result.state === 'denied') {
                console.log('Permission to use ambient light sensor is denied.');
                return;
            }
        
            const als = new AmbientLightSensor({frequency: 10});
            als.addEventListener('activate', () => console.log('Ready to measure EV.'));
            als.addEventListener('error', event => console.log(`Error: ${event.error.name}`));
            als.addEventListener('reading', () => {
                // Defaut ISO value.
                const ISO = 100;
                // Incident-light calibration constant.
                const C = 250;

                let lux = als.illuminance
                window.exposure = Math.round(Math.log2((lux * ISO) / C)); 
            });
        
            als.start();
        });
    } catch (error) {
        // Handle construction errors.
        if (error.name === 'SecurityError') {
            // See the note above about feature policy.
            console.log('Sensor construction was blocked by a feature policy.');
        } else if (error.name === 'ReferenceError') {
            console.log('Sensor is not supported by the User Agent.');
        } else {
            throw error;
        }
    }
}

function accelerometer() {
    let accelerometer = null;
    try {
        navigator.permissions.query({ name: 'accelerometer' }).then(result => {
            if (result.state === 'denied') {
                console.log('Permission to use accelerometer sensor is denied.');
                return;
            }
            // Use the sensor.
            accelerometer = new Accelerometer({ referenceFrame: 'device' });
            accelerometer.addEventListener('activate', () => console.log('Ready to measure accelerometer'))
            accelerometer.addEventListener('error', event => console.log(`Error: ${event.error.name}`));
            accelerometer.addEventListener('reading', e => {
                window.acc.x = accelerometer.x
                window.acc.y = accelerometer.y
                window.acc.z = accelerometer.z
                reloadOnShake(accelerometer)
            }); 
            accelerometer.start();
        });
    } catch (error) {
        // Handle construction errors.
        if (error.name === 'SecurityError') {
            // See the note above about feature policy.
            console.log('Sensor construction was blocked by a feature policy.');
        } else if (error.name === 'ReferenceError') {
            console.log('Sensor is not supported by the User Agent.');
        } else {
            throw error;
        }
    }
}


// TODO Add noise and accelerometer
function a() {
    console.log((new Date()).toString(), window.lat, window.lon, window.exposure);
}

window.ubicacion();
window.light();
//window.accelerometer();

var t = setInterval(a, 1000);
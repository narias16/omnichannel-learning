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
                console.log("Acceleration along the X-axis " + accelerometer.x);
                console.log("Acceleration along the Y-axis " + accelerometer.y);
                console.log("Acceleration along the Z-axis " + accelerometer.z);
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
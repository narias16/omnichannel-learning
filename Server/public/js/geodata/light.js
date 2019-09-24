
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
            let EV = Math.round(Math.log2((lux * ISO) / C));
            
            //console.log(`Illuminance is: ${lux}`);
            console.log(`Exposure Value (EV) is: ${EV}`);
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

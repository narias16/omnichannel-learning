function location(){
    if (navigator.geolocation) {
        watchID = navigator.geolocation.watchPosition(function (position) {
            pos = {
                lat: position.coords.latitude,
                lon: position.coords.longitude,
            };
    
            console.log(pos.lat, pos.lon);
        });
    } else {
        console.log('Browser doesnt support Geolocation');
    }
} 
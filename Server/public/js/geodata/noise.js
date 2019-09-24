URL = window.URL || window.webkitURL;

var gumStream; // Stream from getUserMedia
var rec; // Reorder.js object

// MediaStreamAudioSourceNode we'll be recording 
// shim for AudioContext when its not available
var input;

var AudioContext = window.AudioContext || window.webkitAudioContext;

var recordButton = document.getElementById("recordButton");
recordButton.addEventListener("click", noise);

function noise() {
    startRecording();
    var t = setInterval(saveRecording, 5000);
}

function startRecording() {
    document.getElementById("recordButton").disabled = true;

    // New audio context to help us record
    var audioContext = new AudioContext;

    // Simple constraints object
    var constraints = {
        audio: true, 
        video: false
    }

    // https://developer.mozilla.org/en-US/docs/Web/API/MediaDevices/getUserMedia
    navigator.mediaDevices.getUserMedia(constraints)
    .then(function(stream) {
        console.log("getUserMedia() success, stream created, initializing Recorder.js ...");
        // Assign no gumStream for later use
        gumStream = stream;
        // Use the stream
        input = audioContext.createMediaStreamSource(stream);
        // Create the Recorder object and configure to record mono sound (1 channel)
        rec = new Recorder(input, {
            numChannels: 1
        })
        // start the recording process 
        rec.record();
        console.log("Recording started");
    })
    .catch(function(err) {
        console.log(err);
    });
}

function saveRecording() {
    console.log("stopping recording ");
    rec.stop();
    gumStream.getAudioTracks()[0];
    rec.exportWAV(createDownloadLink);
    
    rec.clear();
    rec.record();
}

function createDownloadLink(blob) {
    var url = URL.createObjectURL(blob);
    var audio = document.createElement('audio');
    var li = document.createElement('li');
    var link = document.createElement('a');
    // add controls to the <audio> element
    audio.controls = true;
    audio.src = url;
    // link the <a> element to the blob 
    link.href = url;
    link.download = new Date().toISOString() + '.wav';
    link.innerHTML = link.download;
    // add the new audio and <a> elements to the <li> element
    li.appendChild(audio);
    li.appendChild(link);
    // add the <li> element to the ordered list
    recordingsList.appendChild(li); 
}
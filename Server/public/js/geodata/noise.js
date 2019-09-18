URL = window.URL || window.webkitURL;
var gumStream;
// Stream from getUserMedia
var rec;
// Reorder.js object
var input;
// MediaStreamAudioSourceNode we'll be recording 
// shim for AudioContext when its not available
var AudioContext = window.AudioContext || window.webkitAudioContext;
var audioContext = new AudioContext;
// New audio context to help us record

function noise() {}

function startRecording() {
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

function stopRecording() {
    console.log("stopping recording ");
    
    rec.stop();
    gumStream.getAudioTracks()[0].stop()

    rec.exportWAV(createDownloadLink)
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
    li.appendChild(au);
    li.appendChild(link);
    // add the <li> element to the ordered list
    recordingsList.appendChild(li); 
}
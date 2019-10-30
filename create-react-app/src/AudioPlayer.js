import React from 'react';
import ReactAudioPlayer from 'react-audio-player';

export default function AudioPlayer({value}) {
    return (
        <React.Fragment>
        <ReactAudioPlayer
            src={value}
            autoPlay
            controls
            />
        </React.Fragment>
    );
}
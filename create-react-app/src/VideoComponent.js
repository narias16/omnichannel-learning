import "../node_modules/video-react/dist/video-react.css"; // import css

import React from 'react';
import { Player } from 'video-react';

export default function VideoComponent({value}) {
  return (
    <React.Fragment>
        <Player
            playsInline
            src={value}
        />
    </React.Fragment>
  );
};
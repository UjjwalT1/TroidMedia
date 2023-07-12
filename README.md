# TroidMedia
Compose Desktop based Media Player . The player keep tracks of the recent media played . Creates a Home Page that contains the recent folder opened . Users can maunally add paths for which they want access inside the application .

# But:
This project uses VLCJ as the media encoding library and while its working fine in test build environment but when I create its distributable then unfortunately the class that is responsible for callback function that renders output from the data stored in buffer suffers NoClassDEefFound Error.
As a result no buffers for frames are being allocaed to media and only the audio is functional. Because I haven't yet found  the solution for this issue Im halting its further development.

# TroidMedia
Compose Desktop based Media Player . The player keep tracks of the recent media played . Creates a Home Page that contains the recent folder opened . Users can maunally add paths for which they want access inside the application .

# But:
This project uses VLCJ as the media encoding library and while its working fine in test build environment but when I create its distributable then unfortunately the class that is responsible for callback function that renders output from the data stored in buffer suffers NoClassDEefFound Error.
As a result no buffers for frames are being allocaed to media and only the audio is functional. Because I haven't yet found  the solution for this issue Im halting its further development.


Altough the Jar file works fine as expected. I think that the problem is with the manner of packaging that I am currently unaware of.

Added Folders
![image](https://github.com/UjjwalT1/TroidMedia/assets/121283901/c36b6678-936e-4e47-b673-7fa4438ee20b)

Playing sample video
![image](https://github.com/UjjwalT1/TroidMedia/assets/121283901/1df95ae3-10b7-44d2-b0c0-ad820179b185)

Settings Tab
![image](https://github.com/UjjwalT1/TroidMedia/assets/121283901/9d491396-666b-4c98-9abc-44e21bb840ff)






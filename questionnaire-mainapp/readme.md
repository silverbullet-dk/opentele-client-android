Diverse bygge-instruktioner
===========================
For at bygge skal der være en ANDROID_HOME være sat op i path. Se: [http://www.scratchmytail.com/2011/08/12/eclipse-no-android-sdk-path-could-be-found/](http://www.scratchmytail.com/2011/08/12/eclipse-no-android-sdk-path-could-be-found/)


Intel-emulator
--------------
Det er mange gange hurtigere at køre på en Intel-emulator end en ARM-emulator, og det er endda også meget
hurtigere end på et faktisk device. Følg instruktionerne her:
[http://www.buildroid.org/blog/?page_id=121](http://www.buildroid.org/blog/?page_id=121)

For at få 10.0.2.2 til at fungerer for at få adgang til din localhost fra emulatoren:
* I VirtualBox, gå i menuen "VirtualBox -> Preferences...", gå til "Network"-fanen. Opret et nyt "Host-only
network", og redigér det. På første fane sætter du "IPv4 Address" til "10.0.2.2".

Når din virtuelle maskine er startet, skal adb forbinde til den. Det gøres med

    adb connect localhost:5555


Kørsel af integrationstests (p.t. Mac-only)
-------------------------------------------
Installér først Xcode. Åbn Xcode, gå ind i Preferences, og installér "Command Line Tools" på "Components"-fanen.

Kør følgende på en kommandolinje:

    sudo gem install calabash-android -v 0.3.6

Nu køres integrationstestene således (det antages at en Android-emulator er kørende):

    mvn clean install -Pcalabash

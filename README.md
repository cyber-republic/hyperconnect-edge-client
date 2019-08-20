# HyperConnect IoT - Edge Client for Raspbian, Ubuntu, MacOS and Windows

The Edge Client is the software component that runs on the IoT device and can be used with a Graphical User Interface (GUI).

Edge Clients can be remotely via the Remote Controllers for [Android](https://github.com/cyber-republic/hyperconnect-remote-controller-android) or [iOS](https://github.com/cyber-republic/hyperconnect-remote-controller-ios)

![HyperConnect IoT](/images/hyperconnect-banner.png)

# Installation

- [Installation](#installation)
  * [Raspbian on Raspberry Pi](#raspbian-on-raspberry-pi)
    + [Prerequisites](#prerequisites)
    + [Installation](#installation-1)
    + [Programming languages](#programming-languages)
      - [Open a Terminal](#open-a-terminal)
    + [Use Prebuilt Binaries](#use-prebuilt-binaries)
      - [Download from the GitHub repository.](#download-from-the-github-repository)
      - [Change directory. Replace YOUR-PATH with your local path to the directory.](#change-directory-replace-your-path-with-your-local-path-to-the-directory)
      - [Start the HyperConnect Edge Client as Administrator using 'sudo' .](#start-the-hyperconnect-edge-client-as-administrator-using--sudo--)
  * [Windows](#windows)
    + [Prerequisites](#prerequisites-1)
    + [Installation](#installation-2)
      - [Download from the GitHub repository.](#download-from-the-github-repository-1)
      - [Open Command Prompt(CMD).](#open-command-prompt-cmd-)
      - [Change directory. Replace YOUR-PATH with your local path to the directory.](#change-directory-replace-your-path-with-your-local-path-to-the-directory-1)
      - [Start the HyperConnect Edge Client.](#start-the-hyperconnect-edge-client)
  * [Ubuntu](#ubuntu)
    + [Prerequisites](#prerequisites-2)
    + [Programming languages](#programming-languages-1)
    + [Installation](#installation-3)
      - [Use Prebuilt Binaries](#use-prebuilt-binaries-1)
      - [Download from the GitHub repository.](#download-from-the-github-repository-2)
      - [Change directory. Replace YOUR-PATH with your local path to the directory.](#change-directory-replace-your-path-with-your-local-path-to-the-directory-2)
      - [Start the HyperConnect Edge Client.](#start-the-hyperconnect-edge-client-1)
  * [MacOS](#macos)
    + [Prerequisites](#prerequisites-3)
    + [Programming languages](#programming-languages-2)
    + [Installation](#installation-4)
      - [Use Prebuilt Binaries](#use-prebuilt-binaries-2)
      - [Download from the GitHub repository.](#download-from-the-github-repository-3)
      - [Change directory. Replace YOUR-PATH with your local path to the directory.](#change-directory-replace-your-path-with-your-local-path-to-the-directory-3)
      - [Start the HyperConnect Edge Client.](#start-the-hyperconnect-edge-client-2)
  * [Libraries](#libraries)
  * [Contribution](#contribution)
  * [Acknowledgments](#acknowledgments)
  * [License](#license)


## Raspbian on Raspberry Pi

### Prerequisites
- Raspberry Pi Board
- Raspbian OS installed
- Monitor, Keyboard, Mouse
- Direct or remote access (VNC)
-  [Raspbian OS](https://www.raspberrypi.org/downloads/raspbian/) installed

### Installation

- Install Git
```
sudo apt-get install git
```

### Programming languages

#### Open a Terminal

- Update
```
sudo apt-get update
```
- Install Java
```
sudo apt-get install openjdk-8-jdk
```
- Install JavaFX
```
sudo apt-get install openjfx
```
- Install Python
```
sudo apt-get install python3
```

### Use Prebuilt Binaries

#### Download from the GitHub repository.
```
git clone https://github.com/cyber-republic/hyperconnect-edge-client
```

#### Change directory. Replace YOUR-PATH with your local path to the directory.
```
cd YOUR-PATH/hyperconnect-edge-client/demo
```

#### Start the HyperConnect Edge Client as Administrator using 'sudo' .
```
sudo java -jar hyper_connect.jar
```
It may take 5-15 seconds at start for the front-end to load. Please be patient.



## Windows

### Prerequisites
The following must be installed:

 - Java ([https://www.java.com/en/download/](https://www.java.com/en/download/))
 - JavaFX (As of JDK 7u6 JavaFX is included with the standard JDK and JRE bundles)
 - Python ([https://www.python.org/downloads/](https://www.python.org/downloads/))


### Installation

#### Download from the GitHub repository.

Manually: [https://github.com/cyber-republic/hyperconnect-edge-client/archive/master.zip](https://github.com/cyber-republic/hyperconnect-edge-client/archive/master.zip)

Or using Git:
```
git clone https://github.com/cyber-republic/hyperconnect-edge-client
```

#### Open Command Prompt(CMD).

#### Change directory. Replace YOUR-PATH with your local path to the directory.
```
cd YOUR-PATH/hyperconnect-edge-client/demo
```

#### Start the HyperConnect Edge Client.
```
java -jar hyper_connect.jar
```


## Ubuntu

### Prerequisites
The following must be installed:

### Programming languages
- Update
```
sudo apt-get update
```
- Install Java
```
sudo apt-get install openjdk-8-jdk
```
- Install JavaFX
```
sudo apt-get install openjfx
```
- Install Python
```
sudo apt-get install python3
```

### Installation

#### Use Prebuilt Binaries

#### Download from the GitHub repository.
```
git clone https://github.com/cyber-republic/hyperconnect-edge-client
```

#### Change directory. Replace YOUR-PATH with your local path to the directory.
```
cd YOUR-PATH/hyperconnect-edge-client/demo
```

#### Start the HyperConnect Edge Client.
```
java -jar hyper_connect.jar
```


## MacOS

### Prerequisites
The following must be installed:

### Programming languages
- Update
```
sudo apt-get update
```
- Install Java
```
sudo apt-get install openjdk-8-jdk
```
- Install JavaFX
```
sudo apt-get install openjfx
```
- Install Python
```
sudo apt-get install python3
```

### Installation

#### Use Prebuilt Binaries

#### Download from the GitHub repository.
```
git clone https://github.com/cyber-republic/hyperconnect-edge-client
```

#### Change directory. Replace YOUR-PATH with your local path to the directory.
```
cd YOUR-PATH/hyperconnect-edge-client/demo
```

#### Start the HyperConnect Edge Client.
```
java -jar hyper_connect.jar
```




## Libraries

- Elastos Carrier Native SDK: https://github.com/elastos/Elastos.NET.Carrier.Native.SDK

## Contribution
We welcome contributions to the HyperConnect IoT project.

## Acknowledgments
A sincere thank you to all teams and projects that we rely on directly or indirectly.

## License
This project is licensed under the terms of the [GPLv3 license](https://github.com/cyber-republic/hyperconnect-edge-client/blob/master/LICENSE).
